package cris.sic.refugio.persistencia;

import cris.sic.refugio.modelo.Usuario;
import cris.sic.refugio.modelo.Animal;
import cris.sic.refugio.modelo.Adoptante;
import cris.sic.refugio.modelo.Solicitud;
import cris.sic.refugio.modelo.Rescate;
import cris.sic.refugio.modelo.Bitacora;

// Clase de almacenamiento global en memoria utilizando arreglos estáticos y contadores
public class BaseDatosMemoria {

    // Capacidad máxima de almacenamiento para los arreglos estáticos
    public static final int MAX_USUARIOS = 100;
    public static final int MAX_ANIMALES = 100;
    public static final int MAX_ADOPTANTES = 100;
    public static final int MAX_SOLICITUDES = 100;
    public static final int MAX_RESCATES = 100;
    public static final int MAX_BITACORA = 1000;

    // Dimensiones de la matriz de celdas/ubicaciones del refugio
    public static final int FILAS_REFUGIO = 5;
    public static final int COLUMNAS_REFUGIO = 5;

    // Contenedores globales estáticos para cada entidad
    public static Usuario[] usuarios = new Usuario[MAX_USUARIOS];
    public static Animal[] animales = new Animal[MAX_ANIMALES];
    public static Adoptante[] adoptantes = new Adoptante[MAX_ADOPTANTES];
    public static Solicitud[] solicitudes = new Solicitud[MAX_SOLICITUDES];
    public static Rescate[] rescates = new Rescate[MAX_RESCATES];
    public static Bitacora[] bitacoraAcciones = new Bitacora[MAX_BITACORA];
    public static Bitacora[] bitacoraErrores = new Bitacora[MAX_BITACORA];

    // Matriz de ubicaciones del refugio (almacena el código del animal o null si está libre)
    public static String[][] ubicacionesRefugio = new String[FILAS_REFUGIO][COLUMNAS_REFUGIO];

    // Contadores enteros auxiliares para el control de registros en los arreglos
    public static int contadorUsuarios = 0;
    public static int contadorAnimales = 0;
    public static int contadorAdoptantes = 0;
    public static int contadorSolicitudes = 0;
    public static int contadorRescates = 0;
    public static int contadorBitacoraAcciones = 0;
    public static int contadorBitacoraErrores = 0;
}
