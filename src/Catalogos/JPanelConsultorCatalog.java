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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author C0nacyt
 */
public class JPanelConsultorCatalog extends javax.swing.JDialog {

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
    public JPanelConsultorCatalog(java.awt.Frame parent, boolean modal, String accion) {
        super(parent, modal);
        initComponents();

        this.setLocationRelativeTo(null);
        this.accion = accion;
        if (accion.equals("NEW")) {
            // BORRAR REGISTROS PROM EN NOMBRE
            clearBox();
            accion = "NEW";
            btnNuevoConsultor1.setText("GUARDAR");
            lblTitulo.setText("Crear Nuevo Consultor");
            txtNom.requestFocus();
        }
        String sSQLConsulta = "SELECT ID_consultor,  `nombre_c`, `apellidoP_c`, `apellidoM_c`, `correo_c`, `estatus_c` "
                + "FROM consultor "
                + " ORDER BY `consultor`.`nombre_c` ASC";
        CargaDatos(sSQLConsulta);
    }

    private void CargaDatos(String Cadena) {
        String sSQL = Cadena;
        String[] registro = new String[12];
        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        int Cont = 1;
        String[] titulos = {"ID", "NOMBRE DEL CONSULTOR", "CORREO ACTUAL", "ESTATUS"};
        TABLARegistros = new DefaultTableModel(null, titulos);

        try {
            Statement st = (Statement) cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            //`nombre_c`, `apellidoP_c`, `apellidoM_c`, `correo_c`, `estatus_c`
            while (rs.next()) {
                registro[0] = rs.getString("ID_consultor");
                registro[1] = rs.getString("nombre_c") + " " + rs.getString("apellidoP_c") + " " + rs.getString("apellidoM_c");
                registro[2] = rs.getString("correo_c");
                if (rs.getString("estatus_c").equals("0")) {
                    registro[3] = "INACTIVO";
                } else {
                    registro[3] = "ACTIVO";
                }
                TABLARegistros.addRow(registro);
                Cont++;
            }
            lblRegistrosFoundService.setText((Cont - (1)) + " registro(s)");
            TBLRegistros.setModel(TABLARegistros);
            AjustaAnchosTabla();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ERROR AL BUSCAR REGISTRO, INTENTE CON OTRO.\nERROR: " + e);
        }

    }

