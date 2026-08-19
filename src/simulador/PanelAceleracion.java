package simulador;

import javax.swing.*; // Librería Swing
import java.awt.*;    // Librería AWT para manejar colores, fuentes y diseño de componentes

// Clase que representa un panel donde se calcula la aceleración en un instante dado
class PanelAceleracion extends JPanel {
    // Campos de texto y etiquetas donde se muestran los resultados
    private JTextField txtTiempo;
    private JLabel lblResultado1, lblResultado2;

    // Constructor del panel, recibe una referencia al panel gráfico principal para obtener los datos físicos
    public PanelAceleracion(PanelGrafico panelGrafico) {
        // Configuración del diseño y apariencia general del panel
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10)); // Distribuye los componentes de izquierda a derecha
        setBackground(new Color(245, 245, 245));            // Color de fondo gris claro
        setBorder(BorderFactory.createTitledBorder(         // Borde con título explicativo
                "Calcular aceleración - Ingresa un tiempo para saber qué aceleración llevaba en ese instante"));
        setPreferredSize(new Dimension(0, 90));             // Altura preferida del panel
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 110)); // Evita que se deforme al redimensionar

        // Creación de los componentes visuales del panel
        JLabel lblTiempo = new JLabel("Tiempo (s):"); // Etiqueta que indica qué debe ingresar el usuario
        txtTiempo = new JTextField(8);                // Campo de texto para ingresar el tiempo
        JButton btnCalcular = new JButton("Calcular");// Botón que ejecuta el cálculo de aceleración

        // Etiquetas donde se mostrarán los resultados de cada esfera
        lblResultado1 = new JLabel("Esfera 1: -");
        lblResultado2 = new JLabel("Esfera 2: -");

        // Se agregan los componentes al panel en orden
        add(lblTiempo);
        add(txtTiempo);
        add(btnCalcular);
        add(lblResultado1);
        add(lblResultado2);

        // Acción del botón "Calcular"
        btnCalcular.addActionListener(e -> {
            try {
                // Obtiene el valor del tiempo ingresado por el usuario
                double t = Double.parseDouble(txtTiempo.getText().trim());

                //  Validación: evita tiempos negativos
                if (t < 0) {
                    lblResultado1.setText("⚠️ Tiempo no puede ser negativo");
                    lblResultado2.setText("");
                    return; // Detiene la ejecución si el valor no es válido
                }

                // Obtiene las aceleraciones de ambas esferas a partir del panel gráfico principal
                double a1 = panelGrafico.getAceleracionEsfera1(t);
                double a2 = panelGrafico.getAceleracionEsfera2(t);

                // Muestra los resultados en pantalla con dos decimales
                lblResultado1.setText(String.format("Esfera 1: %.2f m/s²", a1));
                lblResultado2.setText(String.format("Esfera 2: %.2f m/s²", a2));
            } catch (NumberFormatException ex) {
                //  Si el usuario escribe un valor que no puede convertirse a número
                lblResultado1.setText("⚠️ Tiempo inválido");
                lblResultado2.setText("");
            }
        });
    }
}

