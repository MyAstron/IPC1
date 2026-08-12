package cris.sic.estacionamiento;

import java.util.Scanner;
import java.util.Random;

public class Practica1 {

    // Las variables de esta seccion se encargan de almacenar la representacion del tablero de estacionamiento,
    // el estado actual de los vehiculos parqueados, los contadores de control y los montos recaudados
    static char[][] tablero = new char[10][10];
    static String[] placas = new String[64];
    static int[] filasVehiculos = new int[64];
    static int[] columnasVehiculos = new int[64];
    static int contadorActivos = 0;
    static int totalCobrados = 0;
    static double totalRecaudado = 0.0;
    static int filaE = -1;
    static int colE = -1;
    static int filaS = -1;
    static int colS = -1;

    public static void main(String[] args) {
        // Inicializacion del sistema: se generan las posiciones del perimetro y se arranca el menu interactivo
        generarEntradaSalida();
        inicializarTablero();
        desplegarMenu();
    }

    // Proceso de generacion aleatoria de la Entrada (E) y Salida (S) en el perimetro sin considerar esquinas
    public static void generarEntradaSalida() {
        Random random = new Random();
        int idxE = random.nextInt(32);
        int idxS;
        do {
            idxS = random.nextInt(32);
        } while (idxS == idxE);

        obtenerCoordenadasPerimetrales(idxE, true);
        obtenerCoordenadasPerimetrales(idxS, false);
    }

    // Traduccion del indice perimetral horario (0 a 31) a coordenadas de matriz bidimensional
    public static void obtenerCoordenadasPerimetrales(int idx, boolean esEntrada) {
        int fila, col;
        if (idx < 8) {
            fila = 0;
            col = idx + 1;
        } else if (idx < 16) {
            fila = idx - 8 + 1;
            col = 9;
        } else if (idx < 24) {
            fila = 9;
            col = idx - 16 + 1;
        } else {
            fila = idx - 24 + 1;
            col = 0;
        }

        if (esEntrada) {
            filaE = fila;
            colE = col;
        } else {
            filaS = fila;
            colS = col;
        }
    }

