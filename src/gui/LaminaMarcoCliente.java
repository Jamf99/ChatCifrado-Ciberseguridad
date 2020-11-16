package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		setLayout(new BorderLayout());
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
			
			ObjectOutputStream paqueteDatos = new ObjectOutputStream(misocket.getOutputStream());
			paqueteDatos.writeObject(datos);
			misocket.close();
			
		} catch (Exception e2) {
			
		}

	}
	
	private class EnviaTexto implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {	
			
			try {
				campochat.append("\n"+nick.getText()+": " + campo1.getText());
				
				Socket misocket = new Socket(ClientChat.LOCAL_HOST, ClientChat.PUERTO_CLIENTE); 
			
				PaqueteEnvio datos = new PaqueteEnvio();
				datos.setNick(nick.getText());
				datos.setMensaje(campo1.getText());
				datos.setEstado(PaqueteEnvio.ONLINE);
				
				ObjectOutputStream paqueteDatos = new ObjectOutputStream(misocket.getOutputStream());
				paqueteDatos.writeObject(datos);
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
					campochat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());
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
