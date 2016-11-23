import java.util.*;
import java.net.*;
import java.io.*;
import UGDB.*;
import java.util.Scanner;
import java.util.regex.*;
public class Server1{
          private static String ourServ = "server2";
          private static Hashtable<Socket,String> logged = new Hashtable<Socket,String>(); //Clientes loggedados
          public static void main(String[] args) throws Exception { // CICLO Conexion con clientes.
                    ServerSocket listener = new ServerSocket(1400);
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
                                       }
                                       catch(Exception a){}
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

          }
          private static void sendMail(Socket socket) throws IOException{
                   BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                   ArrayList<String> recep = new ArrayList<String>();
                   ArrayList<String> buenos = new ArrayList<String>();
                   String sub = "";
                   String body = "";
                   String contact ="";
                   DB conndb = new DB("Mail.db");
                   String input = in.readLine();
                   System.out.println("todo va bien");
                   while(!input.equals("END SEND MAIL")){
                              String[] ll = to(input);
                              if ((ll[0].equals("MAIL") && ll[1].equals("TO")) && (!ll[ll.length-1].equals("*"))){
                                        recep.add(ll[2]);
                                        System.out.println(input);
                              }
                              else if ((ll[0].equals("MAIL") && ll[1].equals("TO")) && (ll[ll.length-1].equals("*"))){
                                        recep.add(ll[2]);
                                        System.out.println(input);
                              }
                              else if((ll[0].equals("MAIL")) && (ll[1].equals("SUBJECT"))){
                                        sub = input.substring(12,input.length());
                                        System.out.println(input);
                              }
                              else if ((ll[0].equals("MAIL")) && (ll[1].equals("BODY"))){
                                        body = input.substring(9,input.length());
                                        System.out.println(input);
                              }
                              else if ((ll[0].equals("MAIL")) && (ll[1].equals("FROM"))){
                                        contact = ll[2];
                                        System.out.println(input);
                              }
                              else {
                                         System.out.println("Se ha cerrado");
                                        break;

                              }
                              input = in.readLine();
                   }
                   if (contact.equals("")){
                              for(int i = 0; i < recep.size(); i++){
                                        Scanner sc = new Scanner(recep.get(i));
                                        sc.useDelimiter("@");
                                        String user = sc.next();
                                        String serv = sc.next();
                                        try{
                                                  conndb.connect();
                                                  try{
                                                  if (conndb.executeQuery("select * from Contactos where usuarioid = '"+logged.get(socket)+"' AND usuario = '"+user+"' AND sid = '"+serv+"'","rs1")) {
                                                                      conndb.next("rs1");
                                                                      if ((conndb.getString("usuario","rs1").equals(user)) && (conndb.getString("sid","rs1").equals(serv))) {
                                                                                buenos.add(recep.get(i));
                                                                      }
                                                 }
                                       } catch(Exception a){System.out.println(a);}
                                                 conndb.close();
                                        }
                                        catch(Exception a){}
                              }
                              for(int i = 0; i < buenos.size(); i++){
                                        Scanner sc = new Scanner(buenos.get(i));
                                        sc.useDelimiter("@");
                                        String user = sc.next();
                                        String serv = sc.next();
                                        try{
                                                 conndb.connect();
                                                 conndb.executeNonQuery("INSERT INTO Correos (sender,subject,body,usuario) VALUES ('"+logged.get(socket)+"','"+sub+"','"+body+"','"+user+"')");
                                                 conndb.close();
                                       }catch(Exception e){
                                                 e.printStackTrace();
                                       }
                              }
                   }
                   else{
                              for(int i = 0; i < recep.size(); i++){
                                        Scanner sc = new Scanner(recep.get(i));
                                        sc.useDelimiter("@");
                                        String user = sc.next();
                                        String serv = sc.next();
                                        try{
                                                  conndb.connect();
                                                  try{
                                                  if (conndb.executeQuery("select * from Contactos where usuarioid = '"+logged.get(socket)+"' AND usuario = '"+user+"' AND sid = '"+serv+"'","rs1")) {
                                                                      conndb.next("rs1");
                                                                      if ((conndb.getString("usuario","rs1").equals(user)) && (conndb.getString("sid","rs1").equals(serv))) {
                                                                                buenos.add(recep.get(i));
                                                                      }
                                                 }
                                       } catch(Exception a){System.out.println(a);}
                                                 conndb.close();
                                        }
                                        catch(Exception a){}
                              }
                              for(int i = 0; i < buenos.size(); i++){
                                        Scanner sc = new Scanner(buenos.get(i));
                                        sc.useDelimiter("@");
                                        String user = sc.next();
                                        String serv = sc.next();
                                        try{
                                                 conndb.connect();
                                                 conndb.executeNonQuery("INSERT INTO Correos (sender,subject,body,usuario) VALUES ('"+logged.get(socket)+"','"+sub+"','"+body+"','"+contact+"')");
                                                 conndb.close();
                                       }catch(Exception e){
                                                 e.printStackTrace();
                                       }
                              }
                   }


          }

}
