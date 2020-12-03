package encriptacion;

public class KeyDiffieHellman {

    final static int p = 1019;
    final static int g = 2;
    
    
    /**
     * El metodo realiza un calculo recursivo tal que el resultado final es el Ta y Tb
     * @param g Numero cualquiera
     * @param x Llave privada -> Numero aleoterio Sb Sa
     * @param p Es un numero primo muy grande
     * @return
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
     * Crea el par de claves Publica y Privada de un cliente
     * La clave privada es un numero entero aleatorio menor a mil
     * La clave publica la realiza con el método de diffiehellman
     * @return Retorna el par de claves, pública y privada
     */
    public static int[] generateKeys() {
        int privateKey = (int) (Math.random() * 1000);
        int publicKey = modularExponentiation(g, privateKey, p);
        return new int[] {privateKey, publicKey};
    }

    
    /**
     * Encripta un mensaje con el algoritmo AES en texto plano de acuerdo a las siguientes entradas: 
     * @param myPrivateKey Mi llave privada
     * @param recipientPublicKey La llave publica de la persona con la que establezco el canal de comunicacion
     * @param message Mensaje en texto plano
     * @return
     */
    public static String encryptMessage(int myPrivateKey, int recipientPublicKey, String message) {
        int sharedKey = modularExponentiation(recipientPublicKey, myPrivateKey, p);
        return AES.encrypt(message, sharedKey);
    }

    public static String decryptMessage(int myPrivateKey, int senderPublicKey, String message) {
        int sharedKey = modularExponentiation(senderPublicKey, myPrivateKey, p);
        return AES.decrypt(message, sharedKey);
    }
    
}
