package org.luminousql;

import javax.crypto.SecretKey;

class EncryptionUtilTest {

    @org.junit.jupiter.api.Test
    void encryptCycle() throws Exception {
        SecretKey sk = EncryptionUtil.getKey("123456", "Imfeelingsalty");
        SecretKey sk2 = EncryptionUtil.getKey("123456", "Imfeelingsalty");
        EncryptedStringBundle bun = EncryptionUtil.encrypt(sk, "TheQuickBrownFox");
        EncryptedStringBundle bun2 = EncryptionUtil.encrypt(sk, "TheQuickBrownFox");
        String plain = EncryptionUtil.decrypt(sk, bun);
        String plain2 = EncryptionUtil.decrypt(sk2, bun2);
        assert plain.equals(plain2);
        assert plain.equals("TheQuickBrownFox");
    }
}