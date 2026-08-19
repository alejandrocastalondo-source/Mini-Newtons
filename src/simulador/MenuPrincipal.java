package simulador;

import javax.swing.*; // Librería Swing para componentes gráficos
import java.awt.*;    // Librería AWT para gráficos y layouts
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//  Clase principal del menú de inicio del simulador MINI NEWTONS
// Aquí se muestran los botones que permiten acceder a las distintas secciones:
// simulación, fórmulas, modo ecológico y salida.
class MenuPrincipal extends JFrame {

    //  Constructor: define la ventana principal del menú
    public MenuPrincipal() {
        setTitle(" MINI NEWTONS - Aventura de Física");   // Título en la barra de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    // Cierra completamente la app al salir
        setSize(600, 500);                                 // Tamaño fijo de la ventana
        setLocationRelativeTo(null);                       // Centra la ventana en pantalla
        setLayout(new BorderLayout());                     // Layout general tipo BorderLayout

        // 🪐 Panel principal con fondo animado
        JPanel panelPrincipal = new JPanel() {
            private float angle = 0; // Variable para animar el gradiente del fondo

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                //  Fondo gradiente dinámico que cambia suavemente
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(100, 150, 255),   // Color inicial
                        (float) (getWidth() * Math.sin(angle)), getHeight(),  // Posición variable del degradado
                        new Color(200, 230, 255)                 // Color final
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight()); // Rellena el fondo

                //  Dibuja pequeñas estrellas decorativas aleatorias
                g2d.setColor(Color.YELLOW);
                for (int i = 0; i < 20; i++) {
                    int x = (int) (Math.random() * getWidth());
                    int y = (int) (Math.random() * getHeight());
                    g2d.fillOval(x, y, 3, 3);
                }

                angle += 0.01; // Actualiza el ángulo para animar el gradiente
            }
        };
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS)); // Organización vertical

        //  Título principal del juego
        JLabel titulo = new JLabel(" MINI NEWTONS ", JLabel.CENTER);
        titulo.setFont(new Font("Comic Sans MS", Font.BOLD, 42));     // Fuente divertida y grande
        titulo.setForeground(new Color(255, 215, 0));                 // Amarillo brillante
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0)); // Márgenes verticales

        //  Subtítulo del menú
        JLabel subtitulo = new JLabel(" Aventura de Caída Libre ", JLabel.CENTER);
        subtitulo.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        subtitulo.setForeground(new Color(239, 19, 19, 176)); // Rojo con transparencia
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        //  Emoji decorativo central
        JLabel emojiLabel = new JLabel("🔬", JLabel.CENTER);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        emojiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //  Añadir los elementos visuales al panel principal
        panelPrincipal.add(Box.createVerticalStrut(20));  // Espacio superior
        panelPrincipal.add(titulo);
        panelPrincipal.add(Box.createVerticalStrut(10));
        panelPrincipal.add(subtitulo);
        panelPrincipal.add(Box.createVerticalStrut(20));
        panelPrincipal.add(emojiLabel);
        panelPrincipal.add(Box.createVerticalStrut(30));

        //  Creación de los botones con un diseño personalizado
        JButton btnSimulacion = crearBotonDivertido(" ¡Vamos a Simular!", new Color(46, 204, 113));         // Verde
        JButton btnFormulas = crearBotonDivertido(" Descubre la Magia de la Física", new Color(155, 89, 182)); // Morado
        JButton btnSalir = crearBotonDivertido(" Salir", new Color(52, 152, 219));                            // Azul
        JButton btnEcoCaida = this.crearBotonDivertido(" Eco-Caída Libre", new Color(153, 137, 34));          // Dorado

        //  Asignar acciones a los botones
        btnSimulacion.addActionListener(e -> {
            abrirSimulador(); // Abre la simulación principal
            dispose();        // Cierra el menú para liberar memoria
        });

        btnEcoCaida.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new VentanaEcoCaida()); // Abre la simulación ecológica
        });

        btnFormulas.addActionListener(e -> abrirVentanaFormulas()); // Abre la ventana de fórmulas

        btnSalir.addActionListener(e -> System.exit(0)); // Cierra la aplicación

        //  Añadir los botones al panel
        panelPrincipal.add(Box.createVerticalStrut(12));
        panelPrincipal.add(btnSimulacion);
        panelPrincipal.add(Box.createVerticalStrut(12));
        panelPrincipal.add(btnFormulas);
        panelPrincipal.add(Box.createVerticalStrut(12));
        panelPrincipal.add(btnEcoCaida);
        panelPrincipal.add(Box.createVerticalStrut(12));
        panelPrincipal.add(btnSalir);

        // Finalmente, se agrega todo al JFrame principal
        add(panelPrincipal, BorderLayout.CENTER);
        setVisible(true); // Muestra la ventana del menú
    }

    //  Metodo que crea botones personalizados con efectos visuales y de hover
    private JButton crearBotonDivertido(String texto, Color colorBase) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                //  Fondo gradiente para el botón
                GradientPaint gp = new GradientPaint(0, 0, colorBase.brighter(), 0, getHeight(), colorBase.darker());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                //  Borde decorativo
                g2.setColor(colorBase.darker().darker());
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 25, 25);

                //  Texto centrado
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
            }
        };

        //  Propiedades básicas del botón
        boton.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        boton.setForeground(Color.WHITE);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setPreferredSize(new Dimension(400, 60));
        boton.setMaximumSize(new Dimension(400, 60));

        //  Efecto hover (cursor y repintado)
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cambia el cursor
                boton.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // Restaura el cursor
                boton.repaint();
            }
        });

        return boton;
    }

    //  Metodo para abrir la simulación principal
    private void abrirSimulador() {
        SwingUtilities.invokeLater(() -> {
            MarcoSimulador marco = new MarcoSimulador();          // Crea la ventana de simulación
            marco.setTitle(" MINI NEWTONS - Simulacion de esferas");
            marco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            marco.setSize(1300, 800);
            marco.setLocationRelativeTo(null);                  // Centra en pantalla
            marco.setVisible(true);
        });
    }

    //  Metodo para abrir la ventana de fórmulas teóricas
    private void abrirVentanaFormulas() {
        new VentanaFormulas(); // Abre directamente la ventana correspondiente
    }
}

