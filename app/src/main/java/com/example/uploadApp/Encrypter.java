package com.example.uploadApp;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encrypter {
    private static final String transformation = "AES";
    private JSONObject data = null;
    private String securityKey = null;

    public Encrypter(JSONObject jsonObject, String key) {
        data = jsonObject;
        securityKey = key;
    }

    public JSONObject exec() {
        try {
            String timestamp = data.getString("upload account");
            String key = modifyKey(getSha256Hex(securityKey + timestamp, "UTF-8"));
            String encrypted = encrypt(data.getString("wifi fingerprints"), key);
            data.put("wifi fingerprints", encrypted);
            return data;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String modifyKey(String key) {
        int AESLen = 32;
        if (key.length() < AESLen) {
            int paddingLength = AESLen - key.length();
            StringBuilder keyBuilder = new StringBuilder(key);
            for (int i = 0; i < paddingLength; i++) {
                keyBuilder.append("0");
            }
            key = keyBuilder.toString();
        } else if (key.length() > AESLen) {
            key = key.substring(0, AESLen);
        }
        return key;
    }

    private static String encrypt(String plainText, String secretKey) throws Exception {
        byte[] keyBytes = secretKey.getBytes("UTF-8");
        Key key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String getSha256Hex(String text, String encoding) {
        String shaHex = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes(encoding));
            byte[] digest = md.digest();
            shaHex = bytesToHexString(digest);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return shaHex;
    }

    private static String bytesToHexString(byte[] bytes) {
        BigInteger bigInteger = new BigInteger(1, bytes);
        StringBuilder hexString = new StringBuilder(bigInteger.toString(16));

        while (hexString.length() < 64) {
            hexString.insert(0, "0");
        }

        return hexString.toString();
    }
}
