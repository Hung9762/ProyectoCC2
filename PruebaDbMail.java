import java.util.*;
import java.io.*;

public class PruebaDbMail{
	public static void main(String[] args) {

		Scanner lectorOpciones = new Scanner(System.in);
		int opcion = 0;
		do{
			System.out.println("\nSeleccione opción a realizar:\n\n  (1) Ingreso de servidores.\n  (2) Ingreso de usuario.\n  (3) Ingreso de contacto.\n  (4) Ver contactos.\n  (5) Mandar mail.\n  (6) Ver mails.\n  (7) Login.\n  (8) Salida");
			System.out.print("\n Opción: ");
			opcion = lectorOpciones.nextInt();
			switch (opcion) {
			case 1:
				insertarServidor();
				break;
			case 2:
				insertarUsuario();
				break;
			
			case 3:
				insertarContacto();
				break;
			case 4:
				verContactos();
				break;
			case 5:
				mandarMail();
				break;
			case 6:
				verMails();
				break;
			case 7:
				login();
				break;	
			}
		} while (opcion != 8);
	}

	public static void insertarServidor(){
		Scanner lector = new Scanner(System.in);
		System.out.print("\nIngrese el nombre del servidor: ");
		String nombreServer = lector.nextLine();
            try{
            	DB conndb = new DB("Mail.db");
                conndb.connect();
                System.out.println("executing insert");
        	    String query = "INSERT INTO Server (sid) VALUES ("+nombreServer+")";
                System.out.println(conndb.executeNonQuery(query));
            }catch(Exception e){
             	System.out.println(e.getClass());
             	System.out.println(e.getMessage());
            }
	}	
	

	public static void insertarUsuario(){
		Scanner lector = new Scanner(System.in);
		System.out.print("\nIngrese usuario: ");
		String usuario = lector.nextLine();
		System.out.print("\nIngrese contraseña: ");
		String pass = lector.nextLine();
		System.out.print("\nIngrese server: ");
		String server = lector.nextLine();
		boolean serverExist = false;
		boolean userExist = false;
            try{
            	DB conndb = new DB("Mail.db");
                conndb.connect();
                if (conndb.executeQuery("select usuario from Usuarios","rs1")) {
                	while(conndb.next("rs1")){//next method makes tuple fetch, if there are no more tuples method returns false
						String usuariosExistentes = conndb.getString("usuario","rs1").toString();
						if (usuario.equals(usuariosExistentes)) {
							userExist = true;
						}
					}
					if(userExist){
	                	System.out.println("====> ERROR, EL USUARIO QUE DESEA INGRESAR YA EXISTE");
						return;
                	}
                }
                if (conndb.executeQuery("select sid from Server","rs2")) {
					while(conndb.next("rs2")){//next method makes tuple fetch, if there are no more tuples method returns false
						String serversGuardados = conndb.getString("sid","rs2").toString();
						if (server.equals(serversGuardados)) {
							serverExist = true;
						}
					}
					if (serverExist) {
						System.out.println("executing insert");
					   	String query = "INSERT INTO Usuarios (usuario,password,sid) VALUES ('"+usuario+"','"+pass+"',"+server+")";
					    System.out.println(conndb.executeNonQuery(query));
					}else{
						System.out.println("====> ERROR, EL SERVIDOR QUE DESEA INGRESAR NO EXISTE");
						return;
					}
				}
            }catch(Exception e){
             	System.out.println(e.getClass());
             	System.out.println(e.getMessage());
            }
	}

	public static void insertarContacto(){
		Scanner lector = new Scanner(System.in);
		System.out.print("\nIngrese usuario a guardar: ");
		String usuarioAGuardar = lector.nextLine();
		System.out.print("\nIngrese server al que pertenece: ");
		String server = lector.nextLine();
		System.out.print("\nIngrese usuario que lo esta guardando: ");
		String usuarioQueLoGuarda = lector.nextLine();
		boolean serverExist = false;
		boolean userExist = false;
            try{
            	DB conndb = new DB("Mail.db");
                conndb.connect();
                if (conndb.executeQuery("select usuario from Usuarios","rs1")) {
                	while(conndb.next("rs1")){//next method makes tuple fetch, if there are no more tuples method returns false
						String usuariosExistentes = conndb.getString("usuario","rs1").toString();
						if (usuarioQueLoGuarda.equals(usuariosExistentes)) {
							userExist = true;
						}
					}
					if(!userExist){
	                	System.out.println("====> ERROR, EL USUARIO AL QUE LE DESEA AGREGAR EL CONTACTO NO EXISTE");
						return;
                	}
                }
                if (conndb.executeQuery("select sid from Server","rs2")) {
					while(conndb.next("rs2")){//next method makes tuple fetch, if there are no more tuples method returns false
						String serversGuardados = conndb.getString("sid","rs2").toString();
						if (server.equals(serversGuardados)) {
							serverExist = true;
						}
					}
					if (serverExist) {
						System.out.println("executing insert");
					   	String query = "INSERT INTO Contactos (usuario,sid,usuarioid) VALUES ('"+usuarioAGuardar+"','"+server+"','"+usuarioQueLoGuarda+"')";
					    System.out.println(conndb.executeNonQuery(query));
					}else{
						System.out.println("====> ERROR, EL SERVIDOR AL QUE PERTENECE NO EXISTE");
						return;
					}
				}
            }catch(Exception e){
             	System.out.println(e.getClass());
             	System.out.println(e.getMessage());
            }
	}

