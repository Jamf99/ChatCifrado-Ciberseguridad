package socket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import modelo.PaqueteEnvio;


public class ServerChat implements Runnable {

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		ServerChat server = new ServerChat();
	}
	
	public ServerChat() {
		Thread mihilo = new Thread(this);
		mihilo.start();
	}

	@SuppressWarnings({ "unused", "resource" })
	@Override
	public void run() {
		
		try {
			
			ServerSocket servidor = new ServerSocket(ClientChat.PUERTO_CLIENTE);	
			String nick, ip, mensaje, estado;	
			ArrayList<String> listaIP = new ArrayList<String>();	
			HashMap<String, String> NickIP = new HashMap<String, String>();	
			PaqueteEnvio paqueteRecibido;
			
			while(true) {
				System.out.println("Esperando clientes . . .");
				Socket miSocket = servidor.accept();
				
				ObjectInputStream paqueteDatos = new ObjectInputStream(miSocket.getInputStream());
				byte[] rec = new byte[512];
				int bytes_read;
				bytes_read = paqueteDatos.read(rec,0,rec.length);
				System.out.println(bytes_read);
				ByteArrayInputStream bs= new ByteArrayInputStream(rec); // bytes es el byte[]
				ObjectInputStream is = new ObjectInputStream(bs);
				paqueteRecibido = (PaqueteEnvio)is.readObject();
				System.out.println(paqueteRecibido.toString());
				nick = paqueteRecibido.getNick();
				ip = paqueteRecibido.getIp();
				mensaje = paqueteRecibido.getMensaje();
				System.out.println("Mensaje llego al servidor: " + mensaje);
				estado = paqueteRecibido.getEstado();
				System.out.println(estado);
				
				InetAddress localizacion = miSocket.getInetAddress();
				String ipRemota = localizacion.getHostAddress();
				
				if(estado.equals(PaqueteEnvio.ONLINE)) { 
					//System.out.println("paso el if");
					for(String ipActual : NickIP.values()) {
						//System.out.println("paso un for");
						if(!ipRemota.equals(ipActual)) {
							//System.out.println("paso el for if");
							Socket enviaDestinatario = new Socket(ipActual, ClientChat.PUERTO_CLIENTE2);
							/*
							ObjectOutputStream paqueteReEnvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
							paqueteReEnvio.writeObject(paqueteRecibido);
							System.out.println("Ya se va a enviar el paquete al cliente que escucha");
							paqueteReEnvio.close();
							
							enviaDestinatario.close();
							
							miSocket.close();
							*/
							ByteArrayOutputStream ba= new ByteArrayOutputStream();
							ObjectOutputStream os = new ObjectOutputStream (ba);
							os.writeObject(paqueteRecibido);  // this es de tipo DatoUdp
							os.close();
							byte[] bytes =  ba.toByteArray(); // devuelve byte[]
							ObjectOutputStream paqueteReEnvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
							paqueteReEnvio.write(bytes, 0, bytes.length);
							System.out.println("Numero de bytes a enviar "+bytes.length);
							paqueteReEnvio.flush();
							enviaDestinatario.close();
							
							miSocket.close();
						}
					}
					
					
				}else { 
					
					NickIP.put(nick, ipRemota);
					
					System.out.println("Conectado el cliente " + nick + " con direccion IP: "+ipRemota);
					listaIP.add(ipRemota);
					
					paqueteRecibido.setIps(listaIP);
					
					for(String z : listaIP){
						
						Socket enviaDestinatario = new Socket(z, ClientChat.PUERTO_CLIENTE2);
						
						ObjectOutputStream paqueteReEnvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
						paqueteReEnvio.writeObject(paqueteRecibido);
						
						paqueteReEnvio.close();
						
						enviaDestinatario.close();
						
						miSocket.close();
					}
					
				}
			}
	
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}

}
