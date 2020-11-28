package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import encriptacion.*;
import modelo.PaqueteEnvio;
import socket.ClientChat;


public class LaminaMarcoCliente extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;
	private JTextField campo1;
	private JButton miboton;
	private JTextArea campochat;
	private JLabel nick;
	private JComboBox<String> ip;
	
	public LaminaMarcoCliente(){
		//setLayout(new BorderLayout());
		String nickUsuario = JOptionPane.showInputDialog("Nick: ");
		JLabel nNick = new JLabel("Nick: ");
		add(nNick);
		nick = new JLabel();
		nick.setText(nickUsuario);
		add(nick);
		
		JLabel texto=new JLabel("Online: ");
		
		add(texto);
		ip = new JComboBox<String>();
		add(ip);
		
		campochat = new JTextArea(12,20);
		campochat.setEditable(false);
		
		add(campochat);
	
		campo1 = new JTextField(20);
	
		add(campo1);		
	
		miboton=new JButton("Enviar");
		EnviaTexto mievento =new EnviaTexto();
		miboton.addActionListener(mievento);
		add(miboton);

		Thread mihilo = new Thread(this);
		
		peticionUsuario(nickUsuario);
		
		mihilo.start();
		
	}
	
	public JTextField getCampo1() {
		return campo1;
	}

	public void setCampo1(JTextField campo1) {
		this.campo1 = campo1;
	}

	public JButton getMiboton() {
		return miboton;
	}

	public void setMiboton(JButton miboton) {
		this.miboton = miboton;
	}

	public JTextArea getCampochat() {
		return campochat;
	}

	public void setCampochat(JTextArea campochat) {
		this.campochat = campochat;
	}

	public JLabel getNick() {
		return nick;
	}

	public void setNick(JLabel nick) {
		this.nick = nick;
	}

	public JComboBox<String> getIp() {
		return ip;
	}

	public void setIp(JComboBox<String> ip) {
		this.ip = ip;
	}

	public void peticionUsuario(String nick) {
		try {
			
			Socket misocket = new Socket(ClientChat.LOCAL_HOST, ClientChat.PUERTO_CLIENTE);
			PaqueteEnvio datos = new PaqueteEnvio();
			datos.setMensaje(" online");
			datos.setEstado(PaqueteEnvio.OFFLINE);
			datos.setNick(nick);
			/**
			ObjectOutputStream paqueteDatos = new ObjectOutputStream(misocket.getOutputStream());
			paqueteDatos.writeObject(datos);
			misocket.close();
			*/
			// ESTA VIVOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO FUNCIONAAAAAAAAAAAA
			ByteArrayOutputStream bs= new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream (bs);
			os.writeObject(datos);  // this es de tipo DatoUdp
			os.close();
			System.out.println(datos.toString());
			byte[] bytes =  bs.toByteArray(); // devuelve byte[]
			ObjectOutputStream paqueteDatos = new ObjectOutputStream(misocket.getOutputStream());
			paqueteDatos.write(bytes, 0, bytes.length);
			System.out.println("Numero de bytes a enviar "+bytes.length);
			paqueteDatos.flush();
			misocket.close();
		} catch (Exception e2) {
			
		}

	}
	
	public class EnviaTexto implements ActionListener{
		
		private AESSecurityCap clienteRecibeTexto;
		private AESSecurityCap clienteEnviaTexto;

		@Override
		public void actionPerformed(ActionEvent e) {	
			
			try {
				campochat.append("\n"+nick.getText()+": " + campo1.getText());
				
				Socket misocket = new Socket(ClientChat.LOCAL_HOST, ClientChat.PUERTO_CLIENTE); 
			
				PaqueteEnvio datos = new PaqueteEnvio();
				datos.setNick(nick.getText());
				
				//AQUÃ� SE PROCEDE A ENCRIPTAR EL MENSAJE
				clienteEnviaTexto = new AESSecurityCap();
				clienteRecibeTexto = new AESSecurityCap();
				clienteEnviaTexto.makeKeyExchangeParams();
				clienteRecibeTexto.makeKeyExchangeParams();
				
				System.out.println("Clave publica de clienteRecibeTexto: " + clienteRecibeTexto.getPublickey());
				System.out.println("Clave publica de clienteEnviaTexto: " + clienteEnviaTexto.getPublickey());
				clienteEnviaTexto.setReceiverPublicKey(clienteRecibeTexto.getPublickey());
				clienteRecibeTexto.setReceiverPublicKey(clienteEnviaTexto.getPublickey());
				
				datos.setClave(clienteEnviaTexto.getPublickey());
				datos.setNodito(clienteRecibeTexto);
				
				
				String enc = clienteEnviaTexto.encrypt(campo1.getText());
				System.out.println("Mensaje encriptado por enviatexto: " + enc);
				System.out.println("Mensaje desencriptado por recibeTexto" + clienteRecibeTexto.decrypt(enc));
				//AQUÃ� SE PROCEDE A ENVIAR EL MENSAJE ENCRIPTADO CON LA CLAVE PÃšBLICA
				datos.setMensaje(enc);
				datos.setEstado(PaqueteEnvio.ONLINE);
				// Modificamos para ver si convierte el objeto en un arreglo de bytes
				ByteArrayOutputStream bs= new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream (bs);
				os.writeObject(datos);  // this es de tipo DatoUdp
				os.close();
				byte[] bytes =  bs.toByteArray(); // devuelve byte[]
				ObjectOutputStream paqueteDatos = new ObjectOutputStream(misocket.getOutputStream());
				paqueteDatos.write(bytes, 0, bytes.length);
				System.out.println("Numero de bytes a enviar "+bytes.length);
				paqueteDatos.flush();
				misocket.close();
				
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				System.out.println(e1.getMessage());;
			}

		}
		
	}
	
	@Override
	public void run() {
		
		try {
			
			@SuppressWarnings("resource")
			ServerSocket servidorCliente = new ServerSocket(ClientChat.PUERTO_CLIENTE2);
			
			Socket cliente;
			
			PaqueteEnvio paqueteRecibido;
			
			while(true) {
				cliente = servidorCliente.accept();
				
				ObjectInputStream flujoEntrada = new ObjectInputStream(cliente.getInputStream());
				
				paqueteRecibido = (PaqueteEnvio)flujoEntrada.readObject();
				
				if(!paqueteRecibido.getMensaje().equals(" online")) {
					//Node receptor = paqueteRecibido.getNodito();
					//Node receptor = new Node();
					System.out.println("Clave publica de clienteRecibeTexto al otro lado: " + paqueteRecibido.getNodito().getPublickey());
					System.out.println("Clave publica de clienteEnviaTexto al otro lado: " + paqueteRecibido.getClave());
					
					paqueteRecibido.getNodito().setReceiverPublicKey(paqueteRecibido.getClave());
					//receptor.setReceiverPublicKey(paqueteRecibido.getClave());
					//clienteRecibeTexto.setReceiverPublicKey(clienteEnviaTexto.getPublickey());
					String mensajeDesencriptado = paqueteRecibido.getNodito().decrypt(paqueteRecibido.getMensaje());
					System.out.println("Mensaje desencriptado: " + mensajeDesencriptado);
					
					campochat.append("\n" + paqueteRecibido.getNick() + ": " + mensajeDesencriptado);
				}else {
					ArrayList<String> ipsMenu = new ArrayList<String>();
					ipsMenu = paqueteRecibido.getIps();
					
					ip.removeAllItems();
					
					for(String z: ipsMenu) {
						ip.addItem(z);
					}
					
				}
				
				
			}
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
}
