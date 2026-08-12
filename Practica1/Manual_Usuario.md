# Manual de Usuario - Sistema de Estacionamiento (UI/UX Avanzada)

Este instructivo le guiará paso a paso sobre el funcionamiento y el uso del Sistema de Estacionamiento de la Práctica 1 (Versión Actualizada con UI/UX Avanzada).

---

## 1. Inicio y Configuración de Temas Visuales

Al iniciar la aplicación, esta se adaptará automáticamente al tema visual configurado mediante la variable `MODO_VISUAL` en el código fuente. Se admiten cuatro temas:

1. **Oficial:** Muestra las celdas libres como `L` y las celdas ocupadas por vehículos como `A`.
2. **Minimalista:** Estética elegante. Celdas libres como `.` y ocupadas como `X`.
3. **Matriarcal:** Celdas libres como `O` y ocupadas como `#`.
4. **Celdas Nativas:** Bloques de alta densidad en terminales modernos: libre como `░` y ocupado como `█`.

---

## 2. Menú Principal e Interfaz

La aplicación se ejecuta a pantalla limpia en la terminal de comandos:

```text
--- MENU PRINCIPAL ---
1. Ingresar vehiculo
2. Retirar vehiculo
3. Mostrar estacionamiento
4. Buscar vehiculo por placa
5. Mostrar ruta mas corta entre entrada y salida
6. Mostrar ingresos
7. Salir
Seleccione una opcion: 
```

Cada vez que selecciona una opción, la terminal se limpia de forma automática para evitar acumulaciones de texto viejo. Al terminar una tarea, el sistema le pedirá presionar `ENTER` antes de regresar al menú principal.

---

## 3. Descripción de Funciones Paso a Paso

### Opción 1: Ingresar vehículo
Registra un nuevo vehículo en el parqueo:
1. Ingrese la placa en el formato `P###LLL` (ejemplo: `P450TQR`).
2. Indique la coordenada de la **Fila** (número del $1$ al $8$).
3. Indique la coordenada de la **Columna** ingresando una letra de la `A` a la `H` (o en minúsculas `a` a `h`). Por ejemplo: escriba `E` y presione Enter.
4. El sistema mostrará la tarifa fija de `Q10.00` y le solicitará el monto entregado por el usuario.
5. El sistema procesará el pago, calculará el cambio y posicionará el auto.

### Opción 2: Retirar vehículo
Retira un vehículo en base a su placa:
1. Ingrese la placa (formato `P###LLL`).
2. Si el vehículo existe, el sistema le informará las coordenadas en donde se ubicaba (ej. `Fila: 3, Columna: D`) y lo removerá del parqueo, actualizando el tablero a su estado libre.

### Opción 3: Mostrar estacionamiento
Esta opción muestra la cuadrícula del parqueo con las cabeceras de columnas identificadas de la `A` a la `H` y las filas del `1` al `8` de forma organizada y elegante. Al final se resume el conteo de espacios libres y ocupados según el tema de símbolos activo.

### Opción 4: Buscar vehículo por placa
Le permite ubicar un vehículo específico:
1. Ingrese la placa.
2. Si está en el parqueo, el sistema le dirá las coordenadas de ubicación (ej. `Fila: 5, Columna: B`).

### Opción 5: Mostrar ruta más corta entre entrada y salida
Esta opción realiza un cálculo matemático en sentido horario y antihorario para determinar el camino más corto en el perímetro exterior. 
Al seleccionarla, el sistema trazará el recorrido de forma animada en un mapa temporal:
* Las esquinas del camino se marcarán con el carácter `+`.
* Los tramos horizontales del perímetro se marcarán con `-`.
* Los tramos verticales del perímetro se marcarán con `|`.
* La Entrada `E` y la Salida `S` permanecerán visibles sobre el camino trazado.
* Al final de la animación, el sistema le recomendará el sentido óptimo a tomar.

### Opción 6: Mostrar ingresos
Muestra un reporte resumido de la recaudación histórica:
* El total de vehículos cobrados.
* La tarifa única del sistema (`Q10.00`).
* El monto acumulativo total recaudado en Quetzales.

### Opción 7: Salir
Finaliza la ejecución del programa y cierra el sistema de consola.
