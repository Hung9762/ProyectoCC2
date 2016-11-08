# ProyectoCC2
Mail and Cliente server


CC2 - Proyecto #3 - Mail Client/Server
Proyecto: Mail Client/Server
Tema : POO y Estructuras de Datos
Fecha de Entrega : Jueves 24 de noviembre del 2016 (antes de las 9 AM si es seccion A, B, C o D; antes de las 6pm si es seccion AN) 
Grupo : Cuatro, el cual se dividira en dos subgrupos de dos personas
Correccion personal : 24 de noviembre del 2016 despues hora de entrega
Este es el proyecto final del curso, por lo que se espera que usted ponga mucho esfuerzo en hacerlo, para demostrar los conocimientos que adquirio durante todo el semestre. El proposito del proyecto es que usted aplique todos los conceptos aprendidos y desarrolle un software completo utilizando como herramienta el lenguaje de programacion Java.
Ademas de implementar lo que se le pida, debe cumplir con las siguientes especificaciones para que su proyecto sea considerado como valido:

El lenguaje de programacion a utilizar para la implementacion debe ser Java.
Por el tipo de proyecto DEBE implementar una interfaz grafica. Para este proyecto se le pide que utilice JFrames, NO APPLETS.
Debe utilizar la o las estructuras de datos vistas en clase que mas le ayuden a guardar los datos requeridos. DEBE de utilizar las clases proveidas por Java para esto.
Para la conservacion de los datos ingresados durante la ejecucion del programa, usted tiene que utilizar una base de datos, de tal forma que cuando se vuelva a ejecutar ya utilice datos guardados en ella. Se le explicara mas adelante como la debe utilizar.
Su comunicacion debe ser cliente-servidor-cliente, no puede ser solo cliente-cliente
En este proyecto consiste en implementar una aplicacion (cliente-servidor) para un servicio de E-mail. Su proyecto, en general, debe permitir a un usuario, mandar emails a contactos que pertenezcan al mismo servidor, y a contactos en otros servidores.
La aplicacion estara dividida en un programa cliente, y un programa servidor, los cuales se comunicaran entre si, utilizando un protocolo de comunicacion definido por nosotros. Dicha comunicacion, solo sera permitida entre cliente y servidor, y/o servidor y servidor.

Programa Cliente:
El programa cliente es el encargado de brindar una interfaz al usuario, en la cual pueda hacer las siguientes tareas: 
Manejo de Contactos: El usuario debe ser capaz de ingresar contactos nuevos (para hacerlo mas simple, solo se guarda el email de el contacto), y ver la lista de contactos que tiene.
Lectura de emails: El usuario debe ser capaz de poder leer los emails que le han enviado. Tanto los emails nuevos, como los ya leidos anteriormente. 
Envio de emails: El usuario debe ser capaz de enviar emails a cualquiera de sus contactos. El email puede tener uno o mas recipientes (to recipients).
Programa Servidor: 
El programa servidor es el encargado de proveer el servicio de envio de emails, entre usuarios del mismo servidor, y desde/hacia usuarios de otros servidores. Dado que es una aplicacion cliente-servidor, para que un programa cliente envie un email, lo debe hacer atravez de el servidor (solicitandole al servidor que envie el email al respectivo contacto), el cual se comunicara con el programa cliente o servidor respectivo y enviara la informacion.

Cada grupo tendra que implementar un programa cliente, y un programa servidor para esta aplicacion. Los grupos se dividiran en dos subgrupos, un subgrupo se encargara de hacer el cliente, y otro subgrupo se encargara de hacer el servidor. 
Uno de los objetivos de el proyecto, es que cualquier cliente perteneciente a cualquier servidor debe poder interactuar (mandar emails) hacia cualquier otro cliente perteneciente a cualquier otro servidor. Para esto, definimos un protocolo de comunicacion, tanto para el programa cliente, como para el programa servidor.

Especificaciones y Protocolo de comunicacion:

