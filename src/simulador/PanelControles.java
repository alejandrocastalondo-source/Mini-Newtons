// Importaciones de clases necesarias para la interfaz gráfica y funcionalidades
package simulador; // Define el paquete donde está esta clase

import javax.swing.*; // Componentes de interfaz gráfica (botones, paneles, etc.)
import javax.swing.text.AbstractDocument; // Para documentos de texto
import javax.swing.text.AttributeSet; // Atributos de texto
import javax.swing.text.BadLocationException; // Excepciones de ubicación en texto
import javax.swing.text.DocumentFilter; // Filtro para validar entrada de texto
import java.awt.*; // Componentes gráficos básicos (colores, fuentes, layouts)
import java.awt.event.ActionListener; // Para manejar eventos de botones
import java.awt.event.MouseAdapter; // Adaptador para eventos del mouse
import java.awt.event.MouseEvent; // Eventos del mouse

// Clase que representa el panel de controles de la simulación
class PanelControles extends JPanel {
    // Campos de texto para los parámetros de la primera esfera
    private JTextField campoRadio1, campoAltura1, campoMasa1, campoGravedad1;
    // Campos de texto para los parámetros de la segunda esfera
    private JTextField campoRadio2, campoAltura2, campoMasa2, campoGravedad2;
    // Botones de control de la simulación
    private JButton botonAplicar, botonPlayPausa, botonReiniciar, botonMostrarGrafica;
    // Botones de opción para resistencia del aire
    private JRadioButton botonAireSi1, botonAireNo1, botonAireSi2, botonAireNo2;
    // Referencia al panel gráfico donde se muestra la simulación
    private PanelGrafico grafico;
    // Referencia al marco principal de la aplicación
    private MarcoSimulador marco;