    // Inicializacion de las celdas del tablero asignando vias perimetrales, accesos y espacios libres internos
    public static void inicializarTablero() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == 0 || i == 9 || j == 0 || j == 9) {
                    tablero[i][j] = '=';
                } else {
                    tablero[i][j] = 'L';
                }
            }
        }
        tablero[filaE][colE] = 'E';
        tablero[filaS][colS] = 'S';
    }

    // Menu principal interactivo con las opciones de operacion, consulta y salida del sistema
    public static void desplegarMenu() {
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;
        do {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Ingresar vehiculo");
            System.out.println("2. Retirar vehiculo");
            System.out.println("3. Mostrar estacionamiento");
            System.out.println("4. Buscar vehiculo por placa");
            System.out.println("5. Mostrar ruta mas corta entre entrada y salida");
            System.out.println("6. Mostrar ingresos");
            System.out.println("7. Salir");
            System.out.print("Seleccione una opcion: ");

            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
                scanner.nextLine();
                switch (opcion) {
                    case 1:
                        ingresarVehiculo(scanner);
                        break;
                    case 2:
                        retirarVehiculo(scanner);
                        break;
                    case 3:
                        mostrarEstacionamiento();
                        break;
                    case 4:
                        buscarVehiculo(scanner);
                        break;
                    case 5:
                        calcularRutaCorta();
                        break;
                    case 6:
                        mostrarIngresos();
                        break;
                    case 7:
                        System.out.println("Saliendo del sistema...");
                        break;
                    default:
                        System.out.println("Opcion no valida. Intente de nuevo.");
                }
            } else {
                System.out.println("Entrada invalida. Ingrese un numero.");
                scanner.nextLine();
                opcion = 0;
            }
        } while (opcion != 7);
    }

    // Modulo para validacion de formato de placas bajo el patron estricto P###LLL
    public static boolean validarPlaca(String placa) {
        if (placa == null || placa.length() != 7) {
            return false;
        }
        if (placa.charAt(0) != 'P') {
            return false;
        }
        for (int i = 1; i <= 3; i++) {
            char c = placa.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        for (int i = 4; i <= 6; i++) {
            char c = placa.charAt(i);
            if (c < 'A' || c > 'Z') {
                return false;
            }
        }
        return true;
    }

    // Verificacion de existencia de una placa especifica en el listado de vehiculos activos
    public static boolean placaExiste(String placa) {
        for (int i = 0; i < 64; i++) {
            if (placas[i] != null && placas[i].equals(placa)) {
                return true;
            }
        }
        return false;
    }

    // Logica de registro, cobro y asignacion de espacio para un vehiculo entrante
    public static void ingresarVehiculo(Scanner scanner) {
        if (contadorActivos >= 64) {
            System.out.println("Estacionamiento lleno. No se pueden ingresar mas vehiculos.");
            return;
        }

        System.out.print("Ingrese la placa del vehiculo (patron P###LLL): ");
        String placa = scanner.nextLine().trim();
        if (!validarPlaca(placa)) {
            System.out.println("Formato de placa invalido.");
            return;
        }
        if (placaExiste(placa)) {
            System.out.println("El vehiculo ya se encuentra estacionado.");
            return;
        }

        System.out.print("Ingrese la fila (1-8): ");
        if (!scanner.hasNextInt()) {
            System.out.println("Fila invalida.");
            scanner.nextLine();
            return;
        }
        int fila = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Ingrese la columna (1-8): ");
        if (!scanner.hasNextInt()) {
            System.out.println("Columna invalida.");
            scanner.nextLine();
            return;
        }
        int col = scanner.nextInt();
        scanner.nextLine();

        if (fila < 1 || fila > 8 || col < 1 || col > 8) {
            System.out.println("Coordenadas fuera de rango. Deben ser de 1 a 8.");
            return;
        }

        if (tablero[fila][col] != 'L') {
            System.out.println("El espacio seleccionado no esta libre.");
            return;
        }

        double tarifa = 10.00;
        System.out.printf("Tarifa fija: Q%.2f%n", tarifa);
        double monto = 0.0;

        while (true) {
            System.out.print("Ingrese el monto entregado por el usuario: Q");
            if (scanner.hasNextDouble()) {
                monto = scanner.nextDouble();
                scanner.nextLine();
                if (monto < tarifa) {
                    System.out.println("Pago insuficiente. Debe ingresar al menos Q10.00.");
                } else {
                    break;
                }
            } else {
                System.out.println("Entrada invalida. Ingrese un monto numerico.");
                scanner.nextLine();
            }
        }

        double cambio = monto - tarifa;
        System.out.printf("Pago aceptado. Su cambio es: Q%.2f%n", cambio);

        tablero[fila][col] = 'A';

        for (int i = 0; i < 64; i++) {
            if (placas[i] == null) {
                placas[i] = placa;
                filasVehiculos[i] = fila;
                columnasVehiculos[i] = col;
                break;
            }
        }

        contadorActivos++;
        totalCobrados++;
        totalRecaudado += tarifa;
        System.out.println("Vehiculo registrado con exito.");
    }

    // Remocion del vehiculo del tablero y liberacion del espacio en los arreglos paralelos
    public static void retirarVehiculo(Scanner scanner) {
        System.out.print("Ingrese la placa del vehiculo a retirar (patron P###LLL): ");
        String placa = scanner.nextLine().trim();
        if (!validarPlaca(placa)) {
            System.out.println("Formato de placa invalido.");
            return;
        }

        int idx = -1;
        for (int i = 0; i < 64; i++) {
            if (placas[i] != null && placas[i].equals(placa)) {
                idx = i;
                break;
            }
        }

        if (idx != -1) {
            int fila = filasVehiculos[idx];
            int col = columnasVehiculos[idx];
            System.out.printf("Vehiculo retirado del espacio Fila: %d, Columna: %d.%n", fila, col);
            tablero[fila][col] = 'L';
            placas[idx] = null;
            filasVehiculos[idx] = 0;
            columnasVehiculos[idx] = 0;
            contadorActivos--;
        } else {
            System.out.println("Vehiculo no encontrado.");
        }
    }

    // Despliegue en consola de la cuadricula de estacionamiento e impresion de estadisticas de ocupacion
    public static void mostrarEstacionamiento() {
        System.out.println("\n--- TABLERO DE ESTACIONAMIENTO ---");
        System.out.print("    ");
        for (int j = 0; j < 10; j++) {
            System.out.print(j + " ");
        }
        System.out.println();

        for (int i = 0; i < 10; i++) {
            System.out.print(" " + i + "  ");
            for (int j = 0; j < 10; j++) {
                System.out.print(tablero[i][j] + " ");
            }
            System.out.println();
        }

        int libres = 0;
        int ocupados = 0;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                if (tablero[i][j] == 'L') {
                    libres++;
                } else if (tablero[i][j] == 'A') {
                    ocupados++;
                }
            }
        }
        System.out.println("\nResumen de ocupacion:");
        System.out.println("Espacios libres: " + libres);
        System.out.println("Espacios ocupados: " + ocupados);
    }

    // Busqueda de un vehiculo por su placa y despliegue de su ubicacion fisica en el tablero
    public static void buscarVehiculo(Scanner scanner) {
        System.out.print("Ingrese la placa del vehiculo a buscar (patron P###LLL): ");
        String placa = scanner.nextLine().trim();
        if (!validarPlaca(placa)) {
            System.out.println("Formato de placa invalido.");
            return;
        }

        int idx = -1;
        for (int i = 0; i < 64; i++) {
            if (placas[i] != null && placas[i].equals(placa)) {
                idx = i;
                break;
            }
        }

        if (idx != -1) {
            System.out.println("Vehiculo encontrado.");
            System.out.println("Fila: " + filasVehiculos[idx]);
            System.out.println("Columna: " + columnasVehiculos[idx]);
        } else {
            System.out.println("Vehiculo no encontrado.");
        }
    }

    // Despliegue de los ingresos financieros consolidados acumulados durante la ejecucion
    public static void mostrarIngresos() {
        System.out.println("\n===== INGRESOS =====");
        System.out.println("Vehiculos cobrados: " + totalCobrados);
        System.out.println("Tarifa por vehiculo: Q10.00");
        System.out.printf("Total recaudado: Q%.2f%n", totalRecaudado);
    }

    // Mapeo de coordenadas bidimensionales de borde a un indice lineal perimetral de 0 a 35
    public static int obtenerIndicePerimetral(int fila, int col) {
        if (fila == 0) {
            return col;
        } else if (col == 9) {
            return 9 + fila;
        } else if (fila == 9) {
            return 18 + (9 - col);
        } else if (col == 0) {
            return 27 + (9 - fila);
        }
        return -1;
    }

    // Calculo de las distancias horarias y antihorarias sobre el perimetro y recomendacion de ruta mas corta
    public static void calcularRutaCorta() {
        int indiceE = obtenerIndicePerimetral(filaE, colE);
        int indiceS = obtenerIndicePerimetral(filaS, colS);

        int horario = (indiceS - indiceE + 36) % 36;
        int antihorario = (indiceE - indiceS + 36) % 36;

        System.out.println("\n--- CALCULO DE RUTA MAS CORTA ---");
        System.out.println("Entrada (E) esta en la posicion perimetral: " + indiceE + " (Fila: " + filaE + ", Col: " + colE + ")");
        System.out.println("Salida (S) esta en la posicion perimetral: " + indiceS + " (Fila: " + filaS + ", Col: " + colS + ")");
        System.out.println("Distancia en sentido horario: " + horario + " posiciones");
        System.out.println("Distancia en sentido antihorario: " + antihorario + " posiciones");

        if (horario < antihorario) {
            System.out.println("Recomendacion: Seguir en sentido HORARIO.");
        } else if (antihorario < horario) {
            System.out.println("Recomendacion: Seguir en sentido ANTIHORARIO.");
        } else {
            System.out.println("Recomendacion: Ambas rutas tienen la misma distancia (Empate).");
        }
    }
}
