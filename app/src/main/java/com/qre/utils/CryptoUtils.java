package com.qre.utils;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

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

    public static KeyPair generateKeyPair() {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            return keyGen.generateKeyPair();
        } catch (final NoSuchAlgorithmException e) {
            Log.e("CryptoUtils", "Algoritmo invalido", e);
        }
        return null;
    }

    public static PrivateKey getPrivateKey(byte[] bytes) {
        try {
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            final KeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytes);
            return keyFactory.generatePrivate(privateKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error al generar privateKey", e);
        }
    }

    public static boolean verifySignature(final String publicKey, final String data,
                                          final byte[] signature) {
        try {
            final Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(getPublicKey(publicKey));
            sig.update(data.getBytes(CHARSET_NAME));
            return sig.verify(signature);
        } catch (final NoSuchAlgorithmException | UnsupportedEncodingException |
                InvalidKeyException | SignatureException | InvalidKeySpecException e) {
            return false;
        }

    }

    private static PublicKey getPublicKey(final String key) throws InvalidKeySpecException,
            NoSuchAlgorithmException, UnsupportedEncodingException {
        final byte[] decode = Base64.decode(key, Base64.DEFAULT);
        final X509EncodedKeySpec spec = new X509EncodedKeySpec(decode);
        final KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

}
