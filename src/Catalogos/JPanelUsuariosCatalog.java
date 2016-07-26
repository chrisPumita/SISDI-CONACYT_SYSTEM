/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Catalogos;

import Consultores.Hilo;
import DataBase.ConexionMySQL;
import com.mysql.jdbc.Statement;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author C0nacyt
 */
public class JPanelUsuariosCatalog extends javax.swing.JDialog {

    int x, y;
    DefaultTableModel TABLARegistros;
    String buscaPor, cadena, value;
    boolean statusRegistro = true;
    String accion;

    /**
     * Creates new form Muestra
     *
     * @param parent
     * @param modal
     */
    public JPanelUsuariosCatalog(java.awt.Frame parent, boolean modal, String accion) {
        super(parent, modal);
        initComponents();

        this.setLocationRelativeTo(null);
        this.accion = accion;
        if (accion.equals("NEW")) {
            // BORRAR REGISTROS PROM EN NOMBRE
            clearBox();
            accion = "NEW";
            btnNuevoConsultor1.setText("GUARDAR");
            lblTitulo.setText("Crear Nuevo Usuario");
            txtNom.requestFocus();
        }
        String sSQLConsulta = "SELECT ID_user, CONCAT (nombre_user, ' ' , apellidoP_user, ' ' , apellidoM_user) AS userName,  "
                + "email_user, "
                + "IF (estatus_cuenta_user = 1,'HABILITADO','INHABILITADO') "
                + "AS estado, tipo_cuenta_user FROM usuario ORDER BY userName ASC";
        CargaDatos(sSQLConsulta);
    }

    private void CargaDatos(String Cadena) {
        String sSQL = Cadena;
        String[] registro = new String[12];
        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        int Cont = 0;
        String[] titulos = {"ID", "USUARIO", "CORREO", "ESTATUS", "CUENTA"};
        TABLARegistros = new DefaultTableModel(null, titulos);

        try {
            Statement st = (Statement) cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            //`nombre_c`, `apellidoP_c`, `apellidoM_c`, `correo_c`, `estatus_c`
            while (rs.next()) {
                registro[0] = rs.getString("ID_user");
                registro[1] = rs.getString("userName");
                registro[2] = rs.getString("email_user");
                registro[3] = rs.getString("estado");
                registro[4] = rs.getString("tipo_cuenta_user");
                TABLARegistros.addRow(registro);
                Cont++;
            }
            lblFound.setText("Existen: " + Cont + " usuarios");
            TBLRegistros.setModel(TABLARegistros);
            AjustaAnchosTabla();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ERROR AL BUSCAR REGISTRO, INTENTE CON OTRO.\nERROR: " + e);
        }

    }

