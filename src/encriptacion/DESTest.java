package encriptacion;

import javax.crypto.*;
import javax.crypto.spec.*;


class DESTest {
   public static void main(String[] args) {
      String test = "1";
      try {
         byte[] theKey = null;
         byte[] theMsg = null; 
         byte[] theExp = null; 
         if (test.equals("1")) { 
            theKey = hexToBytes("00000000000000000000000000000000");
            theMsg = hexToBytes("fff80000000000000000000000000000");
            theExp = hexToBytes("8b87145a01ad1c6cede995ea3670454f");
         } else if (test.equals("2")) { 
            theKey = hexToBytes("38627974656B6579"); // "8bytekey"
            theMsg = hexToBytes("6D6573736167652E"); // "message."
            theExp = hexToBytes("7CF45E129445D451");
         } else {
            System.out.println("Usage:");
            System.out.println("java JceSunDesTest 1/2");
            return;
         }	
    //     KeySpec ks = new DESKeySpec(theKey);
         SecretKeySpec  secretKey = new SecretKeySpec(theKey, "AES");
        // SecretKey ky = kf.generateSecret(ks);
         Cipher cf = Cipher.getInstance("AES/ECB/NoPadding");
         cf.init(Cipher.ENCRYPT_MODE,secretKey);
         byte[] theCph = cf.doFinal(theMsg);
         System.out.println("Key     : "+bytesToHex(theKey));
         System.out.println("Message : "+bytesToHex(theMsg));
         System.out.println("Cipher  : "+bytesToHex(theCph));
         System.out.println("Expected: "+bytesToHex(theExp));
      } catch (Exception e) {
         e.printStackTrace();
         return;
      }
   }
   public static byte[] hexToBytes(String str) {
      if (str==null) {
         return null;
      } else if (str.length() < 2) {
         return null;
      } else {
         int len = str.length() / 2;
         byte[] buffer = new byte[len];
         for (int i=0; i<len; i++) {
             buffer[i] = (byte) Integer.parseInt(
                str.substring(i*2,i*2+2),16);
         }
         return buffer;
      }

   }
   public static String bytesToHex(byte[] data) {
      if (data==null) {
         return null;
      } else {
         int len = data.length;
         String str = "";
         for (int i=0; i<len; i++) {
            if ((data[i]&0xFF)<16) str = str + "0" 
               + java.lang.Integer.toHexString(data[i]&0xFF);
            else str = str
               + java.lang.Integer.toHexString(data[i]&0xFF);
         }
         return str.toUpperCase();
      }
   }            
}