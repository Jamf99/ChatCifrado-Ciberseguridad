package socket;

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
				paqueteRecibido = (PaqueteEnvio)paqueteDatos.readObject();
				
				nick = paqueteRecibido.getNick();
				ip = paqueteRecibido.getIp();
				mensaje = paqueteRecibido.getMensaje();
				estado = paqueteRecibido.getEstado();
				
				InetAddress localizacion = miSocket.getInetAddress();
				String ipRemota = localizacion.getHostAddress();
				
				if(estado.equals(PaqueteEnvio.ONLINE)) { 
					
					for(String ipActual : NickIP.values()) {
						
						if(!ipRemota.equals(ipActual)) {
							
							Socket enviaDestinatario = new Socket(ipActual, ClientChat.PUERTO_CLIENTE2);
							
							ObjectOutputStream paqueteReEnvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
							paqueteReEnvio.writeObject(paqueteRecibido);
							
							paqueteReEnvio.close();
							
							enviaDestinatario.close();
							
							miSocket.close();
						}
					}
					
					
				}else { 
					
					System.out.println("Conectado el cliente " + nick + " con dirección IP: "+ipRemota);
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
