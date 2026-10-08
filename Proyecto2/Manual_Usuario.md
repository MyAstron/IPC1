# 📕 Manual de Usuario - Sistema ResiPark
**Guía Operativa y Casos de Uso**  
*Sistema de Control y Parqueo Residencial con Concurrencia Multihilo*

---

## 1. Introducción y Vista General
Bienvenido a **ResiPark**, la plataforma inteligente de control de accesos, gestión de residentes y administración en vivo de espacios de parqueo residencial.

### Pantalla Principal (Dashboard)
Al iniciar la aplicación, se presenta la ventana principal organizada en tres áreas clave:
1. **Barra Superior de Estado:** Muestra en tiempo real la ocupación del Área de Socios (máx. 75), Área General (máx. 75) y el Total Global (máx. 150).
2. **Barra Lateral de Navegación:** Permite alternar instantáneamente entre los 5 paneles del sistema y guardar datos en disco manualmente.
3. **Área de Trabajo Central:** Renderiza la vista del panel seleccionado con interacción reactiva.

---

## 2. Guía Detallada por Paneles

### 👥 2.1 Módulo de Gestión de Residentes (`PanelResidentes`)
Permite administrar el censo de propietarios y sus vehículos autorizados.

```
+-------------------------------------------------------------------------------+
| FORMULARIO RESIDENTE          | VEHÍCULOS DEL RESIDENTE                       |
| ID: [ R-101   ] Nombre: [...] | Placa: [ P123ABC ] Marca: [ Toyota ]          |
| Casa: [ C-14  ] Socio: [x]    | Modelo: [ Yaris ]  Color: [ Gris ]            |
| [ Guardar ] [ Limpiar ]       | Tipo: [ Automóvil v ]  [ + Agregar Vehículo ] |
+-------------------------------+-----------------------------------------------+
| TABLA GENERAL DE RESIDENTES Y ESTADOS                                         |
+-------------------------------------------------------------------------------+
```

#### Acciones Principales:
- **Registrar Residente:** Ingrese ID único (ej: `R-101`), Nombre Completo, Dirección de Casa y marque si es Socio del Club. Presione **Guardar / Actualizar**.
- **Agregar Vehículo:** Seleccione un residente de la tabla inferior, complete los datos del vehículo (Placa, Marca, Modelo, Color, Tipo) y presione **+ Agregar Vehículo** (máximo 3 vehículos por residente).
- **Eliminar Residente o Vehículo:** Seleccione la fila correspondiente y presione el botón de eliminar.
  > [!WARNING]
  > Por seguridad e integridad física, el sistema **no permitirá** eliminar un residente ni desvincular un vehículo si este se encuentra en cola o estacionado dentro del residencial.

---

### 🅿️ 2.2 Módulo de Mapa en Vivo del Parqueo (`PanelParqueo`)
Ofrece una representación gráfica matricial de los 150 espacios divididos en:
- **Área de Socios:** Filas A, B y C (25 espacios cada una = 75 espacios).
- **Área General:** Filas E, F, G, H e I (15 espacios cada una = 75 espacios). *La fila D no existe*.

#### Leyenda de Colores Semántica:
- 🟩 **Verde:** Espacio Libre y disponible.
- 🟪 **Púrpura:** Ocupado por un Residente Socio.
- 🟦 **Azul:** Ocupado por un Residente No Socio.
- 🟧 **Ámbar:** Ocupado por un Visitante Temporal.
- 🌸 **Fucsia/Rosa:** Ocupado por un Socio Desbordado hacia el Área General.

#### Interacción con Celdas:
- **Tooltip Informativo:** Al colocar el cursor sobre cualquier celda, se despliega una tarjeta flotante con el ID de espacio, estado, placa, nombre del conductor, marca y modelo.
- **Detalle y Salida Rápida:** Al hacer clic sobre cualquier celda ocupada, se abre un diálogo modal con los datos completos del vehículo y el botón rápido **🚗💨 Enviar a Cola de Salida**.

---

### 🚗 2.3 Módulo de Garitas de Entrada (`PanelEntrada`)
Administra el flujo de ingreso y la atención concurrente de las 2 Garitas de Entrada.

#### Pasos para Ingreso:
1. **Ingreso de Residentes:**
   - Seleccione el residente en la lista desplegable.
   - Seleccione la placa del vehículo (solo se listan vehículos en estado `FUERA`).
   - Presione **🚗 Enviar a Cola de Entrada**.
2. **Ingreso de Visitantes:**
   - Ingrese el Nombre del Visitante, ID del Residente a quien visita, Placa, Marca, Modelo, Color y Tipo de Vehículo.
   - Presione **🎫 Enviar Visitante a Cola de Entrada**.
3. **Monitoreo Concurrente:**
   - Observe cómo las **Garitas 1 y 2** procesan en paralelo los vehículos de la cola, asignando el espacio correspondiente e informando el resultado en los visores laterales.

---

### 🚙 2.4 Módulo de Garita de Salida (`PanelSalida`)
Gestiona el egreso ordenado de vehículos y la liberación inmediata de espacios en el mapa.

#### Pasos para Salida:
- En la tabla de **Vehículos Estacionados**, seleccione el vehículo deseado.
- Presione **🚙 Solicitar Salida del Residencial**.
- El vehículo pasará a la **Cola de Salida**, donde la **Garita 3** procesará su despacho, liberando el espacio en el parqueo y actualizando la métrica en vivo.

---

### 📋 2.5 Módulo de Bitácora de Eventos (`PanelEventos`)
Muestra el historial cronológico de todas las operaciones realizadas (ingresos, asignaciones, salidas, rechazos por saturación) ordenadas en formato **LIFO** (el evento más reciente aparece en la primera fila).

#### Funciones:
- **Filtrado Dinámico:** Filtre eventos por tipo (Ingresos, Salidas, Rechazos, Asignaciones).
- **Recorrido No Destructivo:** Permite auditorías completas sin alterar el contenido de la pila de eventos.

---

## 3. Cierre del Sistema y Guardado de Datos
- Al pulsar el botón de cerrar la ventana o el botón **💾 Guardar Disco**, el sistema serializará automáticamente toda la información de residentes y vehículos en archivos de texto delimitados por pipe `|` (`residentes.txt` y `vehiculos.txt`), asegurando persistencia total entre sesiones.
