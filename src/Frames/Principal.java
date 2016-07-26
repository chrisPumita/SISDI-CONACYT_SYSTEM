/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Frames;

import Catalogos.JPanelConsultorCatalog;
import Catalogos.JPanelConsultoriasCatalog;
import Catalogos.JPanelProyectosCatalog;
import Catalogos.JPanelServiciosCatalog;
import Catalogos.JPanelServidoresCatalog;
import Catalogos.JPanelUsuariosCatalog;
import Consultores.Hilo;
import Consultores.JPanelBusquedaConsultor;
import Consultores.JPanelCreateReportConsultor;
import Consultores.JPanelUpdateNewConsultor;
import DataBase.ConexionMySQL;
import System.AcercaDe;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import tools.JPanelPassword;

/**
 *
 * @author C0nacyt
 */
public class Principal extends javax.swing.JFrame implements Runnable {

    /**
     * Creates new form Menu_principal
     */
    boolean OptionDate;
    ///---------> DATE TIME
    String hora, minutos, segundos, ampm;
    Calendar calendario;
    Thread h1;
    String tipoCuenta;
    String idUser;
    String password, SISDIVersion, nombreUsuario;

    /**
     * Esta variable esta disponible para todas clases.
     *
     * @param tipoCuenta
     * @param nombreUsuario
     * @param idUser
     * @param password
     */
    public Principal(String tipoCuenta, String nombreUsuario, String idUser, String password) {
        initComponents();

        this.idUser = idUser;
        this.password = password;
        this.tipoCuenta = tipoCuenta;
        this.nombreUsuario = nombreUsuario;

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setIconImage(new ImageIcon(getClass().getResource("/Images/Icon32.png")).getImage());
        OptionDate = true; // true <---- Cero es Modo Create New Register
        h1 = new Thread(this);
        h1.start();
        lblBienvenido.setText("<html> <body> <center> Bienvenid@: <b>" + nombreUsuario + " </center> ID: " + idUser + " </body> </html>");
        txtBuscar.selectAll();
        BotonesBuscar.add(BRPro);
        BotonesBuscar.add(RBCon);

        verificaCuenta();
        verificaNuevoUser();

        loadVersion();
        this.setTitle("SISDI - CONACYT V. " + SISDIVersion + " [ "+tipoCuenta+": "+nombreUsuario+" ]");

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

        SISDIVersion = versionValue;

        //SISDIVersion
    }

    private void verificaNuevoUser() {
        if (password.equalsIgnoreCase("0000")) {
            //Sugerir cambiar la contraseña
            int seleccion = JOptionPane.showOptionDialog(
                    this, // Componente padre
                    "SE SUGIERE MODIFICAR SU CONTRASEÑA ACTUAL\n ¿Desea hacerlo ahora?", //Mensaje
                    "NUEVO USUARIO - CAMBIAR CONTRASEÑA", // Título
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, // null para icono por defecto.
                    new Object[]{"Cambiar ahora", "En otro momento"}, // null para YES, NO y CANCEL
                    "Cambiar ahora");
            if (seleccion != -1) {
                if ((seleccion + 1) == 1) {
                    Frame Admimistracion = null;
                    JPanelPassword PanelAdministrador = new JPanelPassword(Admimistracion, true, idUser);
                    PanelAdministrador.setVisible(true);
                } else {
                }
            }
        } else if (password.equalsIgnoreCase("000000")) {
            //Sugerir cambiar la contraseña
            int seleccion = JOptionPane.showOptionDialog(
                    this, // Componente padre
                    "SE RESTABLECIO SU CONTRASEÑA, SE SUGUIERE VOLVER A MODIFICARLA\n ¿Desea hacerlo ahora?", //Mensaje
                    "CONTRASEÑA RESTABLECIDA - CAMBIAR CONTRASEÑA", // Título
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, // null para icono por defecto.
                    new Object[]{"Cambiar ahora", "En otro momento"}, // null para YES, NO y CANCEL
                    "Cambiar ahora");
            if (seleccion != -1) {
                if ((seleccion + 1) == 1) {
                    Frame Admimistracion = null;
                    JPanelPassword PanelAdministrador = new JPanelPassword(Admimistracion, true, idUser);
                    PanelAdministrador.setVisible(true);
                } else {
                }
            }
        }
    }

