package cris.sic.practica2.datos;

import cris.sic.practica2.modelo.Partida;
import cris.sic.practica2.modelo.Piloto;
import cris.sic.practica2.seguridad.Encriptador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Gestor de persistencia en disco (.txt) para pilotos y partidas.
 * Utiliza exclusivamente librerías nativas de java.io.* y aplica cifrado reversible
 * mediante Encriptador.procesar() antes de escribir y después de leer los datos.
 */
public class GestorArchivos {

    public static final String ARCHIVO_PILOTOS = "pilotos.txt";
    public static final String ARCHIVO_PARTIDAS = "partidas.txt";

    /**
     * Resuelve la ubicación del archivo considerando si la aplicación se ejecuta
     * desde la raíz del repositorio o dentro de la carpeta Practica2.
     *
     * @param nombreArchivo Nombre del archivo a localizar
     * @return Objeto File con la ruta correspondiente
     */
    private static File obtenerArchivo(String nombreArchivo) {
        File dirPractica2 = new File("Practica2");
        if (dirPractica2.exists() && dirPractica2.isDirectory()) {
            return new File(dirPractica2, nombreArchivo);
        }
        return new File(nombreArchivo);
    }

    /**
     * Inicializa los archivos en disco si aún no existen.
     */
    public static void inicializarArchivos() {
        try {
            File archPilotos = obtenerArchivo(ARCHIVO_PILOTOS);
            if (!archPilotos.exists()) {
                archPilotos.createNewFile();
            }
            File archPartidas = obtenerArchivo(ARCHIVO_PARTIDAS);
            if (!archPartidas.exists()) {
                archPartidas.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("[GESTOR ARCHIVOS] Error al inicializar archivos en disco: " + e.getMessage());
        }
    }

    // ==========================================
    // PERSISTENCIA DE PILOTOS
    // ==========================================

    /**
     * Convierte el arreglo de pilotos a formato CSV (Nombre,TipoNave,PunteoMaximo),
     * cifra la cadena mediante Encriptador.procesar() y la almacena en pilotos.txt.
     *
     * @param pilotos Arreglo de objetos Piloto a persistir
     */
    public static void guardarPilotos(Piloto[] pilotos) {
        File archivo = obtenerArchivo(ARCHIVO_PILOTOS);
        try {
            if (!archivo.exists()) {
                archivo.createNewFile();
            }

            StringBuilder csv = new StringBuilder();
            if (pilotos != null) {
                for (Piloto p : pilotos) {
                    if (p != null) {
                        csv.append(p.getNombre()).append(",")
                           .append(p.getTipoNave()).append(",")
                           .append(p.getPunteoMaximo()).append("\n");
                    }
                }
            }

            // Aplicar cifrado XOR
            String textoCifrado = Encriptador.procesar(csv.toString());

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
                bw.write(textoCifrado);
            }
        } catch (IOException e) {
            System.err.println("[GESTOR ARCHIVOS] Error al guardar pilotos en disco: " + e.getMessage());
        }
    }

