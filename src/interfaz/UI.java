package interfaz;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import model.ChatClient;
import javax.swing.JTextArea;

/**
 * Clase que maneja la interfaz gráfica
 */
public class UI {
	
	/**
	 * Nombre del cliente
	 */
	private String name;
	
	/**
	 * Combobox donde se escoge al destinatario
	 */
	private JComboBox<String> recipientPicker;
	
	/**
	 * Panel donde se muestran los mensajes
	 */
	private JTextArea panelMensajitos;
	
	/**
	 * Constructor de la interfaz gráfica
	 * @param name Nombre del cliente
	 * @param client Clase ChatClient
	 */
	public UI(String name, ChatClient client) {
		
		this.name = name;
		
		JFrame frame = new JFrame(name);
		JPanel panel = new JPanel(new BorderLayout());
		
		JPanel controlsPanel = new JPanel();
		JTextField textField = new JTextField(30);
		JCheckBox encryptionCheckBox = new JCheckBox("Encriptar", false);
		recipientPicker = new JComboBox<>();
		JButton sendButton = new JButton("Send");
		
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String recipient = recipientPicker.getSelectedItem().toString();
				String body = textField.getText();
				Boolean encrypted = encryptionCheckBox.isSelected();
				client.sendMessage(recipient, body, encrypted);
				textField.setText("");
			}
		});
		
		controlsPanel.add(textField);
		controlsPanel.add(recipientPicker);
		controlsPanel.add(encryptionCheckBox);
		controlsPanel.add(sendButton);
		
		panel.add(controlsPanel, BorderLayout.SOUTH);
		
		frame.getContentPane().add(panel);
		
		panelMensajitos = new JTextArea();
		panelMensajitos.setEditable(false);
		panel.add(panelMensajitos, BorderLayout.CENTER);
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		frame.setSize(600, 600);
		frame.setVisible(true);
		frame.revalidate();
		frame.repaint();
		
	}
	
	/**
	 * Método que permite refrescar el panel de mensajes
	 * @param clients arreglo de nombres de clientes
	 * @param messages Nuevos mensajes a agregar al panel
	 */
	@SuppressWarnings("unused")
	public void refresh(ArrayList<String> clients, TreeMap<String, String[]> messages) {
		
		if (messages != null) {
	        
	        String texto="Hora\tRemitente\tDestinatario\tMensajito\n";
	        
		    for (Entry<String, String[]> messageEntry: messages.entrySet()) {

		        if (messageEntry.getKey().equals("null")) continue;

		        long milis = Long.valueOf(messageEntry.getKey().split("_")[0]) * 1000;

		        String time = (new SimpleDateFormat("HH:mm:ss")).format(new Date(milis));
		        String sender = messageEntry.getValue()[0];
		        String recipient = messageEntry.getValue()[1];
		        String body = messageEntry.getValue()[2];

		        
				boolean isSender = messageEntry.getValue()[0].equals(name);
		        boolean isRecipient = messageEntry.getValue()[1].equals(name);
		        
		        texto+= time+"\t"+sender+"\t"+recipient+"\t"+body+"\n";

		    }
		    panelMensajitos.setText(texto);
		    
		}
		
		recipientPicker.removeAllItems();
		
		for (String client: clients) {
			recipientPicker.addItem(client);
		}
		
	}
	
}