    private void verificaCuenta() {
        if (tipoCuenta.equals("PROGRAMADOR")) {
            jMenuCommand.setVisible(true);
        } else {
            jMenuCommand.setVisible(false);
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
        Calendar calen = new GregorianCalendar();
        Date fechaHoraActual = new Date();

        calen.setTime(fechaHoraActual);
        ampm = calen.get(Calendar.AM_PM) == Calendar.AM ? "a. m." : "p. m.";

        if (ampm.equals("PM")) {
            int h = calen.get(Calendar.HOUR_OF_DAY) - 12;
            hora = h > 9 ? "" + h : "0" + h;
        } else {
            hora = calen.get(Calendar.HOUR_OF_DAY) > 9 ? "" + calen.get(Calendar.HOUR_OF_DAY) : "0" + calen.get(Calendar.HOUR_OF_DAY);
        }
        minutos = calen.get(Calendar.MINUTE) > 9 ? "" + calen.get(Calendar.MINUTE) : "0" + calen.get(Calendar.MINUTE);
        segundos = calen.get(Calendar.SECOND) > 9 ? "" + calen.get(Calendar.SECOND) : "0" + calen.get(Calendar.SECOND);
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

    ////////// ----------------> BUSQUEDA DE REGISTROS<-------------------\\\\\\\\\\\\\\\\
    private void BuscarRegistro(String valor) {
        ///Tipo de busqueda
        boolean RadioCon = RBCon.isSelected();
        boolean RadioPro = BRPro.isSelected();
        Hilo hilo = new Hilo(300, 30);
        String busquedaTipo = "";
        String sSQL = "";
        if (RadioCon) {
            sSQL = "SELECT ID_consultor FROM consultor "
                    + "WHERE ((CONCAT(nombre_c)  LIKE '%" + valor + "%') "
                    + " or (CONCAT(apellidoP_c)  LIKE '%" + valor + "%')    "
                    + " or (CONCAT(apellidoM_c)  LIKE '%" + valor + "%') )  "
                    + "AND estatus_c = '1' ";
            busquedaTipo = "CON";
        }
        if (RadioPro) {
            sSQL = "SELECT * FROM proyecto";
            busquedaTipo = "PRO";
            //BUSQUEDA POR PROYECTO
        }

        String[] registro = new String[2];
        int NoFounf = 0;
        ConexionMySQL mysql = new ConexionMySQL();
        Connection cn = (Connection) mysql.Conectar();

        try {
            Statement st = (Statement) cn.createStatement();
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
                        + " AND estatus_c = '1' ORDER BY `consultor`.`nombre_c` ASC";
                Frame Admimistracion = null;
                JPanelBusquedaConsultor PanelAdministrador = new JPanelBusquedaConsultor(Admimistracion, true, busquedaTipo, sSQLConsulta, valor);
                PanelAdministrador.setVisible(true);

            } else if (NoFounf == 1) { //SOLO EXISTE UN REGISTRO Y SE ABRIRA LA VENTANA DE UPDATECONSULTOR
                hilo.agregarTexto("PUEDE EDITAR EL REGISTRO", 1);
                hilo.start();

//        JOptionPane.showMessageDialog(null, nombre + " " + apellidoP + " " + apellidoM);
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

        if (RadioCon) {
            //BUSQUEDA POR CONSULTOR

        } else if (RadioPro) {
            //BUSQUEDA POR PROYECTO
        }

    }

    public static String dir() {
        return System.getProperty("user.dir");
    }

    private void openManual() {
        String link = "javadoc/index.html";
        abrirarchivo(link);
        System.out.print(link);
    }

    public void abrirarchivo(String archivo) {
        try {
            File objetofile = new File(archivo);
            Desktop.getDesktop().open(objetofile);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    ////////// ----------------> BUSQUEDA DE REGISTROS<-------------------\\\\\\\\\\\\\\\\
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
        txtBuscar = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        BRPro = new javax.swing.JRadioButton();
        RBCon = new javax.swing.JRadioButton();
        jLabel17 = new javax.swing.JLabel();
        MenuBar = new javax.swing.JMenuBar();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem16 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem31 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem23 = new javax.swing.JMenuItem();
        jMenuItem24 = new javax.swing.JMenuItem();
        jMenuItem29 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem26 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem27 = new javax.swing.JMenuItem();
        jMenuItem28 = new javax.swing.JMenuItem();
        jMenuItem30 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem19 = new javax.swing.JMenuItem();
        jMenuItem20 = new javax.swing.JMenuItem();
        jMenuItem21 = new javax.swing.JMenuItem();
        jMenuCommand = new javax.swing.JMenu();
        jMenuItem22 = new javax.swing.JMenuItem();

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
                .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout JPanelTramite6Layout = new javax.swing.GroupLayout(JPanelTramite6);
        JPanelTramite6.setLayout(JPanelTramite6Layout);
        JPanelTramite6Layout.setHorizontalGroup(
            JPanelTramite6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPanelTramite6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelBus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            .addComponent(PanelPar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
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

        lblHoraActual.setFont(new java.awt.Font("Calibri", 0, 24)); // NOI18N
        lblHoraActual.setForeground(new java.awt.Color(51, 51, 51));
        lblHoraActual.setText("dateTime()");

        lblBienvenido.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lblBienvenido.setForeground(new java.awt.Color(153, 153, 153));
        lblBienvenido.setText("<html> <body> <center> Bienvenid@: $_NAME</center> </body> </html>");
        lblBienvenido.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        lblBienvenido.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel7.setBackground(new java.awt.Color(102, 102, 102));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 36)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(102, 102, 102));
        jLabel7.setText("<html> <body><p>Sistema Integral de la Subdirección de Desarrollo e Innovación </body> </html>");

        txtBuscar.setFont(new java.awt.Font("Arial", 2, 14)); // NOI18N
        txtBuscar.setText("search...");
        txtBuscar.setToolTipText("Escriba para comenzar a buscar");
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

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 446, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 168, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 289, Short.MAX_VALUE)
        );

        BRPro.setBackground(new java.awt.Color(255, 255, 255));
        BRPro.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        BRPro.setText("Proyecto");
        BRPro.setToolTipText("Aun no disponible");
        BRPro.setEnabled(false);
        BRPro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BRProActionPerformed(evt);
            }
        });

