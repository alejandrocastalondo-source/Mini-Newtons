
package simulador;

// Importaciones necesarias para la interfaz gráfica
import javax.swing.*; // Componentes de Swing (JFrame, JPanel, JButton, etc.)
import java.awt.*;    // Componentes AWT (BorderLayout, Color, Font, etc.)

// Clase principal que representa la ventana/frame de la simulación
class MarcoSimulador extends JFrame { // Hereda de JFrame para crear una ventana
    // Declaración de los paneles principales de la aplicación
    private PanelControles panelControles;      // Panel lateral con controles
    private PanelGrafico panelGrafico;          // Panel central con la simulación gráfica
    private PanelAceleracion panelAceleracion;  // Panel inferior con datos de aceleración

    // Constructor de la clase - se ejecuta al crear una instancia
    public MarcoSimulador() {
        // Establece el layout manager principal como BorderLayout
        // BorderLayout divide la ventana en 5 zonas: NORTH, SOUTH, EAST, WEST, CENTER
        setLayout(new BorderLayout());

        // =============================================
        // CREACIÓN DE LA BARRA SUPERIOR DE LA VENTANA
        // =============================================

        // Crea un panel para la barra superior con BorderLayout
        JPanel barraSuperior = new JPanel(new BorderLayout());
        // Establece un borde vacío (márgenes) alrededor del panel: top, left, bottom, right
        barraSuperior.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        // Define el color de fondo de la barra superior (gris azulado claro)
        barraSuperior.setBackground(new Color(245, 245, 250));

        // =============================================
        // TÍTULO EN LA PARTE IZQUIERDA DE LA BARRA
        // =============================================

        // Crea una etiqueta con el título de la aplicación
        JLabel tituloPequeno = new JLabel("MINI NEWTONS - Simulación");
        // Establece la fuente del título (negrita, tamaño 14)
        tituloPequeno.setFont(new Font("SansSerif", Font.BOLD, 14));
        // Agrega el título a la zona OESTE (izquierda) de la barra superior
        barraSuperior.add(tituloPequeno, BorderLayout.WEST);

        // =============================================
        // BOTÓN CERRAR EN LA PARTE DERECHA DE LA BARRA
        // =============================================

        // Crea el botón "Cerrar"
        JButton botonCerrarSuperior = new JButton("Cerrar");
        // Establece la fuente del botón (negrita, tamaño 12)
        botonCerrarSuperior.setFont(new Font("SansSerif", Font.BOLD, 12));
        // Define el color de fondo del botón (rojo)
        botonCerrarSuperior.setBackground(new Color(200, 50, 50));
        // Establece el color del texto (blanco)
        botonCerrarSuperior.setForeground(Color.WHITE);
        // Elimina el borde de enfoque que aparece al hacer clic
        botonCerrarSuperior.setFocusPainted(false);

        // Agrega un ActionListener (escuchador de eventos) al botón
        // Se usa una expresión lambda para definir la acción al hacer clic
        botonCerrarSuperior.addActionListener(e -> {
            // Muestra un cuadro de diálogo de confirmación
            int respuesta = JOptionPane.showConfirmDialog(
                    this, // Ventana padre (esta misma ventana)
                    "¿Estás seguro de que quieres volver al menú principal?\nSe perderá la simulación actual.", // Mensaje
                    "Confirmar cierre", // Título del diálogo
                    JOptionPane.YES_NO_OPTION, // Tipo de opciones (Sí/No)
                    JOptionPane.QUESTION_MESSAGE // Tipo de mensaje (con icono de pregunta)
            );
            // Si el usuario elige "Sí" (YES_OPTION)
            if (respuesta == JOptionPane.YES_OPTION) {
                // Ejecuta el metodo para volver al menú principal
                volverAlMenu();
            }
            // Si elige "No", no hace nada y el diálogo se cierra
        });

        // =============================================
        // PANEL PARA ALINEAR EL BOTÓN A LA DERECHA
        // =============================================

        // Crea un panel auxiliar con FlowLayout alineado a la derecha
        JPanel panelDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        // Hace el panel transparente (sin fondo propio)
        panelDerecha.setOpaque(false);
        // Agrega el botón Cerrar a este panel
        panelDerecha.add(botonCerrarSuperior);
        // Agrega el panel auxiliar a la zona ESTE (derecha) de la barra superior
        barraSuperior.add(panelDerecha, BorderLayout.EAST);

        // =============================================
        // AGREGAR LA BARRA SUPERIOR A LA VENTANA
        // =============================================

        // Agrega la barra superior completa a la zona NORTE de la ventana principal
        add(barraSuperior, BorderLayout.NORTH);

        // =============================================
        // INICIALIZACIÓN DE LOS PANELES PRINCIPALES
        // =============================================

        // Crea el panel gráfico donde se muestra la simulación visual
        panelGrafico = new PanelGrafico();
        // Crea el panel de controles, pasándole referencias al panel gráfico y a este marco
        panelControles = new PanelControles(panelGrafico, this);
        // Crea el panel de aceleración, pasándole referencia al panel gráfico
        panelAceleracion = new PanelAceleracion(panelGrafico);

        // =============================================
        // ORGANIZACIÓN DE LOS PANELES EN LA VENTANA
        // =============================================

        //  Contenedor central que agrupa simulación y aceleración
        // Crea un panel para agrupar el panel gráfico y el de aceleración
        JPanel panelCentro = new JPanel(new BorderLayout());
        // Agrega el panel gráfico en la zona CENTRO (ocupa la mayor parte)
        panelCentro.add(panelGrafico, BorderLayout.CENTER);
        // Agrega el panel de aceleración en la zona SUR (parte inferior)
        panelCentro.add(panelAceleracion, BorderLayout.SOUTH);

        //  Ahora el contenedor completo - distribución final de la ventana
        // Agrega el panel central (gráfico + aceleración) a la zona CENTRO de la ventana
        add(panelCentro, BorderLayout.CENTER);
        // Agrega el panel de controles a la zona ESTE (derecha) de la ventana
        add(panelControles, BorderLayout.EAST);

        // La distribución final queda así:
        // - NORTH: Barra superior con título y botón cerrar
        // - CENTER: Panel central con la simulación gráfica y datos de aceleración
        // - EAST: Panel de controles lateral
        // - SOUTH: Panel Aceleracion
    }

    // =============================================
    // METODO PARA VOLVER AL MENÚ PRINCIPAL
    // =============================================

    // Metodo público que cierra esta ventana y abre el menú principal
    public void volverAlMenu() {
        dispose(); // Cierra y libera los recursos de la ventana actual
        new MenuPrincipal().setVisible(true); // Crea una nueva instancia del menú principal y lo hace visible
    }
}
