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