A continuacion se describe el protocolo completo de comunicacion de un cliente y un servidor. Asegurese de seguirlo al pie de la letra para que pueda comunicarse sin problema con cualquier otro servidor o cliente. Dentro de la definicion de el protocolo, tambien se le dan especificaciones de la funcionalidad que se requiere en el proyecto.

CLIENTE:

La comunicacion entre cliente y servidor se hara a base de comandos escritos en hileras de caracteres. Dado que su servidor o cliente debe poder comunicarse con el servidor o cliente de otro grupo, deben asegurarse de mandar por la red Strings, y no otro tipo de dato. Los comandos del cliente al servidor son descritos en esta parte. Puede que describamos algunos comandos de respuesta del servidor, asi que este atento a tomar nota.

LOGIN:
La primera tarea que debe dejar hacer el programa cliente a la hora de ejecutarse es permitir al usuario hacer login al servidor. El cliente debe leer del usuario el username, el nombre del servidor al que se va a comunicar y su password (tome en cuenta que el password no puede ser visto por el usuario). El usuario y servidor deben ser ingresados por el usuario de la siguiente manera:


	usuario@servidor
El cliente debe separar este String en dos cosas: el usuario y el nombre del servidor. Despues de verificar que el servidor ingresado este en su tabla de IPs de servidores, el cliente debe abrir una conexion hacia ese servidor especifico y mandarle la primera instruccion que seria la de login:

	LOGIN username password
Al recibir este comando, el servidor debe realizar una serie de operaciones:
(1) Primero debe verificar que el usuario (username) se encuentre en su tabla de usuarios. Si no se encuentra, el server debe responder con un error. (lo explicaremos mas tarde)
(2) Ya que se verifico que el usuario exista, se compara el password.
(3) Si el password es valido entonces se manda al cliente el siguiente acknowledge:

	OK LOGIN 
para hacerle ver al cliente que ese usuario existe y que su login esta autorizado, y se marca al usuario como "Logged In"
(4) Si el password es no valido, el server debe responder con un error (los explicaremos en la parte de errores de el server)
Despues de recibir el OK LOGIN del servidor, el cliente debe solicitar la lista de contactos del usuario (la cual esta guardada en el servidor) con la instruccion:

	CLIST username
El server debe responder con una o varias instrucciones, ya que es una lista de uno o varios contactos. Cuando se manda un contact que NO es el ultimo en la lista el server manda una instruccion de la forma :

	OK CLIST contact@server
donde contact es el username del contact, y server el server al que pertenece el contact.
Si el contact es el ultimo en la lista, entonces la instruccion seria:

	OK CLIST contact@server *
con el " * " el cliente sabria que ahi se termino toda la contact list.
Luego de obtener el contact list, se deben solicitar los emails nuevos, si hay alguno. Esto se hace con la instruccion:


	GETNEWMAILS username
El server respondera a este comando, con la lista de emais nuevos, o una instruccion que indique que no existen emails. Si existen emails, el servidor respondera con una instruccion de esta forma:

	OK GETNEWMAILS sender subject body
en donde sender es el email de el contacto o usuario que mando el email, subject es el titulo del email, y body es el cuerpo del email, el texto asociado al email. Dado que pueden haber mas de un nuevo email, para indicar que el email es el ultimo en la lista, se le agrega un "*" al final de la instruccion.

	OK GETNEWMAILS sender subject body *
En el caso de que no hayan emails nuevos, el servidor respondera con la instruccion:

	OK GETNEWMAILS NOMAILS
