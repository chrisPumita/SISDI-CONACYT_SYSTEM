/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Consultores;

import DataBase.ConexionMySQL;
import com.mysql.jdbc.Statement;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.MouseInfo;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Christian (PumitaChris)
 */
public class JPanelCreateReportConsultor extends javax.swing.JDialog {

    int x, y;
    DefaultListModel modelo, modelo2;
    String sSQLQuery, sSQLQueryServer, sSQLQueryServices;
    DefaultTableModel TABLARegistros;
    String USER, SISDIVersion, user;

    /**
     * Creates new form Muestra
     *
     * @param parent
     * @param modal
     * @param USER
     */
    public JPanelCreateReportConsultor(java.awt.Frame parent, boolean modal, String USER) {
        super(parent, modal);
        this.modelo = new DefaultListModel();
        this.modelo2 = new DefaultListModel();
        initComponents();
        this.setLocationRelativeTo(null);
        cargaDatos();
        this.USER = USER;
        loadVersion();

    }

    private void loadVersion() {
        Properties prop = new Properties();
        InputStream is;
        try {
            is = new FileInputStream(System.getProperty("user.dir") + "\\config.properties");
            prop.load(is);
        } catch (IOException e) {
            System.out.println(" \nERROR " + e.toString());
        }
        String versionValue = prop.getProperty("SISDIVersion");
        SISDIVersion = versionValue;
    }