	public static void verContactos(){
		Scanner lector = new Scanner(System.in);
		System.out.print("\nIngrese usuario del cual desea ver los contactos: ");
		String usuario = lector.nextLine();
		try{
            DB conndb = new DB("Mail.db");
            conndb.connect();
            System.out.println("Cliente : CLIST "+usuario);
            if (conndb.executeQuery("select * from Contactos where usuarioid = '"+usuario+"'","rs1")) {
            	while(conndb.next("rs1"))
            		System.out.println("Server : OK CLIST "+conndb.getString("usuario","rs1")+"@"+conndb.getString("sid","rs1"));
            }
        }catch(Exception e){
           	System.out.println(e.getClass());
           	System.out.println(e.getMessage());
        }
	}

	public static void mandarMail(){
		Scanner lector = new Scanner(System.in);
		System.out.print("\nIngrese el mailer: ");
		String mailer = lector.nextLine();
		System.out.print("\nIngrese usuario al cual se le enviara el correo: ");
		String mailTo = lector.nextLine();
		Recipient receptor;
		LinkedList<Recipient> recipients = new LinkedList<Recipient>();
		LinkedList<Recipient> goodRecipients = new LinkedList<Recipient>();
		String recipient;
		String server;
		boolean contactExists = false;
		DB conndb = new DB("Mail.db");
		while(true){
			if (mailTo.contains("*")) {
				recipient = mailTo.substring(mailTo.indexOf("TO")+3,mailTo.indexOf('@'));
				recipient.trim();
				server = mailTo.substring(mailTo.indexOf('@')+1,mailTo.length()-1);
				server.trim();
				receptor = new Recipient(recipient,server);
				recipients.add(receptor);
				break;
			}else{
				recipient = mailTo.substring(mailTo.indexOf("TO")+3,mailTo.indexOf('@'));
				recipient.trim();
				server = mailTo.substring(mailTo.indexOf('@')+1,mailTo.length());
				server.trim();
				receptor = new Recipient(recipient,server);
				recipients.add(receptor);
				System.out.print("\nIngrese usuario al cual se le enviara el correo: ");
				mailTo = lector.nextLine();
			}
		}

		System.out.println();
		String subject = lector.nextLine();
		if (subject.contains("MAIL SUBJECT")) {
			subject = subject.substring(12,subject.length());
		}
		subject = subject.trim();
		System.out.println(subject);
		
		System.out.println();
		String body = lector.nextLine();
		if (body.contains("MAIL BODY")) {
			body = body.substring(9,body.length());
		}
		body = body.trim();
		System.out.println(body);

		System.out.println();
		String instruccionFinal = lector.nextLine();
		if (instruccionFinal.contains("END SEND MAIL")) {
			for (int i = 0;i < recipients.size() ;i++ ) {
				try{
					conndb.connect();
            		if (conndb.executeQuery("select * from Contactos where usuarioid = '"+mailer+"' AND usuario = '"+recipients.get(i).getUsuario()+"' AND sid = "+recipients.get(i).getServer(),"rs1")) {
            			
            			try{
            				conndb.next("rs1");
            				if ((conndb.getString("usuario","rs1").equals(recipients.get(i).getUsuario())) && (conndb.getString("sid","rs1").equals(recipients.get(i).getServer()))) {
            					goodRecipients.add(recipients.get(i));
            				}
            			}catch(Exception e1){
            				System.out.println("Contacto no existe "+recipients.get(i).getUsuario());
            			}	
            		}
            		conndb.close();
        		}catch(Exception e){
           			System.out.println(e.getClass());
           			System.out.println(e.getMessage());
           			e.printStackTrace();
        		}
			}
		}

		for (int i = 0;i < goodRecipients.size() ;i++ ) {
			System.out.println("usuario: "+goodRecipients.get(i).getUsuario()+" server: "+goodRecipients.get(i).getServer());
			try{
				conndb.connect();
				conndb.executeNonQuery("INSERT INTO Correos (sender,subject,body,usuario) VALUES ('"+mailer+"','"+subject+"','"+body+"','"+goodRecipients.get(i).getUsuario()+"')");
				conndb.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}

	}

	public static void verMails(){
		Scanner lector = new Scanner(System.in);
		System.out.print("\nIngrese usuario: ");
		String usuario = lector.nextLine();
		try{
            DB conndb = new DB("Mail.db");
            conndb.connect();
            System.out.println("Cliente : GETNEWMAILS "+usuario);
            if (conndb.executeQuery("select * from Correos where usuario = '"+usuario+"'","rs1")) {
            	while(conndb.next("rs1"))
            		System.out.println("Server : OK GETNEWMAILS "+conndb.getString("sender","rs1")+" \""+conndb.getString("subject","rs1")+"\" \""+conndb.getString("body","rs1")+"\"");
            }
        }catch(Exception e){
           	System.out.println(e.getClass());
           	System.out.println(e.getMessage());
        }
	}

	public static void login(){
		
	}
}