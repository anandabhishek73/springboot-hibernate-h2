package com.abhishek.demo.db.converter;

import com.abhishek.demo.util.SecretLoader;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.persistence.AttributeConverter;
import javax.xml.crypto.KeySelectorException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.UnknownFormatConversionException;

/**
 * A non final JPA Attribute converter class that helps in seamless encryption and decryption of DB Entity attributes.
 * The real beauty of using this module is that {@link org.springframework.data.jpa.repository.JpaRepository} methods
 * can be built with search queries containing plain text filters, and they will get converted to their encrypted DB
 * strings for exact DB search.
 *
 * @param <X> The type of model entity attribute. This shall final concreted in implementing final subclass.
 * @implNote There is a caveat in this approach that currently only the {@link CipherFormat#getDefault()} formatted search will
 * take place for search queries. We are currently observing if a better blanket search can be done for all
 * known {@link CipherFormat}s.
 */
@Slf4j
public abstract class EncryptedAttributeConverter<X> implements AttributeConverter<X, String> {

    /**
     * A sample implementation of a Secret Key loaded for a given {@link CipherFormat}. This one returns a
     * new {@link SecretKey} each time the method is invoked. Works well with in memory database, whose entries are
     * recreated after each startup, as keys will also get changed after each startup.
     *
     * @implNote Not recommended for real use. Implement a {@link java.security.KeyStore} of sorts to load
     * {@link SecretKey} from.
     */
    private static final SecretLoader defaultSecretLoader = new SecretLoader() {
        @Override
        public SecretKey getSecretKey(String alias, int length) throws KeySelectorException {

            try {
                if (alias.equals("AES_V1")) {
                    KeyGenerator aesKeyGenerator = KeyGenerator.getInstance("AES");
                    aesKeyGenerator.init(length, SecureRandom.getInstanceStrong());
                    return aesKeyGenerator.generateKey();
                } else if (alias.equals("BASE_64")) {
                    return null;
                }
                throw new KeySelectorException("Unsupported Alias : " + alias);
            } catch (NoSuchAlgorithmException e) {
                throw new KeySelectorException("Error while fetching key : " + alias, e);
            }
        }

        @Override
        public IvParameterSpec getIvSpec(String alias) throws IllegalArgumentException {
            try {
                if (alias.equals("AES_V1")) {
                    byte[] ivBuffer = new byte[16]; // AES requires 16 byte => 128 bit IV
                    SecureRandom.getInstanceStrong().nextBytes(ivBuffer);
                    return new IvParameterSpec(ivBuffer);
                } else if (alias.equals("BASE_64")) {
                    return null;
                }
                throw new IllegalArgumentException("Unsupported alias for generating IV for alias " + alias);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalArgumentException("Could not get strong random for generating IV for alias " + alias, e);
            }
        }
    };

    /**
     * Enum representing all available and legacy DB encryption logics. Each entry denotes and algorithm and
     * the unique key used by the given named approach.
     */
    public enum CipherFormat {
        BASE_64("BASE64", null, 0),
        AES_V1("AES1", "AES/CBC/PKCS5Padding", 256);

        //        private static Map<String, CipherFormat> dbToEnumMap = new HashMap<>();


        private final String dbPrefix;
        private final String cipherName;
        private final int keySize;
        private SecretKey key;
        private IvParameterSpec iv;

        CipherFormat(String dbPrefix, String cipherName, int keySize) {
            this.dbPrefix = dbPrefix;
            this.cipherName = cipherName;
            this.keySize = keySize;
            try {
                this.key = defaultSecretLoader.getSecretKey(this.name(), this.keySize);
            } catch (KeySelectorException e) {
                log.error("Could not initialize key for CipherFormat : {}", this.name());
            }
            try {
                this.iv = defaultSecretLoader.getIvSpec(this.name());
            } catch (IllegalArgumentException e) {
                log.error("Could not initialize IV for CipherFormat : {}", this.name());
            }
        }

        /**
         * The identifier stored in DB entity column, so that decryption can be done through it.
         *
         * @return DB Prefix string.
         */
        public String getDbPrefix() {
            return dbPrefix;
        }

        /**
         * @return a valid {@link Cipher} algorithm identifier.
         */
        public String getCipherName() {
            return cipherName;
        }

