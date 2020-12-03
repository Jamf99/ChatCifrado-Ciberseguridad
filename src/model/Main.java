package model;

import javax.swing.JOptionPane;

/**
 * Abre un JOptionPane preguntando el nombre del usuario que se va a conectar e inmediatamente se conecta al canal
 * @author Lenovo
 *
 */
public class Main {
	private static final String IP="192.168.0.9";
	public static void main(String[] args) {
		String nombre = JOptionPane.showInputDialog("Ingrese nombre del usuario");
		if (nombre!=null) {
			new ChatClient(nombre, IP, 1111);
		}
		
	}
	
}
