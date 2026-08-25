package cris.sic.estacionamiento;

import java.util.Scanner;
import java.util.Random;

public class Practica1 {

    // Cambia esta variable para seleccionar el tema visual (1: Oficial, 2: Minimalista, 3: Matriarcal, 4: Celdas Nativas)
    static int MODO_VISUAL = 4;

    // Las siguientes variables se utilizan para definir dinamicamente los simbolos de celdas libres y ocupadas
    static char SIMBOLO_LIBRE;
    static char SIMBOLO_OCUPADO;

    // Las variables de esta seccion se encargan de almacenar la representacion del tablero de estacionamiento,
    // el estado actual de los vehiculos parqueados, los contadores de control y los montos recaudados
    static char[][] tablero = new char[10][10];
    static String[] placas = new String[64];
    static int[] filasVehiculos = new int[64];
    static int[] columnasVehiculos = new int[64];
    static int contadorActivos = 0;
    static int totalCobrados = 0;
    static int[] totalCobradosFila = new int[9];
    static double totalRecaudado = 0.0;
    static int filaE = -1;
    static int colE = -1;
    static int filaS = -1;
    static int colS = -1;

    public static void main(String[] args) {
        // Inicializacion del sistema: se generan las posiciones del perimetro y se arranca el menu interactivo
        configurarModoVisual();
        generarEntradaSalida();
        inicializarTablero();
        desplegarMenu();
    }

    // Modulo para asignacion de simbolos del tablero en funcion de la variable de configuracion
    public static void configurarModoVisual() {
        switch (MODO_VISUAL) {
            case 2:
                SIMBOLO_LIBRE = '.';
                SIMBOLO_OCUPADO = 'X';
                break;
            case 3:
                SIMBOLO_LIBRE = 'O';
                SIMBOLO_OCUPADO = '#';
                break;
            case 4:
                SIMBOLO_LIBRE = '░';
                SIMBOLO_OCUPADO = '█';
                break;
            case 1:
            default:
                SIMBOLO_LIBRE = 'L';
                SIMBOLO_OCUPADO = 'A';
                break;
        }
    }

    // Modulo para control de la terminal, borrado de pantalla y retardos temporales
    public static void limpiarPantalla() {
        String term = System.getenv("TERM");
        if (term != null && !term.isEmpty()) {
            try {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            } catch (Exception e) {
                for (int i = 0; i < 50; i++) System.out.println();
            }
        } else {
            for (int i = 0; i < 50; i++) System.out.println();
        }
    }

