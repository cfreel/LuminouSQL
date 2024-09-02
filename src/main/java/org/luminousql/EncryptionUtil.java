package org.luminousql;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class EncryptionUtil {

    // The discussion here: https://stackoverflow.com/questions/992019/java-256-bit-aes-password-based-encryption
    // is relevant; much of the code related to encryption was taken from the accepted answer based off similar needs.

    private static final String ALGO = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256;

    public static String getSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] saltyBytes = new byte[16];
        secureRandom.nextBytes(saltyBytes);
        return Base64.getEncoder().encodeToString(saltyBytes);
    }

    public static SecretKey getKey(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, KEY_SIZE);
        SecretKey secret = factory.generateSecret(spec);
        return new SecretKeySpec(secret.getEncoded(), "AES");
    }

    public static EncryptedStringBundle encrypt(SecretKey secret, String plainText)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] ciphertext = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return new EncryptedStringBundle(ciphertext, iv);
    }

    public static String decrypt(SecretKey secret, EncryptedStringBundle bundle) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        byte[] ciphertext = Base64.getDecoder().decode(bundle.encryptedString);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = Base64.getDecoder().decode(bundle.iv);
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
        return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
    }

    public static String performEncryption(String plainText) throws Exception {
        if (plainText==null || plainText.isEmpty())
            return plainText;
        SecretKey sk = getKey(Configuration.passcode, Configuration.getFixedPortionEncKey());
        EncryptedStringBundle bundle = encrypt(sk, plainText);
        return bundle.toString();
    }

    public static String performDecyption(String cipherText) throws Exception {
        if (cipherText==null || cipherText.isEmpty())
            return cipherText;
        SecretKey sk = getKey(Configuration.passcode, Configuration.getFixedPortionEncKey());
        EncryptedStringBundle bundle = new EncryptedStringBundle(cipherText);
        return decrypt(sk, bundle);
    }

}