        RBCon.setBackground(new java.awt.Color(255, 255, 255));
        RBCon.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        RBCon.setSelected(true);
        RBCon.setText("Consultores");
        RBCon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RBConActionPerformed(evt);
            }
        });

        jLabel17.setBackground(Color.decode("#4D92DF"));
        jLabel17.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/search.png"))); // NOI18N
        jLabel17.setToolTipText("Buscar");
        jLabel17.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF"), Color.decode("#4D92DF")));
        jLabel17.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel17MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblHoraActual)
                        .addGap(25, 25, 25))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblBienvenido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 591, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(109, 109, 109)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(RBCon)
                                        .addGap(18, 18, 18)
                                        .addComponent(BRPro))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblHoraActual)))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(lblBienvenido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BRPro)
                            .addComponent(RBCon))
                        .addGap(36, 36, 36)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(43, 43, 43))))
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
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        MenuBar.setBackground(new java.awt.Color(0, 0, 0));
        MenuBar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        MenuBar.setBorderPainted(false);
        MenuBar.setFont(new java.awt.Font("Calibri", 0, 36)); // NOI18N

        jMenu4.setBackground(new java.awt.Color(0, 0, 0));
        jMenu4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED, null, null, new java.awt.Color(102, 102, 102), new java.awt.Color(102, 102, 102)));
        jMenu4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/Circled User Male-48.png"))); // NOI18N
        jMenu4.setText("Sesión");
        jMenu4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenu4.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N

        jMenuItem16.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem16.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jMenuItem16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/Administrator Male-48.png"))); // NOI18N
        jMenuItem16.setText("Administrar Usuarios");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem16);
        jMenu4.add(jSeparator3);

        jMenuItem17.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem17.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jMenuItem17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/Password-48.png"))); // NOI18N
        jMenuItem17.setText("Cambiar Contraseña");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem17);

        jMenuItem31.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem31.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jMenuItem31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/Exit-48.png"))); // NOI18N
        jMenuItem31.setText("Cerrar Sesón");
        jMenuItem31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem31ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem31);

        MenuBar.add(jMenu4);

        jMenu2.setBackground(new java.awt.Color(0, 0, 0));
        jMenu2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED, null, null, new java.awt.Color(102, 102, 102), new java.awt.Color(102, 102, 102)));
        jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/Idea-48.png"))); // NOI18N
        jMenu2.setText("Proyectos");
        jMenu2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenu2.setEnabled(false);
        jMenu2.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N

        jMenuItem4.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jMenuItem4.setText("Buscar");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuItem18.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jMenuItem18.setText("Agregar Nuevo");
        jMenu2.add(jMenuItem18);

        jMenuItem3.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jMenuItem3.setText("Ver Proyecto");
        jMenu2.add(jMenuItem3);

        jMenuItem5.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jMenuItem5.setText("Generar Reporte");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        MenuBar.add(jMenu2);

        jMenu3.setBackground(new java.awt.Color(0, 0, 0));
        jMenu3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED, null, null, new java.awt.Color(102, 102, 102), new java.awt.Color(102, 102, 102)));
        jMenu3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/Collaboration-48.png"))); // NOI18N
        jMenu3.setText("Consultores");
        jMenu3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenu3.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N

        jMenuItem10.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jMenuItem10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/Find User Male-48.png"))); // NOI18N
        jMenuItem10.setText("Buscar Consultor");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem10);

        jMenuItem11.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem11.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jMenuItem11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/Add User Male-48.png"))); // NOI18N
        jMenuItem11.setText("Agregar Nuevo");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem11);

        jMenuItem12.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jMenuItem12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/Inspection-48.png"))); // NOI18N
        jMenuItem12.setText("Generar Reporte");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem12);

        MenuBar.add(jMenu3);

        jMenu1.setBackground(new java.awt.Color(0, 0, 0));
        jMenu1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED, null, null, new java.awt.Color(102, 102, 102), new java.awt.Color(102, 102, 102)));
        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/Book Shelf-48.png"))); // NOI18N
        jMenu1.setText("Catálogos");
        jMenu1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenu1.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N

        jMenu6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/Collaboration-48.png"))); // NOI18N
        jMenu6.setText("Consultores");
        jMenu6.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N

        jMenuItem23.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jMenuItem23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/Services-48.png"))); // NOI18N
        jMenuItem23.setText("Servicios");
        jMenuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem23ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem23);

        jMenuItem24.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jMenuItem24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/Server-48.png"))); // NOI18N
        jMenuItem24.setText("Servidores");
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem24);

        jMenuItem29.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jMenuItem29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/Organization-48.png"))); // NOI18N
        jMenuItem29.setText("Consultorías");
        jMenuItem29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem29ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem29);

        jMenu1.add(jMenu6);

        jMenu7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/Idea-48.png"))); // NOI18N
        jMenu7.setText("Proyectos");
        jMenu7.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N

        jMenuItem26.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem26.setText("Catálogo de Proyectos");
        jMenuItem26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem26ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem26);
        jMenu7.add(jSeparator5);

        jMenuItem6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem6.setText("Etapas");
        jMenuItem6.setEnabled(false);
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem6);

        jMenuItem27.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem27.setText("Documentos");
        jMenuItem27.setEnabled(false);
        jMenu7.add(jMenuItem27);

        jMenuItem28.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem28.setText("Casillas");
        jMenuItem28.setEnabled(false);
        jMenu7.add(jMenuItem28);

        jMenuItem30.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem30.setText("Versiones de documentoación");
        jMenuItem30.setEnabled(false);
        jMenu7.add(jMenuItem30);

        jMenu1.add(jMenu7);

        MenuBar.add(jMenu1);

        jMenu5.setBackground(new java.awt.Color(0, 0, 0));
        jMenu5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED, null, null, new java.awt.Color(102, 102, 102), new java.awt.Color(102, 102, 102)));
        jMenu5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/Help-48 (1).png"))); // NOI18N
        jMenu5.setText("Ayuda");
        jMenu5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenu5.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N

        jMenuItem19.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jMenuItem19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/User Manual-48.png"))); // NOI18N
        jMenuItem19.setText("Manual");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem19);

        jMenuItem20.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jMenuItem20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/External Link-48.png"))); // NOI18N
        jMenuItem20.setText("Documentación");
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem20);

        jMenuItem21.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem21.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jMenuItem21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/About-48.png"))); // NOI18N
        jMenuItem21.setText("Acerca de");
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem21);

        MenuBar.add(jMenu5);

        jMenuCommand.setBackground(new java.awt.Color(0, 0, 0));
        jMenuCommand.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED, null, null, new java.awt.Color(102, 102, 102), new java.awt.Color(102, 102, 102)));
        jMenuCommand.setText("Programador");
        jMenuCommand.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuCommand.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N

        jMenuItem22.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jMenuItem22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconsBar/Run Command-96.png"))); // NOI18N
        jMenuItem22.setText("Shell");
        jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem22ActionPerformed(evt);
            }
        });
        jMenuCommand.add(jMenuItem22);

        MenuBar.add(jMenuCommand);

        setJMenuBar(MenuBar);

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

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed

    }//GEN-LAST:event_txtBuscarActionPerformed

    private void BRProActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BRProActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BRProActionPerformed

    private void RBConActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RBConActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RBConActionPerformed

    private void txtBuscarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtBuscarMouseClicked
        txtBuscar.selectAll();
    }//GEN-LAST:event_txtBuscarMouseClicked

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

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        //JPanelUsuariosCatalog
        Frame Admimistracion = null;
        JPanelUsuariosCatalog PanelAdministrador = new JPanelUsuariosCatalog(Admimistracion, true, "NEW");
        PanelAdministrador.setVisible(true);
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jLabel17MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MouseClicked
        String value = txtBuscar.getText();

        if (value.equals("")) {
            JOptionPane.showMessageDialog(null, "Debe escribir una palabra");
            txtBuscar.requestFocus();
        } else {
            BuscarRegistro(value);
        }
    }//GEN-LAST:event_jLabel17MouseClicked

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        Frame Admimistracion = null;
        JPanelConsultorCatalog PanelAdministrador = new JPanelConsultorCatalog(Admimistracion, true, "NEW");
        PanelAdministrador.setVisible(true);
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        Frame Admimistracion = null;
        JPanelCreateReportConsultor PanelAdministrador = new JPanelCreateReportConsultor(Admimistracion, true, nombreUsuario);
        PanelAdministrador.setVisible(true);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        String sSQLConsulta = "SELECT @rownum:=@rownum+1 AS contador,  `nombre_c`, `apellidoP_c`, `apellidoM_c`, `correo_c`, `estatus_c` "
                + "FROM consultor, (SELECT @rownum:=0) R WHERE estatus_c = '1'  ORDER BY `consultor`.`nombre_c` ASC";
        Frame Admimistracion = null;
        JPanelBusquedaConsultor PanelAdministrador = new JPanelBusquedaConsultor(Admimistracion, true, "CON", sSQLConsulta, "");
        PanelAdministrador.setVisible(true);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        Frame Admimistracion = null;
        AcercaDe PanelAdministrador = new AcercaDe(Admimistracion, true, nombreUsuario);
        PanelAdministrador.setVisible(true);
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    private void txtBuscarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyPressed
        String value = txtBuscar.getText();
        if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getExtendedKeyCode() == KeyEvent.VK_TAB)) {
            if (value.equals("")) {
                JOptionPane.showMessageDialog(null, "Debe escribir una palabra");
                txtBuscar.requestFocus();
            } else {
                BuscarRegistro(value);
            }
        }
    }//GEN-LAST:event_txtBuscarKeyPressed

    private void jMenuItem29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem29ActionPerformed
        // JPanelConsultoriasCatalog
        Frame Admimistracion = null;
        JPanelConsultoriasCatalog PanelAdministrador = new JPanelConsultoriasCatalog(Admimistracion, true, "NEW");
        PanelAdministrador.setVisible(true);
    }//GEN-LAST:event_jMenuItem29ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        Frame Admimistracion = null;
        JPanelPassword PanelAdministrador = new JPanelPassword(Admimistracion, true, idUser);
        PanelAdministrador.setVisible(true);
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jMenuItem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem23ActionPerformed
        Frame Admimistracion = null;
        JPanelServiciosCatalog PanelAdministrador = new JPanelServiciosCatalog(Admimistracion, true, "NEW");
        PanelAdministrador.setVisible(true);
    }//GEN-LAST:event_jMenuItem23ActionPerformed

    private void jMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem24ActionPerformed
        Frame Admimistracion = null;
        JPanelServidoresCatalog PanelAdministrador = new JPanelServidoresCatalog(Admimistracion, true, "NEW");
        PanelAdministrador.setVisible(true);
    }//GEN-LAST:event_jMenuItem24ActionPerformed

    private void jMenuItem31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem31ActionPerformed
        this.dispose();
        Inicio_Sesion inicio_Sesion = new Inicio_Sesion();
        inicio_Sesion.setVisible(true);
    }//GEN-LAST:event_jMenuItem31ActionPerformed

    private void jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem19ActionPerformed
        String link = "manual.pdf";
        abrirarchivo(link);
    }//GEN-LAST:event_jMenuItem19ActionPerformed

    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
        openManual();
    }//GEN-LAST:event_jMenuItem20ActionPerformed

    private void jMenuItem26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem26ActionPerformed
        Frame Admimistracion = null;
        JPanelProyectosCatalog PanelAdministrador = new JPanelProyectosCatalog(Admimistracion, true, "NEW");
        PanelAdministrador.setVisible(true);
    }//GEN-LAST:event_jMenuItem26ActionPerformed

    private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem22ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem22ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton BRPro;
    private javax.swing.ButtonGroup BotonesBuscar;
    private javax.swing.JPanel JPanelTramite;
    private javax.swing.JPanel JPanelTramite6;
    private javax.swing.JMenuBar MenuBar;
    private javax.swing.JPanel PanelBus;
    private javax.swing.JPanel PanelGob;
    private javax.swing.JPanel PanelPar;
    private javax.swing.JRadioButton RBCon;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenuCommand;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem26;
    private javax.swing.JMenuItem jMenuItem27;
    private javax.swing.JMenuItem jMenuItem28;
    private javax.swing.JMenuItem jMenuItem29;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem30;
    private javax.swing.JMenuItem jMenuItem31;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JLabel lblBienvenido;
    private javax.swing.JLabel lblHoraActual;
    private javax.swing.JLabel lblTramites1;
    private javax.swing.JLabel lblTramites2;
    private javax.swing.JLabel lblTramites3;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
