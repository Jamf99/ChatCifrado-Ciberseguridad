package gui;

import javax.swing.JFrame;


public class MarcoCliente extends JFrame {

	private static final long serialVersionUID = 1L;

	public MarcoCliente(){
		
		setBounds(600,300,280,350);
		LaminaMarcoCliente milamina=new LaminaMarcoCliente();
		add(milamina);
		setVisible(true);
	}	
}
