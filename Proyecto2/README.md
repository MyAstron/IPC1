# 🅿️ ResiPark - Sistema de Control y Simulación Concurrente de Parqueo Residencial

**Universidad de San Carlos de Guatemala (USAC)**  
**Facultad de Ingeniería — Escuela de Ciencias y Sistemas**  
**Introducción a la Programación y Computación 1 (IPC1)**  
*Desarrollado bajo los estándares de ingeniería y calidad del MyAstron Framework*

---

## 📌 Visión General y Arquitectura

**ResiPark** es una plataforma de software de escritorio en Java SE para el control integral de accesos vehiculares, gestión de residentes y monitoreo concurrente de 150 espacios de parqueo divididos en:
- **Área de Socios:** 75 espacios (Filas A, B y C de 25 celdas cada una).
- **Área General:** 75 espacios (Filas E, F, G, H e I de 15 celdas cada una, sin fila D).

### Principios y Restricciones Técnicas de Ingeniería:
1. **Zero Java Collections Framework:** Prohibición absoluta de `ArrayList`, `LinkedList`, `HashMap`, `Queue`, `Stack` o arreglos dinámicos `T[]` para las entidades del modelo. Todas las estructuras fueron implementadas desde cero mediante nodos y enlaces en memoria (`ListaDobleResidentes`, `ListaSimpleVehiculos`, `ListaCircularParqueo`, `ColaFIFO`, `PilaEventos`).
2. **Concurrencia Multihilo Sincronizada:** Operación simultánea de 2 Garitas de Entrada y 1 Garita de Salida como hilos independientes en segundo plano (`Thread`/`Runnable`), con sincronización por monitores (`synchronized`, bucles `while-wait()` y `notifyAll()`).
3. **Interfaz Gráfica 100% Swing Manual:** Diseño modular Dark Dashboard sin editores gráficos drag-and-drop, utilizando `CardLayout`, `BorderLayout` y `GridLayout`.
4. **Despacho Seguro en EDT:** Mutaciones gráficas originadas por eventos de los hilos de garitas canalizadas estrictamente con `SwingUtilities.invokeLater(...)`.
5. **Persistencia Ligera y Cifrado XOR:** Serialización de datos con delimitador pipe `|` (`residentes.txt` y `vehiculos.txt`) mediante `java.io.*` nativo y cifrado reversible simétrico.

---

## 📁 Estructura del Proyecto

```text
Proyecto2/
├── Manual_Tecnico.md                 <-- Documentación técnica y diagramas de flujo/clase
├── Manual_Usuario.md                 <-- Guía de usuario ilustrada y casos de uso
├── README.md                         <-- Documento principal del repositorio
├── pom.xml                           <-- Configuración Maven de empaquetado
├── residentes.txt                    <-- Persistencia en disco de residentes
├── vehiculos.txt                     <-- Persistencia en disco de vehículos
├── target/
│   ├── classes/                      <-- Binarios compilados
│   └── ResiPark.jar                  <-- JAR Ejecutable autocontenido
└── src/
    └── main/
        └── java/
            └── cris/
                └── sic/
                    └── proyecto2/
                        ├── Proyecto2.java            <-- Suite de Pruebas Unitarias Automatizadas
                        ├── estructuras/              <-- Estructuras de datos desde cero
                        │   ├── NodoDoble.java
                        │   ├── ListaDobleResidentes.java
                        │   ├── NodoSimple.java
                        │   ├── ListaSimpleVehiculos.java
                        │   ├── NodoCircular.java
                        │   ├── ListaCircularParqueo.java
                        │   ├── NodoCola.java
                        │   ├── ColaFIFO.java
                        │   ├── NodoPila.java
                        │   └── PilaEventos.java
                        ├── modelo/                   <-- Entidades de negocio y controlador
                        │   ├── EstadoVehiculo.java
                        │   ├── Vehiculo.java
                        │   ├── Residente.java
                        │   ├── Visitante.java
                        │   ├── TipoEspacio.java
                        │   ├── EstadoEspacio.java
                        │   ├── EspacioParqueo.java
                        │   ├── Evento.java
                        │   └── ControladorParqueo.java
                        ├── hilos/                    <-- Lógica concurrente de garitas
                        │   ├── GaritaListener.java
                        │   ├── GaritaEntrada.java
                        │   ├── GaritaSalida.java
                        │   └── SimuladorParqueo.java
                        ├── persistencia/             <-- E/S y cifrado simétrico
                        │   ├── Encriptador.java
                        │   └── GestorArchivos.java
                        ├── vista/                    <-- Interfaz Swing manual
                        │   ├── TemaUI.java
                        │   ├── PanelResidentes.java
                        │   ├── PanelParqueo.java
                        │   ├── PanelEntrada.java
                        │   ├── PanelSalida.java
                        │   ├── PanelEventos.java
                        │   └── VentanaPrincipal.java
                        └── util/
                            └── ValidadorTexto.java
```

---

## 🚀 Instrucciones de Compilación y Ejecución

### 1. Compilación de todo el código fuente:
```bash
cd Proyecto2
javac -d target/classes $(find src/main/java -name "*.java")
```

### 2. Ejecutar la Batería Completa de Pruebas de Inspección (Fases 1 a 9):
```bash
java -cp target/classes cris.sic.proyecto2.Proyecto2
```

### 3. Lanzar la Aplicación Gráfica (Swing):
```bash
java -cp target/classes cris.sic.proyecto2.vista.VentanaPrincipal
```

### 4. Generar y Ejecutar el JAR Distribuible:
```bash
jar cfe target/ResiPark.jar cris.sic.proyecto2.vista.VentanaPrincipal -C target/classes .
java -jar target/ResiPark.jar
```

---

## 📊 Matriz de Cumplimiento de Fases

| Fase | Componente | Estado |
| :--- | :--- | :---: |
| **Fase 1** | Modelo de Dominio Base (`Vehiculo`, `Residente`, `Visitante`) | 🟢 **Completa** |
| **Fase 2** | Estructuras Lineales Propias (`ListaDobleResidentes`, `ListaSimpleVehiculos`) | 🟢 **Completa** |
| **Fase 3** | Estructuras de Flujo y Eventos (`ColaFIFO`, `PilaEventos`) | 🟢 **Completa** |
| **Fase 4** | Parqueo y Listas Circulares (`ListaCircularParqueo`, `ControladorParqueo`) | 🟢 **Completa** |
| **Fase 5** | Persistencia en Disco `java.io.*` y Cifrado XOR (`GestorArchivos`, `Encriptador`) | 🟢 **Completa** |
| **Fase 6** | Lógica Concurrente de Garitas (`GaritaEntrada`, `GaritaSalida`, `Simulador`) | 🟢 **Completa** |
| **Fase 7** | Maquetación Base Swing (`TemaUI`, `PanelResidentes`, `PanelEntrada`, etc.) | 🟢 **Completa** |
| **Fase 8** | Renderizado Dinámico de 150 Celdas (`PanelParqueo`) | 🟢 **Completa** |
| **Fase 9** | Integración UI-Backend y Prueba de Estrés Concurrente (10 Vehículos) | 🟢 **Completa** |
| **Fase 10**| Manuales Técnico/Usuario, JAR Ejecutable y Empaquetado Final | 🟢 **Completa** |