Algo importante, es que la fase de login, no es el unico momento en el que GETNEWMAILS debe ser utilizado. Cada vez que el cliente quiera "refrescar" su lista de emails nuevos, debe hacer esta solicitud, el servidor no manda los emails al cliente automaticamente a la hora de recibirlos, es el cliente el que tiene que hacer esto.
Ya que el cliente obtuvo el contact list del usuario y sus emails nuevos, se termina el proceso de login del cliente.

   EJEMPLO DE LOGIN SESSION SIN ERRORES

	Client : LOGIN andrea password
	Server : OK LOGIN
	Client : CLIST andrea
	Server : OK CLIST contact1@server1 
	Server : OK CLIST contact2@server2 
	Server : OK CLIST contact3@server3 *
	Client : GETNEWMAILS andrea
	Server : OK GETNEWMAILS xxx@server1 "Hola" "Hola, como estas? Nos vemos pronto"
	Server : OK GETNEWMAILS yyy@server2 "Cumple Sofia" "Hola a todos, el cumple de Sofia va a ser en su casa, el sabado a las 8, los veo ahi. " *
ENVIO DE EMAILS:
Para mandar un email a algun contacto, el cliente debe mandar la informacion de el email al servidor, el cual despues se encargara de mandarlo al contacto o server indicado. Las instrucciones que debe mandar el cliente al servidor son las siguientes (todas tienen que ir seguidas, sin ninguna otra instruccion en medio):
Primero, el cliente debe avisar que a continuacion va a mandar datos de un email, con la siguiente instruccion:


	SEND MAIL
Luego, siguiente a esa instruccion (sin esperar respuesta), se manda el recipient o recipients del email, con las instrucciones:

	MAIL TO contact@server
	MAIL TO contact@server *
el "*" indica que ese es el ultimo recipient, o si solo hay uno, ese es el unico. Despues, deben mandarse las instrucciones de los demas datos de el email (subject y body):

	MAIL SUBJECT text
	MAIL BODY text
y por ultimo, se indica que ya se termino de mandar los datos de el email, con la instruccion:

	END SEND MAIL
a lo que el servidor debe contestar con un:

	OK SEND MAIL
si no hay problemas con los datos. Si en dado caso hay algun problema o error, el server respondera con un mensaje de error (discutiremos esto en la parte de errores del server).
NUEVOS CONTACTS:
Para agregar un nuevo contacto, el usuario debe ingresar el contacto de la forma contact@server. El cliente antes de poderlo agregar debe verificar su existencia entonces debe mandar la siguiente instruccion al servidor:


	NEWCONT contact@server
el servidor, despues de verificar que SI existe el contacto, responde al cliente con la instruccion:

	OK NEWCONT contact@server
Si en dado caso, el contacto no existe, el server devolvera una instruccion de error (se mostraran las instrucciones de error cuando se esten describiendo las instrucciones de el server).
NOOP Y LOGOUT:
Cuando el usuario se va a hacer logout del cliente, el cliente debe mandar un aviso al servidor para que el servidor cierre la sesion con ese cliente, y actualice el estado del usuario a offline:


	LOGOUT
Para que el cliente pueda cerrar la sesion, el servidor debe mandarle un mensaje:

	OK LOGOUT
y recibido este mensaje, el cliente puede cerrar la sesion. Si no lo recibo NO puede cerrar la sesion.
El NOOP es un shortcut para NOOPERATION. Esta instruccion sirve para que el servidor sepa que el cliente todavia esta vivo, es decir que el cliente todavia esta conectado. Si el servidor no recibe este NOOP cada cierto tiempo (que pasa sin que el cliente haga nada) entonces el servidor cierra la conexion, ya que no esta seguro que el cliente siga vivo. Esto es para evitar que el servidor mantenga conexiones abiertas que no son utilizadas porque el cliente murio (se desconecto). Cuando hablo de que el cliente murio es por ejemplo que la computadora por alguna razon se apago, y no dio tiempo de hacer un LOGOUT formal, entonces el cliente ya no existe, y el servidor tiene abierta esa conexion todavia, en vano. Entonces, para evitar esto el NOOP se debe mandar cada 20 segundos (20000 milisegundos) despues del ultimo comando. Cada comando que se mande o se reciba (sea cual sea) inicializa el contador del tiempo, y cuando el tiempo llegue a 20000 entonces se manda un noop:


	NOOP
el cual debe seguir con la respuesta del servidor:

	OK NOOP
para que el cliente tambien sepa que el servidor esta vivo.
SERVIDOR:

