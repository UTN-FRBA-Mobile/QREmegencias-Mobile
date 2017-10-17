package com.qre.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class CryptoUtils {

    private static final String CHARSET_NAME = "ISO-8859-1";
    private static final IvParameterSpec IV_PARAMETER_SPEC;

    static {
        try {
            IV_PARAMETER_SPEC = new IvParameterSpec("4e5Wa71fYoT7MFEX".getBytes(CHARSET_NAME));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static Cipher DECRYPTING_CIPHER;

    private static Cipher initCipher(final int mode, final InputStream keyIs) {
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int bytesRead;
            while ((bytesRead = keyIs.read(bytes)) != -1) {
                bos.write(bytes, 0, bytesRead);
            }
            final SecretKeySpec key = new SecretKeySpec(md.digest(bos.toByteArray()), "AES");
            cipher.init(mode, key, IV_PARAMETER_SPEC);
            return cipher;
        } catch (final NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IOException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initDecryptingCipher(final InputStream key) {
        DECRYPTING_CIPHER = initCipher(Cipher.DECRYPT_MODE, key);
    }

    private CryptoUtils() {
    }

    public static byte[] decryptText(final String msg, final InputStream key)
            throws InvalidKeyException, IOException,
            IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException {

        if (DECRYPTING_CIPHER == null) {
            initDecryptingCipher(key);
        }

        return DECRYPTING_CIPHER.doFinal(msg.getBytes(CHARSET_NAME));
    }

}
