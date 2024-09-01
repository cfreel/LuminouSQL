package org.luminousql;

import java.util.Base64;

public class EncryptedStringBundle {
    public String encryptedString;
    public String iv;

    public EncryptedStringBundle(byte[] encryptedString, byte[] iv) {
        this.encryptedString = Base64.getEncoder().encodeToString(encryptedString);
        this.iv = Base64.getEncoder().encodeToString(iv);
    }

    public EncryptedStringBundle(String serialized) {
        String[] parts = serialized.split(":");
        this.encryptedString = parts[0];
        this.iv = parts[1];
    }

    public String toString() {
        return encryptedString +  ":" + iv;
    }
}
