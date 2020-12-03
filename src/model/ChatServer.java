package model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Clase que maneja la lógica del servidor
 */
public class ChatServer {
	
	/**
	 * Arreglo que almacena los clientes
	 */
	public Map<String, Handler> clients = new HashMap<String, Handler>();
	
	/**
	 * Arreglo que almacena los mensajes
	 */
	public TreeMap<String, String[]> messages = new TreeMap<String, String[]>();
	
	
	/**
	 * Método constructor encargado de abrir el puerto de escucha del servidor donde queda a la espera de los clientes
	 * @param port: Número del puerto en el cual va a escuchar y enviar peticiones
	 */
	public ChatServer(int port) {
		
		ServerSocket server;
		
		try {
			
			server = new ServerSocket(port);
			System.out.println("server started");
			
			while (true) {
				try {
					Socket client = server.accept();
					Handler handler = new Handler(client);
					new Thread(handler).start();
				} catch (Exception e) { e.printStackTrace(); }
			}
			
		} catch (Exception e) { e.printStackTrace(); }

		
	}
	
	
	/**
	 * Método que envía un mensaje a todos los clientes
	 * @param bodyParts Partes de las que consiste el mensaje
	 */
	public void sendToAll(String ...bodyParts) {
		for (Entry<String, Handler> clientEntry: clients.entrySet()) {
			clientEntry.getValue().sendToThis(bodyParts);
		}
	}
	
	/**
	 * Método que concatena todos los clientes en un cadena
	 */
	private String clientsToString() {
		int i = 0;
		//Este arreglo almcena los nombres de los clientes
		String[] clientsStrings = new String[clients.size()];
		for (Handler clientValue: clients.values()) {
			clientsStrings[i++] = clientValue.name;
		}
		//Crea un String con el nombre de todos los cliente separado por tabulado
		return String.join("\t", clientsStrings);
	}
	
	
	/**
	 * Crea una cadena con todos los mensajes separados con tabulado
	 * @return Un string con todos los mensajes
	 */
	private String messagesToString() {
		int i = 0;
		String[] messagesStrings = new String[messages.size()];
		for (Entry<String, String[]> messageEntry: messages.entrySet()) {
			messagesStrings[i++] = messageEntry.getKey() + "\t" + String.join("\t", messageEntry.getValue());
		}
		return String.join("\t", messagesStrings);
	}
	
	
	/**
	 * Clase que ejecuta el hilo de espera de clientes
	 */
	private class Handler implements Runnable {
		
		/**
		 * Socket que viene del servidor de la clase constructora
		 */
		public Socket client;
		
		/**
		 * Imprime representaciones formateadas de objetos en un flujo de salida de texto. Otra alternativa para el arrayBytes
		 */
		private PrintWriter writer;
		
		/**
		 * Lee el flujo de datos que le llega
		 */
		private BufferedReader reader;

		/**
		 * Nombre del usuario
		 */
		public String name;
		
		/**
		 * Para saber si el while sigue ejecutandose
		 */
		private boolean alive = true;

		
		/**
		 * Constructor de la clase Handler que inicia la escucha de clientes
		 * @param Client socket del cliente
		 */
		public Handler(Socket client) {
			
			this.client = client;
			
			try {
				OutputStream out = client.getOutputStream();
				writer = new PrintWriter(out);
				InputStream in = client.getInputStream();
				reader = new BufferedReader(new InputStreamReader(in));
			} catch (Exception e) { e.printStackTrace(); }
			
		}
		
		
		/**
		 * Cambia la variable alive a false para que el servidor deje de escuchar
		 */
		public void kill() {
			alive = false;
		}
		
		
		/**
		 * Método que crea la fila con la información completa del mensaje que se va a imprimir en la pantalla de un cliente particular
		 * @param bodyParts Varios parámetros del tipo String
		 */
		public void sendToThis(String ...bodyParts) {
			String body = String.join("\t", bodyParts);
			writer.write(body + "\n");
			writer.flush();
		}
		
		
		/**
		 * Manejador de cliente para saber si ya existe o si es un clientre nuevo
		 * @param parts Partes de las que esta compuesta el registro del mensaje
		 */
		private void handleNewClient(String[] parts) {
			
			String name = parts[1];
			this.name = name;
			
			//Verifica si el cliente esta actualmente registrado en sistema, si est�, lo mata
			if (clients.containsKey(name)) {
				clients.get(name).kill();
				clients.remove(name);
			}
			
			//Si es un cliente nuevo, lo almacena en el Hashmap de clientes
			clients.put(name, this);
			sendToAll("CLIENTS", clientsToString());
			sendToThis("MESSAGES", messagesToString());
			
		}
		
		
		/**
		 * Construye y almacena el mensaje
		 * @param parts Partes de las que esta compuesta el registro del mensaje
		 */
		private void handleNewMessage(String[] parts) {
			
			String id = parts[1];
			String[] message = Arrays.copyOfRange(parts, 2, 5);
			
			messages.put(id, message);
			//Files.saveMessages("server", messages);
			
		}

		
		/**
		 * Hilo principal para la escucha activa del socket del servidor
		 */
		public void run() {
			
			try {
				
				String body;
				//El body va a ser la fila con la informaci�n del mensaje: MESSAGE	fecha	remitente	destinatario	mensaje
				while (!(body = reader.readLine()).startsWith("QUIT") && alive) {
					
					System.out.println(body + "Vamos a verificar que se le asigna a body");
					String[] parts = body.split("\t");
					
					if (parts.length == 0) continue;
					
					//Este if verifica si apenas se conecta el cliente o si ya est� conectado y va a mandar mensaje
					if (parts[0].equals("CLIENT")) {
						handleNewClient(parts);
					} else if (parts[0].equals("MESSAGE")) {
						handleNewMessage(parts);
					}

					sendToAll(body);
					
				}
				
				//Si el nombre ya existe en el arreglo de clientes, entonces lo remueve
				if (clients.containsKey(name)) {
					clients.remove(name);
				}

				sendToThis("QUIT");
				//Envia el mensaje a todas las pantallas de los cliente conectados
				sendToAll("CLIENTS", clientsToString());
				
				writer.close();
				reader.close();
				client.close();
				
			} catch (Exception e) { e.printStackTrace(); }
			
		}

	}
	
	public static void main(String[] args) {
		new ChatServer(1111);
	}

}
