package model;

import javax.swing.JOptionPane;

public class Main {
	private static final String IP="192.168.1.34";
	public static void main(String[] args) {
		String nombre = JOptionPane.showInputDialog("Ingrese nombre del usuario");
		if (nombre!=null) {
			new ChatClient(nombre, IP, 1111);
		}
		
	}
	
}
