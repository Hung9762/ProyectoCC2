import java.util.*;
import java.net.*;
import java.io.*;
import UGDB.*;
import java.util.Scanner;
import java.util.regex.*;
public class Server1{
  private static String ourServ = "server2";
  private static Hashtable<String, String> ipTable = new Hashtable<String, String>();
  private static Hashtable<Socket,String> logged = new Hashtable<Socket,String>(); //Clientes loggedados
  public static void main(String[] args) throws Exception { // CICLO Conexion con clientes.
    (new CDNS()).start();
    (new Servidores()).start();
    ServerSocket listener = new ServerSocket(1400);
    System.out.println("Server en Linea ->");
    try {
      while (true) {
        Socket aux = listener.accept();
        aux.setSoTimeout(21000);
        new Recibos(aux).start();
      }
    }
    finally{
      listener.close();
    }
  }
  private static class Recibos extends Thread { // THREAD QUE RECIBE un socket y manda las respuestas.
    private Socket jj;
    public Recibos(Socket a){
      jj = a;
    }
    public void run(){
      while (true) {
        try{
          BufferedReader in = new BufferedReader(new InputStreamReader(jj.getInputStream()));
          String input = in.readLine();
          String[] arr = to(input);
          if (input != null){
            System.out.println(input);
            if (arr[0].equals("SEND") && arr[1].equals("MAIL")){
              sendMail(jj);
            }
            else{
              enviar(arr,jj);
            }
          }
        }catch(SocketTimeoutException s) {
          try{
            jj.close();
            DB conndb = new DB("Mail.db");
            conndb.connect();
            String query = "update Usuarios set online = 0 where usuario = '" +logged.get(jj)  + "'";
            conndb.executeNonQuery(query);
            conndb.close();
            System.out.println("Socket timed out! "+logged.get(jj));
            logged.remove(jj);      
          }catch(Exception e){}
        }
        catch(Exception a){}
      }
    }
  }
  private static class Servidores extends Thread{
    public void run(){
      try{
        ServerSocket listener = new ServerSocket(1500);
        System.out.println("Server en Linea ->");
        try {
          while (true) {
            new Recibos(listener.accept()).start();
          }
        }
        finally{
          listener.close();
        }
      }
      catch(Exception a){}
    }
  } //CICLO de servidores
  private static class RecibosServidor extends Thread { // THREAD QUE RECIBE Y ENVIA
    private Socket jj;
    public RecibosServidor(Socket a){
      jj = a;
    }
    public void run(){
      while (true) {
        try{
          BufferedReader in = new BufferedReader(new InputStreamReader(jj.getInputStream()));
          String input = in.readLine();
          String[] arr = to(input);
          if (input != null){
            System.out.println(input);
            if (arr[0].equals("SEND") && arr[1].equals("MAIL")){
              sendMailS(jj);
            }
            else{
              enviarS(arr,jj);
            }
          }
        }
        catch(Exception a){}
      }
    }
  }
  private static class CDNS extends Thread{
    public void run(){
      try{
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        Socket socket = new Socket("172.20.10.2", 1200);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Que desea hacer?");
        (new RecibosServidor(socket)).start();
        while(true){
          String men = input.readLine();
          out.println(men);
          out.flush();
        }
      }
      catch(Exception a){}
    }
  }
  private static class CServidores extends Thread{
    String ips;
    public CServidores(String a){
      ips = a;
    }
    public void run(){
      try{
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        Socket socket = new Socket(ips, 1500);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Que desea hacer?");
        (new RecibosDNS(socket)).start();
        while(true){
          String men = input.readLine();
          out.println(men);
          out.flush();
        }
      }
      catch(Exception a){}
    }
  }
  private static class RecibosDNS extends Thread{
    Socket socket;
    public RecibosDNS(Socket a){
      socket = a;
    }
    public void run(){
      try {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while(true){
          String input = in.readLine();
          String[] arr = to(input);
          if (input !=null){
            System.out.println(input);
            if (arr[0].equals("OK") && (arr[1].equals("IPTABLE"))){
              (new CServidores(arr[3])).start();
            }
          }

        }
      } catch (IOException e) {

      }
    }
  }
  //----------------------------- METODOS ------------------------------------------
  private static String[] to(String str){
    Scanner sc = new Scanner(str);
    Scanner sc1 = new Scanner(str);
    int cont = 0;
    while (sc.hasNext()){
      cont++;
      sc.next();
    }
    String[] arr = new String[cont];
    for (int i = 0; i < cont; i++){
      arr[i] = sc1.next();
    }
    return arr;
  } // CREA EL INPUT EN ARREGLO
  private static void enviar(String[] clientCommand, Socket socket) throws IOException{
    PrintWriter output= new PrintWriter(socket.getOutputStream(), true);
    if (clientCommand[0].equals("LOGIN")){
      if (clientCommand.length != 3){
        output.println("INVALID COMMAND ERROR");
      }
      else{
        DB conndb = new DB("Mail.db");
        try{
          conndb.connect();
          String query = "Select online from Usuarios where usuario = '" + clientCommand[1] + "'";
          conndb.executeQuery(query,"rs1");
          try{
            conndb.next("rs1");
            boolean login = conndb.getString("online","rs1").equals("0");
            conndb.close();
            if (login){
              conndb.connect();
              String query2 = "Select * from Usuarios where usuario = '" + clientCommand[1] +"' AND password = '" + clientCommand[2]+"'";
              conndb.executeQuery(query2,"rs2");
              try{
                conndb.next("rs2");
                String usuario = (String)conndb.getString("usuario", "rs2");
                logged.put(socket, usuario);
                conndb.close();
                conndb.connect();
                System.out.println(usuario);
                String query3 = "update Usuarios set online = 1 where usuario = '" + usuario + "'";
                conndb.executeNonQuery(query3);
                conndb.close();
                output.println("OK LOGIN");

              }
              catch(Exception a2){output.println("LOGIN ERROR 102");}
            }
            else{output.println("Ya hay una sesion iniciada");}

          }
          catch(Exception a){output.println("LOGIN ERROR 101");}
        }
        catch(Exception a){}
      }

    }
    else if (clientCommand[0].equals("CLIST")){
      if (clientCommand.length != 2){
        output.println("INVALID COMMAND ERROR");
      }
      else{
        DB conndb = new DB("Mail.db");
        try{
          if (logged.get(socket).equals(clientCommand[1])){
            conndb.connect();
            String query = "select * from Contactos where usuarioid = '"+clientCommand[1]+"'";
            conndb.executeQuery(query,"rs1");
            try{
              ArrayList<String> clist = new ArrayList<String>();
              while(conndb.next("rs1")){
                clist.add("OK CLIST "+conndb.getString("usuario","rs1")+"@"+conndb.getString("sid","rs1"));
              }
              conndb.close();
              for(int i = 0; i < clist.size(); i++){
                if (i == clist.size()-1){
                  output.println(clist.get(i) + " *");
                }
                else{
                  output.println(clist.get(i));
                }
              }
            }
            catch(Exception a1){output.println("CLIST ERROR 103");}
          }
          else{
            output.println("INVALID COMMAND ERROR");
          }
        }
        catch(Exception a){}
      }

    }
    else if (clientCommand[0].equals("NEWCONT")){
      if (clientCommand.length != 2){
        output.println("INVALID COMMAND ERROR");
      }
      else{
        if (Pattern.matches("\\w+@\\w+", clientCommand[1])){
          Scanner sc = new Scanner(clientCommand[1]);
          sc.useDelimiter("@");
          String user = sc.next();
          String serv = sc.next();
          boolean serverExist = false;
          boolean userExist = false;
          System.out.println(user);
          System.out.println(serv);
          DB conndb = new DB("Mail.db");
          if (serv.equals(ourServ)){
            try{
              conndb.connect();
              if (conndb.executeQuery("select usuario from Usuarios","rs1")) {
                while(conndb.next("rs1")){
                  String usuariosExistentes = conndb.getString("usuario","rs1").toString();
                  if (user.equals(usuariosExistentes)) {
                    userExist = true;
                  }
                }
                conndb.close();
                if(!userExist){
                  output.println("NEWCONT ERROR 109");
                }else{
                  conndb.connect();
                  String query = "INSERT INTO Contactos (usuario,sid,usuarioid) VALUES ('"+user+"','"+ourServ+"','"+logged.get(socket)+"')";
                  conndb.executeNonQuery(query);
                  output.println("OK NEWCONT " +clientCommand[1]);
                  conndb.close();
                }
              }

            }catch(Exception e){

            }
          }
          else{

          }

        }
      }
    }
    else if (clientCommand[0].equals("GETNEWMAILS") && (clientCommand.length == 2)){
      //   PrintWriter output= new PrintWriter(socket.getOutputStream(), true);
      DB conndb = new DB("Mail.db");
      try{
        conndb.connect();
        if (conndb.executeQuery("select * from Correos where usuario = '"+clientCommand[1]+"'","rs1")) {
          while(conndb.next("rs1"))
            output.println("OK GETNEWMAILS "+conndb.getString("sender","rs1")+" \""+conndb.getString("subject","rs1")+"\" \""+conndb.getString("body","rs1")+"\"");
        }
      }
      catch(Exception a){}
    }
    else if(clientCommand[0].equals("LOGOUT") && (clientCommand.length == 1) && (logged.containsKey(socket))){
      //PrintWriter output= new PrintWriter(socket.getOutputStream(), true);
      DB conndb = new DB("Mail.db");

      try{
        conndb.connect();
        String query3 = "update Usuarios set online = 0 where usuario = '" +logged.get(socket)  + "'";
        conndb.executeNonQuery(query3);
        conndb.close();
        logged.remove(socket);
        output.println("OK LOGOUT");
      }
      catch(Exception a){}
    }else if(clientCommand[0].equals("NOOP") && (clientCommand.length == 1) && (logged.containsKey(socket))){
      try{
        output.println("OK NOOP");
      }
       catch(Exception a){}
    }

  }
  private static void sendMail(Socket socket) throws IOException{
    PrintWriter output= new PrintWriter(socket.getOutputStream(), true);
    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    ArrayList<String> recep = new ArrayList<String>();
    ArrayList<String> buenos = new ArrayList<String>();
    String sub = "";
    String body = "";
    String contact ="";
    DB conndb = new DB("Mail.db");
    String input = in.readLine();
    System.out.println("todo va bien");
    int  j = 0;
    while(!input.equals("END SEND MAIL")){
      String[] ll = to(input);
      if ((ll[0].equals("MAIL") && ll[1].equals("TO")) && (!ll[ll.length-1].equals("*")) && (j == 0 || j == 1) ){
        recep.add(ll[2]);
        System.out.println(input);
        j = 1;
      }
      else if ((ll[0].equals("MAIL") && ll[1].equals("TO")) && (ll[ll.length-1].equals("*")) && (j == 0 || j == 1)){
        recep.add(ll[2]);
        System.out.println(input);
        j = 2;
      }
      else if((ll[0].equals("MAIL")) && (ll[1].equals("SUBJECT")) && j ==2){
        sub = input.substring(12,input.length());
        if (sub.equals("")){
          output.println("SEND ERROR 107");
        }
        System.out.println(input);
        j =3;
      }
      else if ((ll[0].equals("MAIL")) && (ll[1].equals("BODY")) && j ==3){
        body = input.substring(9,input.length());
        if (body.equals("")){
          output.println("SEND ERROR 108");
        }
        System.out.println(input);
        j = 4;
      }
      else {
        System.out.println("Se ha cerrado");
        j = 5;
        break;

      }
      input = in.readLine();
    }
    if (j != 5){
      j =0;
      for(int i = 0; i < recep.size(); i++){
        Scanner sc = new Scanner(recep.get(i));
        sc.useDelimiter("@");
        String user = sc.next();
        String serv = sc.next();
        if (serv.equals(ourServ)) {
          try{
            conndb.connect();
            try{
              if (conndb.executeQuery("select * from Usuarios where usuario = '"+user+"'","rs1")) {
                conndb.next("rs1");
                if (conndb.getString("usuario","rs1").equals(user)) {
                  buenos.add(recep.get(i));
                }
                conndb.close();
              }
            }catch(Exception a1){
              output.println("SEND ERROR 104 "+user+"@"+serv);
              conndb.close();
            }
          }catch(Exception a){
            a.printStackTrace();
          }
        }else{
            //Preguntar a otro servidor

        }
      }
      for(int i = 0; i < buenos.size(); i++){
        Scanner sc = new Scanner(buenos.get(i));
        sc.useDelimiter("@");
        String user = sc.next();
        String serv = sc.next();
        try{
          if (serv.equals(ourServ)) {
            conndb.connect();
            conndb.executeNonQuery("INSERT INTO Correos (sender,subject,body,usuario) VALUES ('"+logged.get(socket)+"','"+sub+"','"+body+"','"+user+"')");
            conndb.close();
          }else{
            //Mandar correo al otro servidor
          }
        }catch(Exception e){
          e.printStackTrace();
        }
      }
    }




  }
  private static void enviarS(String[] clientCommand, Socket socket) throws IOException{
    PrintWriter output= new PrintWriter(socket.getOutputStream(), true);
    if (clientCommand[0].equals("CHECK") && (clientCommand[1].equals("CONTACT"))){
      if (clientCommand.length != 3){
        output.println("INVALID COMMAND");
      }
      else{
        DB conndb = new DB("Mail.db");
        try {
          conndb.connect();
          Scanner sc = new Scanner(clientCommand[2]);
          sc.useDelimiter("@");
          String user = sc.next();
          String serv = sc.next();
          if (ourServ.equals(serv)){
            String query = "Select * from Usuarios where usuario = '" + user + "'";
            conndb.executeQuery(query,"rs1");
            try{
              conndb.next("rs1");
              String login = (String)conndb.getString("usuario","rs1");
              conndb.close();
            }
            catch(Exception b){output.println("CHECK ERROR 205");}
          }
          else{
            output.println("CHECK ERROR 206");
          }

        }
        catch(Exception a){}
      }
    }
  }
  private static void sendMailS(Socket socket)throws IOException{
    PrintWriter output= new PrintWriter(socket.getOutputStream(), true);
    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    String sender = "";
    String sub = "";
    String body = "";
    String contact ="";

    DB conndb = new DB("Mail.db");
    String input = in.readLine();
    String[] ll = to(input);
    Scanner sc1 = new Scanner(ll[2]);
    sc1.useDelimiter("@");
    String user = sc1.next();
    int  j = 0;
    while(!input.equals("END SEND MAIL")){
      ll = to(input);
      if ((ll[0].equals("MAIL")) && (ll[1].equals("FROM")) && j ==0){
        Scanner sc = new Scanner(ll[2]);
        sc.useDelimiter("@");
        sender = sc.next();
        String serv = sc.next();
        if (serv.equals(ourServ)){
          System.out.println("Es nuestro");
        }
        if (sender.equals("")){

          output.println("SEND ERROR 202");
        }
        System.out.println(input);
        j = 1;
      }
      else if((ll[0].equals("MAIL")) && (ll[1].equals("SUBJECT")) && j ==1){
        sub = input.substring(12,input.length());
        if (sub.equals("")){
          output.println("SEND ERROR 203");
        }
        System.out.println(input);
        j =2;
      }
      else if ((ll[0].equals("MAIL")) && (ll[1].equals("BODY")) && j ==2){
        body = input.substring(9,input.length());
        if (body.equals("")){
          output.println("SEND ERROR 204");
        }
        System.out.println(input);
        j = 3;
      }
      else {
        System.out.println("Se ha cerrado");
        j = 4;
        break;

      }
      input = in.readLine();
    }
    if (j != 4){

      String query = "INSERT INTO Correos (sender,subject,body,usuario) VALUES ('"+sender+"','"+sub+"','"+body+"','"+user+"')";
      try{
        conndb.connect();
        conndb.executeNonQuery(query);
        conndb.close();
      }
      catch(Exception a){}

    }
  }

}