    //CARGANDO LA INFORMACION INICIAL PARA LAS LISTAS DESPLEGABLES
    private void cargaDatos() {
        //CARGAR LOS VALORES DE LAS LISTAS
        CBOConsultorias.removeAllItems();
        CBOProyectos.removeAllItems();
        /*
         SERVIDORES
         SERVICIOS
         PROYECTOS
         CONSULTORIAS
         */
        String sSQL3 = "SELECT sigla_proyecto       FROM `proyecto`     WHERE estatus_proyecto ='1'     ORDER BY `proyecto`.`sigla_proyecto` ASC";
        String sSQL4 = "SELECT nombre_consultoria   FROM `consultoria`  WHERE estado_consultoria = '1'  ORDER BY `consultoria`.`nombre_consultoria` ASC";

        String[] Nombre3 = new String[1];
        String[] Nombre4 = new String[1];
        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        try {
            Statement st3 = (Statement) cn.createStatement();
            Statement st4 = (Statement) cn.createStatement();

            ResultSet rs3 = st3.executeQuery(sSQL3);
            ResultSet rs4 = st4.executeQuery(sSQL4);
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

    //VERIFICAR LA EXISTENCIA DE ELEMETOS YA AGREGADOS
    private void AgregaEnLista(int noLista) { // 1 para Consultoria, 2 para poryectos
        if (noLista == 1) {
            String nombre = CBOConsultorias.getSelectedItem().toString();
            modelo.addElement(nombre);
            ListConsultoria.setModel(modelo);
        } else if (noLista == 2) {
            String nombre = CBOProyectos.getSelectedItem().toString();
            modelo2.addElement(nombre);
            ListProyectos.setModel(modelo2);
        }
    }

    private boolean BuscarEnLista(String nombre, int noLista) {
        boolean EXISTE = false;
        String eName;
        if (noLista == 1) {
            int RowDeLista = ListConsultoria.getModel().getSize();
            if (RowDeLista == 0) {
                EXISTE = false;
            } else {
                for (int i = 0; i < ListConsultoria.getModel().getSize(); i++) {
                    eName = (String) ListConsultoria.getModel().getElementAt(i);
                    if (eName.equals(nombre)) {
                        EXISTE = true;
                    }
                }
            }
        } else if (noLista == 2) {
            int RowDeLista = ListProyectos.getModel().getSize();
            if (RowDeLista == 0) {
                EXISTE = false;
            } else {
                for (int i = 0; i < ListProyectos.getModel().getSize(); i++) {
                    eName = (String) ListProyectos.getModel().getElementAt(i);
                    if (eName.equals(nombre)) {
                        EXISTE = true;
                    }
                }
            }
        }
        return EXISTE;
    }

    private void generarConsulta() {
        sSQLQuery = "SELECT "
                + " consultor.ID_consultor AS id, "
                + "CONCAT (consultor.nombre_c, ' ' ,consultor.apellidoP_c, ' ' ,consultor.apellidoM_c) AS name, "
                + "consultor.correo_c AS correo, "
                + "IF (reg_consultor.estatus_reg_cons = 1,'ACTIVO','INACTIVO') AS edo, "
                + "consultoria.nombre_consultoria AS con, "
                + "proyecto.sigla_proyecto AS proy, "
                + "reg_consultor.f_ingreso AS fechaI, "
                + "reg_consultor.f_baja AS fechaF "
                + "FROM consultor, proyecto, consultoria, reg_consultor  "
                + "WHERE reg_consultor.ID_consultor = consultor.ID_consultor "
                + "AND reg_consultor.ID_proy = proyecto.ID_proyecto "
                + "AND reg_consultor.ID_consultoria = consultoria.ID_consultoria "
                + "AND consultor.estatus_c = 1  ";
        //name,correo,edo,con,proy,fechaI,fechaF,
        if (BoxStatus.isSelected()) {
            String value = "0";
            if (0 == CBOStatus.getSelectedIndex()) {
                value = "1";
            }
            sSQLQuery = sSQLQuery + " AND reg_consultor.estatus_reg_cons = " + value;
        }
        //Si se ha seleccionado filtrar por fecha de ingreso
        if (BoxFIngreso.isSelected()) {
            String fIngreso1, fIngreso2;
            fIngreso1 = "NULL";
            fIngreso2 = "NULL";
            Date FechaEnt1 = JDateAlta1.getDate();
            Date FechaEnt2 = JDateAlta2.getDate();
            if (FechaEnt1 != null && FechaEnt2 != null) {
                fIngreso1 = "'" + UtileriaFecha.ConvertirString(FechaEnt1) + "'";
                fIngreso2 = "'" + UtileriaFecha.ConvertirString(FechaEnt2) + "'";
            }
            sSQLQuery = sSQLQuery + " AND reg_consultor.f_ingreso BETWEEN  " + fIngreso1 + " AND " + fIngreso2 + "  ";
        }
        if (BoxFBaja.isSelected()) {
            String fBaja1, fBaja2;
            fBaja1 = "NULL";
            fBaja2 = "NULL";
            Date FechaBaja1 = JDateBaja1.getDate();
            Date FechaBaja2 = JDateBaja2.getDate();
            if (FechaBaja1 != null && FechaBaja2 != null) {
                fBaja1 = "'" + UtileriaFecha.ConvertirString(FechaBaja1) + "'";
                fBaja2 = "'" + UtileriaFecha.ConvertirString(FechaBaja2) + "'";
            }
            sSQLQuery = sSQLQuery + " AND reg_consultor.f_baja BETWEEN  " + fBaja1 + " AND " + fBaja2 + "  ";
        }

        if (BoxConsultoria.isSelected()) {
            if (ListConsultoria.getModel().getSize() > 0) {
                //Si la List tiene un elemento o mas.
                //obtener todos los elementos y concatenarlos en un or
                sSQLQuery = sSQLQuery + " AND ( ";
                String element;
                for (int i = 0; i < ListConsultoria.getModel().getSize(); i++) {
                    element = ListConsultoria.getModel().getElementAt(i);
                    if (i > 0) {
                        sSQLQuery = sSQLQuery + " OR consultoria.nombre_consultoria = '" + element + "'  ";
                    } else {
                        sSQLQuery = sSQLQuery + " consultoria.nombre_consultoria = '" + element + "'  ";
                    }
                }
                sSQLQuery = sSQLQuery + " )";
            }
        }
        if (BoxProyecto.isSelected()) {
            if (ListProyectos.getModel().getSize() > 0) {
                //Si la List tiene un elemento o mas.
                //obtener todos los elementos y concatenarlos en un or
                sSQLQuery = sSQLQuery + " AND ( ";
                String element;
                for (int i = 0; i < ListProyectos.getModel().getSize(); i++) {
                    element = ListProyectos.getModel().getElementAt(i);
                    if (i > 0) {
                        sSQLQuery = sSQLQuery + " OR proyecto.sigla_proyecto = '" + element + "'  ";
                    } else {
                        sSQLQuery = sSQLQuery + " proyecto.sigla_proyecto = '" + element + "'  ";
                    }
                }
                sSQLQuery = sSQLQuery + " )";
            }
        }

        sSQLQuery = sSQLQuery + " ORDER BY nombre_c ASC";
//        System.out.print(sSQLQuery + "\n");
        cargaDatosTable();
    }

    private void cargaDatosTable() {
        String sSQL = sSQLQuery;
        String[] registro = new String[12];
        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        int Cont = 1;

        String[] titulos = {"ID", "NOMBRE DEL CONSULTOR", "CORREO", "ESTATUS", "CONSULTORIA", "PROYECTO", "F. INGRESO", "F. BAJA"};
        TABLARegistros = new DefaultTableModel(null, titulos);

        try {
            Statement st = (Statement) cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            /* VALORES
             name
             correo
             edo
             con
             proy
             fechaI
             fechaF
             */
            while (rs.next()) {
                registro[0] = rs.getString("id");
                registro[1] = rs.getString("name");
                registro[2] = rs.getString("correo");
                registro[3] = rs.getString("edo");
                registro[4] = rs.getString("con");
                registro[5] = rs.getString("proy");
                registro[6] = rs.getString("fechaI");
                registro[7] = rs.getString("fechaF");
                TABLARegistros.addRow(registro);
                Cont++;
            }
            lblFound.setText("Se encontraron " + (Cont - (1)) + " registro(s)");
            TBLRegistros.setModel(TABLARegistros);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ERROR AL BUSCAR REGISTRO, INTENTE CON OTRO.\nERROR: " + e);
        }
        AjustaAnchosTabla();
    }

    private void AjustaAnchosTabla() {
        int[] anchos = {23, 142, 177, 52, 63, 67, 64, 64};
        for (int i = 0; i < TBLRegistros.getColumnCount(); i++) {
            TBLRegistros.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

    }

    private void limpiaTabla() {
        try {
            DefaultTableModel modelitoTabla = (DefaultTableModel) TBLRegistros.getModel();
            int filas = TBLRegistros.getRowCount();
            for (int i = 0; filas > i; i++) {
                modelitoTabla.removeRow(0);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al limpiar la tabla.");
        }
    }

    //BUSQUEDA DEL CONSULTOR QUE REGRESA EL ID PARA MANDARLO A LA CLASE DE EDITAR CONSULTOR
    private String BuscaConsultor(String consultaSQL) {
        String[] ID = new String[1];
        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        try {
            Statement st1 = (Statement) cn.createStatement();
            ResultSet rs1 = st1.executeQuery(consultaSQL);
            while (rs1.next()) {
                ID[0] = rs1.getString("ID_consultor");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No es posible obtener el ID\n ERROR: " + ex);
        }
        return ID[0];
    }

    private String getTimeDay() {
        Calendar fecha = new GregorianCalendar();
        //Obtenemos el valor del año, mes, día,
        //hora, minuto y segundo del sistema
        //usando el método get y el parámetro correspondiente
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        int hora = fecha.get(Calendar.HOUR_OF_DAY);
        int minuto = fecha.get(Calendar.MINUTE);
        int segundo = fecha.get(Calendar.SECOND);
        String f;
        f = "Fecha Actual: " + dia + "/" + (mes + 1) + "/" + año + "  " + hora + ":" + minuto + ":" + segundo;

        return f;
    }

    ////////// ---->>> REPORTES
    public void creaArchivoHTML() throws FileNotFoundException {
        String[] registro = new String[12];
        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();

        String status = "TODOS";
        String clase;

        if (BoxStatus.isSelected()) {
            if (CBOStatus.getSelectedItem().equals("ACTIVO")) {
                status = "ACTIVOS";
            } else {
                status = "INACTIVOS";
            }
        }
        int NoAgregados;
        NoAgregados = ListConsultoria.getModel().getSize();
        lblDataAgregadosCon.setText("Se han agregado " + NoAgregados + " consultorías");

        FileOutputStream os = new FileOutputStream("reporteConsultoresGeneral.html");
        PrintStream ps = new PrintStream(os);

        ///<<<<------------COLOCAR CODIGO HTML AQUI ----------------->>>>
        ps.println("<!DOCTYPE html>");
        ps.println("<html lang='en'>");
        ps.println("<head>");
        ps.println("<meta charset='UTF-8'>");
        ps.println("<title>Reporte General de Consultores</title>");
        ps.println("<link rel='stylesheet' href='css/stilo.css'>");
        ps.println("</head>");
        ps.println("<body>");
        ps.println("<div>");
        ps.println("<table class='titulo'>");
        ps.println("<tr>");
        ps.println("<td class='logo1'><img src='img/image001.png'></td>");
        ps.println("<td class='tituloDependencia'><h4>Unidad de Tecnologías de la Información y Comunicaciones</h4></td>");
        ps.println("<td class='logo2'><img src='img/image002.png' alt=''></td>");
        ps.println("</tr>");
        ps.println("</table>");
        ps.println("</div>");
        ps.println("<div class='dateTime'>");
        ps.println(getTimeDay());
        ps.println("</div>");
        ps.println("<div>");
        ps.println("<table class='tipeReport'>");
        ps.println("<tr>");
        ps.println("<td>Reporte de Consultores</td>");
        ps.println("<td>Elaborá: " + USER + "</td>");
        ps.println("</tr>");
        ps.println("</table>");
        ps.println("<br>");
        ps.println("<h4 class='detalles'>La información que se muestra a continuacion pertenece a:");
        ps.println("</h4>");
        ps.println("<table class='detallesFiltro'>");
        ps.println("<tr>");
        ps.println("<td class='desc descFechaRegistro'>Consultorías:<br>");
        ps.println("<ul>");
        for (int i = 0; i < ListConsultoria.getModel().getSize(); i++) {
            String nombre = ListConsultoria.getModel().getElementAt(i);
            ps.println("<li>" + nombre + "</li>");
        }
        ps.println("</ul>");
        ps.println("</td>");
        ps.println("<td class='desc descFechaRegistro'>Proyectos:<br>");
        ps.println("<ul>");
        for (int i = 0; i < ListProyectos.getModel().getSize(); i++) {
            String nombre = ListProyectos.getModel().getElementAt(i);
            ps.println("<li>" + nombre + "</li>");
        }
        ps.println("</ul>");
        ps.println("</td>");
        ps.println("<td class='desc descFechaRegistro'>Usuarios:<br>");
        ps.println(status);
        ps.println("</td>");
        ps.println("<td class='descFecha'>");
        ps.println("<table>");
        if (BoxFIngreso.isSelected()) {
            String fIngreso1, fIngreso2;
            fIngreso1 = "NULL";
            fIngreso2 = "NULL";
            Date FechaEnt1 = JDateAlta1.getDate();
            Date FechaEnt2 = JDateAlta2.getDate();
            if (FechaEnt1 != null && FechaEnt2 != null) {
                fIngreso1 = "'" + UtileriaFecha.ConvertirString(FechaEnt1) + "'";
                fIngreso2 = "'" + UtileriaFecha.ConvertirString(FechaEnt2) + "'";
            }
            ps.println("<tr><td>Fecha de alta es del: " + fIngreso1 + " al " + fIngreso2 + "<td></tr>");
        }
        if (BoxFBaja.isSelected()) {
            String fBaja1, fBaja2;
            fBaja1 = "NULL";
            fBaja2 = "NULL";
            Date FechaBaja1 = JDateBaja1.getDate();
            Date FechaBaja2 = JDateBaja2.getDate();
            if (FechaBaja1 != null && FechaBaja2 != null) {
                fBaja1 = "'" + UtileriaFecha.ConvertirString(FechaBaja1) + "'";
                fBaja2 = "'" + UtileriaFecha.ConvertirString(FechaBaja2) + "'";
            }
            ps.println("<tr><td>Fecha de baja es del: " + fBaja1 + " al " + fBaja2 + "</td></tr>");
        }
        ps.println("</table>");
        ps.println("					");
        ps.println("</td>");
        ps.println("</tr>");
        ps.println("</table>");
        ps.println("<table class='reporteTable'>");
        ps.println("<tr class='encabezadosTabla'>");
        ps.println("<th class='date1'>No</th>");
        ps.println("<th class='date2'>Nombre del Consultor</th>");
        ps.println("<th class='date3'>Correo</th>");
        ps.println("<th class='date4'>Estatus</th>");
        ps.println("<th class='date5'>Proyecto</th>");
        ps.println("<th class='date6'>Consultoria</th>");
        ps.println("<th class='date7'>Servicios</th>");
        ps.println("<th class='date8'>Fecha de Ingreso</th>");
        ps.println("<th class='date9'>Fecha de Baja</th>");
        ps.println("</tr>");
        // registrosGey   registrosBlanco

        ps.println("<!--COMIENZA EL CICLO DE LLENADO AUTOMATICO-->");
        try {
            Statement st = (Statement) cn.createStatement();
            ResultSet rs = st.executeQuery(sSQLQuery);
            int numeracion = 0;
            while (rs.next()) {
                ps.println("<tr>");
//                registro[0] = numeracion;
                registro[0] = rs.getString("name");
                registro[1] = rs.getString("correo");
                registro[2] = rs.getString("edo");
                registro[3] = rs.getString("con");
                registro[4] = rs.getString("proy");
                registro[5] = rs.getString("fechaI");
                registro[6] = rs.getString("fechaF");
                numeracion++;
                if (numeracion % 2 == 0) {
                    clase = "registrosGey";
                } else {
                    clase = "registrosBlanco";
                }
                ps.println("<tr class='" + clase + "'>");
                ps.println("<td class='registro1'>" + numeracion + "</td>");
                ps.println("<td class='registro2'>" + registro[0] + "</td>");
                ps.println("<td class='registro3'>" + registro[1] + "</td>");
                if (registro[2].equals("ACTIVO")) {
                    ps.println("<td class='registro4'><img src='img/active.png' alt='" + registro[2] + "'></td>");
                } else {
                    ps.println("<td class='registro4'><img src='img/inactive.png' alt='" + registro[2] + "'></td>");
                }
                ps.println("<td class='registro5'>" + registro[3] + "</td>");
                ps.println("<td class='registro6'>" + registro[4] + "</td>");
                ps.println("<!--COMIENZA A LLENAR LOS SERVICIOS Y SERVIDORES-->");
                ps.println("<td class='registro7'>SERVIDORES<br>SERVICIOS VARIOS</td>");
                if (registro[5] == null) {
                    ps.println("<td class='registro8'></td>");
                } else {
                    ps.println("<td class='registro8'>" + registro[5] + "</td>");
                }
                if (registro[6] == null) {
                    ps.println("<td class='registro9'></td>");
                } else {
                    ps.println("<td class='registro9'>" + registro[6] + "</td>");
                }
                ps.println("</tr>");
            }
            ps.println("<!--TERMINA EL CICLO DE LLENADO AUTOMATICO-->");
            ps.println("</table>");
            ps.println("<p class='creditos'>Reporte generado por SISDI (Sistema Integral de la Subdirección de Desarrollo e Innovación) Ver " + SISDIVersion + " © 2016 Derechos Reservados</p>");
            ps.println("</div>");
            ps.println("</body>");
            ps.println("</html>");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error geberate HTML \n" + ex);
        }
    }

    public void abrirarchivo(String archivo) {
        try {
            File objetofile = new File(archivo);
            Desktop.getDesktop().open(objetofile);
        } catch (IOException ex) {
            System.out.println(ex);
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
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        lblFound1 = new javax.swing.JLabel();
        BoxStatus = new javax.swing.JCheckBox();
        CBOStatus = new javax.swing.JComboBox<>();
        BoxFIngreso = new javax.swing.JCheckBox();
        JDateAlta1 = new com.toedter.calendar.JDateChooser();
        lblFound8 = new javax.swing.JLabel();
        JDateAlta2 = new com.toedter.calendar.JDateChooser();
        BoxFBaja = new javax.swing.JCheckBox();
        JDateBaja1 = new com.toedter.calendar.JDateChooser();
        lblFound9 = new javax.swing.JLabel();
        JDateBaja2 = new com.toedter.calendar.JDateChooser();
        jPanel3 = new javax.swing.JPanel();
        BoxProyecto = new javax.swing.JCheckBox();
        btnAddProyect = new javax.swing.JLabel();
        CBOProyectos = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        ListProyectos = new javax.swing.JList<>();
        btnQuitarProy = new javax.swing.JLabel();
        btnAddAllProyects = new javax.swing.JLabel();
        lblMensajeProy = new javax.swing.JLabel();
        btnEnviar9 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        BoxConsultoria = new javax.swing.JCheckBox();
        btnAddCon = new javax.swing.JLabel();
        CBOConsultorias = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        ListConsultoria = new javax.swing.JList<>();
        btnQuitarCon = new javax.swing.JLabel();
        btnAddAllCon = new javax.swing.JLabel();
        lblDataAgregadosCon = new javax.swing.JLabel();
        btnEnviar6 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        BoxService = new javax.swing.JCheckBox();
        lblFound2 = new javax.swing.JLabel();
        BoxServers = new javax.swing.JCheckBox();
        btnEnviar8 = new javax.swing.JLabel();
        btnNuevo1 = new javax.swing.JLabel();
        lblFound4 = new javax.swing.JLabel();
        lblFound = new javax.swing.JLabel();
        lblFound3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        TBLRegistros = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(0, 102, 102));
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filtrar Busqueda", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 18), new java.awt.Color(102, 102, 102))); // NOI18N
        jPanel7.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N

        lblFound1.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound1.setForeground(new java.awt.Color(51, 51, 51));
        lblFound1.setText("Mostrar Consultores donde:");

        BoxStatus.setBackground(new java.awt.Color(255, 255, 255));
        BoxStatus.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        BoxStatus.setSelected(true);
        BoxStatus.setText("Estatus:");
        BoxStatus.setToolTipText("");
        BoxStatus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BoxStatusMouseClicked(evt);
            }
        });

        CBOStatus.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        CBOStatus.setForeground(new java.awt.Color(51, 51, 51));
        CBOStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ACTIVO", "INACTIVO" }));

        BoxFIngreso.setBackground(new java.awt.Color(255, 255, 255));
        BoxFIngreso.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        BoxFIngreso.setText("Fecha de Ingreso sea:  del");

        lblFound8.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        lblFound8.setForeground(new java.awt.Color(51, 51, 51));
        lblFound8.setText("al");

        BoxFBaja.setBackground(new java.awt.Color(255, 255, 255));
        BoxFBaja.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        BoxFBaja.setText("Fecha de Baja sea:        del");
        BoxFBaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BoxFBajaActionPerformed(evt);
            }
        });

