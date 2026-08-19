package simulador; // Indica que esta clase pertenece al paquete "simulador"

import javax.swing.*; // Proporciona las clases de la biblioteca Swing (ventanas, paneles, botones, etc.)
import java.awt.*;    // Contiene las clases para gráficos 2D (colores, fuentes, dibujos, etc.)
import java.util.List; // Permite usar listas dinámicas (List) para manejar colecciones de datos

// Clase principal que muestra una ventana con las gráficas comparativas
class VentanaGrafica extends JFrame {

    // Constructor de la ventana que recibe dos objetos Esfera para comparar
    public VentanaGrafica(Esfera esfera1, Esfera esfera2) {
        setTitle("Gráficas Comparativas - Velocidad vs Tiempo"); // Título de la ventana
        setSize(900, 600);                                       // Tamaño de la ventana
        setLocationRelativeTo(null);                             // Centra la ventana en la pantalla
        add(new PanelGraficoComparativo(esfera1, esfera2));      // Agrega un panel donde se dibujan las gráficas
        setVisible(true);                                        // Hace visible la ventana
    }

    // Clase interna que extiende JPanel, encargada de graficar la información
    static class PanelGraficoComparativo extends JPanel {
        Esfera esfera1, esfera2; // Referencias a las dos esferas cuyas velocidades se comparan

        // Constructor que recibe las dos esferas y las guarda en atributos internos
        public PanelGraficoComparativo(Esfera esfera1, Esfera esfera2) {
            this.esfera1 = esfera1;
            this.esfera2 = esfera2;
        }

        // Metodo de Swing que se ejecuta automáticamente cada vez que el panel se repinta
        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0); // Llama al metodo original para limpiar el fondo del panel

            // Si ambas esferas no tienen datos, no se dibuja nada
            if (esfera1.getDatosTiempo().isEmpty() && esfera2.getDatosTiempo().isEmpty()) return;

            // Se convierte el objeto Graphics en Graphics2D para obtener funciones más avanzadas
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Suaviza bordes

            // Se obtienen las dimensiones actuales del panel
            int ancho = getWidth(), alto = getHeight();
            // Márgenes para los ejes y bordes del gráfico
            int izquierda = 80, derecha = ancho - 60, arriba = 60, abajo = alto - 120;

            // Fondo con un degradado vertical (de azul muy claro a celeste)
            GradientPaint fondo = new GradientPaint(0, 0, new Color(245, 250, 255),
                    0, alto, new Color(220, 240, 255));
            g.setPaint(fondo);
            g.fillRect(0, 0, ancho, alto); // Dibuja el rectángulo de fondo

            // Título superior del gráfico
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
            g.drawString("Comparación de Velocidades vs Tiempo", ancho/2 - 180, 30);

            // Ejes principales del gráfico
            g.setColor(Color.GRAY);
            g.setStroke(new BasicStroke(2f)); // Grosor de las líneas
            g.drawLine(izquierda, abajo, derecha, abajo); // Eje X (tiempo)
            g.drawLine(izquierda, arriba, izquierda, abajo); // Eje Y (velocidad)

            // Etiquetas de los ejes
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
            g.drawString("Velocidad (m/s)", izquierda - 70, arriba - 10);
            g.drawString("Tiempo (s)", (ancho - 100) / 2, abajo + 40);

            // Cálculo de los valores máximos de tiempo y velocidad para escalar el gráfico
            double tiempoMaximo = Math.max(
                    esfera1.getDatosTiempo().isEmpty() ? 0 : esfera1.getDatosTiempo().get(esfera1.getDatosTiempo().size()-1),
                    esfera2.getDatosTiempo().isEmpty() ? 0 : esfera2.getDatosTiempo().get(esfera2.getDatosTiempo().size()-1)
            );

            double velocidadMaxima = Math.max(
                    esfera1.getDatosVelocidad().stream().mapToDouble(Double::doubleValue).max().orElse(10),
                    esfera2.getDatosVelocidad().stream().mapToDouble(Double::doubleValue).max().orElse(10)
            );

