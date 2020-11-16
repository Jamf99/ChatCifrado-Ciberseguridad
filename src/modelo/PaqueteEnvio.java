package modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class PaqueteEnvio implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final String OFFLINE = "offline";
	public static final String ONLINE = "online";
	
	private String nick;
	private String ip;
	private String mensaje;
	private String estado;
	
	private ArrayList<String> Ips;
	
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public ArrayList<String> getIps() {
		return Ips;
	}
	public void setIps(ArrayList<String> ips) {
		Ips = ips;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}

}
