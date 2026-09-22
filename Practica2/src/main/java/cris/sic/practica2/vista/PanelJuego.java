package cris.sic.practica2.vista;

import cris.sic.practica2.datos.GestorDatos;
import cris.sic.practica2.modelo.Asteroide;
import cris.sic.practica2.modelo.ElementoEspacial;
import cris.sic.practica2.modelo.Enemigo;
import cris.sic.practica2.modelo.NaveJugador;
import cris.sic.practica2.modelo.Partida;
import cris.sic.practica2.modelo.Piloto;
import cris.sic.practica2.modelo.PremioQuaffle;
import cris.sic.practica2.modelo.PremioSnitch;
import cris.sic.practica2.modelo.Proyectil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Random;

/**
 * Lienzo principal del juego espacial (Side-Scroller horizontal en 2D).
 * 
 * Cumple con los requerimientos estrictos y literales del PDF de la Práctica 2:
 * 1. Dirección del Mapa y Elementos:
 *    - Side-Scroller horizontal.
 *    - Todos los enemigos, asteroides (Bludger) y premios (Quaffle, Snitch) aparecen en el borde derecho (X máx)
 *      y se desplazan horizontalmente hacia la izquierda (X mín).
 *    - Desplazamiento continuo del fondo espacial hacia la izquierda en el bucle de juego para ilusión visual de avance.
 * 
 * 2. Lógica de Dificultad según Tipo de Nave:
 *    - Fácil (Explorador): sleep() corto en movimiento, sleep(2000) en disparo.
 *    - Normal (Caza Estelar): sleep() estándar en movimiento, sleep(1000) en disparo.
 *    - Difícil (Acorazado): sleep() mayor en movimiento, sleep(300) en disparo.
 * 
 * 3. Hilos concurrentes para todos los objetos, detección de colisiones y persistencia de fin de partida.
 */
public class PanelJuego extends JPanel {

    public static final int ALTO_HUD = 54;

    private final VentanaPrincipal ventanaPrincipal;
    private final JButton btnVolver;
    private final JButton btnPausa;

    // Entidades del jugador y bucles concurrentes
    private NaveJugador naveJugador;
    private Piloto pilotoActual;
    private GameLoopThread gameLoop;
    private SpawnerThread spawnerThread;

    // Persistencia y estadísticas de la partida en curso
    private int puntajeActual;
    private int enemigosDestruidos;
    private volatile boolean partidaTerminada;

    // Vectores nativos para objetos espaciales (sin colecciones de terceros)
    private static final int MAX_PROYECTILES = 120;
    private static final int MAX_ELEMENTOS = 60;
    private final Proyectil[] proyectiles;
    private int totalProyectiles;
    private final ElementoEspacial[] elementos;
    private int totalElementos;

    // Fondo espacial con campo estelar parallax continuo
    private static final int TOTAL_ESTRELLAS = 120;
    private final Estrella[] estrellas;
    private final Random random;

    // Efecto visual de onda dorada (Snitch) y explosiones
    private int efectoFlashSnitch;
    private String mensajeTemporalHUD;
    private int contadorMensajeHUD;

    // Control de mouse
    private boolean controlPorMouseActivo = false;

    /**
     * Estructura interna para el fondo estelar dinámico con desplazamiento hacia la izquierda.
     */
    private static class Estrella {
        int x;
        int y;
        int velocidad;
        int diametro;
        Color color;

        Estrella(int x, int y, int velocidad, int diametro, Color color) {
            this.x = x;
            this.y = y;
            this.velocidad = velocidad;
            this.diametro = diametro;
            this.color = color;
        }
    }

