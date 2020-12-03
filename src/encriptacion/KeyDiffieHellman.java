package encriptacion;

/**
 * Clase encargada de hacer el intercambio Diffie Hellman de llaves
 */
public class KeyDiffieHellman {

	/**
	 * Número primo definido para el intercambio
	 */
    final static int p = 1019;
    
    /**
     * Número entero definido para el intercambio
     */
    final static int g = 2;
    
    /**
     * Método recursivo que realiza la exponenciación modular de las claves para generar una clave compartida
     * @param g Número cualquiera
     * @param x Llave privada -> Numero aleoterio Sb Sa
     * @param p Número primo muy grande
     * @return La clave compartida
     */
    private static int modularExponentiation(int g, int x, int p) {
        if (x == 0) {
            return 1;
        } else if (x == 1) {
            return g % p;
        } else if (x % 2 == 1) {
            int h = modularExponentiation(g, x - 1, p);
            return (h * g) % p;
        } else {
            int h = modularExponentiation(g, x / 2, p);
            return (h * h) % p;
        }
    }
    
    
    /**
     * Genera el par de claves Publica y Privada para un cliente <br>
     * <ul><li>La clave privada es un numero entero aleatorio menor a mil</li>
     * <li>La clave pública la realiza con el método de Diffie Hellman</li></ul>
     * @return Retorna el par de claves pública y privada
     */
    public static int[] generateKeys() {
        int privateKey = (int) (Math.random() * 1000);
        int publicKey = modularExponentiation(g, privateKey, p);
        return new int[] {privateKey, publicKey};
    }

    
    /**
     * Método encargado de encriptar un mensaje por medio del algoritmo AES teniendo en cuenta la llave compartida <br>
     * de la modulación exponencial
     * @param myPrivateKey Mi llave privada
     * @param recipientPublicKey La llave publica del receptor
     * @param message Mensaje en texto plano
     * @return Mensaje encriptado
     */
    public static String encryptMessage(int myPrivateKey, int recipientPublicKey, String message) {
        int sharedKey = modularExponentiation(recipientPublicKey, myPrivateKey, p);
        return AES.encrypt(message, sharedKey);
    }

    /**
     * Método encargado de desencriptar un mensaje por medio del algoritmo AES teniendo en cuenta la llave compartida <br>
     * de la modulación exponencial
     * @param myPrivateKey Mi llave privada
     * @param recipientPublicKey La llave publica del emisor
     * @param message Mensaje en texto plano
     * @return Mensaje desencriptado
     */
    public static String decryptMessage(int myPrivateKey, int senderPublicKey, String message) {
        int sharedKey = modularExponentiation(senderPublicKey, myPrivateKey, p);
        return AES.decrypt(message, sharedKey);
    }
    
}
