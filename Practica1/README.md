# Práctica 1: Sistema de Estacionamiento

Este proyecto consiste en un sistema de estacionamiento desarrollado en Java que simula la administración de un parqueo de 10x10. Se gestionan de manera rígida e interactiva vehículos mediante el uso de arreglos paralelos estáticos, controlando tarifas, ingresos y calculando la ruta perimetral más corta.

## Requisitos de Ejecución
* **Java Development Kit (JDK):** Versión 21 o superior.
* **Sistema Operativo:** Compatible con consola estándar (Linux, macOS, Windows).

## Estructura del Proyecto
```text
Practica1/
├── src/
│   └── main/
│       └── java/
│           └── cris/
│               └── sic/
│                   └── estacionamiento/
│                       └── Practica1.java  <-- Código Fuente Principal
├── target/                                  <-- Binarios compilados
│   └── Practica1-1.0.jar                    <-- Ejecutable generado
├── pom.xml                                  <-- Configuración Maven
├── README.md                                <-- Guía de ejecución (Este archivo)
├── Manual_Tecnico.md                        <-- Documentación Técnica
└── Manual_Usuario.md                        <-- Guía paso a paso para el usuario
```

## Compilación y Ejecución Manual

### 1. Compilación
Para compilar las clases del proyecto manualmente a la carpeta de salida `target/classes`, ejecute el siguiente comando desde la raíz del directorio `Practica1`:

```bash
mkdir -p target/classes
javac -d target/classes src/main/java/cris/sic/estacionamiento/Practica1.java
```

### 2. Generación del Archivo Ejecutable (`.jar`)
Para empaquetar el proyecto y generar un ejecutable autónomo, corra el siguiente comando:

```bash
jar --create --file target/Practica1-1.0.jar --main-class cris.sic.estacionamiento.Practica1 -C target/classes .
```

### 3. Ejecución
Una vez generado el ejecutable, puede iniciar la aplicación ejecutando:

```bash
java -jar target/Practica1-1.0.jar
```
o ejecutando directamente las clases compiladas:
```bash
java -cp target/classes cris.sic.estacionamiento.Practica1
```
