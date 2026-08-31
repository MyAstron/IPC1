# Manual de Usuario - Sistema de Gestión de Refugio de Animales

Bienvenido a la guía de usuario del **Sistema de Gestión de Refugio de Animales** (Proyecto 1 - IPC1). En este documento encontrará instrucciones paso a paso para utilizar cada una de las funcionalidades de la aplicación.

---

## 1. Requisitos y Ejecución del Sistema

* **Requisitos:** Java Development Kit (JDK 21 o superior).
* **Ejecución:** Al iniciar la aplicación mediante `cris.sic.refugio.Main`, se cargará la persistencia de datos almacenada en los archivos de texto y se abrirá la ventana de inicio de sesión.

---

## 2. Módulo de Autenticación (Inicio de Sesión)

Para acceder al sistema debe ingresar credenciales válidas:

* **Usuario Administrador:**
  * Usuario: `admin1`
  * Contraseña: `Refugio2026`
* **Usuario Auxiliar:**
  * Usuario: `auxiliar1`
  * Contraseña: `Aux2026`

### Control de Seguridad
* Si ingresa credenciales erróneas, el sistema le indicará cuántos intentos le quedan.
* Al acumular **3 intentos fallidos**, el botón **Ingresar** se deshabilitará automáticamente y el evento quedará registrado en la bitácora de errores.

---

## 3. Navegación por Módulos

Una vez iniciada la sesión, la ventana principal mostrará el nombre del usuario activo, el botón para **Cerrar Sesión** y un menú de pestañas para acceder a cada módulo:

---

### 3.1 Módulo de Animales (`Pestaña: Animales`)
Permite registrar y consultar los animales bajo custodia del refugio.

* **Registrar un Animal:**
  1. Ingrese el código con formato `A-xxx` (ejemplo: `A-001`).
  2. Ingrese el nombre, la especie y la edad (entre 0 y 25 años).
  3. Seleccione el estado clínico (`SANO`, `EN_TRATAMIENTO`, `RECUPERADO`).
  4. Presione el botón **Registrar**.
* **Actualizar un Animal:**
  1. Haga clic en una fila de la tabla para cargar los datos en el formulario.
  2. Modifique el nombre, especie, edad o estados.
  3. Presione el botón **Actualizar**.
* **Baja Lógica (Eliminar):**
  1. Seleccione el animal en la tabla.
  2. Presione el botón **Eliminar**.
  3. El animal pasará a estado `ELIMINADO` y liberará su espacio en la matriz si estaba ubicado.
* **Filtros de Búsqueda:**
  * Ingrese código, nombre, especie o seleccione el estado de adopción y presione **Filtrar / Buscar**.

---

### 3.2 Módulo de Adoptantes (`Pestaña: Adoptantes`)
Gestiona el padrón de personas interesadas en adoptar.

* **Registrar Adoptante:**
  1. Ingrese el código con formato `AD-xxx` (ejemplo: `AD-001`).
  2. Ingrese el nombre completo.
  3. Ingrese el **DPI** (exactamente 13 dígitos numéricos sin guiones ni espacios).
  4. Ingrese el **Teléfono** (exactamente 8 dígitos numéricos).
  5. Presione **Registrar**.
* **Editar Adoptante:**
  1. Seleccione el adoptante en la tabla.
  2. Modifique los campos deseados y presione **Actualizar**.
* **Buscar Adoptantes:**
  * Filtre por código, nombre o DPI y presione **Filtrar / Buscar**.

---

### 3.3 Módulo de Solicitudes de Adopción (`Pestaña: Solicitudes`)
Coordina las peticiones de adopción vinculando adoptantes con animales.

* **Crear Solicitud:**
  1. Ingrese el código `S-xxx` (ejemplo: `S-001`).
  2. Ingrese el código de un animal que esté en estado `DISPONIBLE` (ej. `A-001`).
  3. Ingrese el código de un adoptante registrado (ej. `AD-001`).
  4. Ingrese la fecha (ej. `31/08/2026`).
  5. Presione **Registrar**. La solicitud quedará en estado `PENDIENTE`.
* **Aprobar Solicitud:**
  1. Seleccione una solicitud `PENDIENTE` en la tabla.
  2. Presione **Aprobar**.
  3. **Efecto automático:** El animal pasa a estado `ADOPTADO`, su celda en el refugio se libera de inmediato y cualquier otra solicitud pendiente para ese mismo animal pasa automáticamente a `RECHAZADA`.