        /**
         * The symmetric key for encryption and decryption of the attribute.
         *
         * @return SecretKey loaded from some {@link SecretLoader} for this {@link CipherFormat}
         */
        protected SecretKey getKey() {
            return this.key;
        }

        /**
         * Getter for IV Spec for this symmetric encryption scheme. Some encryption scheme do not require an IV, so
         * it is optional by design.
         *
         * @return Optionally an {@link IvParameterSpec} if the scheme requires one.
         */
        protected Optional<IvParameterSpec> getIv() {
            return Optional.ofNullable(this.iv);
        }

        /**
         * Currently AES_V1.
         *
         * @return The default {@link CipherFormat} used by this version of the application.
         */
        static CipherFormat getDefault() {
            return BASE_64;
        }

        /**
         * Utility method to convert DB prefix into {@link CipherFormat}
         *
         * @param dbPrefix Search string for prefix
         * @return null if cannot find a {@link CipherFormat} depicted by the given dbPrefix.
         */
        static CipherFormat getByDbPrefix(String dbPrefix) {
            for (CipherFormat format : values()) {
                if (format.dbPrefix.equals(dbPrefix)) {
                    return format;
                }
            }
            return null;
        }
    }

    /**
     * Delimiter that separates the Encryption Logic identifier and encrypted entity attribute in DB storage.
     */
    static final String ENTITY_SEPARATOR = ":";

    /**
     * Given {@link EncryptedAttributeConverter#convertAttributeToBytes} is implemented by an implementing subclass,
     * this public final method performs the full conversion and encryption of attribute object X to final DB string
     *
     * @param x The model object that will be serialized and encrypted and encoded to suitable format, for DB storage
     * @return The string representation that will be stored in the DB column.
     */
    @SneakyThrows
    @Override
    public final String convertToDatabaseColumn(X x) {
        if (x == null) return null;
        CipherFormat encryptionFormat = getEncryptionFormat(x);
        byte[] painTextEntity = convertAttributeToBytes(x);

        byte[] encryptedEntity = encode(encrypt(painTextEntity, encryptionFormat));
        log.debug("Encrypted entity with logic {}", encryptionFormat.name());
        return encryptionFormat.getDbPrefix() + ENTITY_SEPARATOR + new String(encryptedEntity, StandardCharsets.UTF_8);
    }

    /**
     * Given {@link EncryptedAttributeConverter#convertBytesToAttribute} is implemented by a implementing subclass,
     * this public final method performs the full conversion of DB encrypted value to real domain Object of type X.
     *
     * @param s The string fetched from DB column; which is encrypted.
     * @return decrypted and deserialized Object of type X.
     */
    @SneakyThrows
    @Override
    public final X convertToEntityAttribute(String s) {
        if (s == null) return null;
        String[] entity = s.split(ENTITY_SEPARATOR, 2);
        CipherFormat decryptionFormat = getDecryptionFormat(entity[0]);
        String encryptedEntity = entity.length == 2 ? entity[1] : "";
        byte[] decryptedEntity = decrypt(decode(encryptedEntity), decryptionFormat);
        log.debug("Decrypting entity with logic {}", decryptionFormat.name());
        return convertBytesToAttribute(decryptedEntity);
    }

    /**
     * Suggests the best encryption format for this attribute.
     * Future releases may suggest entity level consistency through this method. Key/encryption rotation of
     * DB values can be driven through this method.
     *
     * @param x the entity to be encrypted
     * @return CipherFormat
     */
    private CipherFormat getEncryptionFormat(X x) {
        return CipherFormat.getDefault();
    }

    private CipherFormat getDecryptionFormat(String dbPrefix) {
        CipherFormat encryptionFormat = CipherFormat.getByDbPrefix(dbPrefix);
        if (encryptionFormat == null) {
            throw new UnknownFormatConversionException("Unknown Entity encryption format : " + dbPrefix);
        }
        return encryptionFormat;
    }

    private byte[] encode(byte[] plainEntityStringValue) {
        return Base64Utils.encode(plainEntityStringValue);
    }

    private byte[] decode(String encodedDbValue) {
        return Base64Utils.decodeFromString(encodedDbValue);
    }