    private void AjustaAnchosTabla() {
        int[] anchos = {23, 115, 125, 70, 89};
        for (int i = 0; i < TBLRegistros.getColumnCount(); i++) {
            TBLRegistros.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    //OTROS METODOS
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
        //VALORES DEL CONSULTOR
        String nombreC, ApP, ApM, correoAct;
        nombreC = txtNom.getText();
        ApP = txtApP.getText();
        ApM = txtApM.getText();
        correoAct = txtMail.getText();
        String valueStatus = "";
        if (true != statusRegistro) {
            valueStatus = "0";
        } else {
            valueStatus = "1";
        }

        if (accion.equals("NEW")) {
            //AGREGA NUEVO
            sentenciaSQL = "INSERT INTO usuario (ID_user, nombre_user, apellidoP_user, apellidoM_user, email_user, f_registroU, f_bajaU, estatus_cuenta_user, tipo_cuenta_user, passwordMD5) "
                    + "VALUES ("
                    + "NULL, '" + nombreC + "', '" + ApP + "', '" + ApM + "', '" + correoAct + "', '" + getTimeDay() + "', NULL, '1', '" + CBOTipoCuenta.getSelectedItem().toString() + "', '" + convierteMD5("0000") + "');";
            ExecuteQuery(sentenciaSQL, "Nuevo registro");
            Hilo hilo = new Hilo(300, 30);
            hilo.agregarTexto("SE HA CREADO UNA CUENTA NUEVA", 1);
            hilo.start();
        } else {
            String fechaBaja = null;
            String accesoUser = "1";
            if (statusRegistro == false) {
                fechaBaja = "'" + getTimeDay() + "'";
                accesoUser = "0";
            }

            //ACTUALIZAR ELEMENTO
            sentenciaSQL = "UPDATE usuario SET "
                    + "nombre_user = '" + nombreC + "', "
                    + "apellidoP_user = '" + ApP + "', "
                    + "apellidoM_user = '" + ApM + "', "
                    + "email_user = '" + correoAct + "', "
                    + "f_bajaU = " + fechaBaja + ", "
                    + "estatus_cuenta_user = '" + accesoUser + "', "
                    + "tipo_cuenta_user = '" + CBOTipoCuenta.getSelectedItem().toString() + "' "
                    + "WHERE usuario.ID_user = " + txtID.getText() + "  ";

            ExecuteQuery(sentenciaSQL, "Actualizar Usuario");
            Hilo hilo = new Hilo(300, 30);
            hilo.agregarTexto("SE ACTUALIZÓ AL USUARIO", 1);
            hilo.start();
        }

        lblTitulo.setText("Usuarios");
        String sSQLConsulta = "SELECT ID_user, CONCAT (nombre_user, ' ' , apellidoP_user, ' ' , apellidoM_user) AS userName,  "
                + "email_user, "
                + "IF (estatus_cuenta_user = 1,'HABILITADO','INHABILITADO') "
                + "AS estado, tipo_cuenta_user FROM usuario ORDER BY userName ASC";
        CargaDatos(sSQLConsulta);
        clearBox();
        accion = "NEW";
        btnNuevoConsultor1.setText("GUARDAR");

    }

    private String convierteMD5(String value) {
        String sessionid = value;

        byte[] defaultBytes = sessionid.getBytes();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(defaultBytes);
            byte messageDigest[] = algorithm.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            String foo = Arrays.toString(messageDigest);
            sessionid = hexString + "";
        } catch (NoSuchAlgorithmException nsae) {
        }
        System.out.print("La palabra: " + value + " se convierte en Codigo encriptado: " + sessionid + "\n");
        return sessionid;
    }

    private String getTimeDay() {
        Calendar fecha = new GregorianCalendar();
        int anio = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        int hora = fecha.get(Calendar.HOUR_OF_DAY);
        int minuto = fecha.get(Calendar.MINUTE);
        int segundo = fecha.get(Calendar.SECOND);
        String f;
        f = anio + "-" + mes + "-" + dia + " " + hora + ":" + minuto + ":" + segundo;
        return f;
    }

