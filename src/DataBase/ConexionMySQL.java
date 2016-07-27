/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataBase;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class ConexionMySQL {

    public String db = "sisdi_db";    

    public String url =   "jdbc:mysql://"+ReadFile()+":3306/" + db; ///IP Configurable

    public String user = "root";
    public String pass = "";
    public ConexionMySQL() {
        ReadFile();
    }

    public String ReadFile() {
        String nombreFichero = "IP.txt";
//Declarar una variable FileReader
        String Mensaje = "";
        FileReader fr = null;
        try {
            //Abrir el fichero indicado en la variable nombreFichero
            fr = new FileReader(nombreFichero);
            //Leer el primer carácter
            //Se debe almacenar en una variable de tipo int
            int caract = fr.read();
            //Se recorre el fichero hasta encontrar el carácter -1
            //   que marca el final del fichero
            while (caract != -1) {
                //Mostrar en pantalla el carácter leído convertido a char
//                System.out.print((char) caract);
                Mensaje =Mensaje + ((char) caract)+"";
                //Leer el siguiente carácter
                caract = fr.read();
            }
        } catch (FileNotFoundException e) {
            //Operaciones en caso de no encontrar el fichero
            JOptionPane.showMessageDialog(null, "El archivo de configuracion ha sido eliminalo");
            System.out.println("Error: Fichero no encontrado");
            //Mostrar el error producido por la excepción
            System.out.println(e.getMessage());
        } catch (Exception e) {
            //Operaciones en caso de error general
            JOptionPane.showMessageDialog(null, "Se ha eliminado el contenido del archivo de sistema");
            System.out.println("Error de lectura del fichero");
            System.out.println(e.getMessage());
        } finally {
            //Operaciones que se harán en cualquier caso. Si hay error o no.
            try {
                //Cerrar el fichero si se ha abierto
                if (fr != null) {
                    fr.close();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "El archivo de configuracion ha sido cambiado Error 0x002INS");
                System.out.println(e.getMessage());
            }
        }
        return Mensaje;

    }

    public Connection Conectar() {
        Connection link = null;
        try {
            //Cargamos el Driver MySQL
            Class.forName("org.gjt.mm.mysql.Driver");
            //Creamos un enlace hacia la base de datos
            link = DriverManager.getConnection(this.url, this.user, this.pass);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERROR AL CONECTAR A LA BASE DE DATOS: \n"
                    + "Verifique que el servidor este encendido.\n"
                    + "Verifique que este conectado a la RED LOCAL\n"
                    + "Si el error persiste reinicie la computadora\n"
                    + "Se ha generado un BUG\n"
                    + "Error presentado: " + e);
//             System.exit(0);
        }
        return link;
    }
}
