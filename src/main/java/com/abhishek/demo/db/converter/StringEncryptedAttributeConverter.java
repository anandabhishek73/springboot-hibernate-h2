package com.abhishek.demo.db.converter;

import javax.persistence.Converter;
import java.nio.charset.StandardCharsets;

/**
 * An JPA attribute converter class that encrypts String attributes.
 */
@Converter
public class StringEncryptedAttributeConverter extends EncryptedAttributeConverter<String> {

    @Override
    public String convertBytesToAttribute(byte[] decryptedValueBytes) {
        return new String(decryptedValueBytes, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] convertAttributeToBytes(String originalTypedAttribute) {
        return originalTypedAttribute.getBytes(StandardCharsets.UTF_8);
    }
}
