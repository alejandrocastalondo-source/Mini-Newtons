
package simulador;


import javax.swing.*; // Componentes de Swing (Timer, JPanel, etc.)
import java.awt.*; // Componentes AWT (Graphics, Color, Font, etc.)
import java.awt.event.ActionEvent; // Eventos de acción
import java.awt.event.ActionListener; // Escuchador de eventos de acción
import java.awt.event.KeyAdapter; // Adaptador para eventos de teclado
import java.awt.event.KeyEvent; // Eventos de teclado
import java.awt.geom.Path2D; // Para crear formas geométricas complejas
import java.util.ArrayList; // Lista dinámica para almacenar objetos
import java.util.Iterator; // Para iterar sobre colecciones
import java.util.Random; // Para generar números aleatorios

// Clase principal del minijuego "EcoCaida" - implementa ActionListener para el timer
class PanelEcoCaida extends JPanel implements ActionListener {

    // =============================================
    // CONSTANTES DEL JUEGO
    // =============================================

    private final Timer timer; // Timer para controlar la actualización del juego
    private final int FPS = 60; // Fotogramas por segundo (velocidad de actualización)
    private final int juegoDuracionSeg = 60; // Duración total del juego en segundos

    // =============================================
    // VARIABLES DEL JUGADOR
    // =============================================

    private int px, py; // Posición X e Y del jugador
    private final int playerSize = 36; // Tamaño del jugador
    private int playerVX = 0; // Velocidad horizontal del jugador
    private int playerVY = 0; // Velocidad vertical del jugador
    private final int playerSpeed = 6; // Velocidad base del jugador

    // =============================================
    // LISTAS DE OBJETOS DEL JUEGO
    // =============================================

    private final ArrayList<Item> items = new ArrayList<>(); // Lista de ítems que caen
    private final ArrayList<Particle> particles = new ArrayList<>(); // Lista de partículas de efectos
    private final Random rnd = new Random(); // Generador de números aleatorios

    // =============================================
    // VARIABLES DE CONTROL DEL JUEGO
    // =============================================

    private int tick = 0; // Contador de ticks para spawn de ítems
    private int spawnInterval = 40; // Intervalo entre spawns de ítems

    private int score = 0; // Puntuación del jugador
    private int timeLeft = juegoDuracionSeg; // Tiempo restante en segundos
    private long lastSecondMillis = System.currentTimeMillis(); // Último tiempo registrado

    private boolean running = true; // Indica si el juego está en ejecución
    private boolean gameOver = false; // Indica si el juego ha terminado

    // =============================================
    // NUEVAS CARACTERÍSTICAS DEL JUEGO
    // =============================================

    private int vidas = 3; // Número de vidas del jugador
    private int combo = 0; // Contador de combo actual
    private int maxCombo = 0; // Combo máximo alcanzado
    private float comboMultiplier = 1.0f; // Multiplicador de puntos por combo
    private int powerUpDuration = 0; // Duración restante del power-up activo
    private PowerUpType activePowerUp = null; // Tipo de power-up activo

    // =============================================
    // CONSTRUCTOR
    // =============================================

    public PanelEcoCaida() {
        // Configuración del panel
        setBackground(new Color(180, 230, 180)); // Fondo verde claro
        setFocusable(true); // Permite que el panel reciba eventos de teclado
        setPreferredSize(new Dimension(900, 700)); // Tamaño preferido
        setDoubleBuffered(true); // Habilita doble buffer para evitar parpadeo
        addKeyListener(new KeyHandler()); // Agrega el escuchador de teclado

        // Posición inicial del jugador (centrado horizontalmente, cerca de la parte superior)
        px = getPreferredSize().width / 2 - playerSize / 2;
        py = 80;

        // Inicializa el timer que actualiza el juego 60 veces por segundo
        timer = new Timer(1000 / FPS, this);
        timer.start(); // Inicia el timer
    }

