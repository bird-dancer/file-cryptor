import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Felix Dumbeck
 * @see github.com/f-eliks
 */

public class Crypto {
    private byte[] readFile(String fileName) throws IOException {
        File file = new File(fileName);
        return Files.readAllBytes(file.toPath());
    }
    private byte[] encrypt(byte[] arr, String password) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Key key = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(arr);
    }
    private Key generateKey(String password) {
        String key = password;
        while (key.length() < 32)
            key += key;
        key = key.substring(0, 32);
        return new SecretKeySpec(key.getBytes(Charset.forName("UTF-8")), "AES");
    }
    private void storeFile(byte[] arr, String fileName) throws IOException {
        InputStream is = new ByteArrayInputStream(arr);
        FileOutputStream fos = new FileOutputStream(fileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0)
            fos.write(buffer, 0, length);
        is.close();
        fos.close();
    }
    private byte[] decrypt(byte[] arr, String password) throws InvalidKeyException, IllegalBlockSizeException,BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        Key key = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(arr);
    }
    private void encryptFile(String fileName, String password) throws InvalidKeyException, NoSuchAlgorithmException,NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
        storeFile(encrypt(readFile(fileName), password), fileName+".crypt");
    }
    private void decryptFile(String fileName, String password) throws InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
        storeFile(decrypt(readFile(fileName), password), (fileName.split(".crypt"))[0]);
    }
    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, IOException {
        Crypto crypto = new Crypto();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Would you to encrypt (e) or decrypt (d) a file?");
            String order = scanner.nextLine();
            if (order.equals("e")) {
                System.out.println("filename: ");
                String fileName = scanner.nextLine();
                System.out.println("password");
                crypto.encryptFile(fileName, scanner.nextLine());
            } else if (order.equals("d")) {
                System.out.println("filename: ");
                String fileName = scanner.nextLine();
                System.out.println("password");
                crypto.decryptFile(fileName, scanner.nextLine());
            }
        }
    }
}