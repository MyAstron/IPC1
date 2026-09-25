# Manual de Usuario — Simulador de Vuelo Espacial 2D
## Universidad de San Carlos de Guatemala — Facultad de Ingeniería
## Escuela de Ciencias y Sistemas — Introducción a la Programación y Computación 1 (IPC1)
## Segundo Semestre 2026 — Práctica 2

Bienvenido a la guía de usuario del **Simulador de Vuelo Espacial 2D** (Práctica 2 - IPC1). En este documento encontrará instrucciones paso a paso para utilizar cada una de las funcionalidades de la aplicación, gestionar pilotos, participar en misiones espaciales y exportar reportes estadísticos.

---

## 1. Requisitos y Ejecución del Sistema

* **Requisitos:** Java Development Kit (JDK 17 o JDK 21 recomendado).
* **Resolución Optimizada:** $1000 \times 600$ píxeles (ventana fija centrada).
* **Persistencia Centralizada:** Todos los archivos de guardado y reportes se gestionan dentro de `./src/datos/`.

### Comandos de Ejecución
Abra una terminal en la carpeta raíz del proyecto y ejecute:
```bash
java -classpath "target/classes:libs/*" cris.sic.practica2.Practica2
```
O si utiliza la carpeta compilada `out/`:
```bash
java -classpath "out:libs/*" cris.sic.practica2.Practica2
```

Al iniciar la aplicación, se cargarán automáticamente los datos descifrados de `./src/datos/pilotos.txt` y `./src/datos/partidas.txt`, abriendo la pantalla del Menú Principal.

---

## 2. Navegación por el Menú Principal

Al ingresar al sistema, la ventana principal le presentará el menú interactivo con temática espacial:

* 🎮 **Jugar:** Abre el cuadro de selección de piloto e inicia una nueva partida espacial.
* 👨‍🚀 **Crear Piloto:** Abre el panel para registrar nuevos pilotos, asignar modelos de naves y eliminar registros obsoletos.
* 🏆 **Top de Puntajes:** Acceso directo al Salón de la Fama con la gráfica de barras de mejores puntuaciones.
* 📜 **Historial:** Muestra la lista detallada de todas las misiones jugadas y la gráfica de evolución temporal.
* ❌ **Salir:** Cierra la aplicación de forma segura previa confirmación en ventana modal.

---

## 3. Módulo de Pilotos (`Crear Piloto`)

Permite registrar nuevos combatientes estelares y configurar su modelo de nave:

### 3.1 Registrar un Nuevo Piloto
1. Ingrese el nombre del piloto en el campo **Nombre del Piloto** (no se admiten nombres vacíos ni repetidos).
2. Seleccione el **Tipo de Nave / Dificultad** en el menú desplegable:
   * 🚀 **Explorador (Dificultad Fácil):**
     * **Velocidad:** Muy rápida ($10\text{ px/frame}$).
     * **Cadencia de Disparo:** $2.0\text{ segundos}$ entre disparos.
     * *Recomendado para jugadores principiantes que buscan alta maniobrabilidad para esquivar obstáculos.*
   * 🚀 **Caza Estelar (Dificultad Normal):**
     * **Velocidad:** Moderada y equilibrada ($7\text{ px/frame}$).
     * **Cadencia de Disparo:** $1.0\text{ segundo}$ entre disparos.
     * *Equilibrio táctico ideal entre movilidad y poder de fuego.*
   * 🚀 **Acorazado (Dificultad Difícil):**
     * **Velocidad:** Pesada y lenta ($4\text{ px/frame}$).
     * **Cadencia de Disparo:** Ráfaga rápida cada $0.3\text{ segundos}$ ($300\text{ ms}$).
     * *Poder destructor masivo que compensa la baja maniobrabilidad.*
3. Presione el botón **💾 Guardar Piloto**.
4. El sistema confirmará el registro mediante una alerta modal y el piloto aparecerá de inmediato en la tabla lateral y en `./src/datos/pilotos.txt`.

### 3.2 Eliminar un Piloto
1. Haga clic sobre la fila del piloto que desea dar de baja en la tabla de la derecha.
2. Presione el botón **🗑️ Eliminar Piloto**.
3. Confirme la acción en el cuadro de diálogo. El piloto se removerá permanentemente de la memoria y del archivo en disco.

---

## 4. Módulo de Combate Espacial (`Jugar`)

Al presionar **🎮 Jugar**, el sistema le solicitará seleccionar el piloto participante antes de entrar al campo de batalla espacial *Side-Scroller*.