        lblFound9.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        lblFound9.setForeground(new java.awt.Color(51, 51, 51));
        lblFound9.setText("al");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        BoxProyecto.setBackground(new java.awt.Color(255, 255, 255));
        BoxProyecto.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        BoxProyecto.setText("Proyecto sea:");
        BoxProyecto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BoxProyectoMouseClicked(evt);
            }
        });

        btnAddProyect.setBackground(Color.decode("#4D92DF"));
        btnAddProyect.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnAddProyect.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnAddProyect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Down.png"))); // NOI18N
        btnAddProyect.setToolTipText("Agregar");
        btnAddProyect.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnAddProyect.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddProyect.setEnabled(false);
        btnAddProyect.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddProyectMouseClicked(evt);
            }
        });

        CBOProyectos.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        CBOProyectos.setForeground(new java.awt.Color(51, 51, 51));
        CBOProyectos.setEnabled(false);

        ListProyectos.setEnabled(false);
        ListProyectos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ListProyectosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(ListProyectos);

        btnQuitarProy.setBackground(Color.decode("#4D92DF"));
        btnQuitarProy.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnQuitarProy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnQuitarProy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Delete_2.png"))); // NOI18N
        btnQuitarProy.setToolTipText("Quitar");
        btnQuitarProy.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnQuitarProy.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQuitarProy.setEnabled(false);
        btnQuitarProy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnQuitarProyMouseClicked(evt);
            }
        });

        btnAddAllProyects.setBackground(Color.decode("#4D92DF"));
        btnAddAllProyects.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnAddAllProyects.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnAddAllProyects.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/DownAll.png"))); // NOI18N
        btnAddAllProyects.setToolTipText("Agregar Todos");
        btnAddAllProyects.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnAddAllProyects.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddAllProyects.setEnabled(false);
        btnAddAllProyects.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddAllProyectsMouseClicked(evt);
            }
        });

        lblMensajeProy.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        lblMensajeProy.setForeground(new java.awt.Color(51, 51, 51));
        lblMensajeProy.setText("Se han agregado 0 proyectos");
        lblMensajeProy.setEnabled(false);

        btnEnviar9.setBackground(Color.decode("#4D92DF"));
        btnEnviar9.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnEnviar9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnEnviar9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Clear.png"))); // NOI18N
        btnEnviar9.setToolTipText("Quitar todo");
        btnEnviar9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnEnviar9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEnviar9.setEnabled(false);
        btnEnviar9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEnviar9MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblMensajeProy, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(BoxProyecto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(20, 20, 20))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(CBOProyectos, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnAddProyect)
                                    .addComponent(btnQuitarProy, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addComponent(btnEnviar9, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(btnAddAllProyects, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BoxProyecto)
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(CBOProyectos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddAllProyects, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnAddProyect, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEnviar9, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnQuitarProy, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMensajeProy)
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        BoxConsultoria.setBackground(new java.awt.Color(255, 255, 255));
        BoxConsultoria.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        BoxConsultoria.setText("Consultoría sea:");
        BoxConsultoria.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BoxConsultoriaMouseClicked(evt);
            }
        });
        BoxConsultoria.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                BoxConsultoriaPropertyChange(evt);
            }
        });

        btnAddCon.setBackground(Color.decode("#4D92DF"));
        btnAddCon.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnAddCon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnAddCon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Down.png"))); // NOI18N
        btnAddCon.setToolTipText("Agregar");
        btnAddCon.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnAddCon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddCon.setEnabled(false);
        btnAddCon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddConMouseClicked(evt);
            }
        });

        CBOConsultorias.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        CBOConsultorias.setForeground(new java.awt.Color(51, 51, 51));
        CBOConsultorias.setEnabled(false);

        ListConsultoria.setEnabled(false);
        jScrollPane1.setViewportView(ListConsultoria);

        btnQuitarCon.setBackground(Color.decode("#4D92DF"));
        btnQuitarCon.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnQuitarCon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnQuitarCon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Delete_2.png"))); // NOI18N
        btnQuitarCon.setToolTipText("Quitar");
        btnQuitarCon.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnQuitarCon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQuitarCon.setEnabled(false);
        btnQuitarCon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnQuitarConMouseClicked(evt);
            }
        });

        btnAddAllCon.setBackground(Color.decode("#4D92DF"));
        btnAddAllCon.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnAddAllCon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnAddAllCon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/DownAll.png"))); // NOI18N
        btnAddAllCon.setToolTipText("Agregar Todos");
        btnAddAllCon.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnAddAllCon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddAllCon.setEnabled(false);
        btnAddAllCon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddAllConMouseClicked(evt);
            }
        });

        lblDataAgregadosCon.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        lblDataAgregadosCon.setForeground(new java.awt.Color(51, 51, 51));
        lblDataAgregadosCon.setText("Se han agregado 0 consultorías");
        lblDataAgregadosCon.setEnabled(false);

        btnEnviar6.setBackground(Color.decode("#4D92DF"));
        btnEnviar6.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnEnviar6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnEnviar6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Clear.png"))); // NOI18N
        btnEnviar6.setToolTipText("Quitar todo");
        btnEnviar6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnEnviar6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEnviar6.setEnabled(false);
        btnEnviar6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEnviar6MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDataAgregadosCon, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CBOConsultorias, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnAddCon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnQuitarCon)
                                .addComponent(btnEnviar6))
                            .addComponent(btnAddAllCon, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(BoxConsultoria, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BoxConsultoria)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(CBOConsultorias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddAllCon, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnAddCon, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22)
                        .addComponent(btnEnviar6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnQuitarCon, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblDataAgregadosCon)
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        BoxService.setBackground(new java.awt.Color(255, 255, 255));
        BoxService.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        BoxService.setText("Mostrar Servicios");
        BoxService.setEnabled(false);

        lblFound2.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound2.setForeground(new java.awt.Color(51, 51, 51));
        lblFound2.setText("Opciones de Impresión:");

        BoxServers.setBackground(new java.awt.Color(255, 255, 255));
        BoxServers.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        BoxServers.setText("Mostrar Servidores");
        BoxServers.setEnabled(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BoxService)
                    .addComponent(lblFound2)
                    .addComponent(BoxServers))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFound2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BoxService)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BoxServers)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnEnviar8.setBackground(Color.decode("#4D92DF"));
        btnEnviar8.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnEnviar8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnEnviar8.setText("Imprimir");
        btnEnviar8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnEnviar8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEnviar8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEnviar8MouseClicked(evt);
            }
        });

        btnNuevo1.setBackground(Color.decode("#4D92DF"));
        btnNuevo1.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnNuevo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnNuevo1.setText("Filtrar");
        btnNuevo1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnNuevo1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNuevo1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnNuevo1MouseClicked(evt);
            }
        });

        lblFound4.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        lblFound4.setForeground(new java.awt.Color(51, 51, 51));
        lblFound4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblFound4.setText("<html>Si no marca las casillas se considerará todos los elementos");
        lblFound4.setEnabled(false);
        lblFound4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFound1)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(BoxStatus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CBOStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(16, 16, 16)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(BoxFIngreso)
                            .addComponent(BoxFBaja))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(JDateAlta1, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                            .addComponent(JDateBaja1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFound8)
                            .addComponent(lblFound9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(JDateAlta2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(JDateBaja2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                .addComponent(btnNuevo1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(btnEnviar8, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblFound4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(JDateAlta1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(JDateAlta2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblFound8))
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblFound1)
                        .addComponent(BoxFIngreso)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(BoxFBaja, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(BoxStatus)
                        .addComponent(CBOStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(JDateBaja2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(JDateBaja1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblFound9)))
                .addGap(6, 6, 6)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnEnviar8, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnNuevo1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblFound4)))
                .addContainerGap())
        );

        lblFound.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound.setForeground(new java.awt.Color(51, 51, 51));
        lblFound.setText("Se encontraron: # registros");

        lblFound3.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        lblFound3.setForeground(new java.awt.Color(51, 51, 51));
        lblFound3.setText("Seleccione un consultor para ver y editar la información");
        lblFound3.setEnabled(false);

        TBLRegistros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        TBLRegistros.setEnabled(false);
        TBLRegistros.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TBLRegistrosMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(TBLRegistros);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(lblFound)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblFound3)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFound)
                    .addComponent(lblFound3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

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

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("Generar Reporte de Registro de Consultor");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25)
                    .addComponent(jLabel1))
                .addGap(247, 247, 247))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void BoxFBajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BoxFBajaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BoxFBajaActionPerformed

    private void btnAddConMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddConMouseClicked
        if (btnAddCon.isEnabled()) {
            String nombreServicio = CBOConsultorias.getSelectedItem().toString();
            //dar el id del Servicio elegido
            if (BuscarEnLista(nombreServicio, 1) == true) {
                //Ya existe
                JOptionPane.showMessageDialog(null, "Esta Consultoria ya fue añadida al filtro, elija otro.");
            } else {
                AgregaEnLista(1);
                int NoAgregados;
                NoAgregados = ListConsultoria.getModel().getSize();
                lblDataAgregadosCon.setText("Se han agregado " + NoAgregados + " consultorías");
            }
        }

    }//GEN-LAST:event_btnAddConMouseClicked

    private void btnAddProyectMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddProyectMouseClicked
        if (btnAddProyect.isEnabled()) {
            String nombreServicio = CBOProyectos.getSelectedItem().toString();
            //dar el id del Servicio elegido
            if (BuscarEnLista(nombreServicio, 2) == true) {
                //Ya existe
                JOptionPane.showMessageDialog(null, "Este proyecto ya fue añadida al filtro, elija otro.");
            } else {
                AgregaEnLista(2);
                int NoAgregados;
                NoAgregados = ListProyectos.getModel().getSize();
                lblDataAgregadosCon.setText("Se han agregado " + NoAgregados + " proyectos");
            }
        }


    }//GEN-LAST:event_btnAddProyectMouseClicked

    private void btnAddAllConMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddAllConMouseClicked
        //AGREGA TODOS DE GOLPE
        if (btnAddAllCon.isEnabled()) {
            modelo.clear();
            for (int i = 0; i < CBOConsultorias.getItemCount(); i++) {
                String nombre = CBOConsultorias.getItemAt(i);
                modelo.addElement(nombre);
                ListConsultoria.setModel(modelo);
            }
            int NoAgregados;
            NoAgregados = ListConsultoria.getModel().getSize();
            lblDataAgregadosCon.setText("Se han agregado " + NoAgregados + " consultorías");
        }

    }//GEN-LAST:event_btnAddAllConMouseClicked

    private void btnAddAllProyectsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddAllProyectsMouseClicked
        if (btnAddAllProyects.isEnabled()) {
            modelo2.clear();
            for (int i = 0; i < CBOProyectos.getItemCount(); i++) {
                String nombre = CBOProyectos.getItemAt(i);
                modelo2.addElement(nombre);
                ListProyectos.setModel(modelo2);
            }
            int NoAgregados;
            NoAgregados = ListProyectos.getModel().getSize();
            lblMensajeProy.setText("Se han agregado " + NoAgregados + " proyectos");
        }

    }//GEN-LAST:event_btnAddAllProyectsMouseClicked

    private void btnEnviar6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEnviar6MouseClicked
        modelo.clear();
        int NoAgregados;
        NoAgregados = ListConsultoria.getModel().getSize();
        lblDataAgregadosCon.setText("Se han agregado " + NoAgregados + " consultorías");
    }//GEN-LAST:event_btnEnviar6MouseClicked

    private void btnEnviar9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEnviar9MouseClicked
        modelo2.clear();
        int NoAgregados;
        NoAgregados = ListProyectos.getModel().getSize();
        lblMensajeProy.setText("Se han agregado " + NoAgregados + " proyectos");
    }//GEN-LAST:event_btnEnviar9MouseClicked

    private void btnNuevo1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNuevo1MouseClicked
        limpiaTabla();
        generarConsulta();
        TBLRegistros.setEnabled(true);
    }//GEN-LAST:event_btnNuevo1MouseClicked

    private void btnQuitarConMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnQuitarConMouseClicked
        if (btnQuitarCon.isEnabled()) {
            int elementSelect = ListConsultoria.getSelectedIndex();
            if (elementSelect < 0) {
                JOptionPane.showMessageDialog(null, "Debe elejir un elemento para quitar");
            } else {
                modelo.removeElementAt(elementSelect);
            }
        }
        int NoAgregados;
        NoAgregados = ListConsultoria.getModel().getSize();
        lblDataAgregadosCon.setText("Se han agregado " + NoAgregados + " consultorías");
    }//GEN-LAST:event_btnQuitarConMouseClicked

    private void ListProyectosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ListProyectosMouseClicked

    }//GEN-LAST:event_ListProyectosMouseClicked

    private void btnQuitarProyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnQuitarProyMouseClicked
        if (btnQuitarProy.isEnabled()) {
            int elementSelect = ListProyectos.getSelectedIndex();
            if (elementSelect < 0) {
                JOptionPane.showMessageDialog(null, "Debe elejir un elemento para quitar");
            } else {
                modelo2.removeElementAt(elementSelect);
            }
        }
        int NoAgregados;
        NoAgregados = ListProyectos.getModel().getSize();
        lblMensajeProy.setText("Se han agregado " + NoAgregados + " proyectos");
    }//GEN-LAST:event_btnQuitarProyMouseClicked

    private void BoxConsultoriaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_BoxConsultoriaPropertyChange

    }//GEN-LAST:event_BoxConsultoriaPropertyChange

    private void BoxConsultoriaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BoxConsultoriaMouseClicked
        if (BoxConsultoria.isSelected()) {
            ListConsultoria.setEnabled(true);
            btnAddCon.setEnabled(true);
            btnAddAllCon.setEnabled(true);
            btnEnviar6.setEnabled(true);
            btnQuitarCon.setEnabled(true);
            CBOConsultorias.setEnabled(true);
            modelo.clear();
            int NoAgregados;
            NoAgregados = ListConsultoria.getModel().getSize();
            lblDataAgregadosCon.setText("Se han agregado " + NoAgregados + " consultorías");
            ListConsultoria.setSelectedIndex(0);
        } else {
            ListConsultoria.setEnabled(false);
            btnAddCon.setEnabled(false);
            btnAddAllCon.setEnabled(false);
            btnEnviar6.setEnabled(false);
            btnQuitarCon.setEnabled(false);
            CBOConsultorias.setEnabled(false);
            modelo.clear();
            int NoAgregados;
            NoAgregados = ListConsultoria.getModel().getSize();
            lblDataAgregadosCon.setText("Se han agregado " + NoAgregados + " consultorías");
            ListConsultoria.setSelectedIndex(0);
        }
    }//GEN-LAST:event_BoxConsultoriaMouseClicked

    private void BoxProyectoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BoxProyectoMouseClicked
        if (BoxProyecto.isSelected()) {
            CBOProyectos.setEnabled(true);
            btnAddAllProyects.setEnabled(true);
            btnAddProyect.setEnabled(true);
            btnEnviar9.setEnabled(true);
            btnQuitarProy.setEnabled(true);
            ListProyectos.setEnabled(true);
            modelo2.clear();
            int NoAgregados;
            NoAgregados = ListProyectos.getModel().getSize();
            lblMensajeProy.setText("Se han agregado " + NoAgregados + " consultorías");
            ListProyectos.setSelectedIndex(0);
        } else {
            CBOProyectos.setEnabled(false);
            btnAddAllProyects.setEnabled(false);
            btnAddProyect.setEnabled(false);
            btnEnviar9.setEnabled(false);
            btnQuitarProy.setEnabled(false);
            ListProyectos.setEnabled(false);
            modelo2.clear();
            int NoAgregados;
            NoAgregados = ListProyectos.getModel().getSize();
            lblMensajeProy.setText("Se han agregado " + NoAgregados + " consultorías");
            ListProyectos.setSelectedIndex(0);
        }
    }//GEN-LAST:event_BoxProyectoMouseClicked

    private void BoxStatusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BoxStatusMouseClicked
        if (BoxStatus.isSelected()) {
            CBOStatus.setEnabled(true);
        } else {
            CBOStatus.setEnabled(false);
        }
    }//GEN-LAST:event_BoxStatusMouseClicked

    private void btnEnviar8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEnviar8MouseClicked
        limpiaTabla();
        generarConsulta();
        TBLRegistros.setEnabled(true);
        try {
            creaArchivoHTML();
            abrirarchivo("reporteConsultoresGeneral.html");
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "No es posible generar el documento, porfavor verifique" + ex);
        }

