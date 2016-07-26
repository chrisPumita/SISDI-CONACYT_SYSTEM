package Consultores;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import com.sun.awt.AWTUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Hilo extends Thread {
    private Ventana ventana;
    private float opacidad = 0.3f;              // opacidad inicial
    private final int TIEMPO = 3000;         // tiempo en milisegundos que estara activa la ventana
     int ValX = 0;
     int ValY = 0;
    public Hilo(int V_X, int V_Y) {
        ValX = V_X;
        ValY = V_Y;
        ventana = new Ventana();
        AWTUtilities.setWindowOpacity(ventana, 0.0f);
        ventana.setVisible(true);
    }

    public void agregarTexto(String msj, int tipoColor) {
        ventana.agregarTexto(msj, tipoColor);
    }

    @Override
    public void run() {
        try {
            hacerVisible();
            Thread.sleep(TIEMPO);
            desvanecer();
            ventana.dispose();
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }

    private void hacerVisible() throws InterruptedException {
        opacidad = 0.3f;
        while (opacidad < 1) {
            AWTUtilities.setWindowOpacity(ventana, opacidad);
            opacidad += 0.03f;
            Thread.sleep(20);
        }
    }

    private void desvanecer() throws InterruptedException {
        opacidad = 1.0f;
        while (opacidad > 0) {
            AWTUtilities.setWindowOpacity(ventana, opacidad);
            opacidad -= 0.03f;
            Thread.sleep(20);
        }
    }

    class Ventana extends JDialog {
        private final int BARRA_DE_ESTADO = 40; // Tamaño de la barra de estado en windows 
        private SimpleAttributeSet attrib;
        private JScrollPane scrollPane;
        private JTextPane textoPane;
//        private JButton btnCerrar;
        public Ventana() {
            iniciarComponentes();
//            setPreferredSize(new java.awt.Dimension(350, 50)); 
            ubicacionVentana();
            attrib = new SimpleAttributeSet();
        }

        private void ubicacionVentana() {
            int tamanioX = getWidth();
            int tamanioY = getHeight();
            int maxX = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
            int maxY = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
            // ubicacion de la ventana
            setLocation(maxX - tamanioX, maxY - tamanioY - BARRA_DE_ESTADO);
//            setLocation(maxX - tamanioX, maxY - tamanioY);
        }

        private void iniciarComponentes() {
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            scrollPane = new JScrollPane();
            textoPane = new JTextPane();
//            btnCerrar = new JButton();
            setAlwaysOnTop(true);                          // siempre arriba
            setPreferredSize(new java.awt.Dimension(ValX, ValY));           // tamaño de la ventana
            setResizable(false);                             // no se puede modificar el tamaño
            setUndecorated(true);                           // no tiene los controles de estado
            scrollPane.setAutoscrolls(true);
            textoPane.setEditable(false);
            textoPane.setBackground(Color.WHITE);
            scrollPane.setViewportView(textoPane);
            getContentPane().add(scrollPane, BorderLayout.CENTER);
            pack();

        }

        public void agregarTexto(String msj, int tipoColor) {
            try {
                StyleConstants.setForeground(attrib, getColorTexto(tipoColor));
                StyledDocument sd = textoPane.getStyledDocument();
                if (!textoPane.getText().isEmpty()) {
                    sd.insertString(sd.getLength(), "n", attrib);
                }
                sd.insertString(sd.getLength(), msj, attrib);
            } catch (BadLocationException e) {
                System.err.println(e);
            }
        }

        private Color getColorTexto(int tipo) {
            switch (tipo) {
                case 0:     // NEGRO
                    return new Color(0, 0, 0);
                case 1:     // VERDE
                    return new Color(0, 130, 0);
                case 2:     // ROJO
                    return new Color(255, 0, 0);
                default:    // Negro
                    return new Color(0, 0, 0);
            }
        }
    }
}