Usualmente, el servidor responde a las peticiones del cliente o de otro servidor. En esta parte le explicaremos como debe actuar el servidor con respecto a cada comando recibido ya sea del cliente o de otro servidor.

COMUNICACION CLIENTE - SERVIDOR 

LOGIN username password
Este comando es mandado por el cliente para hacer un login de un usuario especifico. Dentro de el servidor existe una tabla de usuarios existentes con su password, y el estado en el que se encuentran (online/offline). Lo primero que debe hacer el servidor es buscar si el usuario existe. Si no existe debe mandar al cliente el comando de error:


	LOGIN ERROR 101
donde 101 representa al tipo de error "unknown user".
Si el usuario si existe, entonces compara el password que se guardo en la tabla con el recibido en el comando. Si el password NO es igual entonces el servidor manda al cliente el comando de error:

	LOGIN ERROR 102
donde 102 representa al tipo de error "invalid password".
Si el password es correcto, entonces el servidor debe cambiar el estado del usuario a online y mandar al cliente la respuesta:

	OK LOGIN
que significa que el usuario ya esta ingresado como online.
CLIST username
Este es el comando de solicitud de contact list por un cliente. Al recibir el servidor el request de una contact list, debe buscar los contacts asociados con ese usuario y sus estados. Si no hay ningun contact asociado con el usuario el servidor debe mandar un comando de error:


	CLIST ERROR 103
donde 103 corresponde a "no contacts found". Notese que este no es un error como tal, es un warning. Nosotros trabajaremos errores y warnings de igual manera.
Si existe algun contact entonces el servidor debe mandar los siguientes comandos:

	OK CLIST contact@server     o

	OK CLIST contact@server *
donde contact es el username del contacto, y server es el servidor al que pertenece el contact. El "*" quiere decir que ese contacto es el ultimo en la lista.
SEND MAIL
Esta es el comando que manda el cliente cuando quiere avisarle al servidor que esta por mandar informacion de un email. El servidor debe esperar a recibir el


	END SEND MAIL
para analizar la informacion de email que el cliente mando. Una de las cosas que el servidor debe hacer, es verificar que el o los recipients existan. Si el contacto es de el mismo servidor, esto lo haria chequeando la tabla de usuarios, si el contacto es de otro servidor, tiene que preguntar a ese servidor si ese contacto existe (esto lo discutiremos en las conversaciones servidor-servidor). Si el contacto no existe, entonces debe mandar al cliente el siguiente error:

	SEND ERROR 104 contact@server 
en donde 104 es el error que indica "unknown contact". Si el servidor no existe (o no se encuentra online), entonces se manda al cliente la instruccion:

	SEND ERROR 105 contact@server 
en donde 105 indica un error de "unknown server". Ademas de esto, el server debe chequear que los datos que se mandaron esten completos, si no es asi, mandar al cliente una de estas tres intrucciones de error:

	SEND ERROR 106
	SEND ERROR 107
	SEND ERROR 108	
en donde 106 indica un error de "no recipient(s)", 107 un error de "no subject" y 108 un error de "no body".
NEWCONT contact@server
Este es el comando del cliente que nos indica que su usuario quiere agregar un nuevo contacto. Antes de agregar un nuevo contact el servidor debe asegurarse que el contact realmente exista. Si el contact es local, debe buscarlo en su tabla de usuarios, si no lo encuentra, entonces mandara un mensaje de error:


	NEWCONT ERROR 109 contact@server
que corresponde al tipo de error "contact not found".
Si el contact no es local, entonces hay que verificar con el servidor correspondiente. Si el server no existe o no esta online, hay que mandar al cliente el mensaje de error:

	NEWCONT ERROR 110 contact@server
que corresponde al tipo de error "server not found". Si el contacto existe, entonces se debe mandar un mensaje al cliente :

	OK NEWCONT contact@server
NOOP
NOOP es la instruccion de NO OPERATION solo para que el servidor sepa que el cliente todavia esta vivo. Al recibirla, el servidor debe reiniciar el contador del tiempo del cliente, y mandar una instruccion:


	OK NOOP
