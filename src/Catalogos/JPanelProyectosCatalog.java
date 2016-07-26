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
public class JPanelProyectosCatalog extends javax.swing.JDialog {

    int x, y;
    DefaultTableModel TABLARegistros;
    DefaultTableModel MyTableModel;
    String buscaPor, cadena, value;
    boolean statusRegistro = true;
    String accion;

    /**
     * Creates new form Muestra
     *
     * @param parent
     * @param modal
     * @param accion
     */
    public JPanelProyectosCatalog(java.awt.Frame parent, boolean modal, String accion) {
        super(parent, modal);
        initComponents();

        this.setLocationRelativeTo(null);
        this.accion = accion;
        if (accion.equals("NEW")) {
            // BORRAR REGISTROS PROM EN NOMBRE
            clearBox();
            accion = "NEW";
            btnNuevoConsultor1.setText("Guardar");
            lblTitulo.setText("Crear Nuevo Proyecto");
            txtSiglas.requestFocus();
        }
        String sSQLConsulta = "SELECT "
                + "ID_proyecto, nombre_proyecto, sigla_proyecto, estatus_proyecto "
                + "FROM proyecto ORDER BY proyecto.sigla_proyecto ASC";
        CargaDatos(sSQLConsulta);
    }

    private void CargaDatos(String Cadena) {
        //MODELO DE LA TABLA 
        String sSQL = Cadena;
        Object[] registro = new Object[12];
        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        int Cont = 0;
        String[] columnNames = {"ID", "NOMBRE DEL PROYECTO", "SIGLAS", "ESTATUS"};
        TABLARegistros = new DefaultTableModel(null, columnNames);

        try {
            Statement st = (Statement) cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            //----->  ID_proyecto, nombre_proyecto, sigla_proyecto, estatus_proyecto
            while (rs.next()) {
                registro[0] = rs.getString("ID_proyecto");
                registro[1] = rs.getString("nombre_proyecto");
                registro[2] = rs.getString("sigla_proyecto");
                if (rs.getString("estatus_proyecto").equals("0")) {

                    registro[3] = "TERMINADO";
//                    registro[3] = new javax.swing.ImageIcon(getClass().getResource("/Images/No.png"));
                } else {
                    registro[3] = "ACTIVO";
//                    registro[3] = new javax.swing.ImageIcon(getClass().getResource("/Images/Ok.png"));
                }
                TABLARegistros.addRow(registro);
                Cont++;
            }
            lblRegistrosFoundService.setText(Cont + " Proyectos");
            TBLRegistros.setModel(TABLARegistros);
            AjustaAnchosTabla();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ERROR AL BUSCAR REGISTRO, INTENTE CON OTRO.\nERROR: " + e);
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    private void AjustaAnchosTabla() {
        int[] anchos = {23, 274, 67, 66};
        for (int i = 0; i < TBLRegistros.getColumnCount(); i++) {
            TBLRegistros.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    //OTROS METODOS
    private void cambiaValor() {
        if (statusRegistro == true) {
            statusRegistro = false;
            lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/off.png")));
            lblRegistroStatus.setToolTipText("NO DISPONIBLE");

        } else {
            statusRegistro = true;
            lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/on.png")));
            lblRegistroStatus.setToolTipText("DISPONIBLE");
        }
    }

    private void GuardarRegistro() {
        String sentenciaSQL = "";
        //VALORES DEL CONSULTOR
        String siglas, nombre;
        siglas = txtSiglas.getText();
        nombre = txtNombre.getText();
        String valueStatus = "";
        if (true != statusRegistro) {
            valueStatus = "0";
        } else {
            valueStatus = "1";
        }

        if (accion.equals("NEW")) {
            //AGREGA NUEVO
            sentenciaSQL = "INSERT INTO proyecto ("
                    + "ID_proyecto, nombre_proyecto, sigla_proyecto, fecha_inicio, fecha_fin, "
                    + "porc_avance, ambiente_proyecto, estatus_proyecto) VALUES ("
                    + "NULL, '" + nombre + "', '" + siglas + "', NULL, NULL, '0', '', '" + valueStatus + "');";
            ExecuteQuery(sentenciaSQL, "Nuevo registro");
            Hilo hilo = new Hilo(300, 30);
            hilo.agregarTexto("SE HA CREADO UN NUEVO PROYECTO", 1);
            hilo.start();
        } else {
            //ACTUALIZAR ELEMENOT
            sentenciaSQL = "UPDATE proyecto SET "
                    + "nombre_proyecto = '" + nombre + "', "
                    + "sigla_proyecto = '" + siglas + "', "
                    + "estatus_proyecto = '" + valueStatus + "' "
                    + "WHERE proyecto.ID_proyecto = " + txtID.getText() + " ";

            ExecuteQuery(sentenciaSQL, "Actualizar registro de servidor");
            Hilo hilo = new Hilo(300, 30);
            hilo.agregarTexto("SE ACTUALIZÓ PROYECTO", 1);
            hilo.start();
        }

        lblTitulo.setText("Catalogo de Proyectos");
        String sSQLConsulta = "SELECT "
                + "ID_proyecto, nombre_proyecto, sigla_proyecto, estatus_proyecto "
                + "FROM proyecto ORDER BY proyecto.sigla_proyecto ASC";
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
        txtID.setText("AU");
        txtSiglas.setText("");
        txtNombre.setText("");
    }

    /////////<<<<<<<<<<<<<<< CARGANDO LA INFORMACIÓN DE LA BASE DE DATOS >>>>>>>>>>>>>>>>>>>>>>>>
    private void CargarRegistro(String sSQL) {
        int conConsultor = 0;
        String sSQL1 = sSQL;
        String[] Datos1 = new String[10];

        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        try {
            Statement st1 = (Statement) cn.createStatement();
            ResultSet rs1 = st1.executeQuery(sSQL1);
            //Cargando la informacion en las caja
            while (rs1.next()) {
                //ID_proyecto, nombre_proyecto, sigla_proyecto, estatus_proyecto
                Datos1[0] = rs1.getString("ID_proyecto");
                Datos1[1] = rs1.getString("nombre_proyecto");
                Datos1[2] = rs1.getString("sigla_proyecto");
                Datos1[3] = rs1.getString("estatus_proyecto");
                conConsultor++;
            }

            if (conConsultor > 0) {

                if (Datos1[3].equals("0") && accion.equals("NEW")) {
                    //EL SERVIICIO POR DEFAULT ESTA BLOQUEADO, SE MOSTRARA COMO ACTIVO Y SE ACTUALIZARA AL GUARDAR
                    statusRegistro = true;
                    lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/on.png")));
                    lblRegistroStatus.setToolTipText("ACTIVO");
                } else if (Datos1[3].equals("0") && accion.equals("UPDATE")) {
                    //EL SERVICIO YA ESTABA BAJA, MOSTRARLO BAJA
                    statusRegistro = false;
                    lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/off.png")));
                    lblRegistroStatus.setToolTipText("TERMINADO");
                } else if (Datos1[3].equals("1") && accion.equals("UPDATE")) {
                    //El registro esta activo, mostrarlo Activo
                    statusRegistro = true;
                    lblRegistroStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/on.png")));
                    lblRegistroStatus.setToolTipText("ACTIVO");
                }
                /*CARGANDO INFORMACION A LAS TextFiel*/
                txtID.setText(Datos1[0]);
                txtSiglas.setText(Datos1[2]);
                txtNombre.setText(Datos1[1]);
                btnNuevoConsultor1.setText("Actualizar");
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
        txtSiglas = new javax.swing.JTextField();
        lblFound2 = new javax.swing.JLabel();
        lblRegistroStatus = new javax.swing.JLabel();
        lblIDRegistro = new javax.swing.JLabel();
        btnNuevoRegistro = new javax.swing.JLabel();
        btnNuevoConsultor1 = new javax.swing.JLabel();
        lblFound3 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TBLRegistros = new javax.swing.JTable();
        lblRegistrosFoundService = new javax.swing.JLabel();

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
        lblTitulo.setText("Catalogo de Proyectos");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 313, Short.MAX_VALUE)
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
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información del servicio", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 18), new java.awt.Color(102, 102, 102))); // NOI18N
        jPanel7.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N

        lblFound1.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound1.setForeground(new java.awt.Color(51, 51, 51));
        lblFound1.setText("ID:");

        txtID.setEditable(false);
        txtID.setBackground(new java.awt.Color(255, 255, 255));
        txtID.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txtID.setForeground(new java.awt.Color(51, 51, 51));
        txtID.setText("AU");

        txtSiglas.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txtSiglas.setForeground(new java.awt.Color(51, 51, 51));
        txtSiglas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSiglasActionPerformed(evt);
            }
        });
        txtSiglas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSiglasKeyPressed(evt);
            }
        });