            // Marcas y etiquetas numéricas en los ejes
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            for (int i = 0; i <= 10; i++) {
                // Coordenadas en X e Y para las divisiones
                int x = izquierda + (int)((i / 10.0) * (derecha - izquierda));
                int y = abajo - (int)((i / 10.0) * (abajo - arriba));

                // Pequeñas marcas en los ejes
                g.drawLine(x, abajo - 5, x, abajo + 5); // marcas horizontales (tiempo)
                g.drawLine(izquierda - 5, y, izquierda + 5, y); // marcas verticales (velocidad)

                // Valores numéricos de referencia
                g.drawString(String.format("%.1f", (tiempoMaximo * i) / 10), x - 10, abajo + 20);
                g.drawString(String.format("%.1f", (velocidadMaxima * i) / 10), izquierda - 50, y + 5);
            }

            // Línea correspondiente a la Esfera 1 (color del propio objeto)
            if (!esfera1.getDatosTiempo().isEmpty()) {
                g.setColor(esfera1.getColor());
                g.setStroke(new BasicStroke(2.5f));
                dibujarLinea(g, esfera1.getDatosTiempo(), esfera1.getDatosVelocidad(),
                        tiempoMaximo, velocidadMaxima, izquierda, derecha, arriba, abajo);
            }

            // Línea correspondiente a la Esfera 2 (color del propio objeto)
            if (!esfera2.getDatosTiempo().isEmpty()) {
                g.setColor(esfera2.getColor());
                g.setStroke(new BasicStroke(2.5f));
                dibujarLinea(g, esfera2.getDatosTiempo(), esfera2.getDatosVelocidad(),
                        tiempoMaximo, velocidadMaxima, izquierda, derecha, arriba, abajo);
            }

            // Leyenda que identifica el color de cada esfera
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 14));

            // Cuadro de color y texto para Esfera 1
            g.setColor(esfera1.getColor());
            g.fillRect(derecha - 200, arriba + 20, 15, 15);
            g.setColor(Color.BLACK);
            g.drawString("Esfera 1 ", derecha - 180, arriba + 32);

            // Cuadro de color y texto para Esfera 2
            g.setColor(esfera2.getColor());
            g.fillRect(derecha - 200, arriba + 50, 15, 15);
            g.setColor(Color.BLACK);
            g.drawString("Esfera 2 ", derecha - 180, arriba + 62);

            // Información adicional sobre las propiedades de cada esfera
            g.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
            g.setColor(Color.DARK_GRAY);

            // Datos de masa, radio y resistencia del aire
            String info1 = String.format("Esfera 1: m=%.1fkg, r=%.2fm %s",
                    esfera1.getMasa(), esfera1.getRadio(),
                    esfera1.isConResistenciaAire() ? "CON aire" : "SIN aire");

            String info2 = String.format("Esfera 2: m=%.1fkg, r=%.2fm %s",
                    esfera2.getMasa(), esfera2.getRadio(),
                    esfera2.isConResistenciaAire() ? "CON aire" : "SIN aire");

            // Muestra la información al pie del gráfico
            g.drawString(info1, izquierda + 10, arriba + 30);
            g.drawString(info2, izquierda + 10, arriba + 50);
        }

        // Metodo auxiliar para trazar las líneas del gráfico de velocidad vs tiempo
        private void dibujarLinea(Graphics2D g, List<Double> tiempos, List<Double> velocidades,
                                  double tiempoMaximo, double velocidadMaxima,
                                  int izquierda, int derecha, int arriba, int abajo) {
            for (int i = 1; i < tiempos.size(); i++) {
                // Conversión de valores de tiempo y velocidad a coordenadas en pantalla
                int x1 = izquierda + (int)((tiempos.get(i - 1) / tiempoMaximo) * (derecha - izquierda));
                int y1 = abajo - (int)((velocidades.get(i - 1) / velocidadMaxima) * (abajo - arriba));
                int x2 = izquierda + (int)((tiempos.get(i) / tiempoMaximo) * (derecha - izquierda));
                int y2 = abajo - (int)((velocidades.get(i) / velocidadMaxima) * (abajo - arriba));

                // Trazo de un segmento entre los puntos (x1, y1) y (x2, y2)
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }
}

