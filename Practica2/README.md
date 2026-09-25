# Práctica 2: Simulador de Vuelo Espacial

Simulador de vuelo espacial en 2D desarrollado en Java con interfaz gráfica en Swing, bucle de juego basado en hilos (`Thread`/`Runnable`), persistencia en memoria con vectores nativos, persistencia en disco (.txt) con cifrado simétrico XOR, reportes con `JFreeChart` y exportación a HTML.

## Estructura del Proyecto

```text
Practica2/
├── pom.xml
├── README.md
├── Manual_Tecnico.md                          <-- Documentación de arquitectura, hilos y persistencia
├── Manual_Usuario.md                          <-- Guía paso a paso para el usuario
├── Diagrama/
│   ├── DiagraFlujo.excalidraw
│   ├── DiagramaFlujo_FondoBlanco.png
│   └── DiagramaFlujo_Transparente.png
└── src/
    ├── datos/                                 <-- Almacenamiento persistente centralizado (cifrado)
    │   ├── pilotos.txt
    │   ├── partidas.txt
    │   ├── reporte_grafica.png
    │   └── reporte_partidas.html
    └── main/
        └── java/
            └── cris/
                └── sic/
                    └── practica2/
                        ├── Practica2.java             <-- Punto de entrada principal (Lanza VentanaPrincipal en Swing)
                        ├── modelo/
                        │   ├── Piloto.java            <-- Modelo de datos del Piloto (cadencia y sleep de dificultad)
                        │   ├── Partida.java           <-- Modelo de datos de la Partida
                        │   ├── NaveJugador.java       <-- Nave del jugador con hilos de movimiento y disparo por dificultad
                        │   ├── Proyectil.java         <-- Hilo independiente de proyectil láser (X_min -> X_max)
                        │   ├── ElementoEspacial.java  <-- Clase base de hilos para objetos Side-Scroller (X_max -> X_min)
                        │   ├── Enemigo.java           <-- Hilo de nave enemiga (+20 pts / daño fatal)
                        │   ├── Asteroide.java         <-- Hilo de asteroide/bludger (paraliza nave por 2s)
                        │   ├── PremioQuaffle.java     <-- Hilo de contenedor Quaffle (+10 pts)
                        │   └── PremioSnitch.java      <-- Hilo de Snitch Espacial (+150 pts y onda de barrido)
                        ├── datos/
                        │   ├── GestorDatos.java       <-- Persistencia en memoria (vectores) sincronizada con disco
                        │   └── GestorArchivos.java    <-- Persistencia física java.io.* (lectura/escritura .txt)
                        ├── seguridad/
                        │   └── Encriptador.java       <-- Servicio de seguridad (Cifrado simétrico reversible XOR)
                        └── vista/
                            ├── TemaEspacial.java      <-- Paleta de colores, fuentes y estilos
                            ├── VentanaPrincipal.java  <-- JFrame principal (1000x600 px, CardLayout)
                            ├── PanelMenuPrincipal.java<-- Menú interactivo (Jugar, Piloto, Top, Historial, Salir)
                            ├── PanelCrearPiloto.java  <-- Vista para registro de pilotos (con tabla en vivo)
                            ├── PanelJuego.java        <-- Lienzo gráfico del simulador espacial Side-Scroller
                            ├── GameLoopThread.java    <-- Hilo principal de renderizado (~60 FPS) y avance de fondo
                            ├── SpawnerThread.java     <-- Hilo generador continuo de elementos en borde derecho
                            └── PanelReportes.java     <-- Vista de reportes (Top de Puntajes e Historial)
```

## Mecánicas del Simulador (Side-Scroller y Dificultad por Hilos)

### 1. Dirección del Mapa y Elementos Espaciales
* **Side-Scroller horizontal:** El desplazamiento continuo del fondo espacial se ejecuta en `GameLoopThread` hacia la izquierda (`x -= vel`) simulando el avance hacia el espacio profundo.
* **Aparición y trayectoria:** Todos los elementos (`Enemigo`, `Asteroide`, `PremioQuaffle`, `PremioSnitch`) se generan en el borde derecho ($X_{\text{máx}}$) e inician su propio hilo desplazándose horizontalmente hacia la izquierda ($X_{\text{mín}}$).
* **Proyectiles láser:** Se generan en la proa del jugador y se desplazan de izquierda a derecha ($X_{\text{mín}} \to X_{\text{máx}}$) en hilos independientes a 60 FPS.

### 2. Lógica de Dificultad en Hilos según el Tipo de Nave
* **Fácil (Explorador):**
  * Hilo de movimiento del jugador: `sleep(12)` ms (movimiento fluido y veloz).
  * Hilo de disparo del jugador: `sleep(2000)` ms (cadencia de 2.0 segundos).
* **Normal (Caza Estelar):**
  * Hilo de movimiento del jugador: `sleep(25)` ms (movimiento estándar).
  * Hilo de disparo del jugador: `sleep(1000)` ms (cadencia de 1.0 segundo).
* **Difícil (Acorazado):**
  * Hilo de movimiento del jugador: `sleep(50)` ms (nave pesada/lenta para esquivar).
  * Hilo de disparo del jugador: `sleep(300)` ms (ráfaga rápida cada 0.3 segundos).

### 3. Obstáculos y Premios
* **Asteroide (Bludger):** Paraliza y bloquea el control de la nave por 2 segundos ante colisión.
* **Contenedor Quaffle:** Otorga **+10 puntos**.
* **Snitch Espacial:** Otorga **+150 puntos** y desata una onda de choque dorada que elimina a todos los enemigos en pantalla.

## Compilación y Ejecución Manual

### 1. Compilación
```bash
mkdir -p target/classes
javac -d target/classes $(find src/main/java -name "*.java")
```

### 2. Ejecución de la Aplicación
```bash
java -cp target/classes cris.sic.practica2.Practica2
```