    public static void pausaLenta(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void esperarEnter(Scanner scanner) {
        System.out.println("\nPresione ENTER para continuar...");
        scanner.nextLine();
        limpiarPantalla();
    }

    // Modulo de conversion de columna caracter a indice numerico
    public static int columnaLetraAIndice(char letra) {
        letra = Character.toUpperCase(letra);
        if (letra >= 'A' && letra <= 'H') {
            return (letra - 'A') + 1;
        }
        return -1;
    }

    // Modulo para mapear indice numerico de columna a caracter
    public static char columnaIndiceALetra(int col) {
        if (col >= 1 && col <= 8) {
            return (char) ('A' + (col - 1));
        }
        return '?';
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
                    tablero[i][j] = SIMBOLO_LIBRE;
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
            limpiarPantalla();
            System.out.println("--- MENU PRINCIPAL ---");
            System.out.println("1. Ingresar vehiculo");
            System.out.println("2. Retirar vehiculo");
            System.out.println("3. Mostrar estacionamiento");
            System.out.println("4. Buscar vehiculo por placa");
            System.out.println("5. Mostrar ruta mas corta entre entrada y salida");
            System.out.println("6. Mostrar ingresos");
            System.out.println("7. Salir");
            
            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
                scanner.nextLine();
                switch (opcion) {
                    case 1:
                        limpiarPantalla();
                        ingresarVehiculo(scanner);
                        esperarEnter(scanner);
                        break;
                    case 2:
                        limpiarPantalla();
                        retirarVehiculo(scanner);
                        esperarEnter(scanner);
                        break;
                    case 3:
                        limpiarPantalla();
                        mostrarEstacionamiento();
                        esperarEnter(scanner);
                        break;
                    case 4:
                        limpiarPantalla();
                        buscarVehiculo(scanner);
                        esperarEnter(scanner);
                        break;
                    case 5:
                        limpiarPantalla();
                        calcularRutaCorta(scanner);
                        esperarEnter(scanner);
                        break;
                    case 6:
                        limpiarPantalla();
                        mostrarIngresos();
                        esperarEnter(scanner);
                        break;
                    case 7:
                        limpiarPantalla();
                        System.out.println("Saliendo del sistema...");
                        break;
                    default:
                        limpiarPantalla();
                        System.out.println("Opcion no valida. Intente de nuevo.");
                        esperarEnter(scanner);
                }
            } else {
                limpiarPantalla();
                System.out.println("Entrada invalida. Ingrese un numero.");
                scanner.nextLine();
                opcion = 0;
                esperarEnter(scanner);
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

        System.out.print("Ingrese la columna (A-H): ");
        String colInput = scanner.nextLine().trim();
        if (colInput.isEmpty()) {
            System.out.println("Columna invalida.");
            return;
        }
        char colChar = colInput.charAt(0);
        int col = columnaLetraAIndice(colChar);

        if (fila < 1 || fila > 8 || col < 1 || col > 8) {
            System.out.println("Coordenadas fuera de rango. Filas de 1-8 y Columnas de A-H.");
            return;
        }

        if (tablero[fila][col] != SIMBOLO_LIBRE) {
            System.out.println("El espacio seleccionado no esta libre.");
            return;
        }

        int[][] camino = buscarCaminoBFS(filaE, colE, fila, col);
        if (camino == null) {
            System.out.println("No hay una ruta transitable libre por el interior para llegar a este espacio.");
            return;
        }

        char[][] copia = new char[10][10];
        for (int i = 0; i < 10; i++) {
            System.arraycopy(tablero[i], 0, copia[i], 0, 10);
        }
        marcarCaminoEnTablero(copia, camino);
        imprimirTableroAnimado(copia);
        System.out.printf("%nRuta de ingreso calculada con éxito: %d pasos.%n", camino.length - 1);

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

        tablero[fila][col] = SIMBOLO_OCUPADO;

        for (int i = 0; i < 64; i++) {
            if (placas[i] == null) {
                placas[i] = placa;
                filasVehiculos[i] = fila;
                columnasVehiculos[i] = col;
                break;
            }
        }

        totalCobradosFila[fila] += tarifa;
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

            int[][] camino = buscarCaminoBFS(fila, col, filaS, colS);
            if (camino == null) {
                System.out.println("No hay una ruta transitable libre hacia la salida. El vehiculo esta bloqueado.");
                return;
            }

            char[][] copia = new char[10][10];
            for (int i = 0; i < 10; i++) {
                System.arraycopy(tablero[i], 0, copia[i], 0, 10);
            }
            marcarCaminoEnTablero(copia, camino);
            imprimirTableroAnimado(copia);
            System.out.printf("%nRuta de salida calculada con éxito: %d pasos.%n", camino.length - 1);

            char colLetra = columnaIndiceALetra(col);
            System.out.printf("Vehiculo retirado del espacio Fila: %d, Columna: %c.%n", fila, colLetra);
            tablero[fila][col] = SIMBOLO_LIBRE;
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
        System.out.println("--- TABLERO DE ESTACIONAMIENTO ---");
        System.out.println("      A B C D E F G H");

        for (int i = 0; i < 10; i++) {
            if (i >= 1 && i <= 8) {
                System.out.print("  " + i + " ");
            } else {
                System.out.print("    ");
            }

            for (int j = 0; j < 10; j++) {
                System.out.print(tablero[i][j] + " ");
            }
            System.out.println();
        }

        int libres = 0;
        int ocupados = 0;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                if (tablero[i][j] == SIMBOLO_LIBRE) {
                    libres++;
                } else if (tablero[i][j] == SIMBOLO_OCUPADO) {
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
            System.out.println("Columna: " + columnaIndiceALetra(columnasVehiculos[idx]));
        } else {
            System.out.println("Vehiculo no encontrado.");
        }
    }

    // Despliegue de los ingresos financieros consolidados acumulados durante la ejecucion
    public static void mostrarIngresos() {
        System.out.println("===== INGRESOS =====");
        System.out.println("Vehiculos cobrados: " + totalCobrados);
        System.out.println("Tarifa por vehiculo: Q10.00");
        System.out.printf("Total recaudado: Q%.2f%n", totalRecaudado);
        for (int i = 1; i <= 8; i++){
            System.err.println("Para la fila "+ i +" has recaudado Q."+ totalCobradosFila[i]);
        }
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

    // Modulo para obtener las coordenadas bidimensionales a partir de un indice perimetral completo (0 a 35)
    public static int[] obtenerCoordenadasDeIndicePerimetral36(int idx) {
        int[] coords = new int[2];
        if (idx < 10) {
            coords[0] = 0;
            coords[1] = idx;
        } else if (idx < 19) {
            coords[0] = idx - 9;
            coords[1] = 9;
        } else if (idx < 28) {
            coords[0] = 9;
            coords[1] = 27 - idx;
        } else {
            coords[0] = 36 - idx;
            coords[1] = 0;
        }
        return coords;
    }

    // Modulo auxiliar para marcar el trazo del camino perimetral en la matriz de ruta
    public static void marcarRutaEnCopia(char[][] copia, int idx) {
        int[] coords = obtenerCoordenadasDeIndicePerimetral36(idx);
        int r = coords[0];
        int c = coords[1];

        if (copia[r][c] == 'E' || copia[r][c] == 'S') {
            return;
        }

        if ((r == 0 && c == 0) || (r == 0 && c == 9) || (r == 9 && c == 0) || (r == 9 && c == 9)) {
            copia[r][c] = '+';
        } else if (r == 0 || r == 9) {
            copia[r][c] = '-';
        } else if (c == 0 || c == 9) {
            copia[r][c] = '|';
        }
    }

    // Modulo para busqueda de camino mas corto (BFS) esquivando obstaculos y bordes
    public static int[][] buscarCaminoBFS(int startX, int startY, int endX, int endY) {
        int[] qX = new int[100];
        int[] qY = new int[100];
        int qHead = 0;
        int qTail = 0;

        int[][] parentX = new int[10][10];
        int[][] parentY = new int[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                parentX[i][j] = -1;
                parentY[i][j] = -1;
            }
        }

        qX[qTail] = startX;
        qY[qTail] = startY;
        qTail++;
        parentX[startX][startY] = startX;
        parentY[startX][startY] = startY;

        int[] dirX = {-1, 1, 0, 0};
        int[] dirY = {0, 0, -1, 1};

        boolean encontrado = false;
        while (qHead < qTail) {
            int currX = qX[qHead];
            int currY = qY[qHead];
            qHead++;

            if (currX == endX && currY == endY) {
                encontrado = true;
                break;
            }

            for (int d = 0; d < 4; d++) {
                int nextX = currX + dirX[d];
                int nextY = currY + dirY[d];

                if (nextX < 0 || nextX >= 10 || nextY < 0 || nextY >= 10) {
                    continue;
                }

                if (parentX[nextX][nextY] != -1) {
                    continue;
                }

                boolean esDestino = (nextX == endX && nextY == endY);
                boolean esInternaLibre = (nextX >= 1 && nextX <= 8 && nextY >= 1 && nextY <= 8 && tablero[nextX][nextY] == SIMBOLO_LIBRE);

                if (esDestino || esInternaLibre) {
                    qX[qTail] = nextX;
                    qY[qTail] = nextY;
                    qTail++;
                    parentX[nextX][nextY] = currX;
                    parentY[nextX][nextY] = currY;
                }
            }
        }

        if (!encontrado) {
            return null;
        }

        int[] pathX = new int[100];
        int[] pathY = new int[100];
        int steps = 0;
        int tempX = endX;
        int tempY = endY;

        while (!(tempX == startX && tempY == startY)) {
            pathX[steps] = tempX;
            pathY[steps] = tempY;
            steps++;
            int px = parentX[tempX][tempY];
            int py = parentY[tempX][tempY];
            tempX = px;
            tempY = py;
        }
        pathX[steps] = startX;
        pathY[steps] = startY;
        steps++;

        int[][] camino = new int[steps][2];
        for (int i = 0; i < steps; i++) {
            camino[i][0] = pathX[steps - 1 - i];
            camino[i][1] = pathY[steps - 1 - i];
        }

        return camino;
    }

    // Modulo para marcar el camino trazado en una copia temporal del tablero
    public static void marcarCaminoEnTablero(char[][] copia, int[][] camino) {
        if (camino == null || camino.length < 2) {
            return;
        }

        for (int i = 0; i < camino.length; i++) {
            int r = camino[i][0];
            int c = camino[i][1];

            if (copia[r][c] == 'E' || copia[r][c] == 'S') {
                continue;
            }

            if (i > 0 && i < camino.length - 1) {
                int prevR = camino[i - 1][0];
                int prevC = camino[i - 1][1];
                int nextR = camino[i + 1][0];
                int nextC = camino[i + 1][1];

                boolean horizontal = (prevR == r && nextR == r);
                boolean vertical = (prevC == c && nextC == c);

                if (horizontal) {
                    copia[r][c] = '-';
                } else if (vertical) {
                    copia[r][c] = '|';
                } else {
                    copia[r][c] = '+';
                }
            } else {
                copia[r][c] = '*';
            }
        }
    }

    // Modulo para imprimir el tablero temporal con retardo animado fila por fila
    public static void imprimirTableroAnimado(char[][] mat) {
        limpiarPantalla();
        System.out.println("--- TABLERO DE RUTA ---");
        System.out.println("      A B C D E F G H");

        for (int i = 0; i < 10; i++) {
            if (i >= 1 && i <= 8) {
                System.out.print("  " + i + " ");
            } else {
                System.out.print("    ");
            }

            for (int j = 0; j < 10; j++) {
                System.out.print(mat[i][j] + " ");
            }
            System.out.println();
            pausaLenta(100);
        }
    }

    // Calculo de las distancias horarias y antihorarias sobre el perimetro y recomendacion de ruta mas corta
    public static void calcularRutaCorta(Scanner scanner) {
        int[][] camino = buscarCaminoBFS(filaE, colE, filaS, colS);
        if (camino == null) {
            System.out.println("\n--- CALCULO DE RUTA MAS CORTA ---");
            System.out.println("No existe una ruta libre transitable por el interior entre la Entrada y la Salida.");
            return;
        }

        char[][] copia = new char[10][10];
        for (int i = 0; i < 10; i++) {
            System.arraycopy(tablero[i], 0, copia[i], 0, 10);
        }
        marcarCaminoEnTablero(copia, camino);
        imprimirTableroAnimado(copia);

        System.out.println("\n--- CALCULO DE RUTA MAS CORTA ---");
        System.out.println("Entrada (E) esta en (Fila: " + filaE + ", Col: " + colE + ")");
        System.out.println("Salida (S) esta en (Fila: " + filaS + ", Col: " + colS + ")");
        System.out.printf("Distancia de la ruta mas corta encontrada: %d pasos.%n", camino.length - 1);
        System.out.println("La ruta se traza esquivando vehiculos y bordes de forma exitosa.");
    }
}
