/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Consultores;

import Catalogos.JPanelConsultorCatalog;
import Catalogos.JPanelConsultoriasCatalog;
import Catalogos.JPanelProyectosCatalog;
import Catalogos.JPanelServiciosCatalog;
import Catalogos.JPanelServidoresCatalog;
import DataBase.ConexionMySQL;
import com.mysql.jdbc.Statement;
import java.awt.Color;
import java.awt.Frame;
import java.awt.MouseInfo;
import java.awt.Point;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author C0nacyt
 */
public class JPanelUpdateNewConsultor extends javax.swing.JDialog {

    public static String IDService;
    int x, y;
    String ID_Busqueda, accion, accionService, ID_regCons, accionServer;
    DefaultTableModel TABLARegistroConsultor = new DefaultTableModel();
    DefaultTableModel TABLAServicios = new DefaultTableModel();
    DefaultTableModel TABLAServidores = new DefaultTableModel();
    boolean statusRegistro = true;

    /**
     * Creates new form Muestra
     *
     * @param parent
     * @param modal
     * @param ID
     */
    public JPanelUpdateNewConsultor(java.awt.Frame parent, boolean modal, String ID) {
        super(parent, modal);
        initComponents();
//        Shape forma = new RoundRectangle2D.Double(0, 0, this.getBounds().width, this.getBounds().height, 30, 30);
//        AWTUtilities.setWindowShape(this, forma);
        this.setLocationRelativeTo(null);

        cargaDatos();
        ID_Busqueda = ID;
        if (ID_Busqueda.equals("")) {
            accion = "NEW";
            lblTitulo.setText("Ingresar nuevo consultor");
            clearBox();
        } else {
            CargarRegistro(ID_Busqueda);
            accion = "UPDATE";
//            btnGuararUpdateConsultor.setText("Actualizar");
            lblTitulo.setText("Modificar Consultor");
        }

    }

