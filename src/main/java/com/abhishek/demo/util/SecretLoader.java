package com.abhishek.demo.util;

import lombok.NonNull;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.crypto.KeySelectorException;

/**
 * Base interface that facilitates the loading of secrets to be used by
 * {@link com.abhishek.demo.db.converter.EncryptedAttributeConverter} through various
 * {@link com.abhishek.demo.db.converter.EncryptedAttributeConverter.CipherFormat}s
 */
public interface SecretLoader {

    /**
     * Should load and initialize {@link SecretKey} from any source, like {@link java.security.KeyStore}; and returns
     * a valid key for given alias.
     *
     * @param alias     - The identifier of alias to find and load the key for.
     * @param keyLength - helper attribute to confirm the length of key is as per expectation
     * @return SecretKey if successfully loaded from the secret storage.
     * @throws KeySelectorException If valid key cannot be found/loaded for the given alias
     */
    default SecretKey getSecretKey(@NonNull String alias, int keyLength) throws KeySelectorException {
        throw new KeySelectorException("Could not get SecretKey by alias : " + alias);
    }

    /**
     * Method that tries to load Initialization Vector for a given alias.
     *
     * @param alias - The search/load key for IV
     * @return An loaded and initialized IV
     * @throws IllegalArgumentException if IV for given alias cannot be loaded
     */
    default IvParameterSpec getIvSpec(@NonNull String alias) throws IllegalArgumentException {
        throw new IllegalArgumentException("Could not get Initialization vector for alias : " + alias);
    }

}
