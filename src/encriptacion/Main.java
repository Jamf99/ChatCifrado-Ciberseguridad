package encriptacion;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Node server = new Node();
        Node client = new Node();

        server.setReceiverPublicKey(client.getPublickey());

        client.setReceiverPublicKey(server.getPublickey());

        String data = "hello";

        String enc = server.encrypt(data);

        System.out.println("hello is coverted to "+enc);

        System.out.println(enc+" is converted to "+client.decrypt(enc));

    }
}