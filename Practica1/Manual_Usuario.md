# Manual de Usuario - Sistema de Estacionamiento

Este instructivo le guiará paso a paso sobre el funcionamiento y el uso del Sistema de Estacionamiento de la Práctica 1.

---

## 1. Inicio y Menú Principal

Al ejecutar la aplicación, se generará de manera aleatoria el mapa del estacionamiento con su Entrada (`E`) y Salida (`S`). En la pantalla se le desplegará el menú interactivo:

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

Para seleccionar una opción, escriba el número correspondiente (1 al 7) y presione la tecla `Enter`.

---

## 2. Descripción de Funciones

### Opción 1: Ingresar vehículo
Utilice esta opción para registrar un vehículo en el parqueo.
1. Ingrese la placa del vehículo respetando estrictamente el formato `P###LLL` (ejemplo: `P123ABC`).
2. Indique la coordenada de fila donde se estacionará (número del $1$ al $8$).
3. Indique la coordenada de columna donde se estacionará (número del $1$ al $8$).
4. El sistema le informará que la tarifa fija es de `Q10.00` y le solicitará el monto entregado por el usuario. Escriba el monto (ejemplo: `15.00`).
5. El sistema le confirmará el pago indicando el cambio a devolver y posicionará el vehículo (representado por una `'A'`) en el tablero.

### Opción 2: Retirar vehículo
Utilice esta opción cuando un cliente se retire del estacionamiento.
1. Ingrese la placa del vehículo a retirar (formato `P###LLL`).
2. Si el vehículo existe, el sistema le indicará las coordenadas que ocupaba, cambiará el estado de la celda a libre (`'L'`) y lo removerá de la base de datos.
3. Si el vehículo no se encuentra, se le mostrará el mensaje `"Vehiculo no encontrado"`.

### Opción 3: Mostrar estacionamiento
Esta opción muestra gráficamente el estado actual del estacionamiento en una matriz de $10 \times 10$:
* Los bordes perimetrales están marcados con el símbolo `=`.
* El acceso de entrada está marcado con la letra `E`.
* La salida del parqueo está marcada con la letra `S`.
* Las celdas internas libres están indicadas con `L` y las ocupadas con `A`.
* Al final de la matriz se imprime el conteo resumido de los espacios disponibles y ocupados.

### Opción 4: Buscar vehículo por placa
Le permite ubicar un vehículo específico sin tener que ver todo el mapa.
1. Escriba la placa del vehículo (patrón `P###LLL`).
2. Si está en el parqueo, el sistema le devolverá su fila y columna correspondiente.

### Opción 5: Mostrar ruta más corta entre entrada y salida
Calcula de manera matemática el camino más rápido entre el punto de Entrada (`E`) y el de Salida (`S`) del estacionamiento siguiendo el perimetral exterior:
* Le indicará la distancia en número de celdas recorridas tanto en sentido **horario** como **antihorario**.
* Le dará una recomendación explícita sobre qué sentido tomar.

### Opción 6: Mostrar ingresos
Muestra un reporte resumido de la recaudación histórica:
* El total de vehículos cobrados.
* La tarifa única del sistema (`Q10.00`).
* El monto acumulativo total recaudado en Quetzales.

### Opción 7: Salir
Finaliza la ejecución del programa y cierra el sistema de consola.

---

## 3. Ejemplo de Flujo de Operación

1. **Mostrar tablero inicial:**
   Selecciona la opción `3` para ver el diseño generado aleatoriamente.
2. **Ingresar un vehículo:**
   Selecciona la opción `1`, ingresa la placa `P302KSM`, fila `2`, columna `5`, ingresa un pago de `Q20.00` y el sistema le devuelve `Q10.00` de cambio.
3. **Verificación de registro:**
   Selecciona la opción `3` y podrá ver una letra `'A'` en la fila 2, columna 5 de la matriz.
4. **Buscar coordenadas:**
   Selecciona la opción `4` para buscar `P302KSM`, el sistema le responderá con `Fila: 2, Columna: 5`.
5. **Retiro de vehículo:**
   Selecciona la opción `2`, ingresa `P302KSM` y el espacio es liberado.