    // Metodo para crear campos de texto con validacion
    private JTextField crearCampoDivertido(String valorDefecto, String tooltip) {
        // Crea un campo de texto con valor por defecto y ancho de 10 caracteres
        JTextField campo = new JTextField(valorDefecto, 10);
        // Establece la fuente del texto
        campo.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        // Crea un borde compuesto con línea azul y padding interno
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 255), 2),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        // Establece color de fondo azul muy claro
        campo.setBackground(new Color(250, 255, 255));
        // Texto que aparece al pasar el mouse sobre el campo
        campo.setToolTipText(tooltip);

        // Agregar DocumentFilter para validar entrada (solo números)
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                // Obtiene el texto actual más el nuevo texto a insertar
                String newText = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                // Verifica si el texto coincide con el patrón de números (incluyendo decimales y negativos)
                if (newText.matches("-?\\d*(\\.\\d*)?")) {
                    // Si es válido, permite el reemplazo
                    super.replace(fb, offset, length, text, attrs);
                }
                // Si no coincide, no permite la entrada
            }
        });

        return campo;
    }

    // Metodo auxiliar para crear filas de campos con etiqueta
    private JPanel crearFilaCampo(String etiqueta, JTextField campo) {
        // Crea un panel con BorderLayout y espacio horizontal de 10 píxeles
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        // Hace el panel transparente
        panel.setOpaque(false);
        // Establece tamaño máximo para la fila
        panel.setMaximumSize(new Dimension(300, 35));

        // Crea la etiqueta para el campo
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        lbl.setForeground(new Color(80, 80, 80));

        // Agrega la etiqueta a la izquierda y el campo al centro
        panel.add(lbl, BorderLayout.WEST);
        panel.add(campo, BorderLayout.CENTER);

        return panel;
    }

    // Metodo para crear paneles de esfera con diseño decorado
    private JPanel crearPanelEsfera(String titulo, JTextField campoAltura, JTextField campoMasa,
                                    JTextField campoGravedad, JTextField campoRadio,
                                    JRadioButton botonAireSi, JRadioButton botonAireNo) {
        // Crea un panel personalizado con fondo degradado
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Convierte Graphics a Graphics2D para más funcionalidades
                Graphics2D g2d = (Graphics2D) g;
                // Habilita suavizado de bordes
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Crea un gradiente de color de blanco a azul muy claro
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 255, 255), 0, getHeight(), new Color(240, 248, 255));
                g2d.setPaint(gp);
                // Rellena el panel con bordes redondeados
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Dibuja el borde del panel
                g2d.setColor(new Color(200, 220, 255));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 15, 15);
            }
        };

        // Configura el layout del panel (vertical)
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // Agrega padding interno
        panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        // Establece tamaño máximo
        panel.setMaximumSize(new Dimension(350, 280));

        // Título del panel
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        lblTitulo.setForeground(new Color(70, 130, 180));
        // Centra el título
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTitulo);
        // Agrega espacio vertical
        panel.add(Box.createRigidArea(new Dimension(0,10)));

        // Agregar campos en filas
        panel.add(crearFilaCampo(" Altura (m):", campoAltura));
        panel.add(Box.createRigidArea(new Dimension(0,5)));
        panel.add(crearFilaCampo(" Masa (kg):", campoMasa));
        panel.add(Box.createRigidArea(new Dimension(0,5)));
        panel.add(crearFilaCampo(" Gravedad (m/s²):", campoGravedad));
        panel.add(Box.createRigidArea(new Dimension(0,5)));
        panel.add(crearFilaCampo(" Radio (m):", campoRadio));
        panel.add(Box.createRigidArea(new Dimension(0,10)));

        // Panel para la opción de resistencia del aire
        JPanel panelAire = new JPanel();
        panelAire.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelAire.setOpaque(false);

        // Etiqueta para resistencia del aire
        JLabel lblAire = new JLabel(" Resistencia aire:");
        lblAire.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        panelAire.add(lblAire);

        // Configurar botones de radio
        botonAireSi.setText("Sí");
        botonAireNo.setText("No");
        botonAireSi.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        botonAireNo.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        // Selecciona "No" por defecto
        botonAireNo.setSelected(true);

        // Grupo de botones para que solo uno pueda estar seleccionado
        ButtonGroup grupoAire = new ButtonGroup();
        grupoAire.add(botonAireSi);
        grupoAire.add(botonAireNo);

        // Agrega los botones al panel
        panelAire.add(botonAireSi);
        panelAire.add(botonAireNo);

        panel.add(panelAire);

        return panel;
    }

    // Metodo para crear botones con diseño personalizado
    private JButton crearBotonMagico(String texto, Color colorBase, ActionListener action) {
        // Crea un botón personalizado con pintado manual
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                // Habilita suavizado
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Crea gradiente de color para el botón
                GradientPaint gp = new GradientPaint(0, 0, colorBase.brighter(), 0, getHeight(), colorBase.darker());
                g2.setPaint(gp);
                // Rellena el botón con bordes redondeados
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Dibuja el borde del botón
                g2.setColor(colorBase.darker().darker());
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);

                // Dibuja el texto del botón
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                // Centra el texto
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
            }
        };

        // Configura propiedades del botón
        boton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        boton.setForeground(Color.WHITE);
        // Desactiva el pintado por defecto
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        // Centra el botón
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Establece tamaño preferido y máximo
        boton.setPreferredSize(new Dimension(300, 45));
        boton.setMaximumSize(new Dimension(300, 45));
        // Asigna el action listener
        boton.addActionListener(action);

        // Efecto hover (cambiar cursor al pasar el mouse)
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return boton;
    }

    // Constructor principal de la clase
    public PanelControles(PanelGrafico grafico, MarcoSimulador marco) {
        this.grafico = grafico;
        this.marco = marco;

        // Cambiar a un layout que permita scroll
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(380, 0));
        setBackground(new Color(245, 250, 255));

        // Panel interno que contendrá todos los componentes
        JPanel panelInterno = new JPanel();
        panelInterno.setLayout(new BoxLayout(panelInterno, BoxLayout.Y_AXIS));
        panelInterno.setBackground(new Color(245, 250, 255));
        panelInterno.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        // Título mejorado con HTML para formato
        JLabel titulo = new JLabel("<html><div style='text-align: center;'>"
                + "<font size='6' color='#1E90FF'>🎮 CONTROLES</font><br>"
                + "<font size='3' color='#666666'>¡Ajusta la magia!</font></div></html>");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelInterno.add(titulo);
        panelInterno.add(Box.createRigidArea(new Dimension(0,20)));

        // Crear campos para las esferas con valores por defecto y tooltips
        campoAltura1 = crearCampoDivertido("100", " ¿Qué tan alto? (más que 0)");
        campoMasa1 = crearCampoDivertido("5", " ¿Qué tan pesada? (más que 0)");
        campoGravedad1 = crearCampoDivertido("9.8", " Fuerza mágica (más que 0)");
        campoRadio1 = crearCampoDivertido("0.3", " Tamaño mágico (0.005 a 0.5)");
        botonAireSi1 = new JRadioButton("Sí");
        botonAireNo1 = new JRadioButton("No");

        campoAltura2 = crearCampoDivertido("100", " ¿Qué tan alto? (más que 0)");
        campoMasa2 = crearCampoDivertido("1", " ¿Qué tan pesada? (más que 0)");
        campoGravedad2 = crearCampoDivertido("9.8", " Fuerza mágica (más que 0)");
        campoRadio2 = crearCampoDivertido("0.5", " Tamaño mágico (0.005 a 0.5)");
        botonAireSi2 = new JRadioButton("Sí");
        botonAireNo2 = new JRadioButton("No");

        // Paneles para cada esfera con diseño mejorado
        panelInterno.add(crearPanelEsfera(" ESFERA AZUL",
                campoAltura1, campoMasa1, campoGravedad1, campoRadio1, botonAireSi1, botonAireNo1));

        panelInterno.add(Box.createRigidArea(new Dimension(0,25)));

        panelInterno.add(crearPanelEsfera(" ESFERA ROJA",
                campoAltura2, campoMasa2, campoGravedad2, campoRadio2, botonAireSi2, botonAireNo2));

        panelInterno.add(Box.createRigidArea(new Dimension(0,25)));

        // Botón aplicar valores
        botonAplicar = crearBotonMagico("¡Aplicar Valores a Ambas Esferas!",
                new Color(46, 204, 113), e -> aplicarValores());
        panelInterno.add(botonAplicar);

        panelInterno.add(Box.createRigidArea(new Dimension(0,15)));

        // Botón play/pausa con funcionalidad toggle
        botonPlayPausa = crearBotonMagico(" ¡Comenzar la Simulacion!",
                new Color(52, 152, 219), e -> {
                    if (grafico.estaEjecutandose()) {
                        grafico.pausar();
                        botonPlayPausa.setText(" ¡Continuar Simulacion!");
                    } else {
                        grafico.reproducir();
                        botonPlayPausa.setText(" ¡Pausar Simulacion!");
                    }
                });
        panelInterno.add(botonPlayPausa);

        panelInterno.add(Box.createRigidArea(new Dimension(0,10)));

        // Botón reiniciar
        botonReiniciar = crearBotonMagico(" ¡Reiniciar Todo!",
                new Color(155, 89, 182), e -> {
                    grafico.reiniciar();
                    botonPlayPausa.setText(" ¡Comenzar la Simulacion!");
                });
        panelInterno.add(botonReiniciar);

        panelInterno.add(Box.createRigidArea(new Dimension(0,10)));

        // Botón para mostrar la gráfica
        botonMostrarGrafica = crearBotonMagico(" ¡Ver Gráficas!",
                new Color(241, 196, 15), e -> {
                    // Verifica si hay datos para mostrar
                    if (grafico.getEsfera1().getDatosTiempo().isEmpty() && grafico.getEsfera2().getDatosTiempo().isEmpty()) {
                        JOptionPane.showMessageDialog(this,
                                " ¡Primero debes comenzar la simulacion para ver las gráficas!",
                                "Aventura por Comenzar", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // Crea ventana de gráficas si hay datos
                        new VentanaGrafica(grafico.getEsfera1(), grafico.getEsfera2());
                    }
                });
        panelInterno.add(botonMostrarGrafica);

        // Agrega espacio flexible al final
        panelInterno.add(Box.createVerticalGlue());

        // Agregar el panel interno a un JScrollPane para hacerlo desplazable
        JScrollPane scrollPane = new JScrollPane(panelInterno);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);
    }

    // Metodo para aplicar los valores ingresados a las esferas
    private void aplicarValores() {
        try {
            // Obtener y convertir valores de la esfera 1
            double altura1 = Double.parseDouble(campoAltura1.getText());
            double masa1 = Double.parseDouble(campoMasa1.getText());
            double gravedad1 = Double.parseDouble(campoGravedad1.getText());
            double radio1 = Double.parseDouble(campoRadio1.getText());
            boolean resistenciaAire1 = botonAireSi1.isSelected();

            // Obtener y convertir valores de la esfera 2
            double altura2 = Double.parseDouble(campoAltura2.getText());
            double masa2 = Double.parseDouble(campoMasa2.getText());
            double gravedad2 = Double.parseDouble(campoGravedad2.getText());
            double radio2 = Double.parseDouble(campoRadio2.getText());
            boolean resistenciaAire2 = botonAireSi2.isSelected();

            // VALIDACIONES PARA ESFERA 1
            if (radio1 < 0.005 || radio1 > 0.5) {
                JOptionPane.showMessageDialog(this,
                        " El radio de la Esfera Azul debe estar entre 0.005 y 0.5 metros.",
                        "¡Atención!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (altura1 <= 0) {
                JOptionPane.showMessageDialog(this,
                        " La altura de la Esfera Azul debe ser mayor que 0.",
                        "¡Atención!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (masa1 <= 0) {
                JOptionPane.showMessageDialog(this,
                        " La masa de la Esfera Azul debe ser mayor que 0.",
                        "¡Atención!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (gravedad1 <= 0) {
                JOptionPane.showMessageDialog(this,
                        " La gravedad de la Esfera Azul debe ser mayor que 0.",
                        "¡Atención!", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // VALIDACIONES PARA ESFERA 2
            if (radio2 < 0.005 || radio2 > 0.5) {
                JOptionPane.showMessageDialog(this,
                        " El radio de la Esfera Roja debe estar entre 0.005 y 0.5 metros.",
                        "¡Atención!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (altura2 <= 0) {
                JOptionPane.showMessageDialog(this,
                        " La altura de la Esfera Roja debe ser mayor que 0.",
                        "¡Atención!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (masa2 <= 0) {
                JOptionPane.showMessageDialog(this,
                        " La masa de la Esfera Roja debe ser mayor que 0.",
                        "¡Atención!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (gravedad2 <= 0) {
                JOptionPane.showMessageDialog(this,
                        " La gravedad de la Esfera Roja debe ser mayor que 0.",
                        "¡Atención!", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Aplicar valores validados a esfera 1
            grafico.getEsfera1().setAlturaInicial(altura1);
            grafico.getEsfera1().setMasa(masa1);
            grafico.getEsfera1().setGravedad(gravedad1);
            grafico.getEsfera1().setRadio(radio1);
            grafico.getEsfera1().setConResistenciaAire(resistenciaAire1);

            // Aplicar valores validados a esfera 2
            grafico.getEsfera2().setAlturaInicial(altura2);
            grafico.getEsfera2().setMasa(masa2);
            grafico.getEsfera2().setGravedad(gravedad2);
            grafico.getEsfera2().setRadio(radio2);
            grafico.getEsfera2().setConResistenciaAire(resistenciaAire2);

            // Mensaje de éxito
            JOptionPane.showMessageDialog(this,
                    " ¡Valores aplicados con éxito!\nLas esferas están listas para la aventura.\nDale a Reiniciar y luego Comenzar.",
                    "¡A Simular!", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            // Manejo de error si se ingresan valores no numéricos
            JOptionPane.showMessageDialog(this,
                    " Por favor ingresa solo números validos en todos los campos.",
                    "¡Números Requeridos!", JOptionPane.ERROR_MESSAGE);
        }
    }
}