    private boolean ExecuteQuery(String sSQL, String Message) {
        boolean execute = false;
        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        try {
            com.mysql.jdbc.PreparedStatement pst = (com.mysql.jdbc.PreparedStatement) cn.prepareStatement(sSQL);
            execute = pst.execute();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, Message + "\nNO UPDATE SQL" + ex);
        }
        return execute;
    }

    private void clearBox() {
        txtID.setText("AU");
        txtNom.setText("");
        txtApP.setText("");
        txtApM.setText("");
        txtMail.setText("");
        CBOTipoCuenta.setSelectedIndex(0);
        txtFRegistro.setText("");
        txtFbaja.setText("");
    }

    private void CargarRegistro(String sSQL) {
        int conConsultor = 0;
        String sSQL1 = sSQL;
//        System.out.print("LLAMANDO A LOS RGISTROS DE ESTE CONSULTOR: " + sSQL2 + "\n");
        String[] Datos1 = new String[10];

        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        try {
            Statement st1 = (Statement) cn.createStatement();
            ResultSet rs1 = st1.executeQuery(sSQL1);
            // ID_consultor, nombre_c, apellidoP_c, apellidoM_c, correo_c, estatus_c
            //Cargando la informacion en las caja
            while (rs1.next()) {
                //ID_user, nombre_user, apellidoP_user, apellidoM_user, email_user, 
                //f_registroU, f_bajaU, estatus_cuenta_user, tipo_cuenta_user
                Datos1[0] = rs1.getString("ID_user");
                Datos1[1] = rs1.getString("nombre_user");
                Datos1[2] = rs1.getString("apellidoP_user");
                Datos1[3] = rs1.getString("apellidoM_user");
                Datos1[4] = rs1.getString("email_user");
                Datos1[5] = rs1.getString("f_registroU");
                Datos1[6] = rs1.getString("f_bajaU");
                Datos1[7] = rs1.getString("estatus_cuenta_user");
                Datos1[8] = rs1.getString("tipo_cuenta_user");
                conConsultor++;
            }

            if (conConsultor > 0) {
                /*CARGANDO INFORMACION A LAS TextFiel*/
                txtID.setText(Datos1[0]);
                txtNom.setText(Datos1[1]);
                txtApP.setText(Datos1[2]);
                txtApM.setText(Datos1[3]);
                txtMail.setText(Datos1[4]);
                txtFRegistro.setText(Datos1[5]);
                if (Datos1[6] == null) {
                    txtFbaja.setVisible(false);
                    lblFecha.setVisible(false);
                } else {
                    txtFbaja.setText(Datos1[6]);
                    txtFbaja.setVisible(true);
                    lblFecha.setVisible(true);
                }
                /*CARGANDO INFORMACION A LAS TextFiel*/
                if (Datos1[7].equals("0") && accion.equals("NEW")) {
                    //EL SERVIICIO POR DEFAULT ESTA BLOQUEADO, SE MOSTRARA COMO ACTIVO Y SE ACTUALIZARA AL GUARDAR
                    statusRegistro = true;
                    lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/on.png")));
                    lblRegistroStatus.setToolTipText("ACTIVO");
                } else if (Datos1[7].equals("0") && accion.equals("UPDATE")) {
                    //EL SERVICIO YA ESTABA BAJA, MOSTRARLO BAJA
                    statusRegistro = false;
                    lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/off.png")));
                    lblRegistroStatus.setToolTipText("INACTIVO");
                } else if (Datos1[7].equals("1") && accion.equals("UPDATE")) {
                    //El registro esta activo, mostrarlo Activo
                    statusRegistro = true;
                    lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/on.png")));
                    lblRegistroStatus.setToolTipText("ACTIVO");
                }
                //Cargar el tipo de usuario 
                CBOTipoCuenta.setSelectedItem(Datos1[8]);

            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No es posible cargar la informacion:\n ERROR: " + ex);
        }
        if (conConsultor == 0) {
            this.dispose();
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
        lblRegistroStatus = new javax.swing.JLabel();
        lblIDRegistro = new javax.swing.JLabel();
        btnNuevoRegistro = new javax.swing.JLabel();
        btnNuevoConsultor1 = new javax.swing.JLabel();
        lblFound6 = new javax.swing.JLabel();
        txtFRegistro = new javax.swing.JTextField();
        lblFecha = new javax.swing.JLabel();
        txtFbaja = new javax.swing.JTextField();
        lblFound8 = new javax.swing.JLabel();
        CBOTipoCuenta = new javax.swing.JComboBox<>();
        lblFound = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TBLRegistros = new javax.swing.JTable();

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
        lblTitulo.setText("Usuarios");

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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblTitulo)
                    .addComponent(jLabel1))
                .addGap(247, 247, 247))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información de Usuario", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 18), new java.awt.Color(102, 102, 102))); // NOI18N
        jPanel7.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N

        lblFound1.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound1.setForeground(new java.awt.Color(51, 51, 51));
        lblFound1.setText("ID:");

        txtID.setEditable(false);
        txtID.setBackground(new java.awt.Color(255, 255, 255));
        txtID.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txtID.setForeground(new java.awt.Color(51, 51, 51));
        txtID.setText("AU");

        txtNom.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txtNom.setForeground(new java.awt.Color(51, 51, 51));
        txtNom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomActionPerformed(evt);
            }
        });

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
        lblFound5.setText("E-Mail:");

        txtApP.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txtApP.setForeground(new java.awt.Color(51, 51, 51));
        txtApP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtApPActionPerformed(evt);
            }
        });

        txtApM.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txtApM.setForeground(new java.awt.Color(51, 51, 51));
        txtApM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtApMActionPerformed(evt);
            }
        });

        txtMail.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txtMail.setForeground(new java.awt.Color(51, 51, 51));
        txtMail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMailActionPerformed(evt);
            }
        });

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

        lblIDRegistro.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblIDRegistro.setForeground(new java.awt.Color(51, 51, 51));
        lblIDRegistro.setText("Acceso:");

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
        btnNuevoConsultor1.setText("Save");
        btnNuevoConsultor1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        btnNuevoConsultor1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNuevoConsultor1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnNuevoConsultor1MouseClicked(evt);
            }
        });

        lblFound6.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound6.setForeground(new java.awt.Color(51, 51, 51));
        lblFound6.setText("Fecha y Hora de  Alta:");

        txtFRegistro.setEditable(false);
        txtFRegistro.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txtFRegistro.setForeground(new java.awt.Color(51, 51, 51));

        lblFecha.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFecha.setForeground(new java.awt.Color(51, 51, 51));
        lblFecha.setText("Fecha y Hora de Baja:");

        txtFbaja.setEditable(false);
        txtFbaja.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txtFbaja.setForeground(new java.awt.Color(51, 51, 51));

        lblFound8.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound8.setForeground(new java.awt.Color(51, 51, 51));
        lblFound8.setText("Tipo de Cuenta:");

        CBOTipoCuenta.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        CBOTipoCuenta.setForeground(new java.awt.Color(51, 51, 51));
        CBOTipoCuenta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ADMINISTRADOR", "SERVICIO SOCIAL", "PROGRAMADOR" }));

        lblFound.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound.setForeground(new java.awt.Color(51, 51, 51));
        lblFound.setText("$Found");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                                .addComponent(lblFound3, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(lblFound6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNom)
                            .addComponent(txtApP, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtApM, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtMail, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(txtFRegistro)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblFound8)
                                .addGap(16, 16, 16))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(lblFound)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnNuevoRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNuevoConsultor1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblIDRegistro)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRegistroStatus))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(lblFecha)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFbaja, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CBOTipoCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFound6)
                    .addComponent(txtFRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFound8))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFecha)
                            .addComponent(txtFbaja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CBOTipoCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblRegistroStatus)
                    .addComponent(lblIDRegistro))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnNuevoConsultor1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnNuevoRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblFound, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Usuarios Registrados", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 18), new java.awt.Color(102, 102, 102))); // NOI18N
        jPanel8.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N

        TBLRegistros.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        TBLRegistros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        TBLRegistros.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TBLRegistrosMouseClicked(evt);
            }
        });
        TBLRegistros.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                TBLRegistrosComponentHidden(evt);
            }
        });
        jScrollPane1.setViewportView(TBLRegistros);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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

    private void lblRegistroStatusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblRegistroStatusMouseClicked
        cambiaValor();
    }//GEN-LAST:event_lblRegistroStatusMouseClicked

    private void btnNuevoRegistroMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNuevoRegistroMouseClicked
        clearBox();
        accion = "NEW";
        btnNuevoConsultor1.setText("GUARDAR");
        lblTitulo.setText("Crear Nuevo Consultor");
        txtNom.requestFocus();
    }//GEN-LAST:event_btnNuevoRegistroMouseClicked

    private void btnNuevoConsultor1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNuevoConsultor1MouseClicked
        boolean vaidaDatos = false;
        if (txtNom.getText().length() < 1) {
            txtNom.setBackground(Color.red);
            vaidaDatos = false;
        } else {
            vaidaDatos = true;
        }
        if (txtApP.getText().length() < 1) {
            txtApP.setBackground(Color.red);
            vaidaDatos = false;
        } else {
            vaidaDatos = true;
        }
        if (txtApM.getText().length() < 1) {
            txtApM.setBackground(Color.red);
            vaidaDatos = false;
        } else {
            vaidaDatos = true;
        }

        if (vaidaDatos == true) {
            txtNom.setBackground(Color.white);
            txtApP.setBackground(Color.white);
            txtApM.setBackground(Color.white);
            GuardarRegistro();
        }
        txtNom.requestFocus();

    }//GEN-LAST:event_btnNuevoConsultor1MouseClicked

    private void txtNomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomActionPerformed
        txtApP.requestFocus();
    }//GEN-LAST:event_txtNomActionPerformed

    private void txtApPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtApPActionPerformed
        txtApM.requestFocus();
    }//GEN-LAST:event_txtApPActionPerformed

    private void txtApMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtApMActionPerformed
        txtMail.requestFocus();
    }//GEN-LAST:event_txtApMActionPerformed

    private void TBLRegistrosComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_TBLRegistrosComponentHidden
        // TODO add your handling code here:
    }//GEN-LAST:event_TBLRegistrosComponentHidden

    private void TBLRegistrosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TBLRegistrosMouseClicked
        String idUser = TBLRegistros.getValueAt(TBLRegistros.getSelectedRow(), 0).toString();
        try {
            int idConvert = Integer.parseInt(idUser);
            accion = "UPDATE";
            lblTitulo.setText("Actualizar al Usuario");
            String sSQL = "SELECT ID_user, nombre_user, apellidoP_user, apellidoM_user, email_user, "
                    + "f_registroU, f_bajaU, estatus_cuenta_user, tipo_cuenta_user "
                    + "FROM usuario "
                    + "WHERE ID_user = " + idConvert + " ";
            CargarRegistro(sSQL);
        } catch (Exception e) {
            lblTitulo.setText("Usuarios");
            String sSQLConsulta = "SELECT ID_user, CONCAT (nombre_user, ' ' , apellidoP_user, ' ' , apellidoM_user) AS userName,  "
                    + "email_user, "
                    + "IF (estatus_cuenta_user = 1,'HABILITADO','INHABILITADO') "
                    + "AS estado, tipo_cuenta_user FROM usuario ORDER BY userName ASC";
            CargaDatos(sSQLConsulta);
            clearBox();
            accion = "NEW";
            btnNuevoConsultor1.setText("GUARDAR");
            JOptionPane.showMessageDialog(null, "NO DEBE MODIFICAR LOS DATOS DE LA TABLA");
        }
    }//GEN-LAST:event_TBLRegistrosMouseClicked

    private void txtMailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMailActionPerformed
        CBOTipoCuenta.requestFocus();
    }//GEN-LAST:event_txtMailActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> CBOTipoCuenta;
    private javax.swing.JTable TBLRegistros;
    private javax.swing.JLabel btnNuevoConsultor1;
    private javax.swing.JLabel btnNuevoRegistro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblFound;
    private javax.swing.JLabel lblFound1;
    private javax.swing.JLabel lblFound2;
    private javax.swing.JLabel lblFound3;
    private javax.swing.JLabel lblFound4;
    private javax.swing.JLabel lblFound5;
    private javax.swing.JLabel lblFound6;
    private javax.swing.JLabel lblFound8;
    private javax.swing.JLabel lblIDRegistro;
    private javax.swing.JLabel lblRegistroStatus;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTextField txtApM;
    private javax.swing.JTextField txtApP;
    private javax.swing.JTextField txtFRegistro;
    private javax.swing.JTextField txtFbaja;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtMail;
    private javax.swing.JTextField txtNom;
    // End of variables declaration//GEN-END:variables
}
