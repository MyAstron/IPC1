package cris.sic.proyecto2;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import cris.sic.proyecto2.vista.VentanaPrincipal;
import javax.swing.JFrame;

/**
 * Clase principal y punto de entrada oficial para el Sistema ResiPark.
 * 
 * Responsabilidades:
 * 1. Inicializar el entorno visual y Look & Feel del sistema operativo.
 * 2. Despachar de forma segura la interfaz gráfica interactiva Swing (JFrame) en el Event Dispatch Thread (EDT).
 * 3. Inicializar automáticamente la carga de datos persistentes desde ./src/datos/ (residentes.txt y vehiculos.txt).
 * 4. Poner en marcha los hilos concurrentes de las garitas en segundo plano listos para interactuar con la UI.
 * 5. Gestionar el cierre seguro y la persistencia automática de datos al salir de la aplicación.
 * 
 * @author cris_sic
 */
public class Proyecto2 {

    /**
     * Método principal que arranca la aplicación gráfica interactiva ResiPark.
     *
     * @param args Argumentos de línea de comandos (opcionales)
     */
    public static void main(String[] args) {
        // Configuración de Look and Feel del sistema para una integración nativa
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Se mantiene el estilo por defecto si no está disponible el del sistema
        }

        // Despacho seguro en el hilo de eventos de Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            System.out.println("======================================================================");
            System.out.println("   🅿️ INICIANDO RESIPARK - SISTEMA DE CONTROL Y SIMULACIÓN DE PARQUEO");
            System.out.println("======================================================================");
            System.out.println("   • Cargando datos persistentes desde ./src/datos/...");
            System.out.println("   • Inicializando matriz de 150 espacios de parqueo...");
            System.out.println("   • Arrancando hilos concurrentes de Garitas (Entrada 1, 2 y Salida)...");
            System.out.println("   • Desplegando interfaz gráfica interactiva Swing (JFrame)...");
            System.out.println("======================================================================\n");

            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);
            ventana.setVisible(true);
        });
    }
}
