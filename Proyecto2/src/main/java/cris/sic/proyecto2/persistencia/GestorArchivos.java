package cris.sic.proyecto2.persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import cris.sic.proyecto2.estructuras.ListaDobleResidentes;
import cris.sic.proyecto2.estructuras.ListaSimpleVehiculos;
import cris.sic.proyecto2.estructuras.NodoDoble;
import cris.sic.proyecto2.estructuras.NodoSimple;
import cris.sic.proyecto2.modelo.EstadoVehiculo;
import cris.sic.proyecto2.modelo.Residente;
import cris.sic.proyecto2.modelo.Vehiculo;
import cris.sic.proyecto2.util.ValidadorTexto;

/**
 * Gestor central de persistencia en disco para el sistema ResiPark.
 * 
 * Reglas de diseño y restricciones del proyecto:
 * - Uso exclusivo de librerías del paquete java.io.* (BufferedReader, BufferedWriter, FileReader, FileWriter, File).
 * - Cero colecciones del Java Collections Framework (sin ArrayList, HashMap, etc.).
 * - Cero uso de arreglos nativos T[] para almacenamiento persistente.
 * - Formato delimitado por pipe '|' para residentes.txt y vehiculos.txt.
 * - Filtro de integridad: Omisión silenciosa de líneas corruptas, referencias huérfanas o excesos de capacidad.
 * 
 * @author cris_sic
 */
public class GestorArchivos {

    public static final String RUTA_POR_DEFECTO_RESIDENTES = "src/datos/residentes.txt";
    public static final String RUTA_POR_DEFECTO_VEHICULOS = "src/datos/vehiculos.txt";

    /**
     * Resuelve dinámicamente la ruta del archivo residentes.txt en src/datos/.
     */
    public static String getRutaResidentes() {
        if (new File("src/datos/residentes.txt").exists()) {
            return "src/datos/residentes.txt";
        }
        if (new File("Proyecto2/src/datos/residentes.txt").exists()) {
            return "Proyecto2/src/datos/residentes.txt";
        }
        if (new File("Proyecto2").isDirectory()) {
            return "Proyecto2/src/datos/residentes.txt";
        }
        return RUTA_POR_DEFECTO_RESIDENTES;
    }

    /**
     * Resuelve dinámicamente la ruta del archivo vehiculos.txt en src/datos/.
     */
    public static String getRutaVehiculos() {
        if (new File("src/datos/vehiculos.txt").exists()) {
            return "src/datos/vehiculos.txt";
        }
        if (new File("Proyecto2/src/datos/vehiculos.txt").exists()) {
            return "Proyecto2/src/datos/vehiculos.txt";
        }
        if (new File("Proyecto2").isDirectory()) {
            return "Proyecto2/src/datos/vehiculos.txt";
        }
        return RUTA_POR_DEFECTO_VEHICULOS;
    }

    // =========================================================================
    // ESCRITURA / GUARDADO DE DATOS (java.io.BufferedWriter / FileWriter)
    // =========================================================================