    ////-----> CARGAR DATOS <-------//////
    private void cargaDatos() {
        //CARGAR LOS VALORES DE LAS LISTAS
        CBOConsultorias.removeAllItems();
        CBOProyectos.removeAllItems();
        CBOServicios.removeAllItems();
        CBOServidores.removeAllItems();

        //CONSULTA DE LOS REGISTROS DE 
        /*
         SERVIDORES
         SERVICIOS
         PROYECTOS
         CONSULTORIAS
         */
        String sSQL1 = "SELECT nombre_serv          FROM `server`       WHERE estatus_server = '1'      ORDER BY `server`.`nombre_serv` ASC";
        String sSQL2 = "SELECT nombre_s             FROM `servicio`     WHERE estatus_servicio = '1'    ORDER BY `servicio`.`nombre_s` ASC";
        String sSQL3 = "SELECT sigla_proyecto       FROM `proyecto`     WHERE estatus_proyecto ='1'     ORDER BY `proyecto`.`sigla_proyecto` ASC";
        String sSQL4 = "SELECT nombre_consultoria   FROM `consultoria`  WHERE estado_consultoria = '1'  ORDER BY `consultoria`.`nombre_consultoria` ASC";

        String[] Nombre1 = new String[1];
        String[] Nombre2 = new String[1];
        String[] Nombre3 = new String[1];
        String[] Nombre4 = new String[1];

        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        try {
            Statement st1 = (Statement) cn.createStatement();
            Statement st2 = (Statement) cn.createStatement();
            Statement st3 = (Statement) cn.createStatement();
            Statement st4 = (Statement) cn.createStatement();

            ResultSet rs1 = st1.executeQuery(sSQL1);
            ResultSet rs2 = st2.executeQuery(sSQL2);
            ResultSet rs3 = st3.executeQuery(sSQL3);
            ResultSet rs4 = st4.executeQuery(sSQL4);
            /*
             SERVIDORES     ->1
             SERVICIOS      ->2
             PROYECTOS      ->3
             CONSULTORIAS   ->4
             */
//            CBOServidores.addItem("-");
//            CBOServicios.addItem("-");
//            CBOProyectos.addItem("-");
//            CBOConsultorias.addItem("-");
            while (rs1.next()) {
                Nombre1[0] = rs1.getString("nombre_serv");
                CBOServidores.addItem(Nombre1[0]);
            }
            while (rs2.next()) {
                Nombre2[0] = rs2.getString("nombre_s");
                CBOServicios.addItem(Nombre2[0]);
            }
            while (rs3.next()) {
                Nombre3[0] = rs3.getString("sigla_proyecto");
                CBOProyectos.addItem(Nombre3[0]);
            }
            while (rs4.next()) {
                Nombre4[0] = rs4.getString("nombre_consultoria");
                CBOConsultorias.addItem(Nombre4[0]);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No es posible cargar la informacion:\n ERROR: " + ex);
        }
    }
    ////-----> CARGAR DATOS <-------//////

    ////-----> OTROS METODOS <-------//////
    private void clearBox() {
        txtBuscar.setText("Search...");
        txtID.setText("AU");
        txtNom.setText("");
        txtApP.setText("");
        txtApM.setText("");
        txtMail.setText("");
        CBOProyectos.setSelectedIndex(0);
        CBOConsultorias.setSelectedIndex(0);
        txteMail.setText("");

//        
        Calendar dateToday = new GregorianCalendar();
        JDateAlta.setCalendar(dateToday);
        JDateBaja.setCalendar(dateToday);
//
//        btnGuararUpdateConsultor.setText("Guardar");
    }

    private void CargarRegistro(String ID_Busqueda) {
        int conConsultor = 0;
        String sSQL1 = "SELECT "
                + "ID_consultor, "
                + "nombre_c, "
                + "apellidoP_c, "
                + "apellidoM_c, "
                + "correo_c "
                + "FROM consultor WHERE  ID_consultor = " + ID_Busqueda + "";

        String sSQL2 = "SELECT "
                + "reg_consultor.ID_reg_cons, "
                + "reg_consultor.correo_registro, "
                + "proyecto.sigla_proyecto, "
                + "consultoria.nombre_consultoria, "
                + "reg_consultor.f_ingreso, "
                + "reg_consultor.f_baja,  "
                + "reg_consultor.estatus_reg_cons "
                + "FROM reg_consultor,proyecto,consultoria  "
                + "WHERE ID_consultor = " + ID_Busqueda + " "
                + "AND reg_consultor.ID_proy = proyecto.ID_proyecto "
                + "AND reg_consultor.ID_consultoria = consultoria.ID_consultoria "
                + "ORDER BY reg_consultor.ID_reg_cons ASC";
//        System.out.print("LLAMANDO A LOS RGISTROS DE ESTE CONSULTOR: " + sSQL2 + "\n");
        String[] Datos1 = new String[10];
        String[] Datos2 = new String[10];

        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        try {
            Statement st1 = (Statement) cn.createStatement();
            Statement st2 = (Statement) cn.createStatement();

            ResultSet rs1 = st1.executeQuery(sSQL1);
            ResultSet rs2 = st2.executeQuery(sSQL2);

            //Cargando la informacion en las caja
            while (rs1.next()) {
                //ID_consultor, nombre_c, apellidoP_c, apellidoM_c, correo_c
                Datos1[0] = rs1.getString("ID_consultor");
                Datos1[1] = rs1.getString("nombre_c");
                Datos1[2] = rs1.getString("apellidoP_c");
                Datos1[3] = rs1.getString("apellidoM_c");
                Datos1[4] = rs1.getString("correo_c");
                conConsultor++;
            }

            if (conConsultor == 0) {
                JOptionPane.showMessageDialog(null, "El consultor esta Inactivo");

            } else {
                /*CARGANDO INFORMACION A LAS TextFiel*/
                txtID.setText(Datos1[0]);
                txtNom.setText(Datos1[1]);
                txtApP.setText(Datos1[2]);
                txtApM.setText(Datos1[3]);
                txtMail.setText(Datos1[4]);
                /*CARGANDO INFORMACION A LAS TextFiel*/
                int Cont = 0;
                //reg_consultor.ID_reg_cons, reg_consultor.correo_registro, proyecto.sigla_proyecto, consultoria.nombre_consultoria,  
                //reg_consultor.f_ingreso, reg_consultor.f_baja,  reg_consultor.estatus_reg_cons
                String[] titulos = {"ID", "EMAIL", "PROY.", "CONSUL.", "FECHAS", "ESTATUS"};
                TABLARegistroConsultor = new DefaultTableModel(null, titulos);
                String mensajeFecha = "";
                while (rs2.next()) {
                    Datos2[0] = rs2.getString("ID_reg_cons");
                    Datos2[1] = rs2.getString("correo_registro");
                    Datos2[2] = rs2.getString("sigla_proyecto");
                    Datos2[3] = rs2.getString("nombre_consultoria");
                    if (rs2.getString("f_ingreso") == null) {
                        mensajeFecha = "SIN REGISTRO";
                    } else {
                        mensajeFecha = "del " + rs2.getString("f_ingreso");
                    }
                    if (rs2.getString("f_baja") == null) {
                        mensajeFecha = mensajeFecha + "";
                    } else {
                        mensajeFecha = "del " + rs2.getString("f_ingreso") + " al " + rs2.getString("f_baja");
                    }
                    Datos2[4] = mensajeFecha;
                    if (rs2.getString("estatus_reg_cons").equals("0")) {
                        Datos2[5] = "BAJA";
                    } else {
                        Datos2[5] = "ACTIVO";
                    }
                    TABLARegistroConsultor.addRow(Datos2);
                    Cont++;
                }
                if (Cont > 0) {
                    lblRegistrosFound.setText(Cont + " registro(s). Seleccione uno:");
                } else {
                    lblRegistrosFound.setText("No hay Registros de este consultor");
                }
                TBLRegistroConsultor.setModel(TABLARegistroConsultor);
                AjustaAnchosTabla(1);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No es posible cargar la informacion:\n ERROR: " + ex);
        }

        if (conConsultor == 0) {
            this.dispose();
        }

    }

    private void AjustaAnchosTabla(int Tabla) {
        if (Tabla == 1) {
            int[] anchos = {21, 68, 42, 57, 97, 60};
            for (int i = 0; i < TBLRegistroConsultor.getColumnCount(); i++) {
                TBLRegistroConsultor.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
            }
        }
        if (Tabla == 2) {
            int[] anchos = {21, 61, 61, 67, 61, 54, 100};
            for (int i = 0; i < TBLServicios.getColumnCount(); i++) {
                TBLServicios.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
            }
        }
        if (Tabla == 3) {
            int[] anchos = {21, 73, 123, 56, 153};
            for (int i = 0; i < TBLServidores.getColumnCount(); i++) {
                TBLServidores.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
            }
        }

    }

    ///METODOS NUEVO REGISTRO
    private void activaObjetos() {
        lblRegistroStatus.setEnabled(true);
        CBOProyectos.setEnabled(true);
        btnAddProyecto.setEnabled(true);
        txteMail.setEnabled(true);
        CBOConsultorias.setEnabled(true);
        JDateAlta.setEnabled(true);
        JDateBaja.setEnabled(true);
        btnAddConsultoria.setEnabled(true);
        CBOServicios.setEnabled(true);
        btnAddServicio.setEnabled(true);
        btnAddAllServicios.setEnabled(true);
        btnNuevoServicio.setEnabled(true);
        btnQuitarTodosServicio.setEnabled(true);
        btnGuardarUpdateRegistro.setEnabled(true);
        TBLServicios.setEnabled(true);
        TBLServidores.setEnabled(true);
        btnAddProyecto.setEnabled(true);
        btnAddConsultoria.setEnabled(true);
        CBOServidores.setEnabled(true);
        btnAddServer.setEnabled(true);
        btnNuevoServer.setEnabled(true);
        btnQuitarAllServer.setEnabled(true);
        btnAddAllServer.setEnabled(true);
        btnUseEmailActual.setEnabled(true);
        btnUseEmailActual.setEnabled(true);
        btnImprimir.setEnabled(true);
    }

    private void bloequeaObjetos() {
        lblRegistroStatus.setEnabled(false);
        CBOProyectos.setEnabled(false);
        btnAddProyecto.setEnabled(false);
        txteMail.setEnabled(false);
        CBOConsultorias.setEnabled(false);
        JDateAlta.setEnabled(false);
        JDateBaja.setEnabled(false);
        btnAddConsultoria.setEnabled(false);
        CBOServicios.setEnabled(false);
        btnAddServicio.setEnabled(false);
        btnAddAllServicios.setEnabled(false);
        btnNuevoServicio.setEnabled(false);
        btnQuitarTodosServicio.setEnabled(false);
        btnGuardarUpdateRegistro.setEnabled(false);
        TBLServicios.setEnabled(false);
        TBLServidores.setEnabled(false);
        btnAddProyecto.setEnabled(false);
        btnAddConsultoria.setEnabled(false);
        CBOServidores.setEnabled(false);
        btnAddServer.setEnabled(false);
        btnNuevoServer.setEnabled(false);
        btnQuitarAllServer.setEnabled(false);
        btnAddAllServer.setEnabled(false);
        btnUseEmailActual.setEnabled(false);
        btnImprimir.setEnabled(false);
    }

    private void limpiaObj() {
//        TABLAServicios.clear();
        CBOProyectos.setSelectedIndex(0);
        CBOConsultorias.setSelectedIndex(0);
        CBOServicios.setSelectedIndex(0);
        CBOServidores.setSelectedIndex(0);
        txteMail.setText("");

        PanelServiciosServers.setTitleAt(0, "Servicios");
        PanelServiciosServers.setTitleAt(1, "Servidores");
        lblRegistrosFoundSS1.setText("");
        lblRegistrosFoundSS2.setText("");

        lblRegistrosFoundService1.setText("");
        lblRegistrosFoundService.setText("");
        for (int i = TBLRegistroConsultor.getRowCount() - 1; i >= 0; i--) {
            TABLARegistroConsultor.removeRow(i);
        }
        for (int i = TBLServicios.getRowCount() - 1; i >= 0; i--) {
            TABLAServicios.removeRow(i);
        }
        for (int i = TBLServidores.getRowCount() - 1; i >= 0; i--) {
            TABLAServidores.removeRow(i);
        }
//        
        TBLRegistroConsultor.setModel(TABLARegistroConsultor);
        TBLServicios.setModel(TABLAServicios);
        TBLServidores.setModel(TABLAServidores);
    }

    ///METODOS NUEVO REGISTRO
    ////-----> OTROS METODOS <-------//////
    private void cambiaValor() {
        if (statusRegistro == true) {
            statusRegistro = false;
            lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/off.png")));
            lblRegistroStatus.setToolTipText("INACTIVO");
        } else {
            statusRegistro = true;
            lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/on.png")));
            lblRegistroStatus.setToolTipText("ACTIVO");
        }
    }

    protected static boolean esEmailCorrecto(String email) {
        boolean valido = false;
        Pattern patronEmail = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)(\\.[A-Za-z]{2,})$");
        Matcher mEmail = patronEmail.matcher(email.toLowerCase());
        if (mEmail.matches()) {
            valido = true;
        }
        return valido;
    }

    /* ---------> AGREGAR CONSULTAR REGISTROS DE SERVICIOS O SERVIDORES <--------------*/
    private boolean BuscarEnTabla(String nombreElemento, int noTabla) {
        boolean EXISTE = false;
        if (noTabla == 1) {
            String servicioName;
            int RowDeTabla = TBLServicios.getRowCount();
            if (RowDeTabla == 0) {
                EXISTE = false;
            } else {
                for (int i = 0; i < TBLServicios.getRowCount(); i++) {
                    servicioName = (String) TBLServicios.getValueAt(i, 1);
                    if (nombreElemento.equals(servicioName)) {
                        EXISTE = true;
                    }
                }
            }
        } else if (noTabla == 2) {
            String serverName;
            int RowDeTabla = TBLServidores.getRowCount();
            if (RowDeTabla == 0) {
                EXISTE = false;
            } else {
                for (int i = 0; i < TBLServidores.getRowCount(); i++) {
                    serverName = (String) TBLServidores.getValueAt(i, 1);
                    if (nombreElemento.equals(serverName)) {
                        EXISTE = true;
                    }
                }
            }
        }

        return EXISTE;
    }

    private void agregarServicioATabla(String nombreService, String idServicio, String idRegConsultor) {
        Frame Admimistracion = null;
        JPanelAddService PanelAdministrador;
        PanelAdministrador = new JPanelAddService(Admimistracion, true, nombreService, idServicio, accionService, idRegConsultor);
        PanelAdministrador.setVisible(true);

        //SE ACTUALIZARA LA TABLA DE SERVICIOS
        MostrarServiciosServidores(ID_regCons);
    }

    private void agregarServerATabla(String nombreServer, String idServer, String idRegConsultor) {
        Frame Admimistracion = null;
        JPanelAddServer PanelAdministrador;
        PanelAdministrador = new JPanelAddServer(Admimistracion, true, nombreServer, idServer, accionServer, idRegConsultor);
        PanelAdministrador.setVisible(true);

        //SE ACTUALIZARA LA TABLA DE SERVICIOS
        MostrarServiciosServidores(ID_regCons);
    }

    private void MostrarInformacionRegServicio(String id_regConsultor) {
        String sSQL = "SELECT reg_consultor.ID_reg_cons, reg_consultor.correo_registro,"
                + " proyecto.sigla_proyecto, consultoria.nombre_consultoria, reg_consultor.f_ingreso,"
                + " reg_consultor.f_baja, reg_consultor.estatus_reg_cons FROM reg_consultor, proyecto, "
                + "consultoria "
                + "WHERE reg_consultor.ID_proy = proyecto.ID_proyecto "
                + "AND reg_consultor.ID_consultoria = consultoria.ID_consultoria "
                + "AND reg_consultor.ID_reg_cons = " + id_regConsultor + " ;  ";
        String[] registro = new String[9];
        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        int cont = 0;
        try {
            Statement st = (Statement) cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                registro[0] = rs.getString("ID_reg_cons");
                registro[1] = rs.getString("f_ingreso");
                registro[2] = rs.getString("f_baja");
                registro[3] = rs.getString("correo_registro");
                registro[4] = rs.getString("estatus_reg_cons");
                registro[5] = rs.getString("nombre_consultoria");
                registro[6] = rs.getString("sigla_proyecto");
                cont++;
            }
            if (cont == 0) {
                JOptionPane.showMessageDialog(null, "ERROR INESPERADO :O");
            } else {
                // LLENANDO LOS CAMPOS CON LA INFORMACION CORRESPONDIENTE
                lblIDRegistro.setText("Registro: " + registro[0]);
                CBOProyectos.setSelectedItem(registro[6]);
                CBOConsultorias.setSelectedItem(registro[5]);

                txteMail.setText(registro[3]);

                if (registro[4].equals("1")) {
                    //El registro esta activo
                    statusRegistro = true;
                    lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/on.png")));
                    lblRegistroStatus.setToolTipText("ACTIVO");
                } else {
                    //el registro esta inactivo
                    statusRegistro = false;
                    lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/off.png")));
                    lblRegistroStatus.setToolTipText("INACTIVO");
                }

                //Cargando y poniendo las fechas, el el caso de que no tenga registro se omitirá esta accion
                if (registro[1] == null) {
                    Date date = null;
                    JDateAlta.setDate(date);
                } else {
                    //Intentar colocar la fecha correposndiete
                    SimpleDateFormat formato = new SimpleDateFormat("yyy-MM-dd");
                    Date fechaDate = null;
                    try {
                        fechaDate = formato.parse(registro[1]);
                        Calendar newCalendar = Calendar.getInstance();
                        newCalendar.setTime(fechaDate);
                        JDateAlta.setCalendar(newCalendar);
                    } catch (ParseException ex) {
                        System.out.println("ERROR AL CONVERTIR LA FECHA INICIO\n" + ex);
                    }
                }
                if (registro[2] == null) {
                    Date date = null;
                    JDateBaja.setDate(date);
                } else {
                    SimpleDateFormat formato = new SimpleDateFormat("yyy-MM-dd");
                    Date fechaDate = null;
                    try {
                        fechaDate = formato.parse(registro[2]);
                        Calendar newCalendar = Calendar.getInstance();
                        newCalendar.setTime(fechaDate);
                        JDateBaja.setCalendar(newCalendar);
                    } catch (ParseException ex) {
                        System.out.println("ERROR AL CONVERTIR LA FECHA BAJA\n" + ex);
                    }
                }

            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No es posible mostrar al valor de la orden  " + ex);
        }

    }

    private void MostrarServiciosServidores(String ID_RegConsultor) {
        String sSQL1 = " SELECT "
                + "ID_reg_serv, "
                + "nombre_serv, "
                + "usuario_servicio, "
                + "password_servicio, "
                + "fecha_alta, "
                + "estatus_acceso_serv, "
                + "descripcion_serv "
                + "FROM reg_servicio WHERE ID_regConsultorFK = " + ID_RegConsultor + "  ";

        String sSQL2 = "SELECT "
                + "ID_reg_server, "
                + "nombre_server, "
                + "estatus_acceso_server, "
                + "puertos_server, "
                + "descripcion_server "
                + "FROM reg_server WHERE ID_regConsultor =  " + ID_RegConsultor + " ";

        String[] registro = new String[9];
        String[] registro2 = new String[9];

        String[] titulos = {"ID", "SERVICIO", "USUARIO", "PASSWORD", "ALTA", "ESTATUS", "DESCRIPCIÓN"};
        String[] titulos2 = {"ID", "SERVIDOR", "PUERTOS", "ESTATUS", "DESCRIPCIÓN"};
        TABLAServicios = new DefaultTableModel(null, titulos);
        TABLAServidores = new DefaultTableModel(null, titulos2);
        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        int cont = 0, contServ = 0;
        try {
            Statement st1 = (Statement) cn.createStatement();
            Statement st2 = (Statement) cn.createStatement();

            ResultSet rs1 = st1.executeQuery(sSQL1);
            ResultSet rs2 = st2.executeQuery(sSQL2);
            while (rs1.next()) {
                registro[0] = rs1.getString("ID_reg_serv");
                registro[1] = rs1.getString("nombre_serv");

                registro[2] = rs1.getString("usuario_servicio");

                registro[3] = rs1.getString("password_servicio");

                registro[4] = rs1.getString("fecha_alta");

                if (rs1.getString("estatus_acceso_serv").equals("1")) {
                    registro[5] = "ACTIVO";
                } else {
                    registro[5] = "INACTIVO";
                }
//                registro[5] = rs1.getString("estatus_acceso_serv");
                registro[6] = rs1.getString("descripcion_serv");

                TABLAServicios.addRow(registro);
                cont++;
            }
            TBLServicios.setModel(TABLAServicios);
            AjustaAnchosTabla(2);
            lblRegistrosFoundService.setText("Se encontraron " + cont + " servicio(s)");
            lblRegistrosFoundSS1.setText("Se encontraron " + cont + " servicio(s)");
            PanelServiciosServers.setTitleAt(0, "Servicios (" + cont + ")");
//            PanelServicios.setBorder(BorderFactory.createTitledBorder("Servicios ("+cont+")"));

            //SERVIDORES
            /*
             + "ID_reg_server, "
             + "nombre_server, "
             + "estatus_acceso_server, "
             + "puertos_server, "
             + "descripcion_server "
             {"ID", "SERVIDOR", "PUERTOS", "ESTATUS", "DESCRIPCIÓN"};
             */
            while (rs2.next()) {
                registro2[0] = rs2.getString("ID_reg_server");
                registro2[1] = rs2.getString("nombre_server");
                registro2[2] = rs2.getString("puertos_server");
                if (rs2.getString("estatus_acceso_server").equals("1")) {
                    registro2[3] = "ACTIVO";
                } else {
                    registro2[3] = "INACTIVO";
                }
                registro2[4] = rs2.getString("descripcion_server");

                TABLAServidores.addRow(registro2);
                contServ++;
            }

            TBLServidores.setModel(TABLAServidores);
            AjustaAnchosTabla(3);
            lblRegistrosFoundService1.setText("Se encontraron " + contServ + " servidor(es)");
            lblRegistrosFoundSS2.setText("Se encontraron " + contServ + " servidor(es)");
//            PanelServers.setBorder(BorderFactory.createTitledBorder("Servidores ("+contServ+")"));
            PanelServiciosServers.setTitleAt(1, "Servidores (" + contServ + ")");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No es posible cargar la informacion de los servicios:\n ERROR: " + ex);
        }
    }

    private boolean ExecuteQuery(String sSQL) {
        boolean execute = false;
        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        try {
            com.mysql.jdbc.PreparedStatement pst = (com.mysql.jdbc.PreparedStatement) cn.prepareStatement(sSQL);
            execute = pst.execute();
            execute = true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "NO UPDATE: " + ex);
        }
        return execute;
    }