para avizarle a el cliente que recibio su mensaje, y para que el sepa que el servidor todavia esta vivo tambien.
COMUNICACION SERVIDOR-SERVIDOR

El servidor no solo debe comunicarse con los clientes, sino tambien con otros servidores para hacer posible el envio de emails entre usuarios que pertenecen a diferentes servidores.

Mas adelante hablaremos de como sabe el server que servidor existen y cuales son sus ips, por el momento supondremos que el servidor ya tiene una tabla con los ips y nombres de los servidores que estan online en este momento.
La comunicacion entre servidor y servidor, se reduce a envio de emails y chequeo de contactos existentes.

SEND MAIL contact@server
Este comando es enviado de un servidor al otro para indicar que va a mandar informacion acerca de un email que se necesita ser enviado al contacto que se indica en el mismo. Despues a esto, el servidor que mando el comando, debe proseguir a enviar los datos de el email (sin esperar respuesta), de la siguiente forma:


	MAIL FROM username@server
	MAIL SUBJECT text
	MAIL BODY text
y despues concluir con el comando:

	END SEND MAIL
Si no hay problema con el email, entonces el servidor debe contestar con un

	OK SEND MAIL
En el caso de que el contacto no exista, el servidor debe enviar el mensaje de error:

	SEND ERROR 201 contact@server
el cual representa el tipo de error "unknown contact". En el caso de otros errores, el servidor debe mandar una de estas posibles instrucciones de error.

	SEND ERROR 202 
	SEND ERROR 203 
	SEND ERROR 204 
En donde 202 es el tipo de error "no sender (from)", 203 es "no subject" y 204 es "no body".
El servidor que recibio la notificacion de envio de email, debe guardar el email en la base de datos, el cual sera mandado al usuario en el momento en el que el cliente lo solicite.

CHECK CONTACT contact@server
Este es el comando que envia un server a otro cuando quiere chequear que un contacto exista. Al recibir este mensaje, el servidor debe chequear sus usuarios locales, y si el contacto si existe enviar de regreso:


	OK CHECK CONTACT contact@server
Si el contacto no existe entonces mandar el warning:

	CHECK ERROR 205
el cual representa el tipo de error "unknown contact", y si el server que aparece en "contact@server" de la instruccion, no es este server, entonces se debe mandar el error:

	CHECK ERROR 206
el cual representa el tipo de error "not this server".
COMUNICACION SERVIDOR - DNS

Para que esta aplicacion funcione correctamente , y los servidores puedan conectarse entre si, va existir un programa (hecho por nosotros, no por ustedes) que va a tener la tarea de un DNS. Este programa llevara el registro de los servidores que esten "online" y sus ips, para que los servidores puedan solicitarla cuando lo necesiten.
Para llevar la tabla actualizada, se necesita que cuando el servidor empiece a ejecutarse, mande un mensaje al programa DNS (cuyo ip debe poderse ingresar en el programa servidor), avisandole que ahora esta en linea, y registrando su ip. La instruccion para esto es:


	ONLINE servername ip
a lo que el DNS le contestara un

	OK ONLINE servername
Si el servidor que manda esta instruccion, ya existe en la tabla de el DNS, entonces se modificara el ip en la tabla (si el ip que se mando en la instruccion, es diferente al que estaba en la tabla). A la hora de que el servidor se "apague", debe mandar al DNS la instruccion

	OFFLINE servername
a lo que el DNS contestara

	OK OFFLINE servername
Para obtener el ip-table de los servidores en cualquier momento, el server debe mandar al DNS una solicitud, con la instruccion:

	GETIPTABLE
a lo que el DNS contestara con un listado de servidores con sus ips, utilizando

	OK IPTABLE servername ip 	o
	OK IPTABLE servername ip *
en donde el "*" significa que ese server es el ultimo en la lista/tabla
Los errores que manejara(devolvera) el DNS son:


	ONLINE ERROR 301
