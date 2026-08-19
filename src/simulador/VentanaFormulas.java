
package simulador;

// Importaciones necesarias para la interfaz gráfica
import javax.swing.*; // Componentes de Swing (JFrame, JPanel, JButton, etc.)
import java.awt.*;    // Componentes AWT (Graphics, Color, Font, BorderLayout, etc.)

// Clase que representa una ventana para mostrar fórmulas físicas
class VentanaFormulas extends JFrame { // Hereda de JFrame para crear una ventana
    // Constructor de la clase - se ejecuta al crear una instancia
    public VentanaFormulas() {
        // =============================================
        // CONFIGURACIÓN BÁSICA DE LA VENTANA
        // =============================================

        // Establece el título de la ventana
        setTitle(" MINI NEWTONS - El Mágico Mundo de la Física");
        // Define el tamaño de la ventana (ancho x alto)
        setSize(900, 700);
        // Centra la ventana en la pantalla
        setLocationRelativeTo(null);
        // Define el comportamiento al cerrar la ventana (solo cierra esta ventana, no toda la aplicación)
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // =============================================
        // PANEL PRINCIPAL CON FONDO DEGRADADO
        // =============================================

        // Crea un panel personalizado con fondo degradado
        JPanel panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Llama al metodo de la clase padre para asegurar el pintado correcto
                super.paintComponent(g);
                // Convierte Graphics a Graphics2D para más funcionalidades
                Graphics2D g2d = (Graphics2D) g;
                // Crea un gradiente de color de azul muy claro a azul ligeramente más oscuro
                GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 248, 255), 0, getHeight(), new Color(230, 240, 255));
                // Aplica el gradiente
                g2d.setPaint(gradient);
                //Rellena el panel
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        // Establece el layout del panel principal como BorderLayout
        panelPrincipal.setLayout(new BorderLayout());

        // =============================================
        // TÍTULO PRINCIPAL DE LA VENTANA
        // =============================================

        // Crea una etiqueta para el título, centrada
        JLabel titulo = new JLabel(" El Mágico Mundo de la Física", JLabel.CENTER);
        // Establece la fuente (Comic Sans MS, negrita, tamaño 32)
        titulo.setFont(new Font("Comic Sans MS", Font.BOLD, 32));
        // Define el color del texto (azul oscuro)
        titulo.setForeground(new Color(25, 25, 112));
        // Agrega márgenes vacíos alrededor del título (25 píxeles arriba/abajo)
        titulo.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));

        // =============================================
        // PANEL DE FÓRMULAS CON SCROLL
        // =============================================

        // Crea el panel que contendrá todas las fórmulas
        JPanel panelFormulas = new JPanel();
        // Usa BoxLayout vertical para apilar las fórmulas una debajo de otra
        panelFormulas.setLayout(new BoxLayout(panelFormulas, BoxLayout.Y_AXIS));
        // Establece fondo blanco semitransparente
        panelFormulas.setBackground(new Color(255, 255, 255, 200));
        // Agrega márgenes internos de 25 píxeles en todos los lados
        panelFormulas.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // =============================================
        // AGREGAR LAS DIFERENTES SECCIONES DE FÓRMULAS
        // =============================================

        // Agrega la sección de introducción con texto explicativo
        agregarFormulaMejorada(panelFormulas, " INTRODUCCION",
                "Mini Newtons es un simulador educativo que te permite explorar cómo actúan las leyes del movimiento en la caída libre de un cuerpo.",
                "El simulador considera la gravedad y la resistencia del aire utilizando un coeficiente de arrastre (Cd) = 0.47, propio de una esfera.",
                "Por conveniencia, se trabaja con radios entre 0.005 m y 0.5 m, lo que permite equilibrar la precisión física y el rendimiento visual.",
                "Estos valores te ayudarán a comprender cómo la masa, el tamaño y la densidad del aire afectan la aceleración y la velocidad terminal del cuerpo.",
                "Con Mini Newtons podrás experimentar, comparar y aprender cómo la teoría se convierte en movimiento real. ¡Explora la física a tu manera! "
        );

        // Agrega fórmulas de caída libre sin resistencia del aire
        agregarFormulaMejorada(panelFormulas, " CAÍDA LIBRE SIN AIRE",
                " a = g",
                " y = Vo·t + (g·t²)/2",
                " V = Vo - g·t",
                " t = √(2y / g)",
                " Vo = V + g·t");

        // Agrega fórmulas de caída libre con resistencia del aire
        agregarFormulaMejorada(panelFormulas, " CAÍDA LIBRE CON AIRE",
                "Fg = m·g                                  Vt = √( (2mg) / (ρ·Cd·A) )",
                "Fd = ½·ρ·Cd·A·v²                      A = π·r²",
                "Fnet = Fg−Fd",
                "a = Fnet / m",
                "a = g - (1/2m)·ρ·Cd·A·v²"
        );

        // Agrega fórmulas como funciones del tiempo
        agregarFormulaMejorada(panelFormulas, "FORMULAS COMO FUNCIONES",
                "V(t) = g·t (Caida libre sin aire)",
                "a(t) = g (Caida libre sin aire)",
                "V(t) = Vt·tanh(g·t / Vt)",
                "a(t) = g(1 - tanh²(g·t / Vt))",
                "Vt = √( (2mg) / (ρ·Cd·A) )");

        // Agrega definición de variables
        agregarFormulaMejorada(panelFormulas, "VARIABLES",
                "a = Aceleracion                  Fg = Peso                           A = Area",
                "g = Gravedad                      t = Tiempo                         m = masa",
                "V = Velocidad                     Fd = Fuerza de arrastre      r = Radio",
                "y = Altura                          Fnet = Fuerza neta             Cd = Coeficiente de arrastre = 0.47",
                "Vo = Velocidad inicial          Vt = Velocidad terminal"
        );
        agregarFormulaMejorada(panelFormulas, "AUTORES",
                "Daniel Castañeda Londoño",
                "Juan Pablo Osorio Galvis",
                "Juan Esteban Cardona Marin",
                "Juan David Zuñiga Zuñiga");


        // =============================================
        // SCROLL PANE PARA LAS FÓRMULAS
        // =============================================

        // Envuelve el panel de fórmulas en un JScrollPane para hacerlo desplazable
        JScrollPane scrollPane = new JScrollPane(panelFormulas);
        // Crea un borde decorativo con título
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 150, 255), 2), // Borde azul
                "✨ Fórmulas Mágicas ✨" // Título del borde
        ));
        // Establece el color de fondo del área visible del scroll
        scrollPane.getViewport().setBackground(new Color(245, 250, 255));

        // =============================================
        // BOTÓN CERRAR PERSONALIZADO
        // =============================================

        // Crea un botón personalizado con pintado manual
        JButton btnCerrar = new JButton(" Volver al Menú Principal") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                // Habilita suavizado de bordes
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Crea un gradiente rojo-naranja para el botón
                GradientPaint gp = new GradientPaint(0, 0, new Color(218, 14, 14), 0, getHeight(), new Color(253, 120, 62));
                g2.setPaint(gp);
                // Rellena el botón con bordes redondeados
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Dibuja el borde del botón
                g2.setColor(new Color(237, 20, 20));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);

                // Dibuja el texto del botón centrado
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                // Calcula la posición para centrar el texto
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
            }
        };
        // Configura propiedades del botón
        btnCerrar.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setContentAreaFilled(false); // Desactiva el pintado por defecto
        btnCerrar.setBorderPainted(false);     // Oculta el borde por defecto
        btnCerrar.setFocusPainted(false);      // Oculta el borde de enfoque
        btnCerrar.setPreferredSize(new Dimension(250, 45)); // Tamaño preferido
        // Agrega el ActionListener para cerrar la ventana al hacer clic
        btnCerrar.addActionListener(e -> dispose());

        // =============================================
        // PANEL PARA EL BOTÓN CERRAR
        // =============================================

        // Crea un panel para contener el botón cerrar
        JPanel panelBoton = new JPanel();
        // Fondo transparente
        panelBoton.setBackground(new Color(240, 248, 255, 0));
        // Agrega márgenes arriba y abajo
        panelBoton.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        // Agrega el botón al panel
        panelBoton.add(btnCerrar);

        // =============================================
        // ENSAMBLAJE FINAL DE LA VENTANA
        // =============================================

        // Agrega el título en la parte NORTH (arriba)
        panelPrincipal.add(titulo, BorderLayout.NORTH);
        // Agrega el scroll pane con fórmulas en el CENTER (centro, ocupa la mayor parte)
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        // Agrega el panel con el botón en la parte SOUTH (abajo)
        panelPrincipal.add(panelBoton, BorderLayout.SOUTH);

        // Agrega el panel principal a la ventana
        add(panelPrincipal);
        // Hace visible la ventana
        setVisible(true);
    }

    // =============================================
    // METODO PARA AGREGAR FÓRMULAS MEJORADAS
    // =============================================

    // Metodo privado que crea y agrega un panel de fórmula al panel principal
    private void agregarFormulaMejorada(JPanel panel, String titulo, String... formulas) {
        // Crea un panel personalizado para cada grupo de fórmulas
        JPanel panelFormula = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                // Habilita suavizado de bordes
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo con gradiente beige claro
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 250, 240), 0, getHeight(), new Color(255, 245, 230));
                g2d.setPaint(gp);
                // Rellena el panel con bordes redondeados
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Borde decorativo color marrón claro
                g2d.setColor(new Color(210, 180, 140));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);
            }
        };

        // Configura el layout del panel de fórmula (vertical)
        panelFormula.setLayout(new BoxLayout(panelFormula, BoxLayout.Y_AXIS));
        // Agrega márgenes internos
        panelFormula.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        // Establece altura máxima para cada panel de fórmula
        panelFormula.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        // Título de la sección de fórmulas
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(139, 69, 19)); // Color marrón
        panelFormula.add(lblTitulo);

        // Agrega espacio vertical entre el título y las fórmulas
        panelFormula.add(Box.createVerticalStrut(15));

        // Itera sobre cada fórmula proporcionada y la agrega al panel
        for (String formula : formulas) {
            // Crea una etiqueta para cada fórmula con un emoji decorativo
            JLabel lblFormula = new JLabel("🎀 " + formula);
            lblFormula.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
            lblFormula.setForeground(new Color(80, 80, 80)); // Color gris oscuro
            panelFormula.add(lblFormula);
        }

        // Agrega el panel de fórmula completo al panel principal
        panel.add(panelFormula);
        // Agrega espacio vertical entre diferentes grupos de fórmulas
        panel.add(Box.createVerticalStrut(20));
    }
}