//        try {
//            TBLRegistros.print(JTable.PrintMode.FIT_WIDTH);
//        } catch (PrinterException ex) {
//            Logger.getLogger(JPanelCreateReportConsultor.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_btnEnviar8MouseClicked

    private void TBLRegistrosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TBLRegistrosMouseClicked
        if (TBLRegistros.isEnabled()) {
            String id = TBLRegistros.getValueAt(TBLRegistros.getSelectedRow(), 0).toString();
            String sSQL = "SELECT ID_consultor FROM consultor WHERE ID_consultor = " + id + " AND estatus_c = '1' ;";
//        JOptionPane.showMessageDialog(null, nombre + " " + apellidoP + " " + apellidoM);
            String valorID = BuscaConsultor(sSQL);

            if (valorID == null) {
                //El usuario esta inactivo
                JOptionPane.showMessageDialog(null, "Al parecer este consultor está Inhabiltado.\nActive al consultor en el Catálogo");
            } else {
                Frame Admimistracion = null;
                JPanelUpdateNewConsultor PanelAdministrador = new JPanelUpdateNewConsultor(Admimistracion, true, valorID);
                PanelAdministrador.setVisible(true);
            }
        }
    }//GEN-LAST:event_TBLRegistrosMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox BoxConsultoria;
    private javax.swing.JCheckBox BoxFBaja;
    private javax.swing.JCheckBox BoxFIngreso;
    private javax.swing.JCheckBox BoxProyecto;
    private javax.swing.JCheckBox BoxServers;
    private javax.swing.JCheckBox BoxService;
    private javax.swing.JCheckBox BoxStatus;
    private javax.swing.JComboBox<String> CBOConsultorias;
    private javax.swing.JComboBox<String> CBOProyectos;
    private javax.swing.JComboBox<String> CBOStatus;
    private com.toedter.calendar.JDateChooser JDateAlta1;
    private com.toedter.calendar.JDateChooser JDateAlta2;
    private com.toedter.calendar.JDateChooser JDateBaja1;
    private com.toedter.calendar.JDateChooser JDateBaja2;
    private javax.swing.JList<String> ListConsultoria;
    private javax.swing.JList<String> ListProyectos;
    private javax.swing.JTable TBLRegistros;
    private javax.swing.JLabel btnAddAllCon;
    private javax.swing.JLabel btnAddAllProyects;
    private javax.swing.JLabel btnAddCon;
    private javax.swing.JLabel btnAddProyect;
    private javax.swing.JLabel btnEnviar6;
    private javax.swing.JLabel btnEnviar8;
    private javax.swing.JLabel btnEnviar9;
    private javax.swing.JLabel btnNuevo1;
    private javax.swing.JLabel btnQuitarCon;
    private javax.swing.JLabel btnQuitarProy;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblDataAgregadosCon;
    private javax.swing.JLabel lblFound;
    private javax.swing.JLabel lblFound1;
    private javax.swing.JLabel lblFound2;
    private javax.swing.JLabel lblFound3;
    private javax.swing.JLabel lblFound4;
    private javax.swing.JLabel lblFound8;
    private javax.swing.JLabel lblFound9;
    private javax.swing.JLabel lblMensajeProy;
    // End of variables declaration//GEN-END:variables
}
