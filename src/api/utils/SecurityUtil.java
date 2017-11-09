/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.utils;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author nghiapht
 */
public class SecurityUtil {

    private static final String ALGORITHM = "AES";
    private static final String SECRETKEY = "nIZVIvYEEhFmJUou";

    public static String encrypt(String valueToEncrypt) {
        try {
            // gen key
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, key);

            // encrypt value
            byte[] encryptedValue = c.doFinal(valueToEncrypt.getBytes());

            // base64 encode
            byte[] encodedValue = new Base64().encode(encryptedValue);

            // convert to string
            String ret = new String(encodedValue);
            return ret;
        } catch (Exception e) {
        }
        return "";
    }

    public static String decrypt(String valueToDecrypt) {
        try {
            // base64 decode
            byte[] encodedValue = valueToDecrypt.getBytes();
            byte[] decodedValue = new Base64().decode(encodedValue);

            // gen key
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, key);

            // decrypt value
            byte[] decryptedVal = c.doFinal(decodedValue);

            // convert to string
            String ret = new String(decryptedVal);
            return ret;
        } catch (Exception e) {
        }
        return "";
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(SECRETKEY.getBytes(), ALGORITHM);
        return key;
    }

    public static void main(String[] args) throws Exception {
        String source = "12345_123213_123123";
        System.out.println(source);

        String encrypt = encrypt(source);
        System.out.println(encrypt);
        System.out.println(encrypt.length());

        String decrypt = decrypt(encrypt);
        System.out.println(decrypt);
    }
}
