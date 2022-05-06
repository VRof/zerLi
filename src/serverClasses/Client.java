package serverClasses;

public class Client {
	private String ip;
	private String host;
	private String status;
	public Client(String ip, String host, String status) {
		this.ip = ip;
		this.host = host;
		this.status = status;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	

}