    // =============================================
    // METODO PRINCIPAL DE ACTUALIZACIÓN
    // =============================================

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!running) return; // Si el juego no está corriendo, no hace nada

        // Control del tiempo del juego
        long now = System.currentTimeMillis();
        if (now - lastSecondMillis >= 1000) { // Si pasó 1 segundo
            timeLeft = Math.max(0, timeLeft - 1); // Decrementa el tiempo
            lastSecondMillis = now; // Actualiza el último tiempo
            if (timeLeft == 0) { // Si se acabó el tiempo
                endGame(); // Termina el juego
            }
        }

        // Actualizar power-up
        if (powerUpDuration > 0) {
            powerUpDuration--; // Decrementa la duración
            if (powerUpDuration == 0) { // Si se acabó la duración
                activePowerUp = null; // Desactiva el power-up
            }
        }

        // Actualizar posición del jugador
        px += playerVX;
        py += playerVY;

        // Limites de la pantalla (evita que el jugador salga de los bordes)
        px = Math.max(0, Math.min(px, getWidth() - playerSize));
        py = Math.max(0, Math.min(py, getHeight() - playerSize - 80));

        // Actualizar items (hacerlos caer)
        Iterator<Item> it = items.iterator();
        while (it.hasNext()) {
            Item itObj = it.next();
            itObj.y += itObj.vy; // Mueve el ítem hacia abajo
            if (itObj.y > getHeight()) { // Si el ítem sale por la parte inferior
                it.remove(); // Elimina el ítem
                // Perder combo si dejas pasar un item bueno
                if (itObj.type == ItemType.BUENO && combo > 0) {
                    combo = 0; // Resetea el combo
                    comboMultiplier = 1.0f; // Resetea el multiplicador
                }
            }
        }

        // Actualizar partículas
        Iterator<Particle> pit = particles.iterator();
        while (pit.hasNext()) {
            Particle p = pit.next();
            p.update(); // Actualiza la posición de la partícula
            if (p.isDead()) pit.remove(); // Elimina partículas muertas
        }

        // Spawn de nuevos ítems
        tick++;
        if (tick >= spawnInterval) {
            spawnItem(); // Crea un nuevo ítem
            tick = 0; // Reinicia el contador
            // Disminuye gradualmente el intervalo de spawn para aumentar la dificultad
            if (spawnInterval > 10 && rnd.nextInt(10) == 0) spawnInterval--;
        }

        // Detección de colisiones
        Rectangle playerRect = new Rectangle(px, py, playerSize, playerSize);
        it = items.iterator();
        while (it.hasNext()) {
            Item itObj = it.next();
            Rectangle r = new Rectangle(itObj.x, itObj.y, itObj.size, itObj.size);
            if (playerRect.intersects(r)) { // Si hay colisión
                handleItemCollision(itObj); // Maneja la colisión
                it.remove(); // Elimina el ítem
            }
        }

        repaint(); // Vuelve a dibujar el juego
    }

    // =============================================
    // MANEJO DE COLISIONES CON ÍTEMS
    // =============================================

    private void handleItemCollision(Item item) {
        switch (item.type) {
            case BUENO: // Ítem bueno (planta)
                combo++; // Incrementa el combo
                if (combo > maxCombo) maxCombo = combo; // Actualiza combo máximo

                // Calcular multiplicador de combo
                if (combo >= 10) comboMultiplier = 3.0f; // Combo alto = x3
                else if (combo >= 5) comboMultiplier = 2.0f; // Combo medio = x2
                else comboMultiplier = 1.0f; // Combo bajo = x1

                int points = (int)(10 * comboMultiplier); // Calcula puntos
                score += points; // Suma puntos

                // Partículas verdes de efecto
                createParticles(item.x + item.size/2, item.y + item.size/2, new Color(34, 139, 34), 8);
                break;

            case MALO: // Ítem malo (basura)
                if (activePowerUp != PowerUpType.ESCUDO) { // Si no tiene escudo
                    vidas--; // Pierde una vida
                    if (vidas <= 0) { // Si se quedó sin vidas
                        endGame(); // Termina el juego
                    }
                }
                combo = 0; // Resetea el combo
                comboMultiplier = 1.0f; // Resetea el multiplicador
                score = Math.max(0, score - 5); // Resta puntos (mínimo 0)

                // Partículas rojas de efecto
                createParticles(item.x + item.size/2, item.y + item.size/2, new Color(200, 50, 50), 10);
                break;

            case POWER_UP: // Power-up
                activePowerUp = item.powerUpType; // Activa el power-up
                powerUpDuration = FPS * 8; // 8 segundos de duración

                // Efectos específicos de cada power-up
                if (item.powerUpType == PowerUpType.VELOCIDAD) {
                    // Se maneja en el movimiento del jugador
                } else if (item.powerUpType == PowerUpType.PUNTOS_DOBLES) {
                    score += 20; // Bonus de puntos
                } else if (item.powerUpType == PowerUpType.ESCUDO) {
                    // Se maneja en la colisión con basura
                }

                // Partículas doradas de efecto
                createParticles(item.x + item.size/2, item.y + item.size/2, new Color(255, 215, 0), 15);
                break;
        }
    }

    // =============================================
    // CREACIÓN DE PARTÍCULAS DE EFECTO
    // =============================================

    private void createParticles(int x, int y, Color color, int count) {
        for (int i = 0; i < count; i++) {
            // Ángulo y velocidad aleatorios
            double angle = rnd.nextDouble() * Math.PI * 2;
            double speed = 2 + rnd.nextDouble() * 3;
            // Crea nueva partícula
            particles.add(new Particle(x, y,
                    Math.cos(angle) * speed, // Velocidad X
                    Math.sin(angle) * speed, // Velocidad Y
                    color));
        }
    }

    // =============================================
    // GENERACIÓN DE NUEVOS ÍTEMS
    // =============================================

    private void spawnItem() {
        int w = getWidth() > 0 ? getWidth() : 900; // Ancho del panel
        int x = rnd.nextInt(Math.max(1, w - 40)) + 10; // Posición X aleatoria

        // 10% chance de power-up
        if (rnd.nextDouble() < 0.1) {
            PowerUpType[] types = PowerUpType.values();
            PowerUpType powerType = types[rnd.nextInt(types.length)]; // Tipo aleatorio
            items.add(new Item(x, -40, 32, 3, ItemType.POWER_UP, powerType, null));
            return;
        }

        // 65% chance de ítem bueno, 35% chance de ítem malo
        ItemType t = rnd.nextDouble() < 0.65 ? ItemType.BUENO : ItemType.MALO;
        int vy = 2 + rnd.nextInt(4); // Velocidad vertical aleatoria
        int size = t == ItemType.BUENO ? 28 + rnd.nextInt(12) : 30 + rnd.nextInt(10); // Tamaño aleatorio

        TrashType trashType = null;
        if (t == ItemType.MALO) {
            TrashType[] types = TrashType.values();
            trashType = types[rnd.nextInt(types.length)]; // Tipo de basura aleatorio
        }

        // Crea el nuevo ítem
        items.add(new Item(x, -size, size, vy, t, null, trashType));
    }

    // =============================================
    // FINALIZACIÓN DEL JUEGO
    // =============================================

    private void endGame() {
        running = false; // Detiene la actualización del juego
        gameOver = true; // Marca el juego como terminado
    }

    // =============================================
    // METODO DE PINTADO PRINCIPAL
    // =============================================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Fondo degradado (verde claro a verde oscuro)
        GradientPaint gp = new GradientPaint(0, 0, new Color(160, 220, 160), 0, getHeight(), new Color(100, 180, 120));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Suelo (área inferior)
        int sueloY = getHeight() - 80;
        g2.setColor(new Color(80, 120, 60));
        g2.fillRect(0, sueloY, getWidth(), 80);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujar partículas
        for (Particle p : particles) {
            p.draw(g2);
        }

        // Dibujar jugador con efecto de power-up (escudo)
        if (activePowerUp == PowerUpType.ESCUDO) {
            g2.setColor(new Color(100, 200, 255, 100)); // Azul semitransparente
            g2.fillOval(px - 5, py - 5, playerSize + 10, playerSize + 10); // Aura del escudo
        }

        // Dibujar jugador principal
        g2.setColor(new Color(70, 130, 180)); // Azul
        g2.fillOval(px, py, playerSize, playerSize); // Cuerpo del jugador
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(px, py, playerSize, playerSize); // Borde del jugador

        // Dibujar ítems
        for (Item it : items) {
            if (it.type == ItemType.BUENO) {
                drawPlanta(g2, it.x, it.y, it.size); // Dibuja planta
            } else if (it.type == ItemType.MALO) {
                drawBasura(g2, it.x, it.y, it.size, it.trashType); // Dibuja basura
            } else if (it.type == ItemType.POWER_UP) {
                drawPowerUp(g2, it.x, it.y, it.size, it.powerUpType); // Dibuja power-up
            }
        }

        // Dibujar HUD (interfaz de usuario)
        drawHUD(g2);

        // Pantalla de fin del juego
        if (gameOver) {
            // Fondo semitransparente oscuro
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Texto de game over
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
            drawCenteredString(g2, "¡Juego terminado!", getWidth(), getHeight() / 2 - 80);

            g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 28));
            drawCenteredString(g2, "Puntuación final: " + score, getWidth(), getHeight() / 2 - 30);

            g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
            drawCenteredString(g2, "Combo máximo: x" + maxCombo, getWidth(), getHeight() / 2 + 10);

            g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
            drawCenteredString(g2, "Cada acción cuenta para cuidar nuestro planeta", getWidth(), getHeight() / 2 + 50);

            // Botón para volver al menú
            JButton btnVolver = new JButton("Volver al menú");
            btnVolver.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
            btnVolver.setBackground(new Color(46, 134, 193));
            btnVolver.setForeground(Color.WHITE);
            btnVolver.setFocusPainted(false);
            btnVolver.setBounds(getWidth() / 2 - 120, getHeight() / 2 + 90, 240, 50);
            btnVolver.addActionListener(e -> volverAlMenu());
            setLayout(null); // Layout absoluto para posicionar el botón
            add(btnVolver);
            repaint();
        }
    }

    // =============================================
    // DIBUJADO DEL HUD (INTERFAZ DE USUARIO)
    // =============================================

    private void drawHUD(Graphics2D g2) {
        // Puntos
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 22));
        g2.drawString("Puntos: " + score, 20, 35);

        // Tiempo
        g2.drawString("Tiempo: " + timeLeft + "s", getWidth() - 160, 35);

        // Vidas
        g2.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        g2.drawString("Vidas:", 20, 65);
        for (int i = 0; i < vidas; i++) {
            g2.setColor(new Color(220, 50, 50)); // Rojo para las vidas
            g2.fillOval(90 + i * 30, 50, 20, 20); // Corazones
            g2.setColor(Color.WHITE);
            g2.drawOval(90 + i * 30, 50, 20, 20); // Bordes
        }

        // Combo (solo se muestra si hay combo activo)
        if (combo > 1) {
            g2.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
            String comboText = "COMBO x" + combo;
            if (comboMultiplier > 1.0f) {
                comboText += " (" + (int)(comboMultiplier * 100) + "%)"; // Porcentaje de bonus
            }
            g2.setColor(new Color(255, 215, 0)); // Dorado
            g2.drawString(comboText, getWidth() / 2 - 80, 35);

            // Barra de combo visual
            int barWidth = Math.min(combo * 20, 200); // Ancho proporcional al combo
            g2.setColor(new Color(255, 215, 0, 150)); // Dorado semitransparente
            g2.fillRect(getWidth() / 2 - 100, 45, barWidth, 8); // Barra de progreso
            g2.setColor(Color.WHITE);
            g2.drawRect(getWidth() / 2 - 100, 45, 200, 8); // Marco de la barra
        }

        // Power-up activo
        if (activePowerUp != null && powerUpDuration > 0) {
            g2.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
            g2.setColor(new Color(255, 215, 0)); // Dorado
            String powerUpText = "";
            switch (activePowerUp) {
                case VELOCIDAD: powerUpText = "⚡ VELOCIDAD"; break;
                case PUNTOS_DOBLES: powerUpText = "★ PUNTOS x2"; break;
                case ESCUDO: powerUpText = "🛡 ESCUDO"; break;
            }
            // Muestra el power-up y el tiempo restante
            g2.drawString(powerUpText + " (" + (powerUpDuration / FPS) + "s)", 20, 95);
        }
    }

    // =============================================
    // MÉTODOS DE DIBUJADO DE OBJETOS
    // =============================================

    private void drawPlanta(Graphics2D g2, int x, int y, int size) {
        // Hojas (múltiples círculos superpuestos)
        g2.setColor(new Color(34, 139, 34)); // Verde
        g2.fillOval(x, y, size, size / 2);
        g2.fillOval(x + size / 4, y - size / 3, size / 2, size / 2);
        g2.fillOval(x + size / 3, y + size / 6, size / 3, size / 2);

        // Tallo
        g2.setColor(new Color(139, 69, 19)); // Marrón
        g2.fillRect(x + size / 2 - 2, y + size / 2, 4, size / 3);
    }

    private void drawBasura(Graphics2D g2, int x, int y, int size, TrashType type) {
        switch (type) {
            case BOTELLA:
                drawBotella(g2, x, y, size);
                break;
            case LATA:
                drawLata(g2, x, y, size);
                break;
            case BOLSA:
                drawBolsa(g2, x, y, size);
                break;
        }
    }

    private void drawBotella(Graphics2D g2, int x, int y, int size) {
        // Cuerpo de la botella
        g2.setColor(new Color(100, 150, 200, 180)); // Azul semitransparente
        int bodyWidth = size * 2 / 3;
        int bodyHeight = size;
        g2.fillRoundRect(x + size/6, y + size/4, bodyWidth, bodyHeight, 8, 8);

        // Cuello
        g2.fillRect(x + size/3, y, size/3, size/3);

        // Tapa
        g2.setColor(new Color(200, 50, 50)); // Rojo
        g2.fillRoundRect(x + size/3 - 2, y - 6, size/3 + 4, 8, 4, 4);

        // Brillo (efecto de luz)
        g2.setColor(new Color(255, 255, 255, 120)); // Blanco semitransparente
        g2.fillOval(x + size/3, y + size/2, size/4, size/3);

        // Contorno
        g2.setColor(new Color(60, 100, 140)); // Azul oscuro
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x + size/6, y + size/4, bodyWidth, bodyHeight, 8, 8);
    }

    private void drawLata(Graphics2D g2, int x, int y, int size) {
        // Cuerpo de la lata
        g2.setColor(new Color(192, 192, 192)); // Gris plateado
        g2.fillRoundRect(x, y, size, size, 6, 6);

        // Parte superior
        g2.setColor(new Color(160, 160, 160)); // Gris más oscuro
        g2.fillRoundRect(x, y, size, size/5, 6, 6);

        // Anilla
        g2.setColor(new Color(220, 220, 220)); // Gris claro
        g2.fillOval(x + size/3, y - 3, size/3, 8);

        // Etiqueta
        g2.setColor(new Color(200, 80, 80)); // Rojo
        g2.fillRoundRect(x + 4, y + size/3, size - 8, size/3, 4, 4);

        // Líneas decorativas
        g2.setColor(new Color(220, 220, 220));
        g2.drawLine(x + 8, y + size/2, x + size - 8, y + size/2);

        // Contorno
        g2.setColor(new Color(100, 100, 100));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, size, size, 6, 6);
    }

    private void drawBolsa(Graphics2D g2, int x, int y, int size) {
        // Crear forma de bolsa arrugada usando Path2D
        Path2D bolsa = new Path2D.Float();
        bolsa.moveTo(x + size/4, y);
        bolsa.curveTo(x, y + size/3, x, y + 2*size/3, x + size/4, y + size);
        bolsa.lineTo(x + 3*size/4, y + size);
        bolsa.curveTo(x + size, y + 2*size/3, x + size, y + size/3, x + 3*size/4, y);
        bolsa.closePath();

        // Relleno semi-transparente
        g2.setColor(new Color(240, 240, 240, 200));
        g2.fill(bolsa);

        // Arrugas (líneas horizontales)
        g2.setColor(new Color(200, 200, 200));
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i < 3; i++) {
            int yPos = y + size/4 + i * size/4;
            g2.drawLine(x + size/4, yPos, x + 3*size/4, yPos);
        }

        // Contorno
        g2.setColor(new Color(150, 150, 150));
        g2.setStroke(new BasicStroke(2));
        g2.draw(bolsa);

        // Asas
        g2.drawArc(x + size/4, y - 8, size/4, 12, 0, 180);
        g2.drawArc(x + size/2, y - 8, size/4, 12, 0, 180);
    }

    private void drawPowerUp(Graphics2D g2, int x, int y, int size, PowerUpType type) {
        // Centro del power-up
        int centerX = x + size/2;
        int centerY = y + size/2;

        // Resplandor (aura dorada)
        g2.setColor(new Color(255, 215, 0, 80));
        g2.fillOval(x - 5, y - 5, size + 10, size + 10);

        // Estrella (forma geométrica compleja)
        Path2D star = new Path2D.Float();
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI * 2 * i / 10 - Math.PI / 2; // Ángulo para cada punto
            double radius = (i % 2 == 0) ? size/2 : size/4; // Radio alternado para puntas
            double px = centerX + Math.cos(angle) * radius;
            double py = centerY + Math.sin(angle) * radius;
            if (i == 0) star.moveTo(px, py);
            else star.lineTo(px, py);
        }
        star.closePath();

        // Relleno y borde de la estrella
        g2.setColor(new Color(255, 215, 0)); // Dorado
        g2.fill(star);
        g2.setColor(new Color(255, 255, 0)); // Amarillo
        g2.setStroke(new BasicStroke(2));
        g2.draw(star);

        // Símbolo del power-up (centrado)
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, size/2));
        String symbol = "";
        switch (type) {
            case VELOCIDAD: symbol = "⚡"; break;
            case PUNTOS_DOBLES: symbol = "★"; break;
            case ESCUDO: symbol = "⬡"; break;
        }
        FontMetrics fm = g2.getFontMetrics();
        int sx = centerX - fm.stringWidth(symbol)/2;
        int sy = centerY + fm.getAscent()/2 - 2;
        g2.drawString(symbol, sx, sy);
    }

    // =============================================
    // MÉTODOS AUXILIARES
    // =============================================

    private void drawCenteredString(Graphics2D g2, String text, int width, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2; // Calcula X para centrar
        g2.drawString(text, x, y);
    }

    private void volverAlMenu() {
        // Vuelve al menú principal de forma segura en el hilo de EDT
        SwingUtilities.invokeLater(() -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose(); // Cierra la ventana actual
            new MenuPrincipal(); // Abre el menú principal
        });
    }

    // =============================================
    // ENUMERACIONES PARA TIPOS DE OBJETOS
    // =============================================

    private enum ItemType { BUENO, MALO, POWER_UP } // Tipos de ítems

    private enum TrashType { BOTELLA, LATA, BOLSA } // Tipos de basura

    private enum PowerUpType { VELOCIDAD, PUNTOS_DOBLES, ESCUDO } // Tipos de power-up

    // =============================================
    // CLASE INTERNA PARA ÍTEMS
    // =============================================

    private static class Item {
        int x, y, size, vy; // Posición, tamaño y velocidad vertical
        ItemType type; // Tipo de ítem
        PowerUpType powerUpType; // Tipo de power-up (si aplica)
        TrashType trashType; // Tipo de basura (si aplica)

        public Item(int x, int y, int size, int vy, ItemType type, PowerUpType powerUpType, TrashType trashType) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.vy = vy;
            this.type = type;
            this.powerUpType = powerUpType;
            this.trashType = trashType;
        }
    }

    // =============================================
    // CLASE INTERNA PARA PARTÍCULAS
    // =============================================

    private static class Particle {
        double x, y, vx, vy; // Posición y velocidad
        Color color; // Color de la partícula
        int life; // Tiempo de vida restante

        public Particle(double x, double y, double vx, double vy, Color color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
            this.life = 30 + (int)(Math.random() * 20); // Vida aleatoria
        }

        public void update() {
            x += vx; // Actualiza posición X
            y += vy; // Actualiza posición Y
            vy += 0.2; // Gravedad (acelera hacia abajo)
            life--; // Decrementa vida
        }

        public void draw(Graphics2D g2) {
            // La partícula se desvanece según su vida
            int alpha = (int)(255 * (life / 50.0));
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, alpha)));
            g2.fillOval((int)x, (int)y, 4, 4); // Partícula pequeña
        }

        public boolean isDead() {
            return life <= 0; // Partícula muerta cuando vida llega a 0
        }
    }

    // =============================================
    // MANEJADOR DE TECLADO
    // =============================================

    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (!running) return; // No responde si el juego no está corriendo
            int key = e.getKeyCode();
            // Velocidad aumentada si tiene power-up de velocidad
            int speed = (activePowerUp == PowerUpType.VELOCIDAD) ? playerSpeed * 2 : playerSpeed;
            if (key == KeyEvent.VK_LEFT) playerVX = -speed; // Izquierda
            if (key == KeyEvent.VK_RIGHT) playerVX = speed; // Derecha
            if (key == KeyEvent.VK_UP) playerVY = -speed; // Arriba
            if (key == KeyEvent.VK_DOWN) playerVY = speed; // Abajo
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            // Detiene el movimiento cuando se sueltan las teclas
            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) playerVX = 0;
            if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) playerVY = 0;
        }
    }
}
