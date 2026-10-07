# ResiPark - Sistema de Control y Simulación de Parqueo Residencial

Proyecto desarrollado para el curso de **Introducción a la Programación y Computación 1 (IPC1)**, implementando simulación concurrente multihilo, interfaz gráfica Swing manual y estructuras de datos dinámicas lineales desde cero sin librerías externas de colecciones.

---

## 📌 Arquitectura y Reglas del Proyecto

1. **Cero Colecciones de `java.util`:** No se utiliza `ArrayList`, `LinkedList`, `HashMap`, `Stack`, `Queue` ni arreglos nativos `T[]` para almacenar datos de negocio. Todas las estructuras son dinámicas y enlazadas por nodos propios.
2. **Interfaz 100% Swing por Código:** Construcción visual artesanal mediante `JFrame`, `JPanel`, gestores de esquemas (`GridBagLayout`, `BorderLayout`, `CardLayout`, `GridLayout`) sin editores visuales drag-and-drop.
3. **Simulación Concurrente Multihilo:** Garitas de entrada 1 y 2, y garita de salida operando en hilos (`Thread`/`Runnable`) con sincronización mediante `synchronized`, `wait()` y `notifyAll()`.
4. **Despacho Seguro en UI:** Toda mutación gráfica originada por los hilos concurrentes es despachada en el EDT a través de `SwingUtilities.invokeLater`.
5. **Persistencia Ligera en Disco:** Almacenamiento delimitado por pipe `|` en `./src/datos/residentes.txt` y `./src/datos/vehiculos.txt` mediante librerías nativas `java.io.*`.

---

## 📁 Estructura del Proyecto

```text
Proyecto2/
├── pom.xml
├── README.md
├── src/
│   ├── datos/
│   │   ├── residentes.txt            <-- Persistencia en disco de residentes
│   │   └── vehiculos.txt             <-- Persistencia en disco de vehículos
│   └── main/
│       └── java/
│           └── cris/
│               └── sic/
│                   └── proyecto2/
│                       ├── Proyecto2.java            <-- Clase principal / Inspección Fase 1
│                       ├── estructuras/              <-- Nodos y listas enlazadas manuales
│                       │   ├── NodoSimple.java
│                       │   └── ListaSimpleVehiculos.java
│                       ├── modelo/                   <-- Entidades del dominio de negocio
│                       │   ├── EstadoVehiculo.java
│                       │   ├── Vehiculo.java
│                       │   ├── Residente.java
│                       │   └── Visitante.java
│                       ├── hilos/                    <-- Garitas concurrentes
│                       ├── persistencia/             <-- Persistencia con java.io.*
│                       ├── vista/                    <-- Interfaz Swing manual
│                       └── util/                     <-- Validadores y encriptador
```

---

## 🟢 Estado de Avance: Fase 1 Completada

* [x] **Subfase 1.1:** Estructura de paquetes base (`estructuras`, `modelo`, `hilos`, `persistencia`, `vista`, `util`).
* [x] **Subfase 1.2:** Entidad `Vehiculo.java` con enum `EstadoVehiculo` (`FUERA`, `EN_COLA_ENTRADA`, `ESTACIONADO`, `EN_COLA_SALIDA`) y control estricto de transiciones.
* [x] **Subfase 1.3:** Entidad `Residente.java` con ID inmutable, puntero a `ListaSimpleVehiculos`, límite de máximo 3 vehículos y regla de oro para condición de socio.
* [x] **Subfase 1.4:** Entidad `Visitante.java` temporal con vehículo asociado para flujo en colas.
* [x] **Punto de Inspección:** Verificación exitosa en `Proyecto2.java` con 6/6 pruebas aprobadas y cero uso de colecciones prohibidas.