    private void AjustaAnchosTabla() {
        int[] anchos = {23, 171, 156, 54};
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
            sentenciaSQL = "INSERT INTO consultor (ID_consultor, nombre_c, apellidoP_c, apellidoM_c, correo_c, estatus_c) VALUES "
                    + "(NULL, '" + nombreC + "', '" + ApP + "', '" + ApM + "', '" + correoAct + "', '" + valueStatus + "')";
            ExecuteQuery(sentenciaSQL, "Nuevo registro");
            Hilo hilo = new Hilo(300, 30);
            hilo.agregarTexto("SE HA CREADO AGREGADO UN NUEVO CONSULTOR", 1);
            hilo.start();
        } else {
            //ACTUALIZAR ELEMENOT
            sentenciaSQL = "UPDATE consultor SET "
                    + "nombre_c = '" + nombreC + "', "
                    + "apellidoP_c = '" + ApP + "', "
                    + "apellidoM_c = '" + ApM + "', "
                    + "correo_c = '" + correoAct + "', "
                    + "estatus_c = '" + valueStatus + "' "
                    + "WHERE consultor.ID_consultor = " + txtID.getText() + " ";

            ExecuteQuery(sentenciaSQL, "Actualizar registro de servicio");
            Hilo hilo = new Hilo(300, 30);
            hilo.agregarTexto("SE ACTUALIZÓ EL CONSULTOR", 1);
            hilo.start();
        }
        lblTitulo.setText("Catalogo de Consultores");
        String sSQLConsulta = "SELECT ID_consultor,  `nombre_c`, `apellidoP_c`, `apellidoM_c`, `correo_c`, `estatus_c` "
                + "FROM consultor "
                + " ORDER BY `consultor`.`nombre_c` ASC";
        CargaDatos(sSQLConsulta);
        clearBox();
        accion = "NEW";
        btnNuevoConsultor1.setText("GUARDAR");

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
        CBOProyectos.setSelectedIndex(0);
        txtID.setText("AU");
        txtNom.setText("");
        txtApP.setText("");
        txtApM.setText("");
        txtMail.setText("");
        CBOProyectos.setSelectedIndex(0);
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
                //ID_consultor, nombre_c, apellidoP_c, apellidoM_c, correo_c
                Datos1[0] = rs1.getString("ID_consultor");
                Datos1[1] = rs1.getString("nombre_c");
                Datos1[2] = rs1.getString("apellidoP_c");
                Datos1[3] = rs1.getString("apellidoM_c");
                Datos1[4] = rs1.getString("correo_c");
                Datos1[5] = rs1.getString("estatus_c");
                conConsultor++;
            }

            if (conConsultor > 0) {

                if (Datos1[5].equals("0") && accion.equals("NEW")) {
                    //EL SERVIICIO POR DEFAULT ESTA BLOQUEADO, SE MOSTRARA COMO ACTIVO Y SE ACTUALIZARA AL GUARDAR
                    statusRegistro = true;
                    lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/on.png")));
                    lblRegistroStatus.setToolTipText("ACTIVO");
                } else if (Datos1[5].equals("0") && accion.equals("UPDATE")) {
                    //EL SERVICIO YA ESTABA BAJA, MOSTRARLO BAJA
                    statusRegistro = false;
                    lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/off.png")));
                    lblRegistroStatus.setToolTipText("INACTIVO");
                } else if (Datos1[5].equals("1") && accion.equals("UPDATE")) {
                    //El registro esta activo, mostrarlo Activo
                    statusRegistro = true;
                    lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/on.png")));
                    lblRegistroStatus.setToolTipText("ACTIVO");
                }
                /*CARGANDO INFORMACION A LAS TextFiel*/
                txtID.setText(Datos1[0]);
                txtNom.setText(Datos1[1]);
                txtApP.setText(Datos1[2]);
                txtApM.setText(Datos1[3]);
                txtMail.setText(Datos1[4]);
                /*CARGANDO INFORMACION A LAS TextFiel*/
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
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TBLRegistros = new javax.swing.JTable();
        CBOProyectos = new javax.swing.JComboBox<>();
        lblRegistrosFoundService = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        lblFound = new javax.swing.JLabel();
        btnBuscar = new javax.swing.JLabel();

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
        lblTitulo.setText("Catalogo de Consultores");

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
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información de Consultor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 18), new java.awt.Color(102, 102, 102))); // NOI18N
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
        lblFound5.setText("E-Mail Actual:");

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
        lblIDRegistro.setText("Estatus:");

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
                                .addComponent(lblFound3, javax.swing.GroupLayout.Alignment.TRAILING)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNom)
                            .addComponent(txtApP, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtApM, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtMail, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGap(0, 201, Short.MAX_VALUE)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                .addComponent(lblIDRegistro)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblRegistroStatus))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                .addComponent(btnNuevoRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnNuevoConsultor1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))))
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
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRegistroStatus)
                    .addComponent(lblIDRegistro))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevoConsultor1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuevoRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Catálogo de Consultores", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 18), new java.awt.Color(102, 102, 102))); // NOI18N
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

        CBOProyectos.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        CBOProyectos.setForeground(new java.awt.Color(51, 51, 51));
        CBOProyectos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ver Todos", "Ver Activos", "Ver Inactivos" }));
        CBOProyectos.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                CBOProyectosPropertyChange(evt);
            }
        });

        lblRegistrosFoundService.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblRegistrosFoundService.setForeground(new java.awt.Color(51, 51, 51));
        lblRegistrosFoundService.setText("_");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(lblRegistrosFoundService, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(CBOProyectos, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CBOProyectos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRegistrosFoundService))
                .addGap(15, 15, 15)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        lblFound.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound.setForeground(new java.awt.Color(51, 51, 51));
        lblFound.setText("Buscar Consultor:");

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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lblFound)
                        .addGap(18, 18, 18)
                        .addComponent(txtBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblFound)
                        .addComponent(txtBuscar)))
                .addGap(8, 8, 8)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void TBLRegistrosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TBLRegistrosMouseClicked
        //SELECCIONAR UN ELEMENTO DE LA TABLA Y EDITARLO
        if (TBLRegistros.isEnabled()) {
            String idCons = TBLRegistros.getValueAt(TBLRegistros.getSelectedRow(), 0).toString();
            accion = "UPDATE";
            lblTitulo.setText("Actualizar al Consultor");
            String sSQL = "SELECT ID_consultor, nombre_c, apellidoP_c, apellidoM_c, correo_c, estatus_c "
                    + "FROM consultor "
                    + "WHERE ID_consultor = " + idCons + " ";
            CargarRegistro(sSQL);
        }
    }//GEN-LAST:event_TBLRegistrosMouseClicked

    private void TBLRegistrosComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_TBLRegistrosComponentHidden
        // TODO add your handling code here:
    }//GEN-LAST:event_TBLRegistrosComponentHidden

    private void CBOProyectosPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_CBOProyectosPropertyChange
        String sSQLConsulta = "";
        if (CBOProyectos.getSelectedIndex() == 0) {
            sSQLConsulta = "SELECT ID_consultor,  `nombre_c`, `apellidoP_c`, `apellidoM_c`, `correo_c`, `estatus_c` "
                    + "FROM consultor "
                    + " ORDER BY `consultor`.`nombre_c` ASC";
        } else if (CBOProyectos.getSelectedIndex() == 1) {
            ///Se selecciono Ver activos
            sSQLConsulta = "SELECT ID_consultor,  `nombre_c`, `apellidoP_c`, `apellidoM_c`, `correo_c`, `estatus_c` "
                    + "FROM consultor WHERE estatus_c = '1' "
                    + " ORDER BY `consultor`.`nombre_c` ASC";
        } else {
            sSQLConsulta = "SELECT ID_consultor,  `nombre_c`, `apellidoP_c`, `apellidoM_c`, `correo_c`, `estatus_c` "
                    + "FROM consultor WHERE estatus_c = '0' "
                    + " ORDER BY `consultor`.`nombre_c` ASC";
        }
        CargaDatos(sSQLConsulta);
    }//GEN-LAST:event_CBOProyectosPropertyChange

    private void txtBuscarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtBuscarMouseClicked
        txtBuscar.selectAll();
    }//GEN-LAST:event_txtBuscarMouseClicked

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed

    }//GEN-LAST:event_txtBuscarActionPerformed

    private void txtBuscarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyPressed
        String value = txtBuscar.getText();
        if ((evt.getKeyCode() == evt.VK_ENTER) || (evt.getExtendedKeyCode() == evt.VK_TAB)) {
            if (value.equals("")) {
                JOptionPane.showMessageDialog(null, "Debe escribir una palabra");
                txtBuscar.requestFocus();
            } else {
                String sSQL = "SELECT ID_consultor,  `nombre_c`, `apellidoP_c`, `apellidoM_c`, `correo_c`, `estatus_c` "
                        + "FROM consultor "
                        + "WHERE (CONCAT(nombre_c)  LIKE '%" + value + "%') "
                        + " or (CONCAT(apellidoP_c)  LIKE '%" + value + "%')    "
                        + " or (CONCAT(apellidoM_c)  LIKE '%" + value + "%')  "
                        + "ORDER BY `consultor`.`nombre_c` ASC";
                CargaDatos(sSQL);
            }
        }
    }//GEN-LAST:event_txtBuscarKeyPressed

    private void btnBuscarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBuscarMouseClicked
        String value = txtBuscar.getText();
        if (value.equals("")) {
            JOptionPane.showMessageDialog(null, "Debe escribir una palabra");
            txtBuscar.requestFocus();
        } else {
            String sSQL = "SELECT ID_consultor,  `nombre_c`, `apellidoP_c`, `apellidoM_c`, `correo_c`, `estatus_c` "
                    + "FROM consultor "
                    + "WHERE (CONCAT(nombre_c)  LIKE '%" + value + "%') "
                    + " or (CONCAT(apellidoP_c)  LIKE '%" + value + "%')    "
                    + " or (CONCAT(apellidoM_c)  LIKE '%" + value + "%')  "
                    + "ORDER BY `consultor`.`nombre_c` ASC";
            CargaDatos(sSQL);
        }
    }//GEN-LAST:event_btnBuscarMouseClicked

    private void txtNomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomActionPerformed
                txtApP.requestFocus();
    }//GEN-LAST:event_txtNomActionPerformed

    private void txtApPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtApPActionPerformed
        txtApM.requestFocus();
    }//GEN-LAST:event_txtApPActionPerformed

    private void txtApMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtApMActionPerformed
        txtMail.requestFocus();
    }//GEN-LAST:event_txtApMActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> CBOProyectos;
    private javax.swing.JTable TBLRegistros;
    private javax.swing.JLabel btnBuscar;
    private javax.swing.JLabel btnNuevoConsultor1;
    private javax.swing.JLabel btnNuevoRegistro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblFound;
    private javax.swing.JLabel lblFound1;
    private javax.swing.JLabel lblFound2;
    private javax.swing.JLabel lblFound3;
    private javax.swing.JLabel lblFound4;
    private javax.swing.JLabel lblFound5;
    private javax.swing.JLabel lblIDRegistro;
    private javax.swing.JLabel lblRegistroStatus;
    private javax.swing.JLabel lblRegistrosFoundService;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTextField txtApM;
    private javax.swing.JTextField txtApP;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtMail;
    private javax.swing.JTextField txtNom;
    // End of variables declaration//GEN-END:variables
}
