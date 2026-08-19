package simulador;

import javax.swing.*; // Importa los componentes gráficos de Swing
import java.awt.*;    // Importa utilidades de diseño y color

// Clase que define la ventana principal del modo "Eco-Caída Libre" del simulador
class VentanaEcoCaida extends JFrame {

    // Constructor: configura la ventana al crearse
    public VentanaEcoCaida() {
        // --- Propiedades básicas de la ventana ---
        setTitle("🌱 MINI NEWTONS - Eco-Caída Libre"); // Título que aparece en la barra superior
        setSize(900, 700);                             // Tamaño fijo de la ventana
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Cierra solo esta ventana sin detener el programa
        setLocationRelativeTo(null);                   // Centra la ventana en la pantalla
        setResizable(false);                           // Impide que el usuario cambie el tamaño manualmente

        // --- Panel principal del modo Eco-Caída ---
        PanelEcoCaida panelJuego = new PanelEcoCaida(); // Crea el panel con la simulación
        add(panelJuego, BorderLayout.CENTER);           // Lo agrega al centro del marco principal

        // --- Mostrar la interfaz ---
        setVisible(true); // Hace visible la ventana después de configurarla
    }
}