* **Rechazar Solicitud:**
  1. Seleccione la solicitud y presione **Rechazar**.

---

### 3.4 Módulo de Rescates Urgentes (`Pestaña: Rescates`)
Atención de emergencias y reportes de rescates de animales.

* **Registrar Caso de Rescate:**
  1. Ingrese el código `R-xxx` (ejemplo: `R-001`).
  2. Ingrese la dirección o descripción del caso.
  3. Seleccione la prioridad (`ALTA`, `MEDIA`, `BAJA`).
  4. Ingrese la fecha y presione **Registrar Caso**.
* **Atender Caso:**
  1. Seleccione un rescate de la tabla.
  2. **Opción A (Vincular animal existente):** Ingrese el código del animal en el campo *Cód. Animal Exist*.
  3. **Opción B (Generar animal automático):** Deje el campo vacío e ingrese un nombre y especie opcionales.
  4. Presione **Atender Rescate**. El sistema generará o actualizará el animal poniéndolo en estado clínico `EN_TRATAMIENTO` y marcará el rescate como `ATENDIDO`.

---

### 3.5 Módulo de Ubicaciones (`Pestaña: Ubicaciones`)
Representa de forma interactiva la cuadrícula de 5x5 celdas del refugio.

* **Interpretación de Colores:**
  * **Verde (`Libre`):** La celda está disponible para asignación.
  * **Rojo (`A-xxx`):** La celda está ocupada por el animal indicado.
* **Asignar Animal a Celda:**
  1. Haga clic sobre cualquier botón de la cuadrícula.
  2. Ingrese el código del animal (`A-xxx`) en el campo correspondiente.
  3. Presione **Asignar Animal a Celda**.
* **Liberar Celda:**
  1. Haga clic sobre la celda ocupada.
  2. Presione **Liberar Celda**.
* **Actualizar Vista:**
  * Presione **Actualizar Vista** para refrescar la matriz en tiempo real.

---

### 3.6 Módulo de Reportes y Persistencia (`Pestaña: Reportes`)
Genera reportes formales en HTML y permite guardar el estado del sistema.

* **Botones Disponibles:**
  1. **Reporte de Animales Rescatados (HTML):** Exporta `reporte_animales.html`.
  2. **Reporte de Adopciones y Solicitudes (HTML):** Exporta `reporte_adopciones.html`.
  3. **Reporte de Ocupación del Refugio (HTML):** Exporta `reporte_ocupacion.html`.
  4. **Reporte Bitácora de Acciones (HTML):** Exporta `reporte_bitacora_acciones.html`.
  5. **Reporte Bitácora de Errores (HTML):** Exporta `reporte_bitacora_errores.html`.
  6. **Guardar Estado en Archivos (.txt):** Guarda todos los datos en archivos de texto delimitados por `|`.
* Al generar cualquier reporte, el sistema le preguntará si desea abrirlo directamente en su navegador web.

---

### 3.7 Módulo de Información del Estudiante (`Pestaña: Estudiante`)
Muestra la información institucional, curso, semestre y datos del estudiante desarrollador.

---

## 4. Mensajes de Error Comunes y Soluciones

| Mensaje de Alerta | Causa Común | Solución |
| :--- | :--- | :--- |
| **"Código inválido. Debe usar el formato..."** | El código no cumple el prefijo requerido (`A-xxx`, `AD-xxx`, `S-xxx`, `R-xxx`). | Ingrese el prefijo en mayúscula seguido de un guion y 3 dígitos (ej. `A-005`). |
| **"DPI inválido. Debe tener exactamente 13 dígitos."** | El DPI contiene letras o una longitud distinta a 13. | Verifique que sean exactamente 13 números continuos. |
| **"El animal no se encuentra disponible para adopción"** | El animal ya fue adoptado o eliminado. | Seleccione un animal con estado `DISPONIBLE`. |
| **"La celda seleccionada ya está ocupada."** | Se intentó asignar un animal a un espacio ocupado. | Seleccione una celda en verde (`Libre`) o libere primero la celda actual. |
| **"Ha superado el límite de 3 intentos fallidos."** | Se ingresó contraseña o usuario incorrecto 3 veces. | Reinicie la aplicación para intentar nuevamente. |