    /**
     * Guarda la totalidad de los residentes presentes en la lista doble en formato pipe.
     * Estructura: ID|NOMBRE|CASA|SOCIO (SOCIO = 'S' o 'N')
     *
     * @param listaResidentes Lista doble enlazada de residentes
     * @param rutaArchivo     Ruta del archivo de destino
     * @return Cantidad de residentes guardados exitosamente
     * @throws IOException Si ocurre un error de E/S en disco
     */
    public static int guardarResidentes(ListaDobleResidentes listaResidentes, String rutaArchivo) throws IOException {
        if (listaResidentes == null || listaResidentes.estaVacia()) {
            asegurarArchivoVacio(rutaArchivo);
            return 0;
        }

        asegurarDirectorios(rutaArchivo);
        int guardados = 0;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo, false))) {
            NodoDoble actual = listaResidentes.getCabeza();
            while (actual != null) {
                Residente r = actual.getDato();
                if (r != null) {
                    String id = sanitizarPipe(r.getId());
                    String nombre = sanitizarPipe(r.getNombre());
                    String casa = sanitizarPipe(r.getCasaLote());
                    String socio = r.isEsSocio() ? "S" : "N";

                    String linea = id + "|" + nombre + "|" + casa + "|" + socio;
                    bw.write(linea);
                    bw.newLine();
                    guardados++;
                }
                actual = actual.getSiguiente();
            }
        }

        return guardados;
    }

    /**
     * Guarda la totalidad de los vehículos asociados a cada residente en formato pipe.
     * Estructura: PLACA|MARCA|MODELO|COLOR|ID_RESIDENTE (o PLACA|MARCA|MODELO|COLOR|TIPO|ID_RESIDENTE)
     *
     * @param listaResidentes Lista doble de residentes con sus listas de vehículos
     * @param rutaArchivo     Ruta del archivo de destino
     * @return Cantidad de vehículos guardados
     * @throws IOException Si ocurre un error de E/S
     */
    public static int guardarVehiculos(ListaDobleResidentes listaResidentes, String rutaArchivo) throws IOException {
        if (listaResidentes == null || listaResidentes.estaVacia()) {
            asegurarArchivoVacio(rutaArchivo);
            return 0;
        }

        asegurarDirectorios(rutaArchivo);
        int guardados = 0;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo, false))) {
            NodoDoble actualRes = listaResidentes.getCabeza();
            while (actualRes != null) {
                Residente r = actualRes.getDato();
                if (r != null && r.getListaVehiculos() != null && !r.getListaVehiculos().estaVacia()) {
                    NodoSimple actualVeh = r.getListaVehiculos().getCabeza();
                    while (actualVeh != null) {
                        Vehiculo v = actualVeh.getDato();
                        if (v != null) {
                            String placa = sanitizarPipe(v.getPlaca());
                            String marca = sanitizarPipe(v.getMarca());
                            String modelo = sanitizarPipe(v.getModelo());
                            String color = sanitizarPipe(v.getColor());
                            String tipo = sanitizarPipe(v.getTipo());
                            String idPropietario = sanitizarPipe(r.getId());

                            String linea = placa + "|" + marca + "|" + modelo + "|" + color + "|" + tipo + "|" + idPropietario;
                            bw.write(linea);
                            bw.newLine();
                            guardados++;
                        }
                        actualVeh = actualVeh.getSiguiente();
                    }
                }
                actualRes = actualRes.getSiguiente();
            }
        }

        return guardados;
    }

    /**
     * Guarda residentes y vehículos en las rutas por defecto del proyecto (src/datos/).
     *
     * @param listaResidentes Lista doble en memoria
     * @return true si ambas operaciones fueron exitosas
     */
    public static boolean guardarTodo(ListaDobleResidentes listaResidentes) {
        return guardarTodo(listaResidentes, getRutaResidentes(), getRutaVehiculos());
    }

    /**
     * Guarda residentes y vehículos en las rutas especificadas.
     *
     * @param listaResidentes Lista doble en memoria
     * @param rutaResidentes  Ruta archivo residentes
     * @param rutaVehiculos   Ruta archivo vehículos
     * @return true si ambas operaciones fueron exitosas
     */
    public static boolean guardarTodo(ListaDobleResidentes listaResidentes, String rutaResidentes, String rutaVehiculos) {
        try {
            guardarResidentes(listaResidentes, rutaResidentes);
            guardarVehiculos(listaResidentes, rutaVehiculos);
            return true;
        } catch (IOException e) {
            System.err.println("[ERROR PERSISTENCIA] Fallo al guardar datos en disco: " + e.getMessage());
            return false;
        }
    }

    // =========================================================================
    // LECTURA / CARGA Y RECONSTRUCCIÓN EN MEMORIA (java.io.BufferedReader / FileReader)
    // =========================================================================

    /**
     * Lee el archivo de residentes y construye una nueva ListaDobleResidentes.
     * Ignora líneas corruptas, vacías o con identificadores duplicados.
     *
     * @param rutaArchivo Ruta del archivo de residentes
     * @return ListaDobleResidentes reconstruida
     * @throws IOException Si ocurre un error al abrir el archivo
     */
    public static ListaDobleResidentes cargarResidentes(String rutaArchivo) throws IOException {
        ListaDobleResidentes lista = new ListaDobleResidentes();
        File archivo = new File(rutaArchivo);
        if (!archivo.exists() || !archivo.isFile()) {
            return lista;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue; // Omitir comentarios o líneas en blanco
                }

                // Parseo manual por delimitador pipe '|'
                String[] partes = descomponerLineaPipe(linea);
                if (partes == null || partes.length < 4) {
                    continue; // Línea corrupta
                }

                String id = partes[0].trim();
                String nombre = partes[1].trim();
                String casaLote = partes[2].trim();
                String socioStr = partes[3].trim();

                if (id.isEmpty() || nombre.isEmpty() || casaLote.isEmpty()) {
                    continue; // Campos obligatorios vacíos
                }

                boolean esSocio = socioStr.equalsIgnoreCase("S") || socioStr.equalsIgnoreCase("SI")
                        || socioStr.equalsIgnoreCase("SÍ") || socioStr.equalsIgnoreCase("TRUE");

                // Validar duplicidad
                if (lista.buscarPorId(id) != null) {
                    continue; // Ya existe este residente
                }

                Residente residente = new Residente(id, nombre, casaLote, esSocio);
                lista.insertar(residente);
            }
        }

        return lista;
    }

    /**
     * Lee el archivo de vehículos y los asocia a sus respectivos propietarios en la ListaDobleResidentes.
     * Reglas de integridad:
     * - Omite vehículos cuyo residente no exista en la lista.
     * - Omite vehículos si el residente ya tiene 3 vehículos (capacidad máxima).
     * - Omite vehículos con placas repetidas en todo el sistema.
     *
     * @param listaResidentes Lista doble de residentes previamente cargada
     * @param rutaArchivo     Ruta del archivo de vehículos
     * @return Cantidad de vehículos cargados y asignados exitosamente
     * @throws IOException Si ocurre un error al leer el archivo
     */
    public static int cargarVehiculos(ListaDobleResidentes listaResidentes, String rutaArchivo) throws IOException {
        if (listaResidentes == null || listaResidentes.estaVacia()) {
            return 0;
        }

        File archivo = new File(rutaArchivo);
        if (!archivo.exists() || !archivo.isFile()) {
            return 0;
        }

        int vehiculosCargados = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue;
                }

                String[] partes = descomponerLineaPipe(linea);
                if (partes == null || partes.length < 5) {
                    continue; // Formato inválido
                }

                String placa = partes[0].trim();
                String marca = partes[1].trim();
                String modelo = partes[2].trim();
                String color = partes[3].trim();
                String tipo = "Automóvil";
                String idResidente;

                if (partes.length >= 6) {
                    tipo = partes[4].trim();
                    idResidente = partes[5].trim();
                } else {
                    // Formato estándar 5 campos: PLACA|MARCA|MODELO|COLOR|ID_RESIDENTE
                    idResidente = partes[4].trim();
                }

                if (!ValidadorTexto.esPlacaValida(placa)) {
                    continue;
                }

                // Verificar si la placa ya existe en algún residente del sistema
                if (existePlacaEnSistema(listaResidentes, placa)) {
                    continue;
                }

                // Buscar al propietario en la lista doble
                Residente propietario = listaResidentes.buscarPorId(idResidente);
                if (propietario == null) {
                    continue; // Residente no existe (referencia huérfana)
                }

                // Validar límite estricto de máximo 3 vehículos por residente
                if (propietario.getCantidadVehiculos() >= 3) {
                    continue; // Residente ya completó su cuota máxima de 3
                }

                // Normalizar tipo de vehículo si es necesario
                if (!ValidadorTexto.esTipoVehiculoValido(tipo)) {
                    tipo = "Automóvil";
                }

                Vehiculo nuevoVehiculo = new Vehiculo(placa, marca, modelo, color, tipo, propietario);
                nuevoVehiculo.setEstado(EstadoVehiculo.FUERA);

                if (propietario.agregarVehiculo(nuevoVehiculo)) {
                    vehiculosCargados++;
                }
            }
        }

        return vehiculosCargados;
    }

    /**
     * Carga y reconstruye todo el estado de residentes y vehículos a partir de las rutas por defecto (src/datos/).
     *
     * @return ListaDobleResidentes completamente poblada
     */
    public static ListaDobleResidentes cargarTodo() {
        return cargarTodo(getRutaResidentes(), getRutaVehiculos());
    }

    /**
     * Carga y reconstruye todo el estado a partir de rutas específicas.
     *
     * @param rutaResidentes Ruta archivo residentes
     * @param rutaVehiculos  Ruta archivo vehículos
     * @return ListaDobleResidentes poblada
     */
    public static ListaDobleResidentes cargarTodo(String rutaResidentes, String rutaVehiculos) {
        try {
            ListaDobleResidentes lista = cargarResidentes(rutaResidentes);
            cargarVehiculos(lista, rutaVehiculos);
            return lista;
        } catch (IOException e) {
            System.err.println("[ERROR PERSISTENCIA] Fallo al cargar datos desde disco: " + e.getMessage());
            return new ListaDobleResidentes();
        }
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DE PARSEO Y VALIDACIÓN
    // =========================================================================

    /**
     * Descompone una línea en segmentos delimitados por el carácter pipe '|'.
     * Maneja casos sin depender de librerías externas.
     *
     * @param linea Línea a procesar
     * @return Arreglo de cadenas con los tokens
     */
    public static String[] descomponerLineaPipe(String linea) {
        if (linea == null) {
            return new String[0];
        }

        // Conteo manual de delimitadores
        int count = 1;
        for (int i = 0; i < linea.length(); i++) {
            if (linea.charAt(i) == '|') {
                count++;
            }
        }

        String[] tokens = new String[count];
        int start = 0;
        int tokenIdx = 0;

        for (int i = 0; i < linea.length(); i++) {
            if (linea.charAt(i) == '|') {
                tokens[tokenIdx++] = linea.substring(start, i);
                start = i + 1;
            }
        }
        tokens[tokenIdx] = linea.substring(start);

        return tokens;
    }

    /**
     * Verifica de forma no destructiva si una placa ya se encuentra asignada a algún residente.
     *
     * @param listaResidentes Lista doble de residentes
     * @param placa           Placa a verificar
     * @return true si la placa ya está registrada
     */
    public static boolean existePlacaEnSistema(ListaDobleResidentes listaResidentes, String placa) {
        if (listaResidentes == null || placa == null) {
            return false;
        }
        NodoDoble actual = listaResidentes.getCabeza();
        while (actual != null) {
            Residente r = actual.getDato();
            if (r != null && r.getListaVehiculos() != null) {
                if (r.getListaVehiculos().buscarPorPlaca(placa) != null) {
                    return true;
                }
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    private static String sanitizarPipe(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace('|', '/').trim();
    }

    private static void asegurarDirectorios(String rutaArchivo) {
        File file = new File(rutaArchivo);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    private static void asegurarArchivoVacio(String rutaArchivo) throws IOException {
        asegurarDirectorios(rutaArchivo);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo, false))) {
            // Archivo truncado a 0 bytes
        }
    }
}
