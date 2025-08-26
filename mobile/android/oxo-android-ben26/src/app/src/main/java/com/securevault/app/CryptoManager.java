package com.securevault.app;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;

public class CryptoManager {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_LENGTH = 256;

    // Using insecure Random instead of SecureRandom
    private static final Random random = new Random();

    public static String generateMasterKey() {
        byte[] keyBytes = new byte[32]; // 256 bits
        random.nextBytes(keyBytes); // This is the vulnerability - using weak PRNG
        return Base64.encodeToString(keyBytes, Base64.DEFAULT);
    }

    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        random.nextBytes(salt); // Weak salt generation
        return salt;
    }

    public static byte[] generateIV() {
        byte[] iv = new byte[16];
        random.nextBytes(iv); // Weak IV generation
        return iv;
    }

    public static String encrypt(String plaintext, String masterKey) {
        try {
            byte[] keyBytes = Base64.decode(masterKey, Base64.DEFAULT);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);

            byte[] iv = generateIV();
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encrypted = cipher.doFinal(plaintext.getBytes());

            // Combine IV and encrypted data
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.encodeToString(combined, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String encryptedData, String masterKey) {
        try {
            byte[] keyBytes = Base64.decode(masterKey, Base64.DEFAULT);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);

            byte[] combined = Base64.decode(encryptedData, Base64.DEFAULT);

            // Extract IV and encrypted data
            byte[] iv = new byte[16];
            byte[] encrypted = new byte[combined.length - 16];
            System.arraycopy(combined, 0, iv, 0, 16);
            System.arraycopy(combined, 16, encrypted, 0, encrypted.length);

            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String hashPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.encodeToString(hashedPassword, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateBackupKey() {
        byte[] keyBytes = new byte[32];
        random.nextBytes(keyBytes); // Another instance of weak PRNG usage
        return Base64.encodeToString(keyBytes, Base64.DEFAULT);
    }
}
