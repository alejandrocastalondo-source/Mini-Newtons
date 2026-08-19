package simulador;
import javax.swing.*;
// Importa las clases Swing (JPanel, Timer, etc.).
import java.awt.*;
// Importa AWT (Graphics, Color, Dimension, GradientPaint, Font, BasicStroke, etc.)

import java.text.DecimalFormat;


/*
  Clase PanelGrafico: componente Swing que se encarga de dibujar la escena,
  manejar el temporizador de la animación y sincronizar la actualización
  de las dos esferas (modeladas por la clase Esfera).
*/
class PanelGrafico extends JPanel {

    // Referencias a las dos esferas que vamos a simular/dibujar
    private Esfera esfera1, esfera2;

    // Indica si la simulación está en marcha
    private boolean ejecutandose = false;

    // Temporizador Swing que lanza eventos periódicos (usado para animación)
    private Timer temporizador;

    // Formato para mostrar valores numéricos en pantalla (2 decimales)
    private DecimalFormat formatoDecimal = new DecimalFormat("#0.00");

    // Variable auxiliar para desplazar/animar las nubes (simple offset)
    private double desplazamientoNubes = 0;

    // Intervalo de integración (segundos). 0.016 ≈ 16ms -> 60 actualizaciones por segundo

    private double intervalo = 0.016; // 60 FPS ≈ 16ms

    /* Constructor: inicializa el panel gráfico, estilo visual, esferas y timer */
    public PanelGrafico() {
        // Color de fondo inicial del componente (se usa también un gradiente en paintComponent)
        setBackground(new Color(135, 206, 250));

        // Tamaño preferido sugerido al layout manager: ancho x alto (pixels)
        setPreferredSize(new Dimension(800, 500));

        // Crear las dos esferas con nombre y color (Esfera debe tener constructor que acepta nombre y color)
        esfera1 = new Esfera("Esfera 1", new Color(65, 105, 225));  // Azul real
        esfera2 = new Esfera("Esfera 2", new Color(220, 20, 60));   // Rojo carmesí

        // Inicializamos el temporizador Swing: cada 16 ms ejecuta la lambda e -> actualizar()
        // Nota: Swing Timer ejecuta en el hilo de despacho de eventos (EDT), por eso
        // las actualizaciones y el repintado están sincronizados con la UI.
        temporizador = new Timer(16, e -> actualizar()); // ≈60 FPS
    }

    /* Metodo que actualiza la simulación (invocado por el Timer y cuando se reproduce) */
    private void actualizar() {
        // Si la simulación no está activa, no hacemos nada.
        if (!ejecutandose) return;

        // Avanza el desplazamiento de las nubes para animarlas (valor arbitrario)
        desplazamientoNubes += 0.2;

        // --- Sub-steps: realizar varios pasos de integración por frame ---
        // Hacemos esto para mejorar estabilidad/precisión de la integración numérica
        // sin tener que bajar el intervalo global. Por ejemplo, con subSteps=2:
        // se integrará con dt = intervalo/2 dos veces por frame.
        int subSteps = 2; // Número de sub-pasos para mayor precisión
        double subIntervalo = intervalo / subSteps;

        for (int i = 0; i < subSteps; i++) {
            // Actualiza esfera1 sólo si no ha terminado (no llegó al suelo)
            if (!esfera1.haTerminado()) {
                esfera1.actualizar(subIntervalo);
            }
            // Misma lógica para esfera2
            if (!esfera2.haTerminado()) {
                esfera2.actualizar(subIntervalo);
            }
        }

        // Si ambas esferas han llegado al suelo, paramos la simulación y el timer
        if (esfera1.haTerminado() && esfera2.haTerminado()) {
            ejecutandose = false;
            temporizador.stop();
        }

        // Forzar repintado del componente (llama a paintComponent en el EDT)
        repaint(); // Forzar redibujado inmediato
    }

