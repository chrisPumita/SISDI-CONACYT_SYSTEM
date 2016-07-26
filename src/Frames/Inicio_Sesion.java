/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Frames;

import DataBase.ConexionMySQL;
import com.mysql.jdbc.Statement;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author C0nacyt
 */
public class Inicio_Sesion extends javax.swing.JFrame implements Runnable {

    /**
     * Creates new form Inicio_Sesion
     */
    private Connection conexion;
    boolean OptionDate;
    public static String CadenaBrouse;
    ///---------> DATE TIME
    String hora, minutos, segundos, ampm;
    Calendar calendario;
    Thread h1;
    String tipoAcceso, nameUser, idUser, SISDIVersion;

    public Inicio_Sesion() {
        initComponents();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setIconImage(new ImageIcon(getClass().getResource("/Images/Icon32.png")).getImage());
        OptionDate = true; // true <---- Cero es Modo Create New Register
        h1 = new Thread(this);
        h1.start();
        txtUser.selectAll();
        txtUser.requestFocus();
        loadVersion();
        this.setTitle("SISDI - CONACYT V. " + SISDIVersion + " [Iniciar Sesión]");
    }

    public Connection getConexion() {
        return conexion;
    }

    private void loadVersion() {
        Properties prop = new Properties();
        InputStream is = null;

        try {
            is = new FileInputStream(System.getProperty("user.dir") + "\\config.properties");
//			is = new FileInputStream(System.getProperty("Frames/config.properties"));
//            System.out.println(is);
            prop.load(is);
        } catch (IOException e) {
            System.out.println(" \nERROR " + e.toString());
        }
        String versionValue = prop.getProperty("SISDIVersion");
        String rememeberSession = prop.getProperty("sesion.remember");
        String userRemember = prop.getProperty("sesion.load");

        if (rememeberSession.equals("true")) {
            txtUser.setText(userRemember);
            txtPassword.setText("");
            txtPassword.requestFocus();
            CBORemember.setSelected(true);
        } else {
            CBORemember.setSelected(false);
            txtUser.setText(userRemember);
        }
        SISDIVersion = versionValue;

        labelVersion.setText("SISDI Version: " + versionValue);
        //SISDIVersion
    }

    private void recordarSesion() {
        String valorSession, selected;
        if (CBORemember.isSelected()) {
            valorSession = txtUser.getText();
            selected = "true";
        } else {
            valorSession = "ejemplo@conacyt.com.mx";
            selected = "false";
        }
        Properties prop = new Properties();
        prop.setProperty("sesion.load", valorSession);
        prop.setProperty("sesion.remember", selected);
        prop.setProperty("SISDIVersion", SISDIVersion);
        try {
            prop.store(new FileWriter("config.properties"), "Valores de Sesión");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error al escribir propiedad");
        }
    }

    //-----------------> METODOS DEL RELOJ
    @Override
    public void run() {
        Thread ct = Thread.currentThread();
        while (ct == h1) {
            calcula();
            SimpleDateFormat formato = new SimpleDateFormat("E dd-MMM-yyyy");
            String fecha = formato.format(new Date());
//         System.out.print(fecha);
            lblHoraActual.setText(fecha + "  " + hora + ":" + minutos + " " + ampm);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    public void calcula() {
        Calendar Cal = new GregorianCalendar();
        Date fechaHoraActual = new Date();

        Cal.setTime(fechaHoraActual);
        ampm = Cal.get(Calendar.AM_PM) == Calendar.AM ? "a. m." : "p. m.";

        if (ampm.equals("PM")) {
            int h = Cal.get(Calendar.HOUR_OF_DAY) - 12;
            hora = h > 9 ? "" + h : "0" + h;
        } else {
            hora = Cal.get(Calendar.HOUR_OF_DAY) > 9 ? "" + Cal.get(Calendar.HOUR_OF_DAY) : "0" + Cal.get(Calendar.HOUR_OF_DAY);
        }
        minutos = Cal.get(Calendar.MINUTE) > 9 ? "" + Cal.get(Calendar.MINUTE) : "0" + Cal.get(Calendar.MINUTE);
        segundos = Cal.get(Calendar.SECOND) > 9 ? "" + Cal.get(Calendar.SECOND) : "0" + Cal.get(Calendar.SECOND);
    }
    //-----------------> METODOS DEL RELOJ

    public void AbrirPagina(String url) {
        try {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "ERROR AL ABRIR LA PÁGINA");
            }
        } catch (URISyntaxException ex) {
        }
    }

