package cris.sic.proyecto2.persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import cris.sic.proyecto2.estructuras.ListaCircularParqueo;
import cris.sic.proyecto2.estructuras.ListaDobleResidentes;
import cris.sic.proyecto2.estructuras.NodoCircular;
import cris.sic.proyecto2.estructuras.NodoDoble;
import cris.sic.proyecto2.estructuras.NodoPila;
import cris.sic.proyecto2.estructuras.NodoSimple;
import cris.sic.proyecto2.estructuras.PilaEventos;
import cris.sic.proyecto2.modelo.ControladorParqueo;
import cris.sic.proyecto2.modelo.EspacioParqueo;
import cris.sic.proyecto2.modelo.EstadoVehiculo;
import cris.sic.proyecto2.modelo.Evento;
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
 * - Formato delimitado por pipe '|' para residentes.txt, vehiculos.txt, parqueo_socios.txt, parqueo_general.txt y bitacora.txt.
 * - Filtro de integridad: Omisión silenciosa de líneas corruptas, referencias huérfanas o excesos de capacidad.
 * 
 * @author cris_sic
 */
public class GestorArchivos {

    public static final String RUTA_POR_DEFECTO_RESIDENTES = "src/datos/residentes.txt";
    public static final String RUTA_POR_DEFECTO_VEHICULOS = "src/datos/vehiculos.txt";
    public static final String RUTA_POR_DEFECTO_PARQUEO_SOCIOS = "src/datos/parqueo_socios.txt";
    public static final String RUTA_POR_DEFECTO_PARQUEO_GENERAL = "src/datos/parqueo_general.txt";
    public static final String RUTA_POR_DEFECTO_BITACORA = "src/datos/bitacora.txt";

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

    /**
     * Resuelve dinámicamente la ruta del archivo parqueo_socios.txt en src/datos/.
     */
    public static String getRutaParqueoSocios() {
        if (new File("src/datos/parqueo_socios.txt").exists()) {
            return "src/datos/parqueo_socios.txt";
        }
        if (new File("Proyecto2/src/datos/parqueo_socios.txt").exists()) {
            return "Proyecto2/src/datos/parqueo_socios.txt";
        }
        if (new File("Proyecto2").isDirectory()) {
            return "Proyecto2/src/datos/parqueo_socios.txt";
        }
        return RUTA_POR_DEFECTO_PARQUEO_SOCIOS;
    }

    /**
     * Resuelve dinámicamente la ruta del archivo parqueo_general.txt en src/datos/.
     */
    public static String getRutaParqueoGeneral() {
        if (new File("src/datos/parqueo_general.txt").exists()) {
            return "src/datos/parqueo_general.txt";
        }
        if (new File("Proyecto2/src/datos/parqueo_general.txt").exists()) {
            return "Proyecto2/src/datos/parqueo_general.txt";
        }
        if (new File("Proyecto2").isDirectory()) {
            return "Proyecto2/src/datos/parqueo_general.txt";
        }
        return RUTA_POR_DEFECTO_PARQUEO_GENERAL;
    }