    private String DameIDRegistro() {
        String IDRegistro;
        IDRegistro = "";
        String sSQL = "  SELECT MAX(ID_reg_cons) AS id FROM reg_consultor";
        String[] registro = new String[1];
        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        try {
            Statement st = (Statement) cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                registro[0] = rs.getString("id");
            }
            IDRegistro = registro[0];
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No es posible mostrar al valor de la orden  " + ex);
        }
        return IDRegistro;
    }

    private void actualizaRegistroCnsultor() {
        //Obteniendo informacion de los objetos
        String idProyecto, idConsultoria, fIngreso, fBaja, correoRegistro, estatusConsultor;
        String sSQLProyecto = "SELECT ID_proyecto FROM proyecto WHERE sigla_proyecto = '" + CBOProyectos.getSelectedItem().toString() + "'";
        idProyecto = regresaValor(sSQLProyecto, "ID_proyecto");

        String sSQLConsultor = "SELECT ID_consultoria FROM consultoria WHERE nombre_consultoria = '" + CBOConsultorias.getSelectedItem().toString() + "'";
        idConsultoria = regresaValor(sSQLConsultor, "ID_consultoria");

        fIngreso = "NULL";
        Date FechaEnt = JDateAlta.getDate();
        if (FechaEnt != null) {
            fIngreso = "'" + UtileriaFecha.ConvertirString(FechaEnt) + "'";
        }
        fBaja = "NULL";
        Date FechaBaja = JDateBaja.getDate();
        if (FechaBaja != null) {
            fBaja = "'" + UtileriaFecha.ConvertirString(FechaBaja) + "'";
        }

        correoRegistro = txteMail.getText();
        if (statusRegistro == true) {
            estatusConsultor = "1";
        } else {
            estatusConsultor = "0";
        }

        String sSQL = "UPDATE reg_consultor SET "
                + "ID_proy = '" + idProyecto + "', "
                + "ID_consultoria = '" + idConsultoria + "', "
                + "f_ingreso = " + fIngreso + ", "
                + "f_baja = " + fBaja + ", "
                + "correo_registro = '" + correoRegistro + "', "
                + "estatus_reg_cons = '" + estatusConsultor + "' "
                + "WHERE reg_consultor.ID_reg_cons = " + ID_regCons + "  ";
        ExecuteQuery(sSQL);

        //Actualizando la informacion del consultor
        String nombre, primeroApellido, segundoApellido, EmailActual;
        nombre = txtNom.getText();
        primeroApellido = txtApP.getText();
        segundoApellido = txtApM.getText();
        EmailActual = txtMail.getText();
        sSQL = "UPDATE consultor SET "
                + "nombre_c = '" + nombre + "', "
                + "apellidoP_c = '" + primeroApellido + "', "
                + "apellidoM_c = '" + segundoApellido + "', "
                + "correo_c = '" + EmailActual + "' "
                + "WHERE consultor.ID_consultor = " + ID_Busqueda + "  ";
        ExecuteQuery(sSQL);
    }

    private String regresaValor(String consultaSQL, String variableDataBase) {
        String[] registro = new String[1];
        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        try {
            Statement st = (Statement) cn.createStatement();
            ResultSet rs = st.executeQuery(consultaSQL);
            while (rs.next()) {
                registro[0] = rs.getString(variableDataBase);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No es posible mostrar al valor de la orden  " + ex);
        }

        return registro[0];
    }

    private void BuscarRegistro(String valor) {
        ///Tipo de busqueda
        Hilo hilo = new Hilo(300, 30);
        String busquedaTipo = "";
        String sSQL = "";
        //BUSQUEDA POR PROYECTO
        sSQL = "SELECT ID_consultor FROM consultor "
                + "WHERE (CONCAT(nombre_c)  LIKE '%" + valor + "%') "
                + " or (CONCAT(apellidoP_c)  LIKE '%" + valor + "%')    "
                + " or (CONCAT(apellidoM_c)  LIKE '%" + valor + "%')    ";
        busquedaTipo = "CON";
        String[] registro = new String[2];
        int NoFounf = 0;
        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();

        try {
            java.sql.Statement st = (java.sql.Statement) cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                registro[0] = rs.getString("ID_consultor");
                NoFounf++;
            }
            if (NoFounf > 1) { //EXISTEN MAS REGISTROS, ABRIR VENTANA EMERGENTE Y MOSTRAR RESULTADOS
                hilo.agregarTexto("EXITEN " + NoFounf + " COINCIDENCIAS, ELIJA UN REGISTRO", 1);
                hilo.start();
                String sSQLConsulta = "SELECT @rownum:=@rownum+1 AS contador,  `nombre_c`, `apellidoP_c`, `apellidoM_c`, `correo_c`, `estatus_c` "
                        + "FROM consultor, (SELECT @rownum:=0) R "
                        + "WHERE (CONCAT(nombre_c)  LIKE '%" + valor + "%') "
                        + " or (CONCAT(apellidoP_c)  LIKE '%" + valor + "%')    "
                        + " or (CONCAT(apellidoM_c)  LIKE '%" + valor + "%')    "
                        + "AND estatus_c = '1' ORDER BY `consultor`.`nombre_c` ASC";
                this.dispose();
                Frame Admimistracion = null;
                JPanelBusquedaConsultor PanelAdministrador = new JPanelBusquedaConsultor(Admimistracion, true, busquedaTipo, sSQLConsulta, valor);
                PanelAdministrador.setVisible(true);

            } else if (NoFounf == 1) { //SOLO EXISTE UN REGISTRO Y SE ABRIRA LA VENTANA DE UPDATECONSULTOR
                hilo.agregarTexto("PUEDE EDITAR EL REGISTRO", 1);
                hilo.start();

//        JOptionPane.showMessageDialog(null, nombre + " " + apellidoP + " " + apellidoM);
                this.dispose();
                Frame Admimistracion = null;
                JPanelUpdateNewConsultor PanelAdministrador = new JPanelUpdateNewConsultor(Admimistracion, true, registro[0]);
                PanelAdministrador.setVisible(true);

// CargaDatosRegistro(registro[0]);
            } else {
                //MENSAJE DE QUE SI DESEA AGREGARLO

                hilo.agregarTexto("NO SE ENCONTRARON REGISTROS, INTENTE CON OTR0 NOMBRE", 2);
                hilo.start();
                txtBuscar.requestFocus();
                txtBuscar.selectAll();
            }
        } catch (SQLException e) {
            hilo.agregarTexto("ERROR AL BUSCAR REGISTRO", 2);
            hilo.start();
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JLabel();
        lblFound = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        lblFound1 = new javax.swing.JLabel();
        txtID = new javax.swing.JTextField();
        txtNom = new javax.swing.JTextField();
        lblFound2 = new javax.swing.JLabel();
        lblFound3 = new javax.swing.JLabel();
        lblFound4 = new javax.swing.JLabel();
        lblFound5 = new javax.swing.JLabel();
        txtApP = new javax.swing.JTextField();
        txtApM = new javax.swing.JTextField();
        txtMail = new javax.swing.JTextField();
        lblRegistrosFound = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        TBLRegistroConsultor = new javax.swing.JTable();
        btnNuevoRegistro = new javax.swing.JLabel();
        btnNuevoConsultor1 = new javax.swing.JLabel();
        btnNuevoConsultor = new javax.swing.JLabel();
        PanelRegistro = new javax.swing.JPanel();
        lblFound18 = new javax.swing.JLabel();
        lblFound19 = new javax.swing.JLabel();
        lblFound20 = new javax.swing.JLabel();
        lblFound21 = new javax.swing.JLabel();
        JDateAlta = new com.toedter.calendar.JDateChooser();
        JDateBaja = new com.toedter.calendar.JDateChooser();
        CBOProyectos = new javax.swing.JComboBox<String>();
        CBOConsultorias = new javax.swing.JComboBox<String>();
        lblRegistroStatus = new javax.swing.JLabel();
        btnAddProyecto = new javax.swing.JButton();
        btnAddConsultoria = new javax.swing.JButton();
        lblFound7 = new javax.swing.JLabel();
        txteMail = new javax.swing.JTextField();
        lblIDRegistro = new javax.swing.JLabel();
        PanelServiciosServers = new javax.swing.JTabbedPane();
        PanelServicios = new javax.swing.JPanel();
        lblFound11 = new javax.swing.JLabel();
        CBOServicios = new javax.swing.JComboBox<String>();
        btnAddServicio = new javax.swing.JLabel();
        btnAddAllServicios = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TBLServicios = new javax.swing.JTable();
        btnQuitarTodosServicio = new javax.swing.JLabel();
        lblRegistrosFoundService = new javax.swing.JLabel();
        btnNuevoServicio = new javax.swing.JButton();
        PanelServers = new javax.swing.JPanel();
        lblFound12 = new javax.swing.JLabel();
        CBOServidores = new javax.swing.JComboBox<String>();
        btnAddServer = new javax.swing.JLabel();
        btnAddAllServer = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TBLServidores = new javax.swing.JTable();
        btnQuitarAllServer = new javax.swing.JLabel();
        lblRegistrosFoundService1 = new javax.swing.JLabel();
        btnNuevoServer = new javax.swing.JButton();
        btnGuardarUpdateRegistro = new javax.swing.JLabel();
        btnUseEmailActual = new javax.swing.JButton();
        lblRegistrosFoundSS1 = new javax.swing.JLabel();
        lblRegistrosFoundSS2 = new javax.swing.JLabel();
        btnImprimir = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setUndecorated(true);
        setType(java.awt.Window.Type.POPUP);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanel2MouseDragged(evt);
            }
        });
        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel2MousePressed(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/close.png"))); // NOI18N
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        lblTitulo.setBackground(new java.awt.Color(255, 255, 255));
        lblTitulo.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo.setText("Ingresar nuevo consultor / Modificar Consultor");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitulo)
                    .addComponent(jLabel1))
                .addGap(247, 247, 247))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        txtBuscar.setFont(new java.awt.Font("Arial", 2, 14)); // NOI18N
        txtBuscar.setText("search...");
        txtBuscar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtBuscarMouseClicked(evt);
            }
        });
        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtBuscarKeyPressed(evt);
            }
        });

        btnBuscar.setBackground(Color.decode("#4D92DF"));
        btnBuscar.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnBuscar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/search.png"))); // NOI18N
        btnBuscar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnBuscar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBuscar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBuscarMouseClicked(evt);
            }
        });

        lblFound.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound.setForeground(new java.awt.Color(51, 51, 51));
        lblFound.setText("Buscar Consultor:");

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información de Consultor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 18), new java.awt.Color(102, 102, 102))); // NOI18N
        jPanel7.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N

        lblFound1.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound1.setForeground(new java.awt.Color(51, 51, 51));
        lblFound1.setText("ID:");

        txtID.setEditable(false);
        txtID.setBackground(new java.awt.Color(255, 255, 255));
        txtID.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txtID.setForeground(new java.awt.Color(51, 51, 51));
        txtID.setText("0000");

        txtNom.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txtNom.setForeground(new java.awt.Color(51, 51, 51));
        txtNom.setText("0000");

        lblFound2.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound2.setForeground(new java.awt.Color(51, 51, 51));
        lblFound2.setText("Nombre:");

        lblFound3.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound3.setForeground(new java.awt.Color(51, 51, 51));
        lblFound3.setText("Primer Apellido:");

        lblFound4.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound4.setForeground(new java.awt.Color(51, 51, 51));
        lblFound4.setText("Segundo Apellido:");

        lblFound5.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound5.setForeground(new java.awt.Color(51, 51, 51));
        lblFound5.setText("E-Mail Actual:");

        txtApP.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txtApP.setForeground(new java.awt.Color(51, 51, 51));
        txtApP.setText("0000");

        txtApM.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txtApM.setForeground(new java.awt.Color(51, 51, 51));
        txtApM.setText("0000");

        txtMail.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txtMail.setForeground(new java.awt.Color(51, 51, 51));
        txtMail.setText("0000");

        lblRegistrosFound.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblRegistrosFound.setForeground(new java.awt.Color(51, 51, 51));
        lblRegistrosFound.setText("Seleccione un registro:");

        TBLRegistroConsultor.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        TBLRegistroConsultor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TBLRegistroConsultorMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(TBLRegistroConsultor);

        btnNuevoRegistro.setBackground(Color.decode("#4D92DF"));
        btnNuevoRegistro.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnNuevoRegistro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnNuevoRegistro.setText("Nuevo Registro");
        btnNuevoRegistro.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnNuevoRegistro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNuevoRegistro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnNuevoRegistroMouseClicked(evt);
            }
        });

        btnNuevoConsultor1.setBackground(Color.decode("#4D92DF"));
        btnNuevoConsultor1.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnNuevoConsultor1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnNuevoConsultor1.setText("Actualizar");
        btnNuevoConsultor1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnNuevoConsultor1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNuevoConsultor1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnNuevoConsultor1MouseClicked(evt);
            }
        });

        btnNuevoConsultor.setBackground(Color.decode("#4D92DF"));
        btnNuevoConsultor.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnNuevoConsultor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnNuevoConsultor.setText("Nuevo Consultor");
        btnNuevoConsultor.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnNuevoConsultor.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNuevoConsultor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnNuevoConsultorMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblFound5)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(lblFound1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblFound2))
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblFound4)
                                .addComponent(lblFound3, javax.swing.GroupLayout.Alignment.TRAILING)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNom)
                            .addComponent(txtApP, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtApM, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtMail, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(lblRegistrosFound)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(btnNuevoConsultor, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnNuevoConsultor1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnNuevoRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFound2)
                    .addComponent(txtNom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFound1)
                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFound3)
                    .addComponent(txtApP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFound4)
                    .addComponent(txtApM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFound5)
                    .addComponent(txtMail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRegistrosFound)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevoRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuevoConsultor1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuevoConsultor, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        PanelRegistro.setBackground(new java.awt.Color(255, 255, 255));
        PanelRegistro.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información del Registro del Consultor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 18), new java.awt.Color(102, 102, 102))); // NOI18N
        PanelRegistro.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N

        lblFound18.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound18.setForeground(new java.awt.Color(51, 51, 51));
        lblFound18.setText("F. de Alta:");

        lblFound19.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound19.setForeground(new java.awt.Color(51, 51, 51));
        lblFound19.setText("F. de baja:");

        lblFound20.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound20.setForeground(new java.awt.Color(51, 51, 51));
        lblFound20.setText("Proyecto:");

        lblFound21.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound21.setForeground(new java.awt.Color(51, 51, 51));
        lblFound21.setText("Consultoria:");

        JDateAlta.setBackground(new java.awt.Color(0, 0, 153));
        JDateAlta.setDateFormatString("yyyy-MM-dd");
        JDateAlta.setEnabled(false);

        JDateBaja.setBackground(new java.awt.Color(0, 0, 153));
        JDateBaja.setDateFormatString("yyyy-MM-dd");
        JDateBaja.setEnabled(false);

        CBOProyectos.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        CBOProyectos.setForeground(new java.awt.Color(51, 51, 51));
        CBOProyectos.setEnabled(false);

        CBOConsultorias.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        CBOConsultorias.setForeground(new java.awt.Color(51, 51, 51));
        CBOConsultorias.setEnabled(false);

        lblRegistroStatus.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblRegistroStatus.setForeground(new java.awt.Color(51, 51, 51));
        lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/on.png"))); // NOI18N
        lblRegistroStatus.setToolTipText("ACTIVO");
        lblRegistroStatus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblRegistroStatus.setEnabled(false);
        lblRegistroStatus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblRegistroStatusMouseClicked(evt);
            }
        });

        btnAddProyecto.setForeground(new java.awt.Color(0, 102, 204));
        btnAddProyecto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/add.png"))); // NOI18N
        btnAddProyecto.setToolTipText("<HTML>Si el Proyecto no está,<br> de clic en + para agregarlo");
        btnAddProyecto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddProyectoActionPerformed(evt);
            }
        });

        btnAddConsultoria.setForeground(new java.awt.Color(0, 102, 204));
        btnAddConsultoria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/add.png"))); // NOI18N
        btnAddConsultoria.setToolTipText("<html>Si la Consultoría no está, <br>de clic en + para agregarlo");
        btnAddConsultoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddConsultoriaActionPerformed(evt);
            }
        });

        lblFound7.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound7.setForeground(new java.awt.Color(51, 51, 51));
        lblFound7.setText("E-Mail:");

        txteMail.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txteMail.setForeground(new java.awt.Color(51, 51, 51));
        txteMail.setEnabled(false);

        lblIDRegistro.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblIDRegistro.setForeground(new java.awt.Color(51, 51, 51));
        lblIDRegistro.setText("Registro: ID Auto");

        PanelServiciosServers.setBackground(new java.awt.Color(255, 255, 255));
        PanelServiciosServers.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N

        PanelServicios.setBackground(new java.awt.Color(255, 255, 255));

        lblFound11.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound11.setForeground(new java.awt.Color(51, 51, 51));
        lblFound11.setText("Servicios:");

        CBOServicios.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        CBOServicios.setForeground(new java.awt.Color(51, 51, 51));
        CBOServicios.setEnabled(false);

        btnAddServicio.setBackground(Color.decode("#4D92DF"));
        btnAddServicio.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnAddServicio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnAddServicio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Down.png"))); // NOI18N
        btnAddServicio.setText("Agregar");
        btnAddServicio.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnAddServicio.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddServicio.setEnabled(false);
        btnAddServicio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddServicioMouseClicked(evt);
            }
        });
        btnAddServicio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnAddServicioKeyPressed(evt);
            }
        });

        btnAddAllServicios.setBackground(Color.decode("#4D92DF"));
        btnAddAllServicios.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnAddAllServicios.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnAddAllServicios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/DownAll.png"))); // NOI18N
        btnAddAllServicios.setText("Todo");
        btnAddAllServicios.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnAddAllServicios.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddAllServicios.setEnabled(false);
        btnAddAllServicios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddAllServiciosMouseClicked(evt);
            }
        });

        TBLServicios.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        TBLServicios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        TBLServicios.setEnabled(false);
        TBLServicios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TBLServiciosMouseClicked(evt);
            }
        });
        TBLServicios.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                TBLServiciosComponentHidden(evt);
            }
        });
        jScrollPane1.setViewportView(TBLServicios);

        btnQuitarTodosServicio.setBackground(Color.decode("#4D92DF"));
        btnQuitarTodosServicio.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnQuitarTodosServicio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnQuitarTodosServicio.setText("Quitar Todos");
        btnQuitarTodosServicio.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnQuitarTodosServicio.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQuitarTodosServicio.setEnabled(false);

        lblRegistrosFoundService.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblRegistrosFoundService.setForeground(new java.awt.Color(51, 51, 51));
        lblRegistrosFoundService.setText("_");

        btnNuevoServicio.setForeground(new java.awt.Color(0, 102, 204));
        btnNuevoServicio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/add.png"))); // NOI18N
        btnNuevoServicio.setToolTipText("<html>Si el servicio no está, <br>de clic en + para agregarlo");
        btnNuevoServicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoServicioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelServiciosLayout = new javax.swing.GroupLayout(PanelServicios);
        PanelServicios.setLayout(PanelServiciosLayout);
        PanelServiciosLayout.setHorizontalGroup(
            PanelServiciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelServiciosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelServiciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(PanelServiciosLayout.createSequentialGroup()
                        .addComponent(lblFound11)
                        .addGap(18, 18, 18)
                        .addComponent(CBOServicios, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNuevoServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(btnAddServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                        .addComponent(btnAddAllServicios, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblRegistrosFoundService, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelServiciosLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnQuitarTodosServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        PanelServiciosLayout.setVerticalGroup(
            PanelServiciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelServiciosLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(PanelServiciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnNuevoServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(PanelServiciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblFound11)
                        .addComponent(CBOServicios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAddServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAddAllServicios, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRegistrosFoundService)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnQuitarTodosServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        PanelServiciosServers.addTab("Servicios", PanelServicios);

        PanelServers.setBackground(new java.awt.Color(255, 255, 255));

        lblFound12.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound12.setForeground(new java.awt.Color(51, 51, 51));
        lblFound12.setText("Servidores:");

        CBOServidores.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        CBOServidores.setForeground(new java.awt.Color(51, 51, 51));
        CBOServidores.setEnabled(false);

        btnAddServer.setBackground(Color.decode("#4D92DF"));
        btnAddServer.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnAddServer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnAddServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Down.png"))); // NOI18N
        btnAddServer.setText("Agregar");
        btnAddServer.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnAddServer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddServer.setEnabled(false);
        btnAddServer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddServerMouseClicked(evt);
            }
        });

        btnAddAllServer.setBackground(Color.decode("#4D92DF"));
        btnAddAllServer.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnAddAllServer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnAddAllServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/DownAll.png"))); // NOI18N
        btnAddAllServer.setText("Todo");
        btnAddAllServer.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnAddAllServer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddAllServer.setEnabled(false);
        btnAddAllServer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddAllServerMouseClicked(evt);
            }
        });

        TBLServidores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        TBLServidores.setEnabled(false);
        TBLServidores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TBLServidoresMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(TBLServidores);

        btnQuitarAllServer.setBackground(Color.decode("#4D92DF"));
        btnQuitarAllServer.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnQuitarAllServer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnQuitarAllServer.setText("Quitar Todos");
        btnQuitarAllServer.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnQuitarAllServer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQuitarAllServer.setEnabled(false);

        lblRegistrosFoundService1.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblRegistrosFoundService1.setForeground(new java.awt.Color(51, 51, 51));
        lblRegistrosFoundService1.setText("_");

        btnNuevoServer.setForeground(new java.awt.Color(0, 102, 204));
        btnNuevoServer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/add.png"))); // NOI18N
        btnNuevoServer.setToolTipText("<html>Si lel servidor no está, <br>de clic en + para agregarlo");
        btnNuevoServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoServerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelServersLayout = new javax.swing.GroupLayout(PanelServers);
        PanelServers.setLayout(PanelServersLayout);
        PanelServersLayout.setHorizontalGroup(
            PanelServersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelServersLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelServersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(PanelServersLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnQuitarAllServer, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelServersLayout.createSequentialGroup()
                        .addGroup(PanelServersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelServersLayout.createSequentialGroup()
                                .addComponent(lblRegistrosFoundService1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(48, 48, 48))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelServersLayout.createSequentialGroup()
                                .addComponent(lblFound12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CBOServidores, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnNuevoServer, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(btnAddServer, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddAllServer, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        PanelServersLayout.setVerticalGroup(
            PanelServersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelServersLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelServersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(PanelServersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblFound12)
                        .addComponent(CBOServidores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAddServer, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAddAllServer, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnNuevoServer, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblRegistrosFoundService1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnQuitarAllServer, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        PanelServiciosServers.addTab("Servidores", PanelServers);

        btnGuardarUpdateRegistro.setBackground(Color.decode("#4D92DF"));
        btnGuardarUpdateRegistro.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnGuardarUpdateRegistro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnGuardarUpdateRegistro.setText("Guardar");
        btnGuardarUpdateRegistro.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnGuardarUpdateRegistro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGuardarUpdateRegistro.setEnabled(false);
        btnGuardarUpdateRegistro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnGuardarUpdateRegistroMouseClicked(evt);
            }
        });

        btnUseEmailActual.setForeground(new java.awt.Color(0, 102, 204));
        btnUseEmailActual.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Reply-48.png"))); // NOI18N
        btnUseEmailActual.setToolTipText("<HTML>Usar el correo actual");
        btnUseEmailActual.setEnabled(false);
        btnUseEmailActual.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnUseEmailActualMouseClicked(evt);
            }
        });

        lblRegistrosFoundSS1.setFont(new java.awt.Font("Calibri", 2, 12)); // NOI18N
        lblRegistrosFoundSS1.setForeground(new java.awt.Color(51, 51, 51));

        lblRegistrosFoundSS2.setFont(new java.awt.Font("Calibri", 2, 12)); // NOI18N
        lblRegistrosFoundSS2.setForeground(new java.awt.Color(51, 51, 51));

        btnImprimir.setBackground(Color.decode("#4D92DF"));
        btnImprimir.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnImprimir.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnImprimir.setText("Imprimir");
        btnImprimir.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnImprimir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnImprimir.setEnabled(false);
        btnImprimir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnImprimirMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout PanelRegistroLayout = new javax.swing.GroupLayout(PanelRegistro);
        PanelRegistro.setLayout(PanelRegistroLayout);
        PanelRegistroLayout.setHorizontalGroup(
            PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelRegistroLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelServiciosServers)
                    .addGroup(PanelRegistroLayout.createSequentialGroup()
                        .addComponent(CBOConsultorias, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddConsultoria, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblFound19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JDateBaja, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelRegistroLayout.createSequentialGroup()
                        .addComponent(lblFound21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblFound18, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JDateAlta, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelRegistroLayout.createSequentialGroup()
                        .addComponent(lblFound20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblIDRegistro)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRegistroStatus))
                    .addGroup(PanelRegistroLayout.createSequentialGroup()
                        .addComponent(lblRegistrosFoundSS1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(1, 1, 1)
                        .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnGuardarUpdateRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelRegistroLayout.createSequentialGroup()
                        .addComponent(lblRegistrosFoundSS2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(114, 114, 114))
                    .addGroup(PanelRegistroLayout.createSequentialGroup()
                        .addComponent(CBOProyectos, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddProyecto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblFound7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txteMail)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUseEmailActual, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        PanelRegistroLayout.setVerticalGroup(
            PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelRegistroLayout.createSequentialGroup()
                .addGroup(PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(PanelRegistroLayout.createSequentialGroup()
                        .addComponent(lblFound20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CBOProyectos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAddProyecto, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelRegistroLayout.createSequentialGroup()
                        .addGroup(PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblRegistroStatus)
                            .addComponent(lblIDRegistro))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnUseEmailActual, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblFound7)
                                .addComponent(txteMail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(3, 3, 3)
                .addGroup(PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblFound21)
                    .addComponent(JDateAlta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFound18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CBOConsultorias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddConsultoria, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblFound19, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(JDateBaja, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelServiciosServers)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnGuardarUpdateRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelRegistroLayout.createSequentialGroup()
                        .addComponent(lblRegistrosFoundSS1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblRegistrosFoundSS2)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(lblFound)
                        .addGap(18, 18, 18)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(PanelRegistro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelRegistro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblFound)
                                .addComponent(txtBuscar)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        this.dispose();
    }//GEN-LAST:event_jLabel1MouseClicked

    private void jPanel2MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseDragged
        Point point = MouseInfo.getPointerInfo().getLocation();
        setLocation(point.x - x, point.y - y);

    }//GEN-LAST:event_jPanel2MouseDragged

    private void jPanel2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MousePressed
        x = evt.getX();
        y = evt.getY();
    }//GEN-LAST:event_jPanel2MousePressed

    private void txtBuscarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtBuscarMouseClicked
        txtBuscar.selectAll();
    }//GEN-LAST:event_txtBuscarMouseClicked

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed

    }//GEN-LAST:event_txtBuscarActionPerformed

    private void btnBuscarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBuscarMouseClicked
        String value = txtBuscar.getText();

        if (value.equals("")) {
            JOptionPane.showMessageDialog(null, "Debe escribir una palabra");
            txtBuscar.requestFocus();
        } else {
            BuscarRegistro(value);
        }
    }//GEN-LAST:event_btnBuscarMouseClicked

    private void btnNuevoConsultorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNuevoConsultorMouseClicked
        this.dispose();
        Frame Admimistracion = null;
        JPanelConsultorCatalog PanelAdministrador = new JPanelConsultorCatalog(Admimistracion, true, "NEW");
        PanelAdministrador.setVisible(true);

    }//GEN-LAST:event_btnNuevoConsultorMouseClicked

    private void btnGuardarUpdateRegistroMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGuardarUpdateRegistroMouseClicked
        if (btnGuardarUpdateRegistro.isEnabled()) {
            //actualizar registro.
            actualizaRegistroCnsultor();
            limpiaObj();
            bloequeaObjetos();
            statusRegistro = false;
            cambiaValor();
            CargarRegistro(ID_Busqueda);
            String nombre, primeroApellido, segundoApellido, EmailActual;
            nombre = txtNom.getText();
            primeroApellido = txtApP.getText();
            segundoApellido = txtApM.getText();
            EmailActual = txtMail.getText();
            String sSQL = "UPDATE consultor SET "
                    + "nombre_c = '" + nombre + "', "
                    + "apellidoP_c = '" + primeroApellido + "', "
                    + "apellidoM_c = '" + segundoApellido + "', "
                    + "correo_c = '" + EmailActual + "' "
                    + "WHERE consultor.ID_consultor = " + ID_Busqueda + "  ";
            if (true == ExecuteQuery(sSQL)) {
                Hilo hilo = new Hilo(300, 30);
                hilo.agregarTexto("SE ACTUALIZÓ AL CONSULTOR", 1);
                hilo.start();
            }
        }
    }//GEN-LAST:event_btnGuardarUpdateRegistroMouseClicked

    private void btnImprimirMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnImprimirMouseClicked
        // Imprimir al consultor y su informacion especifica

        JOptionPane.showMessageDialog(null, "Actualmente no disponible");

    }//GEN-LAST:event_btnImprimirMouseClicked

    private void btnNuevoRegistroMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNuevoRegistroMouseClicked
        String sSQL = "INSERT INTO `reg_consultor` "
                + "(`ID_reg_cons`, `ID_consultor`, `ID_proy`, `ID_consultoria`, `f_ingreso`, `f_baja`, `correo_registro`, `estatus_reg_cons`) "
                + "VALUES (NULL, '" + txtID.getText() + "', 1, 1, NULL, NULL, NULL, '0')";
        ExecuteQuery(sSQL);
        ID_regCons = DameIDRegistro();
        lblIDRegistro.setText("-Registro: " + ID_regCons);
        accion = "NEW";
        activaObjetos();
        MostrarInformacionRegServicio(ID_regCons);
        MostrarServiciosServidores(ID_regCons);
    }//GEN-LAST:event_btnNuevoRegistroMouseClicked

    private void lblRegistroStatusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblRegistroStatusMouseClicked
        if (!lblRegistroStatus.isEnabled()) {
        } else {
            cambiaValor();
        }
    }//GEN-LAST:event_lblRegistroStatusMouseClicked

    private void btnUseEmailActualMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUseEmailActualMouseClicked
        if (btnUseEmailActual.isEnabled()) {
            txteMail.setText(txtMail.getText());
        }
    }//GEN-LAST:event_btnUseEmailActualMouseClicked

    private void btnAddServicioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnAddServicioKeyPressed

    }//GEN-LAST:event_btnAddServicioKeyPressed

    private void btnAddServicioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddServicioMouseClicked
        if (btnAddServicio.isEnabled()) {
            String nombreServicio = CBOServicios.getSelectedItem().toString();
            //dar el id del Servicio elegido
            if (BuscarEnTabla(nombreServicio, 1) == true) {
                //Ya existe
                JOptionPane.showMessageDialog(null, "Este servicio ya ha sido agregado, elija otro.");
            } else {
                //No existe
                accionService = "NEW";
                agregarServicioATabla(nombreServicio, "", ID_regCons);
            }
        }
    }//GEN-LAST:event_btnAddServicioMouseClicked

    private void TBLRegistroConsultorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TBLRegistroConsultorMouseClicked
        String id = TBLRegistroConsultor.getValueAt(TBLRegistroConsultor.getSelectedRow(), 0).toString();
        activaObjetos();
        ID_regCons = id;
        MostrarInformacionRegServicio(id);
        MostrarServiciosServidores(id);
        accion = "UPDATE";


    }//GEN-LAST:event_TBLRegistroConsultorMouseClicked

    private void TBLServiciosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TBLServiciosMouseClicked
        //SELECCIONAR UN ELEMENTO DE LA TABLA Y EDITARLO

        if (TBLServicios.isEnabled()) {
            String idService = TBLServicios.getValueAt(TBLServicios.getSelectedRow(), 0).toString();
            accionService = "UPDATE";
            String nombresServive = TBLServicios.getValueAt(TBLServicios.getSelectedRow(), 1).toString();
            Frame Admimistracion = null;
            JPanelAddService PanelAdministrador;
            PanelAdministrador = new JPanelAddService(Admimistracion, true, nombresServive, idService, accionService, ID_regCons);
            PanelAdministrador.setVisible(true);
            MostrarServiciosServidores(ID_regCons);
        }

    }//GEN-LAST:event_TBLServiciosMouseClicked

    private void TBLServiciosComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_TBLServiciosComponentHidden
        // TODO add your handling code here:
    }//GEN-LAST:event_TBLServiciosComponentHidden

    private void btnAddAllServerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddAllServerMouseClicked
        if (btnAddAllServer.isEnabled()) {
            String nombreServidor;
            int contAdd = 0;
            for (int i = 0; i < CBOServidores.getItemCount(); i++) {
                nombreServidor = CBOServidores.getItemAt(i);
                if (BuscarEnTabla(nombreServidor, 2) == true) {

                    //Ya existe
                } else {
                    contAdd++;
                    //No existe
                    accionServer = "NEW";
                    agregarServerATabla(nombreServidor, "", ID_regCons);
                }
            }
            if (contAdd == 0) {
                JOptionPane.showMessageDialog(null, "Ya se agregaron todos los servidores");
            }
        }

    }//GEN-LAST:event_btnAddAllServerMouseClicked

    private void btnAddServerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddServerMouseClicked
        if (btnAddServer.isEnabled()) {
            String nombreServicio = CBOServidores.getSelectedItem().toString();
            //dar el id del Servicio elegido

            if (BuscarEnTabla(nombreServicio, 2) == true) {
                //Ya existe
                JOptionPane.showMessageDialog(null, "Este servidor ya ha sido agregado, elija otro.");
            } else {
                //No existe
                accionServer = "NEW";
                agregarServerATabla(nombreServicio, "", ID_regCons);
            }
        }

    }//GEN-LAST:event_btnAddServerMouseClicked

    private void TBLServidoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TBLServidoresMouseClicked
        if (TBLServidores.isEnabled()) {
            //SELECCIONAR UN ELEMENTO DE LA TABLA Y EDITARLO
            String idServer = TBLServidores.getValueAt(TBLServidores.getSelectedRow(), 0).toString();
            accionServer = "UPDATE";
            String nombresServer = TBLServidores.getValueAt(TBLServidores.getSelectedRow(), 1).toString();
            Frame Admimistracion = null;
            JPanelAddServer PanelAdministrador;
            PanelAdministrador = new JPanelAddServer(Admimistracion, true, nombresServer, idServer, accionServer, ID_regCons);
            PanelAdministrador.setVisible(true);
            MostrarServiciosServidores(ID_regCons);
        }

    }//GEN-LAST:event_TBLServidoresMouseClicked

    private void txtBuscarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyPressed
        String value = txtBuscar.getText();
        if ((evt.getKeyCode() == evt.VK_ENTER) || (evt.getExtendedKeyCode() == evt.VK_TAB)) {
            if (value.equals("")) {
                JOptionPane.showMessageDialog(null, "Debe escribir una palabra");
                txtBuscar.requestFocus();
            } else {
                BuscarRegistro(value);
            }
        }
    }//GEN-LAST:event_txtBuscarKeyPressed

    private void btnAddConsultoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddConsultoriaActionPerformed
        Frame Admimistracion = null;
        JPanelConsultoriasCatalog PanelAdministrador = new JPanelConsultoriasCatalog(Admimistracion, true, "NEW");
        PanelAdministrador.setVisible(true);
        cargaDatos();
    }//GEN-LAST:event_btnAddConsultoriaActionPerformed

    private void btnNuevoServicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoServicioActionPerformed
        Frame Admimistracion = null;
        JPanelServiciosCatalog PanelAdministrador = new JPanelServiciosCatalog(Admimistracion, true, "NEW");
        PanelAdministrador.setVisible(true);
        cargaDatos();
    }//GEN-LAST:event_btnNuevoServicioActionPerformed

    private void btnNuevoServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoServerActionPerformed
        Frame Admimistracion = null;
        JPanelServidoresCatalog PanelAdministrador = new JPanelServidoresCatalog(Admimistracion, true, "NEW");
        PanelAdministrador.setVisible(true);
        cargaDatos();
    }//GEN-LAST:event_btnNuevoServerActionPerformed

    private void btnAddProyectoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddProyectoActionPerformed
        Frame Admimistracion = null;
        JPanelProyectosCatalog PanelAdministrador = new JPanelProyectosCatalog(Admimistracion, true, "NEW");
        PanelAdministrador.setVisible(true);
        cargaDatos();
    }//GEN-LAST:event_btnAddProyectoActionPerformed

    private void btnAddAllServiciosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddAllServiciosMouseClicked
        if (btnAddAllServicios.isEnabled()) {
            String nombreServicio;
            int contAdd = 0;
            for (int i = 0; i < CBOServicios.getItemCount(); i++) {
                nombreServicio = CBOServicios.getItemAt(i);
                if (BuscarEnTabla(nombreServicio, 1) != true) {
                    contAdd++;
                    //No existe
                    accionService = "NEW";
                    agregarServicioATabla(nombreServicio, "", ID_regCons);
                } else {
                    //Ya existe
                }
            }
            if (contAdd == 0) {
                JOptionPane.showMessageDialog(null, "Ya se agregaron todos los servidores");
            }
        }
    }//GEN-LAST:event_btnAddAllServiciosMouseClicked

    private void btnNuevoConsultor1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNuevoConsultor1MouseClicked
        //Actualizando la informacion del consultor
        String nombre, primeroApellido, segundoApellido, EmailActual;
        nombre = txtNom.getText();
        primeroApellido = txtApP.getText();
        segundoApellido = txtApM.getText();
        EmailActual = txtMail.getText();
        String sSQL = "UPDATE consultor SET "
                + "nombre_c = '" + nombre + "', "
                + "apellidoP_c = '" + primeroApellido + "', "
                + "apellidoM_c = '" + segundoApellido + "', "
                + "correo_c = '" + EmailActual + "' "
                + "WHERE consultor.ID_consultor = " + ID_Busqueda + "  ";
        if (true == ExecuteQuery(sSQL)) {
            Hilo hilo = new Hilo(300, 30);
            hilo.agregarTexto("SE ACTUALIZÓ AL CONSULTOR", 1);
            hilo.start();
        }
    }//GEN-LAST:event_btnNuevoConsultor1MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> CBOConsultorias;
    private javax.swing.JComboBox<String> CBOProyectos;
    private javax.swing.JComboBox<String> CBOServicios;
    private javax.swing.JComboBox<String> CBOServidores;
    private com.toedter.calendar.JDateChooser JDateAlta;
    private com.toedter.calendar.JDateChooser JDateBaja;
    private javax.swing.JPanel PanelRegistro;
    private javax.swing.JPanel PanelServers;
    private javax.swing.JPanel PanelServicios;
    private javax.swing.JTabbedPane PanelServiciosServers;
    private javax.swing.JTable TBLRegistroConsultor;
    private javax.swing.JTable TBLServicios;
    private javax.swing.JTable TBLServidores;
    private javax.swing.JLabel btnAddAllServer;
    private javax.swing.JLabel btnAddAllServicios;
    private javax.swing.JButton btnAddConsultoria;
    private javax.swing.JButton btnAddProyecto;
    private javax.swing.JLabel btnAddServer;
    private javax.swing.JLabel btnAddServicio;
    private javax.swing.JLabel btnBuscar;
    private javax.swing.JLabel btnGuardarUpdateRegistro;
    private javax.swing.JLabel btnImprimir;
    private javax.swing.JLabel btnNuevoConsultor;
    private javax.swing.JLabel btnNuevoConsultor1;
    private javax.swing.JLabel btnNuevoRegistro;
    private javax.swing.JButton btnNuevoServer;
    private javax.swing.JButton btnNuevoServicio;
    private javax.swing.JLabel btnQuitarAllServer;
    private javax.swing.JLabel btnQuitarTodosServicio;
    private javax.swing.JButton btnUseEmailActual;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblFound;
    private javax.swing.JLabel lblFound1;
    private javax.swing.JLabel lblFound11;
    private javax.swing.JLabel lblFound12;
    private javax.swing.JLabel lblFound18;
    private javax.swing.JLabel lblFound19;
    private javax.swing.JLabel lblFound2;
    private javax.swing.JLabel lblFound20;
    private javax.swing.JLabel lblFound21;
    private javax.swing.JLabel lblFound3;
    private javax.swing.JLabel lblFound4;
    private javax.swing.JLabel lblFound5;
    private javax.swing.JLabel lblFound7;
    private javax.swing.JLabel lblIDRegistro;
    private javax.swing.JLabel lblRegistroStatus;
    private javax.swing.JLabel lblRegistrosFound;
    private javax.swing.JLabel lblRegistrosFoundSS1;
    private javax.swing.JLabel lblRegistrosFoundSS2;
    private javax.swing.JLabel lblRegistrosFoundService;
    private javax.swing.JLabel lblRegistrosFoundService1;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTextField txtApM;
    private javax.swing.JTextField txtApP;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtMail;
    private javax.swing.JTextField txtNom;
    private javax.swing.JTextField txteMail;
    // End of variables declaration//GEN-END:variables

}
