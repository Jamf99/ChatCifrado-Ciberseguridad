package model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import encriptacion.KeyDiffieHellman;
import interfaz.UI;

/**
 * @author Lenovo
 *
 */
public class ChatClient {
	
	//Nombre
	public String name;
	public Handler handler;
	private UI ui;
	
	
	/**
	 * @param name Nombre del cliente
	 * @param ip IP del servidor
	 * @param port Puerto para enviar mensajes
	 */
	public ChatClient(String name, String ip, int port) {
		
		this.name = name;

		//Crea la pantalla gráfica de ese cliente
		ui = new UI(name, this);
		
		handler = new Handler(name, ip, port);
		new Thread(handler).start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				handler.kill();
			}
		});
		
	}
	
	public void sendMessage(String recipient, String body, boolean encrypted) {
		
		String id = String.valueOf(System.currentTimeMillis() / 1000) + "_" + String.valueOf((int) (Math.random() * 1000));
		
		String[] message = new String[] {name, recipient, body};
		handler.messages.put(id, message);
		//Files.saveMessages(name, handler.messages);
		ui.refresh(handler.clients, handler.messages);
		
		if (encrypted) {
			int[] keyPair = KeyDiffieHellman.generateKeys();
			handler.keys.put(id, keyPair);
			handler.send("KEY_REQUEST", id, recipient, String.valueOf(keyPair[1]));
		} else {
			handler.send("MESSAGE", id, String.join("\t", message));
		}
		
	}
	
	
	/**
	 * Clase que corre el hilo para enviar mensajes al servidor que despues serán redirigidos a otro cliente
	 * @author Lenovo
	 *
	 */
	class Handler implements Runnable {
		
		//Socket del cliente
		private Socket server;
		//Imprime representaciones formateadas de objetos en un flujo de salida de texto.
		private PrintWriter writer;
		//Lee el flujo de datos que le llega
		private BufferedReader reader;
		
		//Para saber si el while sigue ejecutandose
		private boolean alive = true;

		//Arraylist con la información de cada una de las filas de registro de mensajes
	    public ArrayList<String> clients = new ArrayList<>();
	    //TreeMap para almacenar mensajes
	    public TreeMap<String, String[]> messages = new TreeMap<String, String[]>();
	    //Map de claves
		private Map<String, int[]> keys = new HashMap<String, int[]>();

		
		/**
		 * Constructor del handler
		 * @param name Nombre del cliente
		 * @param ip IP del servidor
		 * @param port Puerto para enviar mensajes
		 */
		public Handler(String name, String ip, int port) {
			
			try {
				
				server = new Socket(ip, port);
				
				OutputStream out = server.getOutputStream();
				writer = new PrintWriter(out);
				InputStream in = server.getInputStream();
				reader = new BufferedReader(new InputStreamReader(in));
				
				send("CLIENT", name);
			
			} catch (Exception e) { e.printStackTrace(); }
			
			//Actualiza la pantalla de este cliente
			ui.refresh(clients, messages);
			
		}
		
		
		/**
		 * Pone en false el alive
		 */
		public void kill() {
			alive = false;
		}
		
		
		/**
		 * Envia el mensaje con el formato adecuado (tabulados)
		 * @param bodyParts partes que conforman el cuerpo del mensaje
		 */
		public void send(String ...bodyParts) {
			String body = String.join("\t", bodyParts);
			writer.write(body + "\n");
			writer.flush();
		}
		
		
		/**
		 * Este metodo se ejecuta siempre que se va a enviar un mensaje nuevo
		 * @param parts partes que conforman una fila de registro de mensaje
		 */
		private void handleClients(String[] parts) {
			
			clients.clear();
			if (parts[1].equals("null")) return;
			
			for (int i = 1; i < parts.length; i++) {
				clients.add(parts[i]);
			}

			ui.refresh(clients, messages);
			
		}
		
		/**
		 * Construye y almacena el mensaje
		 * @param parts partes que conforman el mensaje
		 */
		private void handleMessages(String[] parts) {
			
			if (parts.length < 2 || parts[1].equals("null")) {
				return;
			}
			
			for (int i = 1; i < parts.length; i += 4) {
				if (!messages.containsKey(parts[i])) {
					messages.put(parts[i], Arrays.copyOfRange(parts, i + 1, i + 4));
				}
			}
			
			//Files.saveMessages(name, messages);
			ui.refresh(clients, messages);
			
		}
		
		
		/**
		 * @param parts
		 */
		private void handleNewKeyRequest(String[] parts) {

			String id = parts[1];
			String recipient = parts[2];
			int senderPublicKey = Integer.parseInt(parts[3]);
			
			if (!recipient.equals(name)) return;
			
			int[] keyPair = KeyDiffieHellman.generateKeys();
			keys.put(parts[1], new int[] {keyPair[0], senderPublicKey});
			
			send("KEY_RESPONSE", id, String.valueOf(keyPair[1]));
			
		}
		
		private void handleNewKeyResponse(String[] parts) {
			
			String id = parts[1];
			int recipientPublicKey = Integer.parseInt(parts[2]);

			if (messages.containsKey(id) && keys.containsKey(id)) {
				String[] message = Arrays.copyOf(messages.get(id), 3);
				int[] keyPair = new int[] {keys.get(id)[0], recipientPublicKey};
				message[2] = KeyDiffieHellman.encryptMessage(keyPair[0], keyPair[1], message[2]);
				send("MESSAGE", id, String.join("\t", message));
			}
			
		}
		
		private void handleNewMessage(String[] parts) {

			String id = parts[1];
			String[] message = Arrays.copyOfRange(parts, 2, 5);
			
			if (message[0].equals(name) || messages.containsKey(id)) return;
			
			if (keys.containsKey(id)) {
				int myPrivateKey = keys.get(id)[0];
				int senderPublicKey = keys.get(id)[1];
				message[2] = KeyDiffieHellman.decryptMessage(myPrivateKey, senderPublicKey, message[2]);
			}

			messages.put(id, message);
			
			//Files.saveMessages(name, messages);
			ui.refresh(clients, messages);
			
		}
		
		public void run() {
			
			try {
				
				String body;
				
				while (!(body = reader.readLine()).startsWith("QUIT") && alive) {
					
					System.out.println(body);
					String[] parts = body.split("\t");
					
					if (parts.length == 0) continue;
					
					if (parts[0].equals("CLIENTS")) {
						handleClients(parts);
					} else if (parts[0].equals("MESSAGES")) {
						handleMessages(parts);
					} else if (parts[0].equals("MESSAGE")) {
						handleNewMessage(parts);
					} else if (parts[0].equals("KEY_REQUEST")) {
						handleNewKeyRequest(parts);
					} else if (parts[0].equals("KEY_RESPONSE")) {
						handleNewKeyResponse(parts);
					}
					
				}
				
				send("QUIT");
				
				writer.close();
				reader.close();
				server.close();
				
			} catch (Exception e) { e.printStackTrace(); }
			
		}
		
	}

}