    /**
     * Resuelve dinámicamente la ruta del archivo bitacora.txt en src/datos/.
     */
    public static String getRutaBitacora() {
        if (new File("src/datos/bitacora.txt").exists()) {
            return "src/datos/bitacora.txt";
        }
        if (new File("Proyecto2/src/datos/bitacora.txt").exists()) {
            return "Proyecto2/src/datos/bitacora.txt";
        }
        if (new File("Proyecto2").isDirectory()) {
            return "Proyecto2/src/datos/bitacora.txt";
        }
        return RUTA_POR_DEFECTO_BITACORA;
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
     * Estructura: PLACA|MARCA|MODELO|COLOR|TIPO|ID_RESIDENTE
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
     * Guarda el estado de una lista circular de parqueo en disco.
     *
     * @param area        Lista circular de parqueo (Socios o General)
     * @param rutaArchivo Ruta de archivo destino
     * @return Cantidad de espacios guardados
     * @throws IOException Si ocurre un error de E/S
     */
    public static int guardarParqueoArea(ListaCircularParqueo area, String rutaArchivo) throws IOException {
        if (area == null || area.getCabeza() == null) {
            asegurarArchivoVacio(rutaArchivo);
            return 0;
        }

        asegurarDirectorios(rutaArchivo);
        int guardados = 0;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo, false))) {
            synchronized (area) {
                // 1. Guardar referencia al último asignado
                String idUltimo = "NULL";
                if (area.getUltimoAsignado() != null && area.getUltimoAsignado().getDato() != null) {
                    idUltimo = area.getUltimoAsignado().getDato().getIdEspacio();
                }
                bw.write("#ULTIMO_ASIGNADO|" + idUltimo);
                bw.newLine();

                // 2. Guardar los 75 espacios secuencialmente
                NodoCircular actual = area.getCabeza();
                int cont = 0;
                while (cont < area.getCapacidad() && actual != null) {
                    EspacioParqueo esp = actual.getDato();
                    if (esp != null) {
                        String id = esp.getIdEspacio();
                        String estado = esp.estaOcupado() ? "OCUPADO" : "LIBRE";
                        String placa = "NULL";

                        if (esp.estaOcupado() && esp.getVehiculoEstacionado() != null) {
                            placa = esp.getVehiculoEstacionado().getPlaca();
                        }

                        String linea = id + "|" + estado + "|" + placa;
                        bw.write(linea);
                        bw.newLine();
                        guardados++;
                    }
                    actual = actual.getSiguiente();
                    cont++;
                }
            }
        }

        return guardados;
    }

    /**
     * Guarda ambas áreas del parqueo (Socios y General) en sus archivos correspondientes.
     *
     * @param controladorParqueo Controlador central del parqueo
     * @param rutaSocios         Ruta archivo parqueo_socios.txt
     * @param rutaGeneral        Ruta archivo parqueo_general.txt
     * @return true si ambas áreas se guardaron con éxito
     */
    public static boolean guardarParqueo(ControladorParqueo controladorParqueo, String rutaSocios, String rutaGeneral) {
        if (controladorParqueo == null) {
            return false;
        }
        try {
            guardarParqueoArea(controladorParqueo.getAreaSocios(), rutaSocios);
            guardarParqueoArea(controladorParqueo.getAreaGeneral(), rutaGeneral);
            return true;
        } catch (IOException e) {
            System.err.println("[ERROR PERSISTENCIA] Fallo al guardar parqueo en disco: " + e.getMessage());
            return false;
        }
    }

    /**
     * Guarda el parqueo en las rutas por defecto (src/datos/).
     */
    public static boolean guardarParqueo(ControladorParqueo controladorParqueo) {
        return guardarParqueo(controladorParqueo, getRutaParqueoSocios(), getRutaParqueoGeneral());
    }

    /**
     * Guarda la bitácora histórica de eventos (Pila LIFO) en disco en orden cronológico.
     * Utiliza un recorrido recursivo desde la base hacia el tope para almacenar los más antiguos primero.
     *
     * @param pila        Pila de eventos en memoria
     * @param rutaArchivo Ruta del archivo bitacora.txt
     * @return Cantidad de eventos guardados
     * @throws IOException Si ocurre un error de E/S
     */
    public static int guardarBitacora(PilaEventos pila, String rutaArchivo) throws IOException {
        if (pila == null || pila.estaVacia()) {
            asegurarArchivoVacio(rutaArchivo);
            return 0;
        }

        asegurarDirectorios(rutaArchivo);
        int guardados = 0;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo, false))) {
            synchronized (pila) {
                escribirEventosRecursivo(bw, pila.getPrimerNodo());
                guardados = pila.getTamaño();
            }
        }

        return guardados;
    }

    private static void escribirEventosRecursivo(BufferedWriter bw, NodoPila nodo) throws IOException {
        if (nodo == null) {
            return;
        }
        // Primero descender hacia la base (los eventos más antiguos)
        escribirEventosRecursivo(bw, nodo.getSiguiente());

        // Al retornar de la recursión, escribir el evento actual
        Evento ev = nodo.getDato();
        if (ev != null) {
            String fechaHora = sanitizarPipe(ev.getFechaHora());
            String tipo = sanitizarPipe(ev.getTipoEvento());
            String garita = sanitizarPipe(ev.getGaritaInvolucrada());
            String desc = sanitizarPipe(ev.getDescripcion());

            String linea = fechaHora + "|" + tipo + "|" + garita + "|" + desc;
            bw.write(linea);
            bw.newLine();
        }
    }

    /**
     * Guarda la bitácora en la ruta por defecto (src/datos/bitacora.txt).
     */
    public static boolean guardarBitacora(PilaEventos pila) {
        try {
            guardarBitacora(pila, getRutaBitacora());
            return true;
        } catch (IOException e) {
            System.err.println("[ERROR PERSISTENCIA] Fallo al guardar bitácora en disco: " + e.getMessage());
            return false;
        }
    }

    /**
     * Guarda residentes, vehículos, estado del parqueo y la bitácora histórica en disco.
     *
     * @param listaResidentes    Lista doble de residentes en memoria
     * @param controladorParqueo Controlador del parqueo en memoria
     * @param pilaEventos        Bitácora de eventos en memoria
     * @return true si toda la persistencia fue exitosa
     */
    public static boolean guardarTodo(ListaDobleResidentes listaResidentes, ControladorParqueo controladorParqueo, PilaEventos pilaEventos) {
        boolean okRes = guardarTodo(listaResidentes, getRutaResidentes(), getRutaVehiculos());
        boolean okParq = (controladorParqueo != null) && guardarParqueo(controladorParqueo);
        boolean okBit = true;
        if (pilaEventos != null) {
            okBit = guardarBitacora(pilaEventos);
        }
        return okRes && okParq && okBit;
    }

    /**
     * Guarda residentes, vehículos y estado del parqueo.
     */
    public static boolean guardarTodo(ListaDobleResidentes listaResidentes, ControladorParqueo controladorParqueo) {
        return guardarTodo(listaResidentes, controladorParqueo, null);
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
                    idResidente = partes[4].trim();
                }

                if (!ValidadorTexto.esPlacaValida(placa)) {
                    continue;
                }

                if (existePlacaEnSistema(listaResidentes, placa)) {
                    continue;
                }

                Residente propietario = listaResidentes.buscarPorId(idResidente);
                if (propietario == null) {
                    continue;
                }

                if (propietario.getCantidadVehiculos() >= 3) {
                    continue;
                }

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
     * Carga el estado de un área de parqueo desde archivo, restaurando espacios ocupados y puntero circular.
     *
     * @param area            Lista circular de parqueo a poblar
     * @param listaResidentes Lista doble de residentes para vincular instancias de Vehiculo
     * @param rutaArchivo     Ruta del archivo de parqueo
     * @return Cantidad de vehículos restaurados en el parqueo
     * @throws IOException Si ocurre un error al leer el archivo
     */
    public static int cargarParqueoArea(ListaCircularParqueo area, ListaDobleResidentes listaResidentes, String rutaArchivo) throws IOException {
        if (area == null) {
            return 0;
        }

        File archivo = new File(rutaArchivo);
        if (!archivo.exists() || !archivo.isFile()) {
            return 0;
        }

        int restaurados = 0;
        String ultimoAsignadoId = null;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) {
                    continue;
                }

                if (linea.startsWith("#ULTIMO_ASIGNADO")) {
                    String[] partesMeta = descomponerLineaPipe(linea);
                    if (partesMeta.length >= 2) {
                        ultimoAsignadoId = partesMeta[1].trim();
                    }
                    continue;
                }

                if (linea.startsWith("#")) {
                    continue;
                }

                String[] partes = descomponerLineaPipe(linea);
                if (partes == null || partes.length < 3) {
                    continue;
                }

                String idEspacio = partes[0].trim();
                String estado = partes[1].trim();
                String placa = partes[2].trim();

                if (estado.equalsIgnoreCase("OCUPADO") && !placa.equalsIgnoreCase("NULL") && !placa.isEmpty()) {
                    Vehiculo vehiculo = buscarVehiculoPorPlaca(listaResidentes, placa);

                    if (vehiculo == null) {
                        vehiculo = new Vehiculo(placa, "Visitante", "Temporal", "Color", "Automóvil", null);
                    }

                    vehiculo.setEstado(EstadoVehiculo.ESTACIONADO);

                    EspacioParqueo espacio = area.buscarPorId(idEspacio);
                    if (espacio != null) {
                        espacio.ocupar(vehiculo);
                        restaurados++;
                    }
                }
            }
        }

        synchronized (area) {
            if (ultimoAsignadoId != null && !ultimoAsignadoId.equalsIgnoreCase("NULL")) {
                area.setUltimoAsignadoPorId(ultimoAsignadoId);
            }
            area.recalcularOcupados();
        }

        return restaurados;
    }

    /**
     * Carga y reconstruye ambas áreas del parqueo desde disco.
     *
     * @param controladorParqueo Controlador del parqueo
     * @param listaResidentes    Lista doble de residentes
     * @param rutaSocios         Ruta archivo parqueo_socios.txt
     * @param rutaGeneral        Ruta archivo parqueo_general.txt
     * @return Total de vehículos restaurados en el parqueo
     */
    public static int cargarParqueo(ControladorParqueo controladorParqueo, ListaDobleResidentes listaResidentes, String rutaSocios, String rutaGeneral) {
        if (controladorParqueo == null) {
            return 0;
        }
        int total = 0;
        try {
            total += cargarParqueoArea(controladorParqueo.getAreaSocios(), listaResidentes, rutaSocios);
            total += cargarParqueoArea(controladorParqueo.getAreaGeneral(), listaResidentes, rutaGeneral);
            controladorParqueo.recalcularOcupados();
        } catch (IOException e) {
            System.err.println("[ERROR PERSISTENCIA] Fallo al cargar parqueo desde disco: " + e.getMessage());
        }
        return total;
    }

    /**
     * Carga el parqueo desde las rutas por defecto (src/datos/).
     */
    public static int cargarParqueo(ControladorParqueo controladorParqueo, ListaDobleResidentes listaResidentes) {
        return cargarParqueo(controladorParqueo, listaResidentes, getRutaParqueoSocios(), getRutaParqueoGeneral());
    }

    /**
     * Carga y reconstruye la bitácora histórica de eventos (Pila LIFO) desde disco.
     *
     * @param pila        Pila de eventos a poblar
     * @param rutaArchivo Ruta del archivo bitacora.txt
     * @return Cantidad de eventos restaurados
     * @throws IOException Si ocurre un error al leer el archivo
     */
    public static int cargarBitacora(PilaEventos pila, String rutaArchivo) throws IOException {
        if (pila == null) {
            return 0;
        }

        File archivo = new File(rutaArchivo);
        if (!archivo.exists() || !archivo.isFile()) {
            return 0;
        }

        int cargados = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue;
                }

                String[] partes = descomponerLineaPipe(linea);
                if (partes == null || partes.length < 4) {
                    continue;
                }

                String fechaHora = partes[0].trim();
                String tipo = partes[1].trim();
                String garita = partes[2].trim();
                String desc = partes[3].trim();

                Evento ev = new Evento(fechaHora, tipo, desc, garita);
                pila.apilar(ev);
                cargados++;
            }
        }

        return cargados;
    }

    /**
     * Carga la bitácora histórica desde la ruta por defecto (src/datos/bitacora.txt).
     */
    public static int cargarBitacora(PilaEventos pila) {
        try {
            return cargarBitacora(pila, getRutaBitacora());
        } catch (IOException e) {
            System.err.println("[ERROR PERSISTENCIA] Fallo al cargar bitácora desde disco: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Carga y reconstruye todo el estado (residentes, vehículos, parqueo y bitácora) a partir de las rutas por defecto.
     *
     * @param controladorParqueo Controlador de parqueo a poblar
     * @param pilaEventos        Pila de bitácora a poblar
     * @return ListaDobleResidentes completamente poblada
     */
    public static ListaDobleResidentes cargarTodo(ControladorParqueo controladorParqueo, PilaEventos pilaEventos) {
        ListaDobleResidentes lista = cargarTodo(getRutaResidentes(), getRutaVehiculos());
        if (controladorParqueo != null) {
            cargarParqueo(controladorParqueo, lista);
        }
        if (pilaEventos != null) {
            cargarBitacora(pilaEventos);
        }
        return lista;
    }

    /**
     * Carga y reconstruye todo el estado (residentes, vehículos y parqueo).
     */
    public static ListaDobleResidentes cargarTodo(ControladorParqueo controladorParqueo) {
        return cargarTodo(controladorParqueo, null);
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
     * Busca una instancia de Vehiculo en toda la lista de residentes a partir de su placa.
     *
     * @param listaResidentes Lista de residentes
     * @param placa           Placa a buscar
     * @return Instancia de Vehiculo, o null si no se encuentra
     */
    public static Vehiculo buscarVehiculoPorPlaca(ListaDobleResidentes listaResidentes, String placa) {
        if (listaResidentes == null || placa == null) {
            return null;
        }
        NodoDoble actualRes = listaResidentes.getCabeza();
        while (actualRes != null) {
            Residente r = actualRes.getDato();
            if (r != null && r.getListaVehiculos() != null) {
                Vehiculo v = r.getListaVehiculos().buscarPorPlaca(placa);
                if (v != null) {
                    return v;
                }
            }
            actualRes = actualRes.getSiguiente();
        }
        return null;
    }

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