    /* paintComponent: metodo Swing donde dibujamos la escena entera */
    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0); // Limpia el fondo y prepara el contexto gráfico

        // Convertimos a Graphics2D para acceder a funciones avanzadas (antialiasing, Paint, etc.)
        Graphics2D g = (Graphics2D) g0;

        // Activamos antialiasing para que las formas queden suaves
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dimensiones actuales del panel (pueden cambiar si la ventana se redimensiona)
        int ancho = getWidth(), alto = getHeight();

        // --- Fondo con degradado vertical para simular el cielo ---
        GradientPaint cielo = new GradientPaint(0, 0, new Color(135, 206, 250),
                0, alto, new Color(173, 216, 230));
        g.setPaint(cielo);
        g.fillRect(0, 0, ancho, alto);

        // --- Sol: un círculo semitransparente en la esquina superior derecha ---
        g.setColor(new Color(255, 223, 0, 200)); // último parámetro = alpha (200/255)
        g.fillOval(ancho - 120, 40, 80, 80);

        // --- Nubes animadas: se dibujan desplazadas por desplazamientoNubes ---
        g.setColor(Color.WHITE);
        // Usamos distintas velocidades multiplicando desplazamientoNubes para parallax
        dibujarNube(g, (int)(50 + desplazamientoNubes % (ancho + 100)) - 100, 80);
        dibujarNube(g, (int)(250 + desplazamientoNubes * 0.8 % (ancho + 100)) - 100, 130);
        dibujarNube(g, (int)(450 + desplazamientoNubes * 1.2 % (ancho + 100)) - 100, 60);

        // --- Césped (suelo visual) ---
        int posicionYSuelo = alto - 80; // coordenada Y del "suelo" en pixels
        g.setColor(new Color(34,139,34)); // color del césped
        g.fillRect(0, posicionYSuelo, ancho, 80); // rectángulo inferior que representa el suelo

        // --- Dibujar las dos esferas en sus posiciones relativas ---
        // Se colocan horizontalmente en 1/3 y 2/3 del ancho para que no se solapen
        dibujarEsfera(g, esfera1, ancho/3, posicionYSuelo, alto);
        dibujarEsfera(g, esfera2, 2*ancho/3, posicionYSuelo, alto);

        // --- Texto informativo: tiempos individuales (arriba a la izquierda) ---
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        g.setColor(Color.BLACK);
        g.drawString(" Tiempo Esfera 1: " + formatoDecimal.format(esfera1.getTiempo()) + " s", 20, 30);
        g.drawString(" Tiempo Esfera 2: " + formatoDecimal.format(esfera2.getTiempo()) + " s", 20, 50);

        // --- Datos detallados de la Esfera 1 ---
        g.setColor(esfera1.getColor()); // usamos el color asociado a la esfera para mayor claridad
        g.drawString(" Esfera 1:", 20, 80);
        g.drawString("   Altura: " + formatoDecimal.format(esfera1.getPosicionY()) + " m", 20, 100);
        // Usamos Math.abs en velocidad para presentar magnitud positiva (velocidad hacia abajo puede ser negativa)
        g.drawString("   Velocidad: " + formatoDecimal.format(Math.abs(esfera1.getVelocidad())) + " m/s", 20, 120);
        g.drawString("   Aceleración: " + formatoDecimal.format(esfera1.getAceleracionActual()) + " m/s²", 20, 140);

        // Indicador visual si la esfera ya terminó (llegó al suelo)
        if (esfera1.haTerminado()) {
            g.setColor(Color.RED);
            g.drawString("    TERMINÓ", 20, 160);
        }

        // --- Datos detallados de la Esfera 2 ---
        g.setColor(esfera2.getColor());
        g.drawString(" Esfera 2:", 20, 190);
        g.drawString("   Altura: " + formatoDecimal.format(esfera2.getPosicionY()) + " m", 20, 210);
        g.drawString("   Velocidad: " + formatoDecimal.format(Math.abs(esfera2.getVelocidad())) + " m/s", 20, 230);
        g.drawString("   Aceleración: " + formatoDecimal.format(esfera2.getAceleracionActual()) + " m/s²", 20, 250);

        if (esfera2.haTerminado()) {
            g.setColor(Color.RED);
            g.drawString("    TERMINÓ", 20, 270);
        }

        // --- Leyenda sobre si cada esfera tiene resistencia del aire activada ---
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        String resistencia1 = esfera1.isConResistenciaAire() ? "CON aire" : "SIN aire";
        String resistencia2 = esfera2.isConResistenciaAire() ? "CON aire" : "SIN aire";
        // Mostramos estas leyendas en la parte inferior derecha del panel
        g.drawString("Esfera 1: " + resistencia1, ancho - 200, alto - 60);
        g.drawString("Esfera 2: " + resistencia2, ancho - 200, alto - 40);
    }

    /*
      Metodo que dibuja una esfera (representación visual) en pantalla en función
      de la altura física (en metros) que proporciona la clase Esfera.
      centroX: coordenada X (centro horizontal) donde se dibuja la esfera
      posicionYSuelo: coordenada Y del suelo en pixels
      alto: altura total del panel en pixels
    */
    private void dibujarEsfera(Graphics2D g, Esfera esfera, int centroX, int posicionYSuelo, int alto) {
        // Altura física actual (en metros). Aseguramos que no sea negativa para evitar problemas gráficos.
        double alturaActual = Math.max(0, esfera.getPosicionY());

        // Calculamos la altura máxima inicial entre las dos esferas para escalar la posición relativa
        double alturaMaxima = Math.max(esfera1.getAlturaInicial(), esfera2.getAlturaInicial());
        if (alturaMaxima < 1) alturaMaxima = 100; // protección: si ambas alturas son muy pequeñas, usar un valor por defecto

        // Fracción (0..1) que representa la altura actual respecto a la altura máxima
        double fraccion = alturaActual / alturaMaxima;
        fraccion = Math.max(0, Math.min(1, fraccion)); // clamp entre 0 y 1

        // Tamaño de la esfera en pixels depende del radio físico (escala arbitraria)
        int tamanoPelota = 50 + (int)(esfera.getRadio() * 20);
        int xPelota = centroX - tamanoPelota/2; // x izquierdo del ovalo (para centrarlo)

        // Espacio vertical disponible entre "techo" visual y el suelo
        int espacioDisponible = posicionYSuelo - 100;
        // Convertimos fracción (0..1) a coordenada Y en pixels:
        // cuando fraccion=1 => yPelota cerca de la parte superior (posicionYSuelo - espacioDisponible)
        // cuando fraccion=0 => yPelota en el suelo (posicionYSuelo - tamanoPelota)
        int yPelota = posicionYSuelo - (int)(fraccion * espacioDisponible) - tamanoPelota;
        yPelota = Math.max(50, yPelota); // margen superior mínimo: no dibujar demasiado arriba

        // --- Creación de un gradiente para dar volumen a la esfera ---
        GradientPaint colorPelota = new GradientPaint(
                xPelota, yPelota, esfera.getColor().brighter(),
                xPelota+tamanoPelota, yPelota+tamanoPelota, esfera.getColor().darker()
        );
        g.setPaint(colorPelota);
        g.fillOval(xPelota, yPelota, tamanoPelota, tamanoPelota); // relleno de la esfera
        g.setColor(Color.BLACK);
        g.drawOval(xPelota, yPelota, tamanoPelota, tamanoPelota); // borde negro para definición

        // --- Dibujar una carita simple ---
        g.setColor(Color.BLACK);
        g.fillOval(xPelota + 15, yPelota + 18, 6, 6); // ojo izquierdo
        g.fillOval(xPelota + tamanoPelota - 21, yPelota + 18, 6, 6); // ojo derecho
        g.drawArc(xPelota + 12, yPelota + 25, tamanoPelota - 24, 12, 180, 180); // boca (arco)

        // --- Línea de referencia vertical desde el centro de la pelota hasta el suelo ---
        // Solo dibujamos si la esfera no está en el suelo (altura > 0.1 m)
        if (alturaActual > 0.1) {
            g.setColor(Color.DARK_GRAY);
            g.setStroke(new BasicStroke(1f));
            g.drawLine(xPelota + tamanoPelota/2, yPelota + tamanoPelota,
                    xPelota + tamanoPelota/2, posicionYSuelo);
        }

        // --- Etiqueta de altura ---
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        g.setColor(Color.BLACK);
        g.drawString(String.format("%.1f m", alturaActual),
                xPelota - 10, yPelota - 5);
    }

    /* Metodo que dibuja una nube simple usando tres óvalos superpuestos.
       x,y son la posición del primer óvalo. */
    private void dibujarNube(Graphics2D g, int x, int y) {
        g.fillOval(x, y, 60, 40);
        g.fillOval(x + 30, y - 20, 50, 50);
        g.fillOval(x + 60, y, 60, 40);
    }

    /* ------------ Métodos públicos para controlar la simulación ------------ */

    // Indica si la simulación está en ejecución
    public boolean estaEjecutandose() { return ejecutandose; }

    // Inicia/reproduce la simulación: arranca el temporizador y fuerza una actualización inmediata
    public void reproducir() {
        if (!ejecutandose) {
            ejecutandose = true;
            temporizador.start();
            // Forzamos una actualización inmediata para reducir la latencia visual al presionar "play"
            actualizar();
        }
    }

    // Pausa la simulación (detiene el timer pero no reinicia los estados)
    public void pausar() {
        ejecutandose = false;
        temporizador.stop();
    }

    // Reinicia la simulación: pausa, reinicia cada esfera a su estado inicial y repinta
    public void reiniciar() {
        pausar();
        esfera1.reiniciar();
        esfera2.reiniciar();
        repaint();
    }

    // Métodos para obtener la aceleración de cada esfera en un tiempo t (externo)
    public double getAceleracionEsfera1(double t) {
        return calcularAceleracionEnTiempoPara(esfera1, t);
    }

    public double getAceleracionEsfera2(double t) {
        return calcularAceleracionEnTiempoPara(esfera2, t);
    }

    /*
      Metodo auxiliar: crea una copia temporal (no invasiva) de la esfera original,
      la simula con pasos locales y devuelve la aceleración que tenga en tiempoObjetivo.

      Este enfoque evita alterar el estado actual de la simulación principal.
      Limitaciones: es un metodo simple y relativamente ineficiente (crea y actualiza un objeto).
    */
    // Metodo auxiliar mejorado: calcula aceleración usando funciones analíticas
    private double calcularAceleracionEnTiempoPara(Esfera esfera, double tiempoObjetivo) {
        if (tiempoObjetivo < 0) {
            return 0; // Tiempo negativo no válido
        }

        // Si la esfera ya llegó al suelo antes del tiempo objetivo
        if (tiempoObjetivo >= calcularTiempoCaida(esfera)) {
            return 0; // Aceleración 0 cuando está en reposo en el suelo
        }

        // Calcular aceleración usando derivadas analíticas
        return calcularAceleracionAnalitica(esfera, tiempoObjetivo);
    }

    // Calcula el tiempo total de caída hasta el suelo
    private double calcularTiempoCaida(Esfera esfera) {
        if (!esfera.isConResistenciaAire() || esfera.getDensidadAire() <= 0) {
            // Caída libre sin aire: t = √(2h/g)
            return Math.sqrt(2 * esfera.getAlturaInicial() / esfera.getGravedad());
        } else {
            // Con resistencia del aire: solución numérica iterativa
            return calcularTiempoCaidaConAire(esfera);
        }
    }

    // Calcula el tiempo de caída con resistencia del aire (metodo iterativo)
    private double calcularTiempoCaidaConAire(Esfera esfera) {
        double tiempo = 0;
        double altura = esfera.getAlturaInicial();
        double velocidad = 0;
        double dt = 0.001; // Paso de tiempo pequeño para precisión

        while (altura > 0) {
            double aceleracion = calcularAceleracionInstantanea(esfera, velocidad);
            velocidad += aceleracion * dt;
            altura -= velocidad * dt;
            tiempo += dt;

            // Prevención de bucle infinito
            if (tiempo > 1000) break;
        }

        return tiempo;
    }

    // Calcula la aceleración analítica en un tiempo específico
    private double calcularAceleracionAnalitica(Esfera esfera, double tiempo) {
        if (!esfera.isConResistenciaAire() || esfera.getDensidadAire() <= 0) {
            // Sin aire: aceleración constante = g
            return esfera.getGravedad();
        } else {
            // Con aire: a(t) = g * (1 - tanh²(g*t/Vt)) = g * sech²(g*t/Vt)
            double velocidadTerminal = calcularVelocidadTerminal(esfera);
            double argumento = esfera.getGravedad() * tiempo / velocidadTerminal;
            double tanhCuadrado = Math.tanh(argumento) * Math.tanh(argumento);
            return esfera.getGravedad() * (1 - tanhCuadrado);
        }
    }

    // Calcula la velocidad terminal de la esfera
    private double calcularVelocidadTerminal(Esfera esfera) {
        double masa = esfera.getMasa();
        double gravedad = esfera.getGravedad();
        double densidadAire = esfera.getDensidadAire();
        double coeficienteArrastre = esfera.getCoeficienteArrastre();
        double area = Math.PI * esfera.getRadio() * esfera.getRadio();

        // Vt = √(2mg / (ρ * Cd * A))
        return Math.sqrt((2 * masa * gravedad) / (densidadAire * coeficienteArrastre * area));
    }

    // Calcula aceleración instantánea para simulación numérica
    private double calcularAceleracionInstantanea(Esfera esfera, double velocidad) {
        if (!esfera.isConResistenciaAire() || esfera.getDensidadAire() <= 0) {
            return esfera.getGravedad();
        } else {
            double masa = esfera.getMasa();
            double gravedad = esfera.getGravedad();
            double densidadAire = esfera.getDensidadAire();
            double coeficienteArrastre = esfera.getCoeficienteArrastre();
            double area = Math.PI * esfera.getRadio() * esfera.getRadio();

            double fuerzaGravedad = masa * gravedad;
            double fuerzaArrastre = 0.5 * densidadAire * coeficienteArrastre * area * velocidad * velocidad;

            return (fuerzaGravedad - fuerzaArrastre) / masa;
        }
    }

    // Getters para acceder a las esferas desde fuera
    public Esfera getEsfera1() { return esfera1; }
    public Esfera getEsfera2() { return esfera2; }
}

