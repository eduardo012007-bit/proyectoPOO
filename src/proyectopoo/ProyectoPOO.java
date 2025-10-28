package proyectopoo;
    import java.sql.Statement;
    import java.sql.ResultSet;
    import java.io.FileWriter;
    import java.io.PrintWriter;
    import java.io.IOException;
    import java.util.Scanner;
    import java.io.PrintStream;
    import java.nio.charset.StandardCharsets;
    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.SQLException;
public class ProyectoPOO {
    public Connection getConexion(){
        //Conexión a la base de datos
        String conectionURL=
                 "jdbc:sqlserver://localhost:1433;"
                +"database=proyecto_POO;"
                +"user=userPOO;"
                +"password=root;"
                +"timeout=30;"
                +"encrypt=true;trustServerCertificate=true";
        try{
            Connection con = DriverManager.getConnection(conectionURL);
            return con;
        }catch(SQLException ex){
            System.out.println("ERROR DE CONEXION: " + ex.getMessage());
            return null;
        }
    }
    public static boolean verRUC(String RUC, Connection conn){
        //variale numérica temporal que valida que  RUC solo tenga números
        long ruc;
        String consulta = "select RUC_Cliente from cliente where RUC_Cliente = '" + RUC + "'";
        //Verifica que RUC tengo 11 dígitos
        if(RUC.length() != 11){
            System.out.println("RUC inválido, el valor ingresado debe ser de 11 dígitos");
            return false;
        }
        try{
            ruc = Long.parseLong(RUC);
            //Si el número es menor a 0, el RUC es inválido
            if(ruc<0){
                System.out.println("RUC inválido, debe ser un númeor positivo");
                return false;
            }
            //Verifica que el RUC empieze con 10 0 20
            if((ruc>=10000000000L && ruc<=10999999999L) || (ruc>=20000000000L && ruc <=20999999999L)){
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(consulta);
                //Verifica qu el RUC sea único en la tabla de la empresa
                if(rs.next()){
                    System.out.println("El RUC ingresado ya existe");
                    rs.close();
                    stmt.close();
                    return false;
                }else{
                    rs.close();
                    stmt.close();
                    return true;
                }
            }else{
                return false;
            }
        }catch(Exception e){
            //Si la conversion de un texto a númeor falla, retorna falso
            System.out.println("RUC inválido, solo se permiten números");
            return false;
        }
    }
    public static boolean verRUC(String RUC){
        if (RUC.length() != 11) {
                return false;
            }
            try {
            long verRuc = Long.parseLong(RUC);
            // Rangos válidos para RUC peruano:
            // 10,000,000,000 - 10,999,999,999 (personas naturales)
            // 20,000,000,000 - 20,999,999,999 (empresas)
            return (verRuc >= 10000000000L && verRuc <= 10999999999L) ||
                    (verRuc >= 20000000000L && verRuc <= 20999999999L);
            } catch (NumberFormatException e) {
                //Error sale cuando el ruc ingresado contiene letras
                System.out.println("ERROR: El RUC debe contener solo números");
                return false;
            }
    }
    //método para verificar el DNI que inserta el usuario
    public static boolean verDNI(String DNI, Connection conn){
        //se usa un variable numérica para verificar que el DNI solo contenga números
        long dni;
        //consulta de SQL server que valida que el DNI sea único en la tabla de la base de datos
        String consulta = "select DNI from cliente where DNI = '" + DNI + "'";
        try{
            //se verifica la longitud del DNI ingresado, pues este debe contener 8 dígitos
            if(DNI.length() != 8){
                System.out.println("DNI inválido, el dni debe tener 8 dígitos");
                return false;
            }
            //se convierte el DNI ingresado de tipo String a long, pues, si no se puede,
            //significa que contiene letras, o que no se puede
            dni = Long.parseLong(DNI);
            if (dni<0){
                System.out.println("DNI inválido, debe ser positivo");
                return false;
            }
            //ejecutamos la consulta SQL, si hay un resultado, significa que el número de DNi ya está registrado
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(consulta);
            //Verifica si el DNI ingresado ya existe
            if(rs.next()){
                System.out.println("DNI inválido, ya existente");
                rs.close();
                stmt.close();
                return false;
            }else{
                rs.close();
                stmt.close();
                return true;
            }
        }catch(Exception e){
            //Si la conversión de texto a número falla, retorna falso
            System.out.println("DNI inválido, solo se permiten números");
            return false;
        }
    }
    public static boolean verDNI(String DNI){
        long dni;
        //se verifica la longitud del DNI ingresado, pues este debe contener 8 dígitos
        if(DNI.length() != 8){
            System.out.println("DNI inválido, el dni debe tener 8 dígitos");
            return false;
        }
        //se convierte el DNI ingresado de tipo String a long, pues, si no se puede,
        //significa que contiene letras, o que no se puede
        try{
            dni = Long.parseLong(DNI);
            if (dni<0){
                System.out.println("DNI inválido, debe ser positivo");
                return false;
            }
        }catch(Exception e){
            System.out.println("El DNI debe contener solo números");
            return false;
        }
        return true;
    }
    public static boolean verTexto(String texto){
        //Valida que cada caracter del texto no sea un valor numérico
        //Verifica que el texto ingreso no contenga números entre sus carácteres
        for(int i = 0; i<texto.length(); i++){
            char c = texto.charAt(i);
            if(Character.isDigit(c)){
                System.out.println("Dato inválido, no puede contener números");
                return false;
            }
        }
        // Verifica que el texto ngresado no contenga caraccteres especiales, solo letras, mayúsculas, minúsculas y tildes
        if (texto.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ]+")) {
            return true;
        } else {
            System.out.println("Solo se premmiten letras, no caracteres especiales");
            return false;
        }
    }
    public static boolean buscarCliente(Connection conn, Scanner lector, int id, String consulta, int opc){
        String texto="";
        do{
            //Menú de selección de opción de búsqueda
            System.out.println("######################################");
            System.out.println("## SELECCIONE LA OPCIÓN DE BÚSQUEDA ##");
            System.out.println("######################################");
            System.out.println("## 1. NOMBRE                        ##");
            System.out.println("## 2. RUC                           ##");
            System.out.println("## 3. DNI                           ##");
            System.out.println("## 4. ID_CLIENTE                    ##");
            System.out.println("## 5. SALIR                         ##");
            System.out.println("efcfwrevw");
            System.out.println("######################################");
            System.out.print("Seleccione la opción de busqueda: ");
            opc=lector.nextInt();
            //Si la opción elegida está fuera de rango, el ingreso se repite
        }while(opc<1 || opc>5);
        lector.nextLine();
        //La consulta de seleciión del cliente cambia según la opción elegida anteriormente
        switch(opc){
            case 1:
                do{
                    System.out.println("Ingrese el nombre del cliente a buscar");
                    texto = lector.nextLine().toUpperCase();
                }while(!verTexto(texto));
                consulta = "select * from cliente where nombre_Cliente = '" + texto +"'";
                break;
            case 2:
                do{
                    System.out.println("Ingrese el RUC del cliente a buscar");
                    texto = lector.nextLine();
                }while(!verRUC(texto));
                consulta = "select * from cliente where RUC_Cliente = '"+ texto + "'";
                break;
            case 3:
                do{
                    System.out.println("Ingrese el DNI del cliente que desee buscar");
                    texto = lector.nextLine();
                }while(!verDNI(texto));
                consulta = "select * from cliente where DNI = '" + texto + "'";
                break;
            case 4:
                System.out.println("Ingres el id del cliente");
                id=lector.nextInt();
                consulta = "select * from clientes where id_Cliente = " + id;
                break;
            case 5:
                System.out.println("Acción cancelada");
                return true;
        }
        System.out.println("Buscando cliente...");
        try{
            Statement stmt = conn.createStatement();
            //Se ejecuta la consulta y se muestra el o los clientes encontrados que cumplan 
            //con las características de seleciión de búsqueda
            ResultSet rs= stmt.executeQuery(consulta);
            while(rs.next()){
                String nombre, apellido, RUC, DNI;
                id = rs.getInt("id_Cliente");
                nombre = rs.getString("nombre_Cliente");
                apellido = rs.getString("apellido_Cliente");
                RUC = rs.getString("RUC_Cliente");
                DNI = rs.getString("DNI");
                System.out.printf(
                        "id_Cliente: %d\n"
                        + "nombre_Cliente: %s \n"
                        + "apellido_Cliente: %s \n"
                        + "RUC_Cliente: %s \n"
                        + "DNI: %s\n"
                        + "--------------------------\n", id, nombre, apellido, RUC, DNI);
            }
            //Se ingresa el id del cliente que corresponda con las opciones mostradas anteriormente
            System.out.println("Ingrese el id del cliente a eliminar(guíese de las búsquedas anteriores)");
            id=lector.nextInt();
            //La consulta cambia según la opción elegida
            switch(opc){
                case 1:
                    consulta = "select * from cliente where id_Cliente = " + id + " and nombre_Cliente = '" + texto +"'";
                    break;
                case 2:
                    consulta = "select * from cliente where id_Cliente = " + id + " and RUC_Cliente = '" + texto +"'";
                    break;
                case 3:
                    consulta = "select * from cliente where id_Cliente = " + id + " and DNI = '" + texto +"'";
                    break;
            }
            //Se ejecuta la consulta creada anteriormente
            rs=stmt.executeQuery(consulta);
            //Si no existe resultado en la base de datos, el id_Cliente ingresado es incorrecto
            if(!rs.next()){
                System.out.println("El id_Ciente ingresado es incorrecto, vuelva a realizar la búsqueda");
                return false;
            }else{
                return true;
            }
        }catch(SQLException e){
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }
    public static void agregarCliente(Connection conn, int opc, Scanner lector){
        lector.nextLine();
        String consulta, nombre, apellido, DNI, RUC, direccion;
        do{
            //Ingreso de valores para el cliente
            do{
                //Ingreso de DNI, si el cliente no lo posee, se ingresa 0
                System.out.print("Ingrese el DNI del cliente(Ingrese 0 si no tiene): ");
                DNI = lector.nextLine();
                if(DNI.equals("0")){
                    break;
                }
                //Repite el ingreso del DNi según el resultado de la función verDNI
            }while(!verDNI(DNI, conn));
            do{
                //Ingreso del RUC, si el cliente no posee uno, se ingresa 0
                System.out.print("Ingres el RUC del cliente (Ingrese 0 si no tiene): ");
                RUC = lector.nextLine();
                if(RUC.equals("0")){
                    break;
                }
                //Repite el ingreso del RUC segun el resultado de la función verRUC
            }while(!verRUC(RUC, conn));
            if(DNI.equals("0") && RUC.equals("0")){
                System.out.println("El cliente debe tener DNI o RUC, sino es inválido");
            }
            //El ingreso se repite si el cliete no posee RUC ni DNI
        }while(DNI.equals("0") && RUC.equals("0"));
        //Ingreso de la dirección del cliente
        System.out.print("Ingrese la dirección del cliente: ");
        direccion = lector.nextLine().toUpperCase();
        do{
            //Ingreso del nombre del cliente
            System.out.print("Ingrese el nombre del cliente: ");
            nombre = lector.nextLine().toUpperCase();
            //el ingreso del nombre se repite según el resultado de la función verTexto
        }while(!verTexto(nombre));
        do{
            //Ingreso del apellido del cliente
            System.out.print("Ingrese apellido del cliente: ");
            apellido = lector.nextLine().toUpperCase();
            //El ingreso del apellido serepite según el resultado de la función verTexto
        }while(!verTexto(apellido));
        try{
            //La consulta a ejecutar cambia dependiendo si el cliente tiene solo RUC, DNI o ambos
            if(RUC.equals("0")){
                consulta = "insert into cliente (DNI, dirección_Cliente, nombre_Cliente, apellido_Cliente)" +
                           "values('" + DNI + "', '" + direccion + "', '" + nombre + "', '" + apellido + "');";
            }else if(DNI.equals("0")){
                consulta = "insert into cliente (RUC_Cliente, dirección_Cliente, nombre_Cliente, apellido_Cliente)" +
                           "values('" + RUC + "', '" + direccion + "', '" + nombre + "', '" + apellido + "');";
            }else{
                consulta = "insert into cliente (RUC_Cliente, DNI, dirección_Cliente, nombre_Cliente, apellido_Cliente)" +
                           "values('" + RUC + "', '" + DNI + "', '" + direccion + "', '" + nombre + "', '" + apellido + "');";
            }
            Statement stmt = conn.createStatement();
            //La consulta creada se ejecuta
            stmt.executeUpdate(consulta);
            //Mensaje de confirmación para agregar registro de cliente
            System.out.println("Cliente agregado exitosamente");
        }catch(SQLException e){
            //Mensaje de error si el registro falla, e impresion de tipo de error
            System.err.println("Error: " + e.getMessage());
            System.out.println("Error tipo 1: Falla en agregar cliente");
        }
    }
    public static void eliminarCliente(Connection conn, Scanner lector, int opc){
        String consulta="";
        int id=0;
        try{
            Statement stmt = conn.createStatement();
            if(buscarCliente(conn, lector, id, consulta, opc)){
                //Si existe el resultado, se ejecuta la consulta para eliminar al cliente
                consulta = "delete cliente where id_Cliente = " + id;
                stmt.executeUpdate(consulta);
            }
        }catch(SQLException e){
            System.err.println("Error: " + e.getMessage());
        }
    }
    public static void modificarCliente(Scanner lector, Connection conn, int opc){
        //variables utilizadas
        String consulta="", texto, DNI, RUC, nom, apel, direc, valNue;
        int id=0;
        if(buscarCliente(conn, lector, id, consulta, opc)){
            try{
                do{
                    //menú de selección para modificar la información de un cliente
                    System.out.println("##############################");
                    System.out.println("## OPCIONES DE MODIFICACIÓN ##");
                    System.out.println("##############################");
                    System.out.println("## 1. NOMBRE                ##");
                    System.out.println("## 2. APELLIDO              ##");
                    System.out.println("## 3. RUC                   ##");
                    System.out.println("## 4. DNI                   ##");
                    System.out.println("## 5. DIRECCIÓN             ##");
                    System.out.println("##############################");
                    opc=lector.nextInt();
                }while(opc<1 || opc>5);
                //Búsqueda del cliente que se desee modificar
                System.out.println("Ingrese el id del cliente a modificar");
                switch(opc){
                    case 1:
                        consulta = "select * from ";
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                }
            }catch(SQLException e){
                    System.err.println("Error: " + e.getMessage());
                    }
        }
    }
    public static void main(String[] args) {
        ProyectoPOO app = new ProyectoPOO();
        //Línea de código que permite el uso de tildes y signos del español
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        Connection conn = app.getConexion();
        //Escaner que permite que el usuario pueda ingresar valores
        Scanner lector=new Scanner(System.in);
        //Variable usada para la selección en los menús del proyecto
        int opc;
        //prueba de la conexión a la base de datos
        if (conn != null) {
            System.out.println("Conexión exitosa con la base de datos.");
        } else {
            System.out.println("No se pudo establecer conexión con la base de datos.");
        }
        do{
            do{
                //Menú de opciones inicial
                System.out.println("##############################");
                System.out.println("##     ELIJA UNA OPCIÓN     ##");
                System.out.println("##############################");
                System.out.println("##  1. CLIENTES             ##");
                System.out.println("##  2. FACTURAS             ##");
                System.out.println("##  3. PRODUCTO             ##");
                System.out.println("##  4. SALIR                ##");
                System.out.println("##############################");
                System.out.print("Seleccione una opción: ");
                opc=lector.nextInt();
                //Verifica que la opción es válida
                if(opc<1 || opc>4){
                    System.out.println("La opcion elegida es inválida (1-4)");
                }
            }while(opc<1 || opc>4);
            switch (opc){
                case 1:
                    do{
                        do{
                            //Menú de la base Clientes
                            System.out.println("#############################");
                            System.out.println("##     ELIJA LA ACCIÓN     ##");
                            System.out.println("#############################");
                            System.out.println("##  1. AGREGAR CLIENTE     ##");
                            System.out.println("##  2. ELIMINAR CLIENTE    ##");
                            System.out.println("##  3. MODIFICAR CLIENTE   ##");
                            System.out.println("##  4. REPORTE             ##");
                            System.out.println("##  5. SALIR               ##");
                            System.out.println("#############################");
                            System.out.print("Seleccione una acción: ");
                            opc=lector.nextInt();
                            //Verifica que la opción es válida
                            if(opc<1 || opc>5){
                                System.out.println("Opción inválida, elija opción válida (1-5)");
                            }
                        }while(opc<1 || opc>5);
                        switch(opc){
                            case 1:
                                agregarCliente(conn, opc, lector);
                                break;
                            case 2:
                                eliminarCliente(conn, lector, opc);
                                opc=2;
                                break;
                            case 3:
                                modificarCliente(lector, conn, opc);
                                break;
                            case 4:
                                break;
                            case 5:
                                System.out.println("Saliendo de la base de clientes...");
                                break;
                        }
                    } while(opc!=5);
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    System.out.println("Saliendo...");
                    break;
            }
        }while(opc!=4);
        lector.close();
    }

}