### 4.1 Controles de la Nave
* **Movimiento con Teclado:**
  * `W` o `Flecha Arriba`: Mover hacia arriba.
  * `S` o `Flecha Abajo`: Mover hacia abajo.
  * `A` o `Flecha Izquierda`: Mover hacia la izquierda.
  * `D` o `Flecha Derecha`: Mover hacia la derecha.
* **Control con Mouse:**
  * **Arrastre (Click izquierdo sostenido + mover):** Desplaza suavemente la nave siguiendo el cursor sin teletransportaciones accidentales.
* **Disparo Láser:**
  * Mantenga presionada la `Barra Espaciadora` para disparar proyectiles continuos según el tiempo de recarga de su nave.
* **Pausa y Retorno:**
  * Tecla `P` o botón **⏸️ Pausar**: Pausa la física y todos los hilos del juego.
  * Tecla `ESC` o botón **⬅️ Menú**: Aborta la partida y regresa al Menú Principal.

### 4.2 Elementos Espaciales y Puntuación

| Elemento | Aspecto Visual | Comportamiento y Efecto |
|---|---|---|
| **Nave Enemiga** | 🛸 Nave hostil roja | Se desplaza hacia la izquierda. Destruirla con proyectil otorga **+20 puntos**. Chocar contra ella provoca **Daño Fatal (Game Over)**. |
| **Asteroide (Bludger)** | 🪨 Roca espacial giratoria | Obstáculo denso. Destruye los proyectiles láser que impacten en él. Chocar contra él **bloquea los propulsores por 2 segundos**. |
| **Contenedor Quaffle** | 📦 Cápsula con orbe naranja | Recompensa espacial. Recogerlo otorga **+10 puntos**. |
| **Snitch Espacial** | 🟡 Esfera dorada con alas | Recompensa legendaria. Otorga **+150 puntos** y genera una **onda expansiva que destruye a todos los enemigos en pantalla**. |

### 4.3 Fin de Partida (Game Over)
Al colisionar contra una nave enemiga, el juego se detiene, registra la puntuación en `./src/datos/partidas.txt`, actualiza el récord del piloto si corresponde y despliega una ventana modal con las opciones **🔄 Reintentar** o **🏠 Menú Principal**.

---

## 5. Módulo de Reportes y Salón de la Fama

La pantalla de **Reportes** permite auditar y exportar el rendimiento histórico:

### 5.1 Pestañas de Consulta
1. **🏆 Top de Puntajes:** Gráfica de barras generada en tiempo real con `JFreeChart` destacando a los mejores 10 puntajes registrados.
2. **📜 Historial de Partidas:** Tabla con el registro detallado de todas las misiones y una gráfica de líneas que ilustra la curva de evolución del jugador.

### 5.2 Exportación de Reportes a HTML y PDF
1. En la parte inferior del panel de reportes, haga clic en el botón **📄 Exportar Reporte HTML / PDF**.
2. El sistema generará automáticamente en la carpeta `./src/datos/`:
   * `reporte_grafica.png`: Captura de alta resolución de la gráfica estadística.
   * `reporte_partidas.html` (y `reporte.html`): Informe web estilizado con CSS espacial, tablas completas y la gráfica incrustada.
3. El reporte se abrirá de inmediato en su navegador web predeterminado.
4. **Para guardarlo como documento PDF:**
   * Presione `Ctrl + P` (o `Cmd + P` en macOS).
   * En el destino de impresión, seleccione **"Guardar como PDF"**.
   * Haga clic en **Guardar**.

---

## 6. Mensajes de Alerta Comunes y Soluciones

| Mensaje de Alerta | Causa Común | Solución |
| :--- | :--- | :--- |
| **"El nombre del piloto no puede estar vacío..."** | Se intentó guardar un piloto sin escribir un nombre. | Ingrese un nombre o identificador válido en el formulario. |
| **"Ya existe un piloto registrado con el nombre..."** | Se ingresó un nombre idéntico a uno existente (sin importar mayúsculas). | Elija un nombre diferente para el nuevo piloto. |
| **"No hay ningún piloto registrado en el sistema."** | Se intentó jugar sin haber creado ningún piloto previamente. | Vaya al módulo **Crear Piloto** y registre al menos un piloto. |
| **"Por favor seleccione un piloto de la tabla para eliminar."** | Se presionó el botón eliminar sin seleccionar una fila en la tabla. | Haga clic sobre la fila del piloto que desea dar de baja y presione eliminar. |