    /**
     * Helper method to encrypt given byte array using the given {@link CipherFormat}
     *
     * @param painTextEntity The byte array representation of entity, obtained by
     *                       {@link EncryptedAttributeConverter#convertAttributeToBytes}
     * @param algo           The cipher algo and key to be used to encrypt this array
     * @return Encrypted byte array
     * @throws IllegalBlockSizeException when byte array block size is not suitable for encryption by given algo
     * @throws IllegalArgumentException  when the Key or IV specs are not in-line with the Cipher algorithm.
     */
    private byte[] encrypt(byte[] painTextEntity, CipherFormat algo) throws IllegalBlockSizeException {
        log.trace("Encrypting entity with {} algo", algo.name());

        try {
            Cipher cipher = algo.getCipherName() == null ? new NullCipher() : Cipher.getInstance(algo.getCipherName());
            if (algo.getIv().isPresent()) {
                cipher.init(Cipher.ENCRYPT_MODE, algo.getKey(), algo.getIv().get());
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, algo.getKey());
            }
            return cipher.doFinal(painTextEntity);
        } catch (NoSuchAlgorithmException e) {
            log.error("JVM environment do no support expected algorithm : " + algo, e);
            throw new IllegalArgumentException(e);
        } catch (NoSuchPaddingException | InvalidKeyException | BadPaddingException e) {
            log.error("JVM environment do no support key ({}) or block/padding ({}): ", algo.getKey(), algo.getCipherName(), e);
            throw new IllegalArgumentException(e);
        } catch (InvalidAlgorithmParameterException e) {
            log.error("IV spec ({}) for Cipher Algo ({}) is not correct.", algo.getIv(), algo.name(), e);
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Helper method to decrypt given encryptedDbValue byte array using the given {@link CipherFormat}
     *
     * @param encryptedDbValue The value to de decrypted
     * @param algo             The cipher algo and key to be used to decrypt this entity
     * @return Decrypted byte array
     * @throws IllegalBlockSizeException when byte array block size is not suitable for decryption by given algo
     *                                   and key size.
     * @throws IllegalArgumentException  when the Key or IV specs are not in-line with the Cipher algorithm.
     */
    private byte[] decrypt(byte[] encryptedDbValue, CipherFormat algo) throws IllegalBlockSizeException {
        log.trace("Decrypting entity with {} algo", algo.name());
        try {
            Cipher cipher = algo.getCipherName() == null ? new NullCipher() : Cipher.getInstance(algo.getCipherName());
            if (algo.getIv().isPresent()) {
                cipher.init(Cipher.DECRYPT_MODE, algo.getKey(), algo.getIv().get());
            } else {
                cipher.init(Cipher.DECRYPT_MODE, algo.getKey());
            }
            return cipher.doFinal(encryptedDbValue);
        } catch (NoSuchAlgorithmException e) {
            log.error("JVM environment do no support expected algorithm : " + algo, e);
            throw new IllegalArgumentException(e);
        } catch (NoSuchPaddingException | InvalidKeyException | BadPaddingException e) {
            log.error("JVM environment do no support key ({}) or padding ({}): ", algo.getKey(), algo.getCipherName(), e);
            throw new IllegalArgumentException(e);
        } catch (InvalidAlgorithmParameterException e) {
            log.error("IV spec ({}) for Cipher Algo ({}) is not correct.", algo.getIv(), algo.name(), e);
            throw new IllegalArgumentException(e);
        }
    }

    // ## Abstract Methods, to be implemented by subclass

    /**
     * A method that effectively is a deserializer that converts the byte array (buffer) representation of the entity
     * attribute that was obtained by the byte [] serializer method @convertAttributeToBytes
     *
     * @param decryptedValueBytes - The byte array representation fetched and decrypted from DB.
     * @return The deserialized and converted value from the serialized byte buffer.
     * @see EncryptedAttributeConverter#convertAttributeToBytes(X)
     */
    public abstract X convertBytesToAttribute(@NonNull byte[] decryptedValueBytes);

    /**
     * This method serializes the object of type X in to a byte [] buffer. This byte array will then be encrypted and
     * stored safely in DB.
     *
     * @param originalTypedAttribute the object to be serialized into byte [] representation.
     * @return a byte array representation of the object of type X.
     * @see EncryptedAttributeConverter#convertBytesToAttribute(byte[])
     */
    public abstract byte[] convertAttributeToBytes(@NonNull X originalTypedAttribute);
}
