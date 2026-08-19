package simulador;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Ejecuta la creación de la interfaz gráfica en el hilo de EDT de Swing
        SwingUtilities.invokeLater(() -> {
            new MenuPrincipal(); // Crea y muestra el menú principal
        });
    }
}