        lblFound2.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound2.setForeground(new java.awt.Color(51, 51, 51));
        lblFound2.setText("Siglas:");

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
        btnNuevoRegistro.setText("Nuevo");
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

        lblFound3.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        lblFound3.setForeground(new java.awt.Color(51, 51, 51));
        lblFound3.setText("Nombre:");

        txtNombre.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txtNombre.setForeground(new java.awt.Color(51, 51, 51));
        txtNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNombreKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(lblFound3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNombre))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnNuevoRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnNuevoConsultor1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(lblFound1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblFound2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSiglas, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblIDRegistro)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRegistroStatus)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblFound2)
                        .addComponent(txtSiglas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblFound1)
                        .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblIDRegistro))
                    .addComponent(lblRegistroStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFound3)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevoConsultor1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuevoRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(lblRegistrosFoundService, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(143, 143, 143)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(lblRegistrosFoundService)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        lblTitulo.setText("Crear Nueva Consultoría");
    }//GEN-LAST:event_btnNuevoRegistroMouseClicked

    private void btnNuevoConsultor1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNuevoConsultor1MouseClicked
        boolean vaidaDatos = false;
        if (txtSiglas.getText().length() < 1) {
            txtSiglas.setBackground(Color.red);
            vaidaDatos = false;
        } else {
            vaidaDatos = true;
        }
        if (txtNombre.getText().length() < 1) {
            txtNombre.setBackground(Color.red);
            vaidaDatos = false;
        } else {
            vaidaDatos = true;
        }

        if (vaidaDatos == true) {
            txtSiglas.setBackground(Color.white);
            txtNombre.setBackground(Color.white);
            GuardarRegistro();
        }
        txtSiglas.requestFocus();
    }//GEN-LAST:event_btnNuevoConsultor1MouseClicked

    private void TBLRegistrosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TBLRegistrosMouseClicked
        //SELECCIONAR UN ELEMENTO DE LA TABLA Y EDITARLO
        if (TBLRegistros.isEnabled()) {
            String idSer = TBLRegistros.getValueAt(TBLRegistros.getSelectedRow(), 0).toString();
            accion = "UPDATE";
            lblTitulo.setText("Actualizar el Servidor");
            String sSQL
                    = "SELECT "
                    + "ID_proyecto, nombre_proyecto, sigla_proyecto, estatus_proyecto "
                    + "FROM proyecto WHERE ID_proyecto = " + idSer + " ";
            CargarRegistro(sSQL);
        }
    }//GEN-LAST:event_TBLRegistrosMouseClicked

    private void TBLRegistrosComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_TBLRegistrosComponentHidden
        // TODO add your handling code here:
    }//GEN-LAST:event_TBLRegistrosComponentHidden

    private void txtSiglasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSiglasKeyPressed

    }//GEN-LAST:event_txtSiglasKeyPressed

    private void txtSiglasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSiglasActionPerformed

        txtNombre.requestFocus();
    }//GEN-LAST:event_txtSiglasActionPerformed

    private void txtNombreKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyPressed

    }//GEN-LAST:event_txtNombreKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JLabel lblFound1;
    private javax.swing.JLabel lblFound2;
    private javax.swing.JLabel lblFound3;
    private javax.swing.JLabel lblIDRegistro;
    private javax.swing.JLabel lblRegistroStatus;
    private javax.swing.JLabel lblRegistrosFoundService;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtSiglas;
    // End of variables declaration//GEN-END:variables
}