    /* VERIFICAR LA SESION*/
    private boolean verificarCuenta(String user, String passWord) {
        //tipoAcceso
        String passMD5 = convierteMD5(passWord);
        boolean acceso = false;
        String consultaSQL = "SELECT tipo_cuenta_user AS cuenta, nombre_user AS name, ID_user AS id  FROM usuario "
                + "WHERE email_user = '" + user + "' AND passwordMD5 = '" + passMD5 + "'  AND estatus_cuenta_user = '1'  ";
        String[] tipoCuenta = new String[5];
        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();
        int cont = 0;
        try {
            Statement st1 = (Statement) cn.createStatement();
            ResultSet rs1 = st1.executeQuery(consultaSQL);
            while (rs1.next()) {
                tipoCuenta[0] = rs1.getString("cuenta");
                tipoCuenta[1] = rs1.getString("name");
                tipoCuenta[2] = rs1.getString("id");
                cont++;
            }
            if (cont == 1) {
                acceso = true;
                nameUser = tipoCuenta[1];
                tipoAcceso = tipoCuenta[0];
                idUser = tipoCuenta[2];
                recordarSesion();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No es posible obtener el ID\n ERROR: " + ex);
        }
        return acceso;
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
        return sessionid;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BotonesBuscar = new javax.swing.ButtonGroup();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        JPanelTramite = new javax.swing.JPanel();
        lblTramites1 = new javax.swing.JLabel();
        PanelGob = new javax.swing.JPanel();
        lblTramites2 = new javax.swing.JLabel();
        JPanelTramite6 = new javax.swing.JPanel();
        PanelBus = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        PanelPar = new javax.swing.JPanel();
        lblTramites3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        lblHoraActual = new javax.swing.JLabel();
        lblBienvenido = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        txtPassword = new javax.swing.JPasswordField();
        jLabel17 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        labelVersion = new javax.swing.JLabel();
        CBORemember = new javax.swing.JCheckBox();

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("jLabel5");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SISDI-CONACYT  -  phCrat  JN 1.0");
        setSize(new java.awt.Dimension(1024, 768));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground( Color.decode("#272829"));
        jPanel2.setForeground(Color.decode("#393C3E"));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/logo.png"))); // NOI18N
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        JPanelTramite.setBackground( Color.decode("#272829"));
        JPanelTramite.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        JPanelTramite.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                JPanelTramiteMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                JPanelTramiteMouseMoved(evt);
            }
        });
        JPanelTramite.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                JPanelTramiteMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                JPanelTramiteMouseExited(evt);
            }
        });

        lblTramites1.setFont(new java.awt.Font("Calibri", 0, 20)); // NOI18N
        lblTramites1.setForeground(new java.awt.Color(255, 255, 255));
        lblTramites1.setText("Trámites");
        lblTramites1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblTramites1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                lblTramites1MouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                lblTramites1MouseMoved(evt);
            }
        });
        lblTramites1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblTramites1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblTramites1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblTramites1MouseExited(evt);
            }
        });

        javax.swing.GroupLayout JPanelTramiteLayout = new javax.swing.GroupLayout(JPanelTramite);
        JPanelTramite.setLayout(JPanelTramiteLayout);
        JPanelTramiteLayout.setHorizontalGroup(
            JPanelTramiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPanelTramiteLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTramites1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        JPanelTramiteLayout.setVerticalGroup(
            JPanelTramiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JPanelTramiteLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTramites1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        PanelGob.setBackground( Color.decode("#272829"));
        PanelGob.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        PanelGob.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                PanelGobMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                PanelGobMouseMoved(evt);
            }
        });
        PanelGob.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                PanelGobMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                PanelGobMouseExited(evt);
            }
        });

        lblTramites2.setFont(new java.awt.Font("Calibri", 0, 20)); // NOI18N
        lblTramites2.setForeground(new java.awt.Color(255, 255, 255));
        lblTramites2.setText("Gobierno");
        lblTramites2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblTramites2.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                lblTramites2MouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                lblTramites2MouseMoved(evt);
            }
        });
        lblTramites2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblTramites2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblTramites2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblTramites2MouseExited(evt);
            }
        });

        javax.swing.GroupLayout PanelGobLayout = new javax.swing.GroupLayout(PanelGob);
        PanelGob.setLayout(PanelGobLayout);
        PanelGobLayout.setHorizontalGroup(
            PanelGobLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelGobLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTramites2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelGobLayout.setVerticalGroup(
            PanelGobLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTramites2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        JPanelTramite6.setBackground( Color.decode("#272829"));
        JPanelTramite6.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                JPanelTramite6MouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                JPanelTramite6MouseMoved(evt);
            }
        });
        JPanelTramite6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                JPanelTramite6MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                JPanelTramite6MouseExited(evt);
            }
        });

        PanelBus.setBackground( Color.decode("#272829"));
        PanelBus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        PanelBus.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                PanelBusMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                PanelBusMouseMoved(evt);
            }
        });
        PanelBus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                PanelBusMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                PanelBusMouseExited(evt);
            }
        });

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/SearchPanel.png"))); // NOI18N
        jLabel16.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jLabel16MouseMoved(evt);
            }
        });
        jLabel16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel16MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel16MouseExited(evt);
            }
        });

        javax.swing.GroupLayout PanelBusLayout = new javax.swing.GroupLayout(PanelBus);
        PanelBus.setLayout(PanelBusLayout);
        PanelBusLayout.setHorizontalGroup(
            PanelBusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelBusLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelBusLayout.setVerticalGroup(
            PanelBusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelBusLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout JPanelTramite6Layout = new javax.swing.GroupLayout(JPanelTramite6);
        JPanelTramite6.setLayout(JPanelTramite6Layout);
        JPanelTramite6Layout.setHorizontalGroup(
            JPanelTramite6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JPanelTramite6Layout.createSequentialGroup()
                .addGap(0, 10, Short.MAX_VALUE)
                .addComponent(PanelBus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        JPanelTramite6Layout.setVerticalGroup(
            JPanelTramite6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelBus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        PanelPar.setBackground( Color.decode("#272829"));
        PanelPar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        PanelPar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                PanelParMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                PanelParMouseMoved(evt);
            }
        });
        PanelPar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                PanelParMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                PanelParMouseExited(evt);
            }
        });

        lblTramites3.setFont(new java.awt.Font("Calibri", 0, 20)); // NOI18N
        lblTramites3.setForeground(new java.awt.Color(255, 255, 255));
        lblTramites3.setText("Participa");
        lblTramites3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblTramites3.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                lblTramites3MouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                lblTramites3MouseMoved(evt);
            }
        });
        lblTramites3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblTramites3MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblTramites3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblTramites3MouseExited(evt);
            }
        });

        javax.swing.GroupLayout PanelParLayout = new javax.swing.GroupLayout(PanelPar);
        PanelPar.setLayout(PanelParLayout);
        PanelParLayout.setHorizontalGroup(
            PanelParLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelParLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTramites3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelParLayout.setVerticalGroup(
            PanelParLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTramites3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(JPanelTramite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelGob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelPar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JPanelTramite6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(JPanelTramite, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(PanelGob, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(JPanelTramite6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
            .addComponent(PanelPar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel3.setBackground( Color.decode("#272829"));
        jPanel3.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.setToolTipText("");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/logo.png"))); // NOI18N
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/logo_mexico.png"))); // NOI18N

        jPanel8.setBackground(Color.decode("#393C3E"));

        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Contacto");
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("¿Qué es gob.mx?");

        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Político de privacidad");
        jLabel11.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel11MouseClicked(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("<html>Es el portal único de trámites, información y participación ciudadana. Leer más");
        jLabel24.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel24.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel24MouseClicked(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Términos y Condiciones");
        jLabel12.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Marco Jurídico");
        jLabel13.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Mapa de sitio");
        jLabel14.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel14MouseClicked(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Portal de Obligaciones de Transparencia");
        jLabel15.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel15MouseClicked(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/face.png"))); // NOI18N
        jLabel18.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel18MouseClicked(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Sistema Infomex");
        jLabel19.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel19MouseClicked(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Síguenos");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Enlaces");

        jLabel8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Declaración de Accesibilidad");
        jLabel8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Denuncia contra servidores públicos");
        jLabel21.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel21MouseClicked(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("<HTML>Mesa de ayuda: dudas e información gobmx@funcionpublica.gob.mx");

        jLabel22.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/twitter.png"))); // NOI18N
        jLabel22.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel22.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel22MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15)
                            .addComponent(jLabel13)
                            .addComponent(jLabel12)
                            .addComponent(jLabel19))
                        .addGap(391, 391, 391))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel8)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addGap(214, 214, 214))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))))
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel21)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                            .addComponent(jLabel18)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel22))
                        .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(215, 215, 215))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel14))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel23))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel18)
                            .addComponent(jLabel22))))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addContainerGap())
            .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/logoconacyt.png"))); // NOI18N
        jLabel4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });

        lblHoraActual.setBackground(new java.awt.Color(102, 102, 102));
        lblHoraActual.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblHoraActual.setForeground(new java.awt.Color(51, 51, 51));
        lblHoraActual.setText("dateTime()");

        lblBienvenido.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        lblBienvenido.setForeground(new java.awt.Color(153, 153, 153));
        lblBienvenido.setText("Iniciar Sesión");
        lblBienvenido.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        lblBienvenido.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel7.setBackground(new java.awt.Color(102, 102, 102));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(102, 102, 102));
        jLabel7.setText("<html> <body>Sistema Integral de la Subdireccion de Desarrollo e Innovación </body> </html>");

        txtUser.setFont(new java.awt.Font("Arial", 2, 14)); // NOI18N
        txtUser.setText("ejemplo@conacyt.com.mx");
        txtUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtUserMouseClicked(evt);
            }
        });
        txtUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserActionPerformed(evt);
            }
        });

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 446, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        txtPassword.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtPassword.setText("jPasswordField1");
        txtPassword.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtPasswordMouseClicked(evt);
            }
        });
        txtPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPasswordKeyPressed(evt);
            }
        });

        jLabel17.setBackground(new java.awt.Color(102, 102, 102));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(102, 102, 102));
        jLabel17.setText("Contraseña:");

        jLabel25.setBackground(new java.awt.Color(102, 102, 102));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(102, 102, 102));
        jLabel25.setText("Correo Electrónico:");

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton1.setText("Enviar");
        jButton1.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jButton1AncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        labelVersion.setBackground(new java.awt.Color(102, 102, 102));
        labelVersion.setFont(new java.awt.Font("Arial", 2, 10)); // NOI18N
        labelVersion.setForeground(new java.awt.Color(102, 102, 102));
        labelVersion.setText("SISDI V 1.1.1");

        CBORemember.setBackground(new java.awt.Color(255, 255, 255));
        CBORemember.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        CBORemember.setSelected(true);
        CBORemember.setText("Recordar usuario");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addComponent(jLabel25)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(labelVersion)
                                        .addComponent(CBORemember))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 134, Short.MAX_VALUE)
                                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblBienvenido)
                                    .addComponent(txtUser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                                    .addComponent(txtPassword, javax.swing.GroupLayout.Alignment.TRAILING))))
                        .addGap(222, 222, 222)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblHoraActual)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(20, 20, 20)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lblBienvenido)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(labelVersion)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(CBORemember)))
                        .addContainerGap(42, Short.MAX_VALUE))
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHoraActual)
                .addGap(18, 18, 18)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void txtUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUserActionPerformed
        txtPassword.setText("");
        txtPassword.requestFocus();
    }//GEN-LAST:event_txtUserActionPerformed

    private void txtUserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtUserMouseClicked
        txtUser.selectAll();
    }//GEN-LAST:event_txtUserMouseClicked

    private void JPanelTramiteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JPanelTramiteMouseEntered


    }//GEN-LAST:event_JPanelTramiteMouseEntered

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        //
        AbrirPagina("https://www.gob.mx/accesibilidad");

    }//GEN-LAST:event_jLabel8MouseClicked

    private void jLabel11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseClicked
        AbrirPagina("https://www.gob.mx/privacidad");
    }//GEN-LAST:event_jLabel11MouseClicked

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        AbrirPagina("https://www.gob.mx/terminos");
    }//GEN-LAST:event_jLabel12MouseClicked

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked
        AbrirPagina("http://www.ordenjuridico.gob.mx/");
    }//GEN-LAST:event_jLabel13MouseClicked

    private void jLabel15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel15MouseClicked
        AbrirPagina("http://portaltransparencia.gob.mx/buscador/search/search.do?method=begin");
    }//GEN-LAST:event_jLabel15MouseClicked

    private void jLabel19MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel19MouseClicked
        AbrirPagina("https://www.infomex.org.mx/gobiernofederal/home.action");
    }//GEN-LAST:event_jLabel19MouseClicked

    private void jLabel14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseClicked
        AbrirPagina("https://www.gob.mx/sitemap");
    }//GEN-LAST:event_jLabel14MouseClicked

    private void jLabel18MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel18MouseClicked
        AbrirPagina("https://www.facebook.com/gobmx");
    }//GEN-LAST:event_jLabel18MouseClicked

    private void jLabel24MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel24MouseClicked
        AbrirPagina("https://www.gob.mx/que-es-gobmx");
    }//GEN-LAST:event_jLabel24MouseClicked

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        AbrirPagina("htfdhf3rh");
    }//GEN-LAST:event_jLabel10MouseClicked

    private void jLabel21MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel21MouseClicked
        AbrirPagina("https://www.gob.mx/tramites/ficha/presentacion-de-quejas-y-denuncias-en-la-sfp/SFP54");
    }//GEN-LAST:event_jLabel21MouseClicked

    private void jLabel22MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel22MouseClicked
        AbrirPagina("https://twitter.com/gobmx");
    }//GEN-LAST:event_jLabel22MouseClicked

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        AbrirPagina("https://www.gob.mx/");
    }//GEN-LAST:event_jLabel1MouseClicked

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        AbrirPagina("https://www.gob.mx/");
    }//GEN-LAST:event_jLabel2MouseClicked

    private void JPanelTramiteMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JPanelTramiteMouseMoved
        JPanelTramite.setBackground(Color.decode("#000000"));
    }//GEN-LAST:event_JPanelTramiteMouseMoved

    private void JPanelTramiteMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JPanelTramiteMouseDragged

    }//GEN-LAST:event_JPanelTramiteMouseDragged

    private void JPanelTramiteMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JPanelTramiteMouseExited
        JPanelTramite.setBackground(Color.decode("#272829"));
    }//GEN-LAST:event_JPanelTramiteMouseExited

    private void lblTramites1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTramites1MouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_lblTramites1MouseDragged

    private void lblTramites1MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTramites1MouseMoved
        JPanelTramite.setBackground(Color.decode("#000000"));
    }//GEN-LAST:event_lblTramites1MouseMoved

    private void lblTramites1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTramites1MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_lblTramites1MouseEntered

    private void lblTramites1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTramites1MouseExited
        JPanelTramite.setBackground(Color.decode("#272829"));
    }//GEN-LAST:event_lblTramites1MouseExited

    private void lblTramites2MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTramites2MouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_lblTramites2MouseDragged

    private void lblTramites2MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTramites2MouseMoved
        PanelGob.setBackground(Color.decode("#000000"));
    }//GEN-LAST:event_lblTramites2MouseMoved

    private void lblTramites2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTramites2MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_lblTramites2MouseEntered

    private void lblTramites2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTramites2MouseExited
        PanelGob.setBackground(Color.decode("#272829"));
    }//GEN-LAST:event_lblTramites2MouseExited

    private void PanelGobMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PanelGobMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_PanelGobMouseDragged

    private void PanelGobMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PanelGobMouseMoved
        PanelGob.setBackground(Color.decode("#000000"));
    }//GEN-LAST:event_PanelGobMouseMoved

    private void PanelGobMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PanelGobMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_PanelGobMouseEntered

    private void PanelGobMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PanelGobMouseExited
        PanelGob.setBackground(Color.decode("#272829"));
    }//GEN-LAST:event_PanelGobMouseExited

    private void lblTramites3MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTramites3MouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_lblTramites3MouseDragged

    private void lblTramites3MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTramites3MouseMoved
        PanelPar.setBackground(Color.decode("#000000"));
    }//GEN-LAST:event_lblTramites3MouseMoved

    private void lblTramites3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTramites3MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_lblTramites3MouseEntered

    private void lblTramites3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTramites3MouseExited
        PanelPar.setBackground(Color.decode("#272829"));
    }//GEN-LAST:event_lblTramites3MouseExited

    private void PanelParMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PanelParMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_PanelParMouseDragged

    private void PanelParMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PanelParMouseMoved
        PanelPar.setBackground(Color.decode("#000000"));
    }//GEN-LAST:event_PanelParMouseMoved

    private void PanelParMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PanelParMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_PanelParMouseEntered

    private void PanelParMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PanelParMouseExited
        PanelPar.setBackground(Color.decode("#272829"));
    }//GEN-LAST:event_PanelParMouseExited

    private void PanelBusMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PanelBusMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_PanelBusMouseDragged

    private void PanelBusMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PanelBusMouseMoved
        PanelBus.setBackground(Color.decode("#000000"));
    }//GEN-LAST:event_PanelBusMouseMoved

    private void PanelBusMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PanelBusMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_PanelBusMouseEntered

    private void PanelBusMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PanelBusMouseExited
        PanelBus.setBackground(Color.decode("#272829"));
    }//GEN-LAST:event_PanelBusMouseExited

    private void JPanelTramite6MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JPanelTramite6MouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_JPanelTramite6MouseDragged

    private void JPanelTramite6MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JPanelTramite6MouseMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_JPanelTramite6MouseMoved

    private void JPanelTramite6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JPanelTramite6MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_JPanelTramite6MouseEntered

    private void JPanelTramite6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JPanelTramite6MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_JPanelTramite6MouseExited

    private void jLabel16MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel16MouseMoved
        PanelBus.setBackground(Color.decode("#000000"));
    }//GEN-LAST:event_jLabel16MouseMoved

    private void jLabel16MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel16MouseExited
        PanelBus.setBackground(Color.decode("#272829"));
    }//GEN-LAST:event_jLabel16MouseExited

    private void lblTramites1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTramites1MouseClicked
        AbrirPagina("https://www.gob.mx/tramites");
    }//GEN-LAST:event_lblTramites1MouseClicked

    private void lblTramites2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTramites2MouseClicked
        AbrirPagina("https://www.gob.mx/gobierno");
    }//GEN-LAST:event_lblTramites2MouseClicked

    private void lblTramites3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTramites3MouseClicked
        AbrirPagina("https://www.gob.mx/participa");
    }//GEN-LAST:event_lblTramites3MouseClicked

    private void jLabel16MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel16MouseClicked
        AbrirPagina("https://www.gob.mx/busqueda");
    }//GEN-LAST:event_jLabel16MouseClicked

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        AbrirPagina("http://www.conacyt.mx/");
    }//GEN-LAST:event_jLabel4MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String usuario = txtUser.getText();
        String Pass = null;
        char[] Password = txtPassword.getPassword();
        Pass = new String(Password);
        if (usuario.equals("") || Pass.equals("")) {
            JOptionPane.showMessageDialog(null, "Debe ingresar un usuario y contraseña valida");
        } else if (verificarCuenta(usuario, Pass) == true) {
            new Principal(tipoAcceso, nameUser, idUser, Pass).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(null, "El Usuario es incorrecto o su cuenta fue suspendida.");
            txtPassword.setText("");
            txtUser.setText("");
            txtUser.requestFocus();
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtPasswordMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPasswordMouseClicked
        txtPassword.selectAll();
    }//GEN-LAST:event_txtPasswordMouseClicked

    private void txtPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPasswordKeyPressed
        if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getExtendedKeyCode() == KeyEvent.VK_TAB)) { //Verifica que el usuario dio enter o tabulador
            String usuario = txtUser.getText();
            String Pass = null;
            char[] Password = txtPassword.getPassword();
            Pass = new String(Password);
            if (usuario.equals("") || Pass.equals("")) {
                JOptionPane.showMessageDialog(null, "Debe ingresar un usuario y contraseña valida");
            } else if (verificarCuenta(usuario, Pass) == true) {
                new Principal(tipoAcceso, nameUser, idUser, Pass).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "El Usuario es incorrecto");
                txtPassword.setText("");
                txtUser.setText("");
                txtUser.requestFocus();
            }
        }
    }//GEN-LAST:event_txtPasswordKeyPressed

    private void jButton1AncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jButton1AncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1AncestorAdded

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            }
            new Inicio_Sesion().setVisible(true);
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup BotonesBuscar;
    private javax.swing.JCheckBox CBORemember;
    private javax.swing.JPanel JPanelTramite;
    private javax.swing.JPanel JPanelTramite6;
    private javax.swing.JPanel PanelBus;
    private javax.swing.JPanel PanelGob;
    private javax.swing.JPanel PanelPar;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JLabel labelVersion;
    private javax.swing.JLabel lblBienvenido;
    private javax.swing.JLabel lblHoraActual;
    private javax.swing.JLabel lblTramites1;
    private javax.swing.JLabel lblTramites2;
    private javax.swing.JLabel lblTramites3;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables

}
