package simulador;

import java.awt.*;           // Para usar la clase Color
import java.util.ArrayList;  // Para crear listas dinámicas
import java.util.List;       // Para manejar colecciones genéricas

// Clase que representa una esfera dentro de la simulación física
class Esfera {
    // --- Propiedades físicas básicas ---
    private double radio, densidadAire, coeficienteArrastre;
    private double alturaInicial, masa, gravedad, velocidadInicial;

    // --- Variables de estado durante la simulación ---
    private double posicionY, velocidad, tiempo;
    private boolean conResistenciaAire;
    private boolean haTerminado; // Indica si la esfera ya tocó el suelo

    // --- Listas de datos usados para generar gráficas ---
    private List<Double> datosTiempo = new ArrayList<>();
    private List<Double> datosVelocidad = new ArrayList<>();
    private List<Double> datosAltura = new ArrayList<>();

    // --- Atributos visuales y de identificación ---
    private Color color;
    private String nombre;

    // --- Constructor ---
    public Esfera(String nombre, Color color) {
        this.nombre = nombre;
        this.color = color;

        // Valores físicos predeterminados
        this.radio = 0.3;              // m
        this.densidadAire = 0.0;       // kg/m³, 0 cuando no hay resistencia
        this.coeficienteArrastre = 0.47; // Valor típico de una esfera
        this.alturaInicial = 100;      // m
        this.masa = 5;                 // kg
        this.gravedad = 9.8;           // m/s²
        this.velocidadInicial = 0;     // m/s
        this.conResistenciaAire = false;
        this.haTerminado = false;

        reiniciar(); // Inicializa los valores de simulación
    }

    // --- Metodo que actualiza la posición, velocidad y aceleración ---
    public void actualizar(double intervalo) {
        if (haTerminado) return; // Si ya llegó al suelo, no se actualiza

        tiempo += intervalo; // Incremento temporal (Δt)

        double aceleracion;

        // Cálculo de la aceleración con o sin resistencia del aire
        if (conResistenciaAire && densidadAire > 0) {
            double area = Math.PI * radio * radio; // Área frontal de la esfera
            double fuerzaArrastre = 0.5 * densidadAire * coeficienteArrastre * area *
                    velocidad * velocidad * Math.signum(velocidad);
            double fuerzaGravedad = -masa * gravedad;
            double fuerzaNeta = fuerzaGravedad - fuerzaArrastre;
            aceleracion = fuerzaNeta / masa; // Segunda ley de Newton
        } else {
            aceleracion = -gravedad; // Caída libre sin aire
        }

        // Actualización de velocidad y posición (integración simple)
        velocidad += aceleracion * intervalo;
        posicionY += velocidad * intervalo;

        // Detección del impacto con el suelo
        if (posicionY <= 0) {
            posicionY = 0;
            haTerminado = true; // Marca que la esfera llegó al suelo
        }

        // Almacena los valores para graficar
        datosTiempo.add(tiempo);
        datosVelocidad.add(Math.abs(velocidad));
        datosAltura.add(posicionY);
    }

    // --- Reinicia los valores para comenzar una nueva simulación ---
    public void reiniciar() {
        this.tiempo = 0;
        this.posicionY = alturaInicial;
        this.velocidad = velocidadInicial;
        this.haTerminado = false;
        this.datosTiempo.clear();
        this.datosVelocidad.clear();
        this.datosAltura.clear();
    }

    // --- Getters y Setters con validaciones físicas ---
    public double getRadio() { return radio; }
    public void setRadio(double radio) {
        // Limita el radio entre 0.005 m y 0.5 m
        this.radio = Math.max(0.005, Math.min(0.5, radio));
    }

    public double getAlturaInicial() { return alturaInicial; }
    public void setAlturaInicial(double alturaInicial) {
        // Evita alturas negativas o nulas
        this.alturaInicial = Math.max(0.1, alturaInicial);
        this.posicionY = this.alturaInicial;
        this.haTerminado = false;
    }

    public double getMasa() { return masa; }
    public void setMasa(double masa) {
        // Impide masas menores a 0.01 kg
        this.masa = Math.max(0.01, masa);
    }

    public double getGravedad() { return gravedad; }
    public void setGravedad(double gravedad) {
        // Impide valores de gravedad irreales
        this.gravedad = Math.max(0.1, gravedad);
    }

    public boolean isConResistenciaAire() { return conResistenciaAire; }
    public void setConResistenciaAire(boolean conResistenciaAire) {
        this.conResistenciaAire = conResistenciaAire;
        // Densidad del aire: 1.2 kg/m³ si hay resistencia, 0 si no
        this.densidadAire = conResistenciaAire ? 1.2 : 0.0;
    }

    // --- Información del estado actual ---
    public double getPosicionY() { return posicionY; }

    public double getVelocidad() {
        // Si ya tocó el suelo, devuelve la velocidad previa al impacto
        if (haTerminado && posicionY <= 0) {
            if (!datosVelocidad.isEmpty()) {
                int index = Math.max(0, datosVelocidad.size() - 2);
                return datosVelocidad.get(index);
            }
        }
        return velocidad;
    }

    public double getTiempo() { return tiempo; }
    public boolean haTerminado() { return haTerminado; }

    // --- Datos para gráficas ---
    public List<Double> getDatosTiempo() { return datosTiempo; }
    public List<Double> getDatosVelocidad() { return datosVelocidad; }
    public List<Double> getDatosAltura() { return datosAltura; }

    // --- Datos visuales ---
    public Color getColor() { return color; }
    public String getNombre() { return nombre; }

    // --- Cálculo de la aceleración actual en función del estado ---
    public double getAceleracionActual() {
        // Si la esfera ya llegó al suelo, usa la última aceleración válida
        if (haTerminado && posicionY <= 0) {
            if (!datosVelocidad.isEmpty()) {
                double ultimaVelocidad = datosVelocidad.get(datosVelocidad.size() - 1);

                if (conResistenciaAire && densidadAire > 0) {
                    double area = Math.PI * radio * radio;
                    double fuerzaArrastre = 0.5 * densidadAire * coeficienteArrastre *
                            area * ultimaVelocidad * ultimaVelocidad;
                    double fuerzaGravedad = masa * gravedad;
                    double fuerzaNeta = fuerzaGravedad - fuerzaArrastre;
                    return Math.abs(fuerzaNeta / masa);
                } else {
                    return Math.abs(gravedad);
                }
            }
            return Math.abs(gravedad); // Valor por defecto
        }

        // Si aún está cayendo, calcula la aceleración dinámica
        if (conResistenciaAire && densidadAire > 0) {
            double area = Math.PI * radio * radio;
            double fuerzaArrastre = 0.5 * densidadAire * coeficienteArrastre *
                    area * velocidad * velocidad * Math.signum(velocidad);
            double fuerzaGravedad = -masa * gravedad;
            double fuerzaNeta = fuerzaGravedad - fuerzaArrastre;
            return Math.abs(fuerzaNeta / masa);
        } else {
            return Math.abs(gravedad);
        }
    }
    public double getDensidadAire() {
        return densidadAire;
    }

    public double getCoeficienteArrastre() {
        return coeficienteArrastre;
    }
}

