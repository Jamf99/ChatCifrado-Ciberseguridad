package encriptacion;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Clase encargada de la encriptación por AES-128
 */
public class AES {

	/**
	 * Método encargado de retornar la clave secreta del cliente en formato de bytes
	 * @param key Llave secreta del cliente
	 * @return Llave secreta del cliente en formato de bytes
	 */
	private static SecretKeySpec getSecretKey(String key) {

		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			byte[] keyBytes = sha.digest(key.getBytes("UTF-8"));
			keyBytes = Arrays.copyOf(keyBytes, 16);
			return new SecretKeySpec(keyBytes, "AES");
		} catch (Exception e) { e.printStackTrace(); }

		return null;

	}
	
	/**
	 * Método encargado de encriptar un mensaje en texto plano 
	 * @param plaintext Texto plano a encriptar
	 * @param key Clave compartida Ta o Tb
	 * @return Texto plano encriptado
	 */
	public static String encrypt(String plaintext, int key) {

		try {
			SecretKeySpec secretKey = getSecretKey(String.valueOf(key));
			/*
			 * AES/CBC/NoPadding (128)
				AES/CBC/PKCS5Padding (128)
				AES/ECB/NoPadding (128)
				AES/ECB/PKCS5Padding (128) <------------------ utilizamos este
				DES/CBC/NoPadding (56)
				DES/CBC/PKCS5Padding (56)
				DES/ECB/NoPadding (56)
				DES/ECB/PKCS5Padding (56)
				DESede/CBC/NoPadding (168)
				DESede/CBC/PKCS5Padding (168)
				DESede/ECB/NoPadding (168)
				DESede/ECB/PKCS5Padding (168)
				RSA/ECB/PKCS1Padding (1024, 2048)
				RSA/ECB/OAEPWithSHA-1AndMGF1Padding (1024, 2048)
				RSA/ECB/OAEPWithSHA-256AndMGF1Padding (1024, 2048)
			 */
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(plaintext.getBytes("UTF-8")));
		} catch (Exception e) { e.printStackTrace(); }

		return plaintext;

	}
	
	
	/**
	 * Método encargado de desencriptar un mensaje en texto plano 
	 * @param plaintext Texto plano a desencriptar
	 * @param key Clave compartida Ta o Tb
	 * @return Texto plano desencriptado
	 */
	public static String decrypt(String ciphertext, int key) {

		try {
			SecretKeySpec secretKey = getSecretKey(String.valueOf(key));
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)));
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (Exception e) { e.printStackTrace(); }
		
		return ciphertext;
		
	}
	
}
