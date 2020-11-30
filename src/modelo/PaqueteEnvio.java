package modelo;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;

import encriptacion.AESSecurityCap;
import encriptacion.Node;

public class PaqueteEnvio implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final String OFFLINE = "offline";
	public static final String ONLINE = "online";
	
	private String nick;
	private String ip;
	private String mensaje;
	private String estado;
	private PublicKey clave;
	private Node nodito;
	
	public Node getNodito() {
		return nodito;
	}
	public void setNodito(Node nodito) {
		this.nodito = nodito;
	}
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
	public PublicKey getClave() {
		return clave;
	}
	public void setClave(PublicKey clave) {
		this.clave = clave;
	}
	@Override
	public String toString() {
		return "PaqueteEnvio [nick=" + nick + ", ip=" + ip + ", mensaje=" + mensaje + ", estado=" + estado + ", clave="
				+ clave + ", nodito=" + nodito + ", Ips=" + Ips + "]";
	}
	

}
