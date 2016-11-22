public class Recipient{
	private String usuario;
	private String server;

	public Recipient(String usuario, String server){
		this.usuario = usuario;
		this.server = server;
	}

	public String getUsuario(){
		return usuario;
	}

	public String getServer(){
		return server;
	}
}