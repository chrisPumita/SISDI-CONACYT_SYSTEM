/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Consultores;

import DataBase.ConexionMySQL;
import com.mysql.jdbc.Statement;
import java.awt.Color;
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
import javax.swing.JOptionPane;

/**
 *
 * @author C0nacyt
 */
public class JPanelAddService extends javax.swing.JDialog {

    String IDRegService, nombreService, ID_RegCons;
    int x, y;
    boolean statusRegistro = true;
    String accion;

    /**
     * Creates new form AddService
     *
     * @param parent
     * @param modal
     * @param nombreService
     * @param accion
     * @param IDRegService
     * @param ID_RegCons
     */
    public JPanelAddService(java.awt.Frame parent, boolean modal, String nombreService, String IDRegService, String accion, String ID_RegCons) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        this.nombreService = nombreService;
        this.IDRegService = IDRegService;
        this.accion = accion;
        this.ID_RegCons = ID_RegCons;
        cargaDatos();
        if (accion.equals("NEW")) {
            //NO ENTRA ID Por lo tanto es nuevo reg Service
            //colocar en el calendar la fecha de hoy

//            Calendar dateToday = new GregorianCalendar();
//            JDateAlta.setCalendar(dateToday);
            CBOServicios.setSelectedItem(nombreService);
            lblTitulo.setText("Agregar Servicio");
            btnEnviar.setText("Agregar");
            btnCancel.setText("Cancelar");
        } else {
            //CARGAR SERVICIO
            lblTitulo.setText("Actualizar Servicio");
            cargaServicioUpdate(IDRegService);
            btnEnviar.setText("Actualizar");
            btnCancel.setText("Quitar");
        }
        txtUserService.requestFocus();
    }

    private void cargaDatos() {
        //CARGAR LOS VALORES DE LAS LISTAS
        String sSQL2 = "SELECT nombre_s "
                + "FROM `servicio` "
                + "WHERE estatus_servicio = '1' "
                + "ORDER BY `servicio`.`nombre_s` ASC";

        String[] Nombre2 = new String[1];
        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        try {
            Statement st2 = (Statement) cn.createStatement();
            ResultSet rs2 = st2.executeQuery(sSQL2);
            while (rs2.next()) {
                Nombre2[0] = rs2.getString("nombre_s");
                CBOServicios.addItem(Nombre2[0]);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No es posible cargar la informacion:\n ERROR: " + ex);
        }
    }

    private void cargaServicioUpdate(String idRegService) {
        String sSQL = "SELECT "
                + "usuario_servicio, "
                + "password_servicio, "
                + "fecha_alta, "
                + "estatus_acceso_serv, "
                + "descripcion_serv "
                + "FROM reg_servicio "
                + "WHERE ID_reg_serv = " + idRegService + ";";
        String[] registro = new String[9];
        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        try {
            Statement st1 = (Statement) cn.createStatement();

            ResultSet rs1 = st1.executeQuery(sSQL);
            while (rs1.next()) {
                registro[0] = rs1.getString("usuario_servicio");
                registro[1] = rs1.getString("password_servicio");
                registro[2] = rs1.getString("fecha_alta");
                registro[3] = rs1.getString("estatus_acceso_serv");
                registro[4] = rs1.getString("descripcion_serv");
            }
            // LLENANDO LOS CAMPOS
            CBOServicios.setSelectedItem(nombreService);
            txtUserService.setText(registro[0]);
            txtPassword.setText(registro[1]);

            //CONVERTIR LA FECHA DE SQL A FORMATO JDATE
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaDate = null;
            if ((registro[2]) == null) {
                Calendar dateToday = new GregorianCalendar();
                JDateAlta.setCalendar(dateToday);
            } else {
                try {
                    fechaDate = formato.parse(registro[2]);
                    Calendar newCalendar = Calendar.getInstance();
                    newCalendar.setTime(fechaDate);
                    JDateAlta.setCalendar(newCalendar);
                } catch (ParseException ex) {
                    System.out.println("ERROR AL CONVERTIR LA FECHA INICIO\n" + ex);
                }
            }

            textAreaDesc.setText(registro[4]);

            //CONVERTIR LA FECHA DE SQL A FORMATO JDATE
            if (registro[3].equals("0") && accion.equals("NEW")) {
                //EL SERVIICIO POR DEFAULT ESTA BLOQUEADO, SE MOSTRARA COMO ACTIVO Y SE ACTUALIZARA AL GUARDAR
                statusRegistro = true;
                lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/on.png")));
                lblRegistroStatus.setToolTipText("ACTIVO");
            } else if (registro[3].equals("0") && accion.equals("UPDATE")) {
                //EL SERVICIO YA ESTABA BAJA, MOSTRARLO BAJA
                statusRegistro = false;
                lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/off.png")));
                lblRegistroStatus.setToolTipText("INACTIVO");
            } else if (registro[3].equals("1") && accion.equals("UPDATE")) {
                //El registro esta activo, mostrarlo Activo
                statusRegistro = true;
                lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/on.png")));
                lblRegistroStatus.setToolTipText("ACTIVO");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No es posible cargar la informacion:\n ERROR: " + ex);
        }
    }

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

    private void GuardarRegistro() {
        String sentenciaSQL = "";
        String userService, passwordService, fAlta, statusService, desService;

        userService = txtUserService.getText();
        passwordService = txtPassword.getText();
        fAlta = "NULL";
        Date FechaEnt = JDateAlta.getDate();
        if (FechaEnt != null) {
            fAlta = "'"+UtileriaFecha.ConvertirString(FechaEnt)+"'";
        }
        String valueStatus = "";
        if (true != statusRegistro) {
            valueStatus = "0";
        } else {
            valueStatus = "1";
        }
        statusService = valueStatus;
        desService = textAreaDesc.getText();
        //En el caso de que sea un UPDATE unicamente se tomara el ID que entro a la clase
        //Si el registro es nuevo, el ID se obtendra de la base de datos
        //Insertará o actualiazará dependiendo la accion a realizar
        if (accion.equals("NEW")) {

            //AGREGA NUEVO
            sentenciaSQL = "INSERT INTO reg_servicio "
                    + "(ID_reg_serv, nombre_serv, usuario_servicio, password_servicio, fecha_alta, "
                    + "ID_serv, ID_regConsultorFK, estatus_acceso_serv, descripcion_serv) VALUES ("
                    + "NULL, '" + CBOServicios.getSelectedItem().toString() + "', '" + userService + "', '" + passwordService + "',"
                    + " " + fAlta + ", '" + dameID() + "', '" + ID_RegCons + "', '" + statusService + "', '" + desService + "')";

            ExecuteQuery(sentenciaSQL, "Nuevo registro");
            Hilo hilo = new Hilo(300, 30);
            hilo.agregarTexto("SE HA CREADO AGREGADO EL SERVICIO " + IDRegService + "", 1);
            hilo.start();
            this.dispose();
        } else {
            //ACTUALIZAR ELEMENOT
            sentenciaSQL = "UPDATE reg_servicio SET "
                    + "nombre_serv = '" + CBOServicios.getSelectedItem().toString() + "', "
                    + "usuario_servicio = '" + userService + "', "
                    + "password_servicio = '" + passwordService + "', "
                    + "fecha_alta = " + fAlta + ", "
                    + "ID_serv = '" + dameID() + "', "
                    + "ID_regConsultorFK = '" + ID_RegCons + "', "
                    + "estatus_acceso_serv = '" + statusService + "', "
                    + "descripcion_serv = '" + desService + "' "
                    + "WHERE reg_servicio.ID_reg_serv = " + IDRegService + "  ";
            ExecuteQuery(sentenciaSQL, "Actualizar registro de servicio");
            Hilo hilo = new Hilo(300, 30);
            hilo.agregarTexto("SE ACTUALIZÓ EL SERVICIO " + IDRegService + "", 1);
            hilo.start();
            this.dispose();
        }
    }

    private String DameIDRegistro() {
        String IDRegistro;
        IDRegistro = "";
        String sSQL = " SELECT @@identity AS id;";
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

    private boolean ExecuteQuery(String sSQL, String Message) {
        boolean execute = false;
        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        try {
            com.mysql.jdbc.PreparedStatement pst = (com.mysql.jdbc.PreparedStatement) cn.prepareStatement(sSQL);
            execute = pst.execute();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, Message+"\nNO UPDATE SQL" + ex);
        }

        return execute;
    }

    private String dameID() {
        String sSQL1 = "SELECT `ID_servicio` FROM `servicio` WHERE `nombre_s` = '" + nombreService + "'";
        String[] IDServ = new String[1];

        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        try {
            Statement st1 = (Statement) cn.createStatement();

            ResultSet rs1 = st1.executeQuery(sSQL1);
            while (rs1.next()) {
                IDServ[0] = rs1.getString("ID_servicio");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No es posible cargar la informacion:\n ERROR: " + ex);
        }
        return IDServ[0];
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
        btnEnviar = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        lblFound = new javax.swing.JLabel();
        txtUserService = new javax.swing.JTextField();
        lblFound1 = new javax.swing.JLabel();
        lblFound2 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JTextField();
        lblFound3 = new javax.swing.JLabel();
        lblFound4 = new javax.swing.JLabel();
        lblRegistroStatus = new javax.swing.JLabel();
        lblFound5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textAreaDesc = new javax.swing.JTextArea();
        JDateAlta = new com.toedter.calendar.JDateChooser();
        CBOServicios = new javax.swing.JComboBox<>();
        btnCancel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setUndecorated(true);

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
        lblTitulo.setText("text_example");

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

        btnEnviar.setBackground(Color.decode("#4D92DF"));
        btnEnviar.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnEnviar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnEnviar.setText("azul");
        btnEnviar.setToolTipText("Enviar");
        btnEnviar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnEnviar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEnviar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEnviarMouseClicked(evt);
            }
        });

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información de Servicio", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 18), new java.awt.Color(102, 102, 102))); // NOI18N
        jPanel7.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N

        lblFound.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound.setForeground(new java.awt.Color(51, 51, 51));
        lblFound.setText("Servicio:");

        txtUserService.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txtUserService.setForeground(new java.awt.Color(51, 51, 51));

        lblFound1.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound1.setForeground(new java.awt.Color(51, 51, 51));
        lblFound1.setText("Usuario:");

        lblFound2.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound2.setForeground(new java.awt.Color(51, 51, 51));
        lblFound2.setText("Password:");

        txtPassword.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txtPassword.setForeground(new java.awt.Color(51, 51, 51));

        lblFound3.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound3.setForeground(new java.awt.Color(51, 51, 51));
        lblFound3.setText("Fecha de Alta:");

        lblFound4.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound4.setForeground(new java.awt.Color(51, 51, 51));
        lblFound4.setText("Estatus:");

        lblRegistroStatus.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblRegistroStatus.setForeground(new java.awt.Color(51, 51, 51));
        lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/on.png"))); // NOI18N
        lblRegistroStatus.setToolTipText("ACTIVO");
        lblRegistroStatus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblRegistroStatus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblRegistroStatusMouseClicked(evt);
            }
        });

        lblFound5.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound5.setForeground(new java.awt.Color(51, 51, 51));
        lblFound5.setText("Descripcion:");

        textAreaDesc.setColumns(20);
        textAreaDesc.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        textAreaDesc.setForeground(new java.awt.Color(51, 51, 51));
        textAreaDesc.setRows(5);
        jScrollPane1.setViewportView(textAreaDesc);

        JDateAlta.setBackground(new java.awt.Color(0, 0, 153));
        JDateAlta.setDateFormatString("yyyy-MM-dd");

        CBOServicios.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        CBOServicios.setForeground(new java.awt.Color(51, 51, 51));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(lblFound)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CBOServicios, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblFound2)
                            .addComponent(lblFound3)
                            .addComponent(lblFound1))
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(txtUserService, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblRegistroStatus)
                                    .addComponent(txtPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                                    .addComponent(JDateAlta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(lblFound4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFound5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFound)
                    .addComponent(CBOServicios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFound1)
                    .addComponent(txtUserService, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFound2)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblFound3)
                    .addComponent(JDateAlta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblFound4)
                    .addComponent(lblRegistroStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFound5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btnCancel.setBackground(new java.awt.Color(204, 0, 0));
        btnCancel.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        btnCancel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnCancel.setText("rojo");
        btnCancel.setToolTipText("Nuevo");
        btnCancel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCancelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnEnviar, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void lblRegistroStatusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblRegistroStatusMouseClicked
        cambiaValor();
    }//GEN-LAST:event_lblRegistroStatusMouseClicked

    private void btnEnviarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEnviarMouseClicked
        GuardarRegistro();
    }//GEN-LAST:event_btnEnviarMouseClicked

    private void btnCancelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelMouseClicked
        if (accion.equals("NEW")) {
            this.dispose();
        } else {
            //eliminar registro.
            String sSQL = "DELETE FROM reg_servicio WHERE reg_servicio.ID_reg_serv = " + IDRegService + "  ";
            boolean ExecuteQuery = ExecuteQuery(sSQL, "Borrar registro");
            if (ExecuteQuery) {
                Hilo hilo = new Hilo(300, 30);
                hilo.agregarTexto("SE HA QUITADO EL SERVICIO DE LA LISTA", 2);
                hilo.start();
            }
            this.dispose();
        }
    }//GEN-LAST:event_btnCancelMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> CBOServicios;
    private com.toedter.calendar.JDateChooser JDateAlta;
    private javax.swing.JLabel btnCancel;
    private javax.swing.JLabel btnEnviar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblFound;
    private javax.swing.JLabel lblFound1;
    private javax.swing.JLabel lblFound2;
    private javax.swing.JLabel lblFound3;
    private javax.swing.JLabel lblFound4;
    private javax.swing.JLabel lblFound5;
    private javax.swing.JLabel lblRegistroStatus;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTextArea textAreaDesc;
    private javax.swing.JTextField txtPassword;
    private javax.swing.JTextField txtUserService;
    // End of variables declaration//GEN-END:variables
}