Error que indica un "ip invalido".

	OFFLINE ERROR 302
Error que indica que el servidor que esta haciendo offline, no existe en la tabla (ya sea nunca hizo un online, o ya habia hecho offline antes).

	GETIPTABLE ERROR 303
Que representa un warning de que no hay servidores en la tabla.
OTRAS INDICACIONES

INVALID COMMAND
El error que cualquiera de los programas envia, si la instruccion que se le envio es invalida, debe ser:


	INVALID COMMAND ERROR
PUERTOS
El servidor debe escuchar en dos puertos diferentes, en el puerto 1400 para los clientes, y en el puerto 1500 para los servidores.
El cliente siempre escuchara en el puerto 1400. El DNS escuchara en el puerto 1200.

FUNCIONES DEL SERVIDOR
El servidor tiene como funcion principal esperar solicitudes de clientes y servidores. Sin embargo, tambien debe tener funciones para el usuario que lo esta corriendo (server administrator).
Una de las funciones que debe permitir el servidor es ingresar el ip de DNS, el cual se mantendra mientras el servidor este ejecutansose.

SOLO se pueden agregar usuarios al servidor por medio del mismo servidor, no por medio de clientes, por lo que debe tener esta opcion.

Tambien debe tener la funcion de poder cambiar los puertos en donde se escucha.

LOG DE COMANDOS:
Tanto el servidor como el cliente tiene que escribir los comandos que manda y escucha en la consola.

INTERFAZ GRAFICA:
Tanto el programa cliente, como el programa servidor deben tener interfaz grafica.

BASE DE DATOS: 
Dado que el servidor debe mantener datos como: usuarios, emails, tabla de servidores, etc, usted DEBE de utilizar una base de datos para guardar estos. De tal forma que cuando el servidor se ejecute, lea los datos de dicha base de datos. El cliente deberia utilizar esta base de datos para guardar los emails (ya leidos de el servidor) por usuarios. 
La base de datos que usted puede utilizar es SQLite (http://www.sqlite.org/). Para el manejo de la base de datos se le provee la clase DB, la cual puede encontrar en este paquete. Este paquete contiene:

La clase DB, en el archivo DB.class
El Javadoc para esta clase (API)
Una clase y una base de datos prueba.
Para poder utilizar la clase, revise el API y la clase de prueba.
Si usted desea utilizar otra base de datos, puede hacerlo, utilizando otra libreria que le ayude a manejar lo puede hacer

PUNTOS EXTRA:
Los puntos extra los dejo a su criterio. Nosotros decidiremos si su trabajo extra merece puntos o no, asi que sea creativo.

ENTREGA:

Su proyecto es entregado por medio del GES y usted solo debe entregar un archivo llamado pj3-grupoN.zip (donde N es el numero de grupo que se les asigno). Dentro de ese archivo .zip deben ir todos los documentos y archivos que se le piden a continuacion, incluyendo los archivos/documentos extra que usted utilizo para la implementacion de su proyecto.

La entrega del proyecto sera personal, el dia que se les indico deben presentarse en la Universidad todos los integrantes del grupo, y traer lo siguiente:

Su proyecto, es decir, todos los archivos .java que utilizo en el mismo. Debe venir preparado para mostrar el funcionamiento del mismo en red. (Computadoras, cables de red, etc) Cada uno de los procedimientos y funciones de su codigo deben estar debidamente comentariados. Asi como cada clase. (esto no quiere decir que excesivamente comentariadas)
Un documento impreso en donde explique como implemento su proyecto, herramientas que utilizo, y todas las estructuras de datos que utilizo, en donde y para que.
Un manual de usuario en donde indicara como utilizar su cliente y su servidor.
GRUPOS:
Para recibir numero de grupo debe mandar un email a cc2fisicc@galileo.edu (o al correo de su profesor de curso), indicando los nombres de sus integrantes, de que seccion son, e indicando tambien quienes son los encargados del cliente, y quienes del servidor.