    /**
     * Lee el contenido cifrado de pilotos.txt, aplica Encriptador.procesar()
     * para descifrarlo a texto plano y reconstruye las instancias de Piloto en un arreglo.
     *
     * @return Arreglo Piloto[] con los pilotos recuperados de disco
     */
    public static Piloto[] cargarPilotos() {
        File archivo = obtenerArchivo(ARCHIVO_PILOTOS);
        try {
            if (!archivo.exists()) {
                archivo.createNewFile();
                return new Piloto[0];
            }

            // Lectura completa del contenido cifrado caracter por caracter
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                int c;
                while ((c = br.read()) != -1) {
                    sb.append((char) c);
                }
            }

            String contenidoCifrado = sb.toString();
            if (contenidoCifrado.trim().isEmpty()) {
                return new Piloto[0];
            }

            // Descifrado mediante Encriptador.procesar()
            String contenidoDescifrado = Encriptador.procesar(contenidoCifrado);

            // Parsear líneas CSV reconstruyendo los objetos Piloto
            String[] lineas = contenidoDescifrado.split("\\r?\\n");
            Piloto[] temporal = new Piloto[lineas.length];
            int contador = 0;

            for (String linea : lineas) {
                if (linea == null || linea.trim().isEmpty()) continue;
                String[] partes = linea.split(",");
                if (partes.length >= 3) {
                    String nombre = partes[0].trim();
                    String tipoNave = partes[1].trim();
                    int punteoMaximo = 0;
                    try {
                        punteoMaximo = Integer.parseInt(partes[2].trim());
                    } catch (NumberFormatException ignored) {}
                    temporal[contador++] = new Piloto(nombre, tipoNave, punteoMaximo);
                }
            }

            Piloto[] resultado = new Piloto[contador];
            System.arraycopy(temporal, 0, resultado, 0, contador);
            return resultado;
        } catch (IOException e) {
            System.err.println("[GESTOR ARCHIVOS] Error al cargar pilotos desde disco: " + e.getMessage());
            return new Piloto[0];
        }
    }

    // ==========================================
    // PERSISTENCIA DE PARTIDAS
    // ==========================================

    /**
     * Convierte el arreglo de partidas a formato CSV (nombrePiloto,puntajeObtenido,fecha,enemigosDestruidos),
     * cifra la cadena mediante Encriptador.procesar() y la almacena en partidas.txt.
     *
     * @param partidas Arreglo de objetos Partida a persistir
     */
    public static void guardarPartidas(Partida[] partidas) {
        File archivo = obtenerArchivo(ARCHIVO_PARTIDAS);
        try {
            if (!archivo.exists()) {
                archivo.createNewFile();
            }

            StringBuilder csv = new StringBuilder();
            if (partidas != null) {
                for (Partida p : partidas) {
                    if (p != null) {
                        csv.append(p.getNombrePiloto()).append(",")
                           .append(p.getPuntajeObtenido()).append(",")
                           .append(p.getFecha()).append(",")
                           .append(p.getEnemigosDestruidos()).append("\n");
                    }
                }
            }

            // Aplicar cifrado XOR
            String textoCifrado = Encriptador.procesar(csv.toString());

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
                bw.write(textoCifrado);
            }
        } catch (IOException e) {
            System.err.println("[GESTOR ARCHIVOS] Error al guardar partidas en disco: " + e.getMessage());
        }
    }

    /**
     * Lee el contenido cifrado de partidas.txt, aplica Encriptador.procesar()
     * para descifrarlo a texto plano y reconstruye las instancias de Partida en un arreglo.
     *
     * @return Arreglo Partida[] con las partidas recuperadas de disco
     */
    public static Partida[] cargarPartidas() {
        File archivo = obtenerArchivo(ARCHIVO_PARTIDAS);
        try {
            if (!archivo.exists()) {
                archivo.createNewFile();
                return new Partida[0];
            }

            // Lectura completa del contenido cifrado
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                int c;
                while ((c = br.read()) != -1) {
                    sb.append((char) c);
                }
            }

            String contenidoCifrado = sb.toString();
            if (contenidoCifrado.trim().isEmpty()) {
                return new Partida[0];
            }

            // Descifrado mediante Encriptador.procesar()
            String contenidoDescifrado = Encriptador.procesar(contenidoCifrado);

            // Parsear líneas CSV reconstruyendo los objetos Partida
            String[] lineas = contenidoDescifrado.split("\\r?\\n");
            Partida[] temporal = new Partida[lineas.length];
            int contador = 0;

            for (String linea : lineas) {
                if (linea == null || linea.trim().isEmpty()) continue;
                String[] partes = linea.split(",");
                if (partes.length >= 4) {
                    String nombrePiloto = partes[0].trim();
                    int puntaje = 0;
                    try {
                        puntaje = Integer.parseInt(partes[1].trim());
                    } catch (NumberFormatException ignored) {}
                    String fecha = partes[2].trim();
                    int enemigos = 0;
                    try {
                        enemigos = Integer.parseInt(partes[3].trim());
                    } catch (NumberFormatException ignored) {}

                    temporal[contador++] = new Partida(nombrePiloto, puntaje, fecha, enemigos);
                }
            }

            Partida[] resultado = new Partida[contador];
            System.arraycopy(temporal, 0, resultado, 0, contador);
            return resultado;
        } catch (IOException e) {
            System.err.println("[GESTOR ARCHIVOS] Error al cargar partidas desde disco: " + e.getMessage());
            return new Partida[0];
        }
    }
}
