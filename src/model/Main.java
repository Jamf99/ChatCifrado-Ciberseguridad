package model;

import javax.swing.JOptionPane;

/**
 * Clase principal donde se ejecuta el Cliente
 */
public class Main {
	
	/**
	 * Ip del cliente
	 */
	private static final String IP="192.168.0.9";
	
	/**
	 * Método principal que permite abrir un JOptionPane que preguntar el nombre del usuario donde se va a conectar<br>
	 * y posteriormente se conecta al canal
	 */
	public static void main(String[] args) {
		String nombre = JOptionPane.showInputDialog("Ingrese nombre del usuario");
		if (nombre!=null) {
			new ChatClient(nombre, IP, 1111);
		}
		
	}
	
}