    public PanelJuego(VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        this.random = new Random();
        this.estrellas = new Estrella[TOTAL_ESTRELLAS];
        this.proyectiles = new Proyectil[MAX_PROYECTILES];
        this.totalProyectiles = 0;
        this.elementos = new ElementoEspacial[MAX_ELEMENTOS];
        this.totalElementos = 0;
        this.puntajeActual = 0;
        this.enemigosDestruidos = 0;
        this.partidaTerminada = false;
        this.efectoFlashSnitch = 0;
        this.mensajeTemporalHUD = null;
        this.contadorMensajeHUD = 0;

        setLayout(new BorderLayout());
        setBackground(TemaEspacial.FONDO_ESPACIAL);
        setFocusable(true);

        // Inicializar campo estelar de fondo
        inicializarEstrellas(1000, 600);

        // ==========================================
        // BARRA SUPERIOR HUD
        // ==========================================
        JPanel panelSuperiorHUD = new JPanel(new BorderLayout());
        panelSuperiorHUD.setOpaque(false);
        panelSuperiorHUD.setPreferredSize(new Dimension(1000, ALTO_HUD));
        panelSuperiorHUD.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        btnPausa = new JButton("⏸️ Pausar (P)");
        TemaEspacial.aplicarEstiloBoton(btnPausa, TemaEspacial.FONDO_PANEL, TemaEspacial.AMARILLO_ORO);
        btnPausa.setPreferredSize(new Dimension(135, 36));
        btnPausa.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnPausa.setFocusable(false);
        btnPausa.addActionListener(e -> alternarPausa());
        panelBotones.add(btnPausa);

        btnVolver = new JButton("⬅️ Menú (ESC)");
        TemaEspacial.aplicarEstiloBoton(btnVolver, TemaEspacial.AZUL_OSCURO, TemaEspacial.TEXTO_BLANCO);
        btnVolver.setPreferredSize(new Dimension(135, 36));
        btnVolver.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnVolver.setFocusable(false);
        btnVolver.addActionListener(e -> volverAlMenu());
        panelBotones.add(btnVolver);

        panelSuperiorHUD.add(panelBotones, BorderLayout.EAST);
        add(panelSuperiorHUD, BorderLayout.NORTH);

        // ==========================================
        // CONTROLES DE TECLADO (Flechas, WASD, Espacio)
        // ==========================================
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                manejarTeclaPresionada(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                manejarTeclaLiberada(e.getKeyCode());
            }
        });

        // ==========================================
        // CONTROLES DE MOUSE
        // ==========================================
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                solicitarFoco();
                if (e.getButton() == MouseEvent.BUTTON1 && naveJugador != null && !estaEnPausa() && !partidaTerminada) {
                    controlPorMouseActivo = true;
                    int maxX = getWidth() > 0 ? getWidth() : 1000;
                    int maxY = getHeight() > 0 ? getHeight() : 600;
                    naveJugador.moverHacia(e.getX(), e.getY(), 0, ALTO_HUD, maxX, maxY);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    controlPorMouseActivo = false;
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (controlPorMouseActivo && naveJugador != null && !estaEnPausa() && !partidaTerminada) {
                    int maxX = getWidth() > 0 ? getWidth() : 1000;
                    int maxY = getHeight() > 0 ? getHeight() : 600;
                    naveJugador.moverHacia(e.getX(), e.getY(), 0, ALTO_HUD, maxX, maxY);
                }
            }
        });
    }

    /**
     * Inicializa las estrellas del fondo con diferentes velocidades para el efecto de desplazamiento continuo.
     */
    private void inicializarEstrellas(int ancho, int alto) {
        Color[] tonos = {
                Color.WHITE,
                new Color(0x80, 0xDE, 0xEA), // Cian suave
                new Color(0xFF, 0xF9, 0xC4), // Blanco cálido
                new Color(0xB0, 0xBE, 0xC5)  // Gris azulado tenue
        };

        for (int i = 0; i < TOTAL_ESTRELLAS; i++) {
            int x = random.nextInt(Math.max(ancho, 1000));
            int y = ALTO_HUD + random.nextInt(Math.max(alto - ALTO_HUD, 500));
            int capa = random.nextInt(3); // 0: lejana, 1: media, 2: cercana
            int velocidad = (capa == 2) ? 4 : (capa == 1 ? 2 : 1);
            int diametro = (capa == 2) ? 3 : (capa == 1 ? 2 : 1);
            Color color = tonos[random.nextInt(tonos.length)];
            estrellas[i] = new Estrella(x, y, velocidad, diametro, color);
        }
    }

    /**
     * Inicia una nueva partida configurando la nave del piloto, arrancando el GameLoop
     * y el generador concurrente de elementos espaciales en el borde derecho.
     *
     * @param piloto Piloto seleccionado
     */
    public synchronized void iniciarPartida(Piloto piloto) {
        detenerPartida();

        this.pilotoActual = piloto;
        this.puntajeActual = 0;
        this.enemigosDestruidos = 0;
        this.partidaTerminada = false;
        this.efectoFlashSnitch = 0;
        this.mensajeTemporalHUD = null;
        this.contadorMensajeHUD = 0;

        int altoPanel = getHeight() > 0 ? getHeight() : 600;
        int yInicial = ALTO_HUD + (altoPanel - ALTO_HUD) / 2 - 18;

        // Crear la nave del jugador e iniciar sus hilos independientes (movimiento y disparo con sleep)
        this.naveJugador = new NaveJugador(80, yInicial, pilotoActual);
        this.naveJugador.iniciarHilos(this);

        // Iniciar el generador de elementos espaciales (Enemigos, Asteroide, Quaffle, Snitch)
        this.spawnerThread = new SpawnerThread(this);
        this.spawnerThread.iniciarSpawner();

        // Iniciar el Game Loop a 60 FPS
        this.gameLoop = new GameLoopThread(this);
        this.gameLoop.iniciarBucle();

        solicitarFoco();
        System.out.println("[PARTIDA] Partida Side-Scroller iniciada con éxito para el piloto: " +
                (piloto != null ? piloto.getNombre() + " (" + piloto.getTipoNave() + ")" : "Desconocido"));
    }

    /**
     * Detiene de forma limpia y segura todos los hilos activos del simulador.
     */
    public synchronized void detenerPartida() {
        if (gameLoop != null) {
            gameLoop.detenerBucle();
            gameLoop = null;
        }

        if (spawnerThread != null) {
            spawnerThread.detenerSpawner();
            spawnerThread = null;
        }

        if (naveJugador != null) {
            naveJugador.detenerHilos();
        }

        // Detener e interrumpir todos los hilos de proyectiles
        for (int i = 0; i < totalProyectiles; i++) {
            if (proyectiles[i] != null) {
                proyectiles[i].destruir();
                proyectiles[i] = null;
            }
        }
        totalProyectiles = 0;

        // Detener e interrumpir todos los hilos de elementos espaciales
        for (int i = 0; i < totalElementos; i++) {
            if (elementos[i] != null) {
                elementos[i].destruir();
                elementos[i] = null;
            }
        }
        totalElementos = 0;

        if (btnPausa != null) {
            btnPausa.setText("⏸️ Pausar (P)");
        }
    }

    /**
     * Alterna la pausa del juego y de todos sus hilos concurrentes.
     */
    public synchronized void alternarPausa() {
        if (gameLoop == null || partidaTerminada) return;

        boolean pausado = gameLoop.alternarPausa();
        if (pausado) {
            btnPausa.setText("▶️ Continuar (P)");
            if (spawnerThread != null) spawnerThread.pausar();
            if (naveJugador != null) naveJugador.pausar();
            for (int i = 0; i < totalProyectiles; i++) {
                if (proyectiles[i] != null) proyectiles[i].pausar();
            }
            for (int i = 0; i < totalElementos; i++) {
                if (elementos[i] != null) elementos[i].pausar();
            }
        } else {
            btnPausa.setText("⏸️ Pausar (P)");
            if (spawnerThread != null) spawnerThread.reanudar();
            if (naveJugador != null) naveJugador.reanudar();
            for (int i = 0; i < totalProyectiles; i++) {
                if (proyectiles[i] != null) proyectiles[i].reanudar();
            }
            for (int i = 0; i < totalElementos; i++) {
                if (elementos[i] != null) elementos[i].reanudar();
            }
            solicitarFoco();
        }
        repaint();
    }

    public void volverAlMenu() {
        detenerPartida();
        ventanaPrincipal.mostrarPanel(VentanaPrincipal.PANEL_MENU);
    }

    public void solicitarFoco() {
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    // ==========================================
    // GESTIÓN DE VECTORES DE OBJETOS CONCURRENTES
    // ==========================================

    /**
     * Registra un nuevo proyectil en el vector nativo e inicia su hilo independiente.
     */
    public synchronized void agregarProyectil(Proyectil p) {
        if (p == null || partidaTerminada) return;
        if (totalProyectiles < MAX_PROYECTILES) {
            proyectiles[totalProyectiles++] = p;
            p.start();
        }
    }

    /**
     * Registra un nuevo elemento espacial en el vector nativo (lanzado por SpawnerThread).
     */
    public synchronized void agregarElementoEspacial(ElementoEspacial e) {
        if (e == null || partidaTerminada) return;
        if (totalElementos < MAX_ELEMENTOS) {
            elementos[totalElementos++] = e;
        }
    }

    /**
     * Efecto Snitch: Destruye a todos los enemigos activos en pantalla y desata una onda expansiva.
     */
    public synchronized void limpiarEnemigosEnPantalla() {
        int eliminados = 0;
        for (int i = 0; i < totalElementos; i++) {
            if (elementos[i] instanceof Enemigo && elementos[i].isActivo()) {
                elementos[i].destruir();
                eliminados++;
            }
        }
        puntajeActual += (eliminados * Enemigo.PUNTOS_RECOMPENSA);
        enemigosDestruidos += eliminados;
        efectoFlashSnitch = 12; // Fotogramas de destello dorado
        mostrarMensajeTemporal("⚡ ¡SNITCH ATRAPADA! +150 PTS & ONDA EXPANSIVA");
    }

    public void mostrarMensajeTemporal(String mensaje) {
        this.mensajeTemporalHUD = mensaje;
        this.contadorMensajeHUD = 90; // ~1.5 segundos a 60 FPS
    }

    // ==========================================
    // GESTIÓN DE TECLADO
    // ==========================================

    private void manejarTeclaPresionada(int keyCode) {
        if (keyCode == KeyEvent.VK_ESCAPE) {
            volverAlMenu();
            return;
        }

        if (keyCode == KeyEvent.VK_P) {
            alternarPausa();
            return;
        }

        if (naveJugador == null || estaEnPausa() || partidaTerminada) {
            return;
        }

        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                naveJugador.setArriba(true);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                naveJugador.setAbajo(true);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                naveJugador.setIzquierda(true);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                naveJugador.setDerecha(true);
                break;
            case KeyEvent.VK_SPACE:
                naveJugador.setDisparandoContinuo(true);
                break;
        }
    }

    private void manejarTeclaLiberada(int keyCode) {
        if (naveJugador == null) return;

        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                naveJugador.setArriba(false);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                naveJugador.setAbajo(false);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                naveJugador.setIzquierda(false);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                naveJugador.setDerecha(false);
                break;
            case KeyEvent.VK_SPACE:
                naveJugador.setDisparandoContinuo(false);
                break;
        }
    }

    // ==========================================
    // FÍSICA, DESPLAZAMIENTO DEL FONDO Y COLISIONES
    // ==========================================

    /**
     * Bucle de física invocado en cada ciclo a 60 FPS por GameLoopThread:
     * 1. Desplazamiento continuo del fondo espacial hacia la izquierda.
     * 2. Actualización de límites de la nave del jugador.
     * 3. Detección de colisiones (Proyectil-Enemigo, Jugador-Asteroide, Jugador-Premios, Jugador-Enemigo).
     * 4. Limpieza de hilos y entidades inactivas en los vectores.
     */
    public synchronized void actualizarFisica() {
        if (partidaTerminada || estaEnPausa()) return;

        int anchoPanel = getWidth() > 0 ? getWidth() : 1000;
        int altoPanel = getHeight() > 0 ? getHeight() : 600;

        // 1. DESPLAZAMIENTO CONTINUO DEL FONDO ESPACIAL HACIA LA IZQUIERDA (GameLoopThread)
        for (Estrella estrella : estrellas) {
            if (estrella != null) {
                estrella.x -= estrella.velocidad;
                if (estrella.x < 0) {
                    estrella.x = anchoPanel + random.nextInt(25);
                    estrella.y = ALTO_HUD + random.nextInt(Math.max(altoPanel - ALTO_HUD, 100));
                }
            }
        }

        // 2. Actualizar nave del jugador
        if (naveJugador != null) {
            naveJugador.actualizar(0, ALTO_HUD, anchoPanel, altoPanel);
        }

        // 3. DETECCIÓN DE COLISIONES
        verificarColisiones();

        // 4. LIMPIEZA DE OBJETOS INACTIVOS O FUERA DE PANTALLA
        limpiarObjetosInactivos();

        // 5. Decrementar contadores de efectos visuales
        if (efectoFlashSnitch > 0) efectoFlashSnitch--;
        if (contadorMensajeHUD > 0) {
            contadorMensajeHUD--;
            if (contadorMensajeHUD == 0) mensajeTemporalHUD = null;
        }
    }

    /**
     * Ejecuta las intersecciones de rectángulos (java.awt.Rectangle.intersects):
     * - Proyectil vs Enemigo: Destruir enemigo, sumar 20 pts.
     * - Proyectil vs Asteroide: Destruir proyectil (impacto con roca).
     * - Jugador vs Asteroide (Bludger): Paraliza la nave por 2 segundos.
     * - Jugador vs PremioQuaffle: Sumar 10 pts.
     * - Jugador vs PremioSnitch: Sumar 150 pts y limpiar enemigos en pantalla.
     * - Jugador vs Enemigo: Daño fatal -> Game Over.
     */
    private void verificarColisiones() {
        if (naveJugador == null || partidaTerminada) return;

        Rectangle boundsJugador = naveJugador.getBounds();

        // A. COLISIONES: PROYECTILES VS ELEMENTOS
        for (int i = 0; i < totalProyectiles; i++) {
            Proyectil p = proyectiles[i];
            if (p == null || !p.isActivo()) continue;
            Rectangle boundsProyectil = p.getBounds();

            for (int j = 0; j < totalElementos; j++) {
                ElementoEspacial elem = elementos[j];
                if (elem == null || !elem.isActivo()) continue;

                if (boundsProyectil.intersects(elem.getBounds())) {
                    if (elem instanceof Enemigo) {
                        // Impacto en nave enemiga
                        p.destruir();
                        elem.destruir();
                        puntajeActual += Enemigo.PUNTOS_RECOMPENSA;
                        enemigosDestruidos++;
                        break;
                    } else if (elem instanceof Asteroide) {
                        // El asteroide destruye el proyectil láser
                        p.destruir();
                        break;
                    }
                }
            }
        }

        // B. COLISIONES: JUGADOR VS ELEMENTOS
        for (int j = 0; j < totalElementos; j++) {
            ElementoEspacial elem = elementos[j];
            if (elem == null || !elem.isActivo()) continue;

            if (boundsJugador.intersects(elem.getBounds())) {
                if (elem instanceof Asteroide) {
                    // Colisión con Asteroide / Bludger: Paralizar nave por 2 segundos (Subfase 7.3)
                    elem.destruir();
                    naveJugador.bloquearPor(2000);
                    mostrarMensajeTemporal("⚠️ ¡IMPACTO CON BLUDGER! PROPULSORES BLOQUEADOS POR 2s");
                } else if (elem instanceof PremioQuaffle) {
                    // Colisión con Quaffle: +10 puntos
                    elem.destruir();
                    puntajeActual += PremioQuaffle.PUNTOS_RECOMPENSA;
                    mostrarMensajeTemporal("🏆 ¡CONTENEDOR QUAFFLE OBTENIDO! +10 PTS");
                } else if (elem instanceof PremioSnitch) {
                    // Colisión con Snitch: +150 puntos y barrido de enemigos
                    elem.destruir();
                    puntajeActual += PremioSnitch.PUNTOS_RECOMPENSA;
                    limpiarEnemigosEnPantalla();
                } else if (elem instanceof Enemigo) {
                    // Colisión fatal con nave enemiga: Fin de juego (Subfase 7.4)
                    elem.destruir();
                    procesarFinDePartida();
                    return;
                }
            }
        }
    }

    /**
     * Compacta los vectores nativos removiendo elementos inactivos o terminados.
     */
    private void limpiarObjetosInactivos() {
        // Limpiar proyectiles inactivos
        int idxP = 0;
        for (int i = 0; i < totalProyectiles; i++) {
            if (proyectiles[i] != null && proyectiles[i].isActivo()) {
                proyectiles[idxP++] = proyectiles[i];
            }
        }
        for (int i = idxP; i < totalProyectiles; i++) {
            proyectiles[i] = null;
        }
        totalProyectiles = idxP;

        // Limpiar elementos espaciales inactivos
        int idxE = 0;
        for (int i = 0; i < totalElementos; i++) {
            if (elementos[i] != null && elementos[i].isActivo()) {
                elementos[idxE++] = elementos[i];
            }
        }
        for (int i = idxE; i < totalElementos; i++) {
            elementos[i] = null;
        }
        totalElementos = idxE;
    }

    /**
     * Gestiona el daño fatal: detiene todos los hilos, guarda la partida en GestorDatos
     * y abre el cuadro de diálogo de Game Over.
     */
    private void procesarFinDePartida() {
        this.partidaTerminada = true;

        // Detener todos los hilos concurrentes
        detenerPartida();

        // Guardar la partida en memoria y en disco (cifrado)
        String nombrePiloto = (pilotoActual != null) ? pilotoActual.getNombre() : "Piloto";
        Partida partida = new Partida(nombrePiloto, puntajeActual, enemigosDestruidos);
        GestorDatos.getInstancia().insertarPartida(partida);

        System.out.printf("[GAME OVER] Piloto: %s | Puntos: %d | Enemigos Destruidos: %d%n",
                nombrePiloto, puntajeActual, enemigosDestruidos);

        // Desplegar diálogo modal de Game Over en el hilo de Swing
        SwingUtilities.invokeLater(() -> {
            String mensaje = String.format(
                    "💥 ¡TU NAVE HA SIDO DESTRUIDA! 💥\n\n" +
                            "👨‍🚀 Piloto: %s\n" +
                            "🚀 Nave: %s (%s)\n" +
                            "⭐ Punteo Obtenido: %d puntos\n" +
                            "🛸 Enemigos Destruidos: %d\n" +
                            "🏆 Récord Actualizado: %d puntos\n\n" +
                            "¿Deseas reiniciar la misión espacial o volver al Menú Principal?",
                    nombrePiloto,
                    (pilotoActual != null ? pilotoActual.getTipoNave() : "—"),
                    (pilotoActual != null ? pilotoActual.getNivelDificultad() : "—"),
                    puntajeActual,
                    enemigosDestruidos,
                    (pilotoActual != null ? pilotoActual.getPunteoMaximo() : puntajeActual)
            );

            Object[] opciones = {"🔄 Reintentar", "🏠 Menú Principal"};
            int seleccion = JOptionPane.showOptionDialog(
                    this,
                    mensaje,
                    "Fin de Partida - Game Over",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (seleccion == JOptionPane.YES_OPTION) {
                iniciarPartida(pilotoActual);
            } else {
                ventanaPrincipal.mostrarPanel(VentanaPrincipal.PANEL_MENU);
            }
        });
    }

    // ==========================================
    // RENDERIZADO DEL PANEL
    // ==========================================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        int ancho = getWidth();
        int alto = getHeight();

        // 1. DIBUJAR FONDO ESPACIAL PROFUNDO CON DEGRADADO
        GradientPaint fondoGradiente = new GradientPaint(
                0, 0, new Color(0x05, 0x08, 0x11),
                0, alto, new Color(0x0D, 0x11, 0x1E)
        );
        g2.setPaint(fondoGradiente);
        g2.fillRect(0, 0, ancho, alto);

        // 2. DIBUJAR ESTRELLAS (DESPLAZAMIENTO CONTINUO HACIA LA IZQUIERDA)
        for (Estrella e : estrellas) {
            if (e != null) {
                g2.setColor(e.color);
                g2.fillOval(e.x, e.y, e.diametro, e.diametro);
            }
        }

        // 3. EFECTO ONDA DORADA DE SNITCH
        if (efectoFlashSnitch > 0) {
            g2.setColor(new Color(255, 215, 0, efectoFlashSnitch * 14));
            g2.fillRect(0, ALTO_HUD, ancho, alto - ALTO_HUD);
        }

        // 4. DIBUJAR PROYECTILES LÁSER
        for (int i = 0; i < totalProyectiles; i++) {
            if (proyectiles[i] != null && proyectiles[i].isActivo()) {
                proyectiles[i].dibujar(g2);
            }
        }

        // 5. DIBUJAR ELEMENTOS ESPACIALES (Enemigos, Asteroides, Premios)
        for (int i = 0; i < totalElementos; i++) {
            if (elementos[i] != null && elementos[i].isActivo()) {
                elementos[i].dibujar(g2);
            }
        }

        // 6. DIBUJAR LA NAVE DEL JUGADOR
        if (naveJugador != null) {
            naveJugador.dibujar(g2);
        }

        // 7. DIBUJAR BARRA SUPERIOR HUD
        dibujarHUD(g2, ancho);

        // 8. DIBUJAR OVERLAY DE PAUSA
        if (estaEnPausa()) {
            dibujarOverlayPausa(g2, ancho, alto);
        }

        g2.dispose();
    }

    private void dibujarHUD(Graphics2D g2, int ancho) {
        g2.setColor(new Color(0x10, 0x15, 0x22, 230));
        g2.fillRect(0, 0, ancho, ALTO_HUD);

        g2.setColor(TemaEspacial.BORDE_CIAN);
        g2.fillRect(0, ALTO_HUD - 2, ancho, 2);

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fila 1: Piloto y Nave
        g2.setFont(TemaEspacial.FUENTE_TEXTO_BOLD);
        g2.setColor(TemaEspacial.AMARILLO_ORO);
        String pilotoTexto = "👨‍🚀 " + (pilotoActual != null ? pilotoActual.getNombre() : "—");
        g2.drawString(pilotoTexto, 15, 22);

        g2.setFont(TemaEspacial.FUENTE_TEXTO);
        g2.setColor(Color.WHITE);
        String naveTexto = "🚀 " + (pilotoActual != null ? pilotoActual.getTipoNave() : "—") +
                " (" + (pilotoActual != null ? pilotoActual.getNivelDificultad() : "—") + ")";
        g2.drawString(naveTexto, 15, 42);

        // Bloque central: Puntos y Enemigos Destruidos
        g2.setFont(new Font("SansSerif", Font.BOLD, 15));
        g2.setColor(TemaEspacial.VERDE_EXITO);
        g2.drawString("⭐ Puntos: " + puntajeActual, 230, 24);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.setColor(new Color(255, 110, 64));
        g2.drawString("🛸 Bajas: " + enemigosDestruidos, 230, 42);

        // Bloque cadencia / recarga
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(TemaEspacial.BORDE_CIAN);
        long recarga = (pilotoActual != null) ? pilotoActual.getTiempoRecargaMs() : 1000;
        String cadenciaTxt = String.format("⚡ Cadencia: %.1fs | Vel: %d px", (recarga / 1000.0), (naveJugador != null ? naveJugador.getVelocidad() : 0));
        g2.drawString(cadenciaTxt, 360, 24);

        // Guía de controles
        g2.setColor(TemaEspacial.TEXTO_SECUNDARIO);
        g2.drawString("🕹️ [WASD / Flechas]: Mover | [Espacio]: Disparo", 360, 42);

        // Mensaje temporal o alerta de bloqueo
        if (naveJugador != null && naveJugador.isBloqueado()) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.setColor(new Color(255, 61, 0));
            g2.drawString("⚠️ ¡SISTEMAS BLOQUEADOS POR BLUDGER (2s)! ⚠️", 620, 32);
        } else if (mensajeTemporalHUD != null) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.setColor(TemaEspacial.AMARILLO_ORO);
            g2.drawString(mensajeTemporalHUD, 600, 32);
        }
    }

    private void dibujarOverlayPausa(Graphics2D g2, int ancho, int alto) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, ALTO_HUD, ancho, alto - ALTO_HUD);

        g2.setFont(TemaEspacial.FUENTE_TITULO_GRANDE);
        g2.setColor(TemaEspacial.AMARILLO_ORO);
        String tituloPausa = "⏸️ SIMULADOR EN PAUSA";
        int anchoTitulo = g2.getFontMetrics().stringWidth(tituloPausa);
        g2.drawString(tituloPausa, (ancho - anchoTitulo) / 2, alto / 2 - 10);

        g2.setFont(TemaEspacial.FUENTE_SUBTITULO);
        g2.setColor(Color.WHITE);
        String subtituloPausa = "Presione 'P' o haga clic en 'Continuar' para reanudar el vuelo espacial";
        int anchoSub = g2.getFontMetrics().stringWidth(subtituloPausa);
        g2.drawString(subtituloPausa, (ancho - anchoSub) / 2, alto / 2 + 25);
    }

    public boolean estaEnPausa() {
        return gameLoop != null && gameLoop.isEnPausa();
    }

    public boolean estaEnEjecucion() {
        return gameLoop != null && gameLoop.isEnEjecucion();
    }

    // Getters para acceso y pruebas
    public NaveJugador getNaveJugador() {
        return naveJugador;
    }

    public Piloto getPilotoActual() {
        return pilotoActual;
    }

    public GameLoopThread getGameLoop() {
        return gameLoop;
    }

    public SpawnerThread getSpawnerThread() {
        return spawnerThread;
    }

    public JButton getBtnVolver() {
        return btnVolver;
    }

    public JButton getBtnPausa() {
        return btnPausa;
    }

    public int getPuntajeActual() {
        return puntajeActual;
    }

    public int getEnemigosDestruidos() {
        return enemigosDestruidos;
    }

    public boolean isPartidaTerminada() {
        return partidaTerminada;
    }

    public int getTotalProyectiles() {
        return totalProyectiles;
    }

    public int getTotalElementos() {
        return totalElementos;
    }
}
