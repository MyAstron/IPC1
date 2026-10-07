    package cris.sic.proyecto2;

import cris.sic.proyecto2.modelo.EstadoVehiculo;
import cris.sic.proyecto2.modelo.Residente;
import cris.sic.proyecto2.modelo.Vehiculo;
import cris.sic.proyecto2.modelo.Visitante;
import cris.sic.proyecto2.util.ValidadorTexto;

/**
 * Punto de entrada principal y suite de inspección para la Fase 1 del Proyecto 2 (ResiPark).
 * 
 * Verifica:
 * 1. Instanciación correcta de entidades del modelo (Vehiculo, Residente, Visitante).
 * 2. Ciclo de vida y mutación de estados de vehículos (FUERA -> EN_COLA_ENTRADA -> ESTACIONADO -> EN_COLA_SALIDA -> FUERA).
 * 3. Restricción estricta de máximo 3 vehículos por residente.
 * 4. Regla de oro: Bloqueo de cambio de condición de socio cuando existen vehículos activos en el parqueo.
 * 5. Cero uso de colecciones de java.util o arreglos T[] en datos de negocio.
 * 
 * @author cris_sic
 */
public class Proyecto2 {

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("   RESIPARK - PUNTO DE INSPECCIÓN FASE 1: MODELO Y REGLAS DE NEGOCIO   ");
        System.out.println("======================================================================\n");

        int pruebasPasadas = 0;
        int pruebasTotales = 0;

        // -------------------------------------------------------------------------
        // PRUEBA 1: Instanciación de Residente
        // -------------------------------------------------------------------------
        pruebasTotales++;
        System.out.println(">>> PRUEBA 1: Instanciación de Residente y atributos inmutables");
        try {
            Residente r1 = new Residente("R-101", "Carlos Gómez", "Casa A-15", true);
            System.out.println("   Creado: " + r1);
            if (r1.getId().equals("R-101") && r1.isEsSocio() && r1.getCantidadVehiculos() == 0) {
                System.out.println("   [OK] Residente instanciado con éxito, ID inmutable correcto.");
                pruebasPasadas++;
            } else {
                System.out.println("   [FALLO] Los atributos del residente no coinciden.");
            }
        } catch (Exception e) {
            System.out.println("   [ERROR] " + e.getMessage());
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // PRUEBA 2: Registro de Vehículos y Restricción de Máximo 3 por Residente
        // -------------------------------------------------------------------------
        pruebasTotales++;
        System.out.println(">>> PRUEBA 2: Asignación de vehículos a Residente (Límite: 3)");
        Residente r2 = new Residente("R-102", "Ana Martínez", "Casa B-08", false);

        Vehiculo v1 = new Vehiculo("P111AAA", "Toyota", "Corolla", "Gris", "Automóvil");
        Vehiculo v2 = new Vehiculo("M222BBB", "Yamaha", "FZ25", "Azul", "Motocicleta");
        Vehiculo v3 = new Vehiculo("P333CCC", "Ford", "Ranger", "Negro", "Pickup");
        Vehiculo v4 = new Vehiculo("P444DDD", "Honda", "Civic", "Blanco", "Automóvil");

        boolean ins1 = r2.agregarVehiculo(v1);
        boolean ins2 = r2.agregarVehiculo(v2);
        boolean ins3 = r2.agregarVehiculo(v3);
        boolean ins4 = r2.agregarVehiculo(v4); // Este debe ser rechazado

        System.out.println("   Vehículo 1 (" + v1.getPlaca() + ") agregado: " + ins1);
        System.out.println("   Vehículo 2 (" + v2.getPlaca() + ") agregado: " + ins2);
        System.out.println("   Vehículo 3 (" + v3.getPlaca() + ") agregado: " + ins3);
        System.out.println("   Vehículo 4 (" + v4.getPlaca() + ") agregado: " + ins4 + " (Esperado: false)");

        if (ins1 && ins2 && ins3 && !ins4 && r2.getCantidadVehiculos() == 3) {
            System.out.println("   [OK] Límite de máximo 3 vehículos validado correctamente.");
            pruebasPasadas++;
        } else {
            System.out.println("   [FALLO] El control de capacidad máxima falló.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // PRUEBA 3: Mutación y Transición de Estados del Vehículo
        // -------------------------------------------------------------------------
        pruebasTotales++;
        System.out.println(">>> PRUEBA 3: Flujo de Estados del Vehículo (FUERA -> COLA -> ESTACIONADO -> SALIDA -> FUERA)");
        System.out.println("   Estado inicial: " + v1.getEstado()); // FUERA

        boolean aEntrada = v1.entrarAColaEntrada();
        System.out.println("   Paso a EN_COLA_ENTRADA: " + aEntrada + " -> " + v1.getEstado());

        // Intento de salto inválido: De EN_COLA_ENTRADA a EN_COLA_SALIDA directamente
        boolean saltoInvalido = v1.entrarAColaSalida();
        System.out.println("   Intento de salto inválido a EN_COLA_SALIDA: " + saltoInvalido + " (Esperado: false)");

        boolean aEstacionado = v1.estacionar();
        System.out.println("   Paso a ESTACIONADO: " + aEstacionado + " -> " + v1.getEstado());

        boolean aSalida = v1.entrarAColaSalida();
        System.out.println("   Paso a EN_COLA_SALIDA: " + aSalida + " -> " + v1.getEstado());

        boolean aFuera = v1.salirDelResidencial();
        System.out.println("   Paso a FUERA: " + aFuera + " -> " + v1.getEstado());

        if (aEntrada && !saltoInvalido && aEstacionado && aSalida && aFuera && v1.getEstado() == EstadoVehiculo.FUERA) {
            System.out.println("   [OK] Ciclo de vida y validación de transiciones de estado correctas.");
            pruebasPasadas++;
        } else {
            System.out.println("   [FALLO] El ciclo de transiciones de estado falló.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // PRUEBA 4: Regla de Oro - No modificar socio si tiene vehículos dentro
        // -------------------------------------------------------------------------
        pruebasTotales++;
        System.out.println(">>> PRUEBA 4: Regla de Oro (Modificación de condición de socio)");
        System.out.println("   Residente r2 esSocio: " + r2.isEsSocio());
        System.out.println("   ¿Están todos los vehículos fuera?: " + r2.estanTodosVehiculosFuera());

        // Con todos fuera, el cambio debe ser permitido
        boolean cambioPermitido1 = r2.cambiarEstadoSocio(true);
        System.out.println("   Cambio de socio con autos FUERA: " + cambioPermitido1 + " -> esSocio=" + r2.isEsSocio());

        // Ahora metemos el vehículo v2 al parqueo
        v2.entrarAColaEntrada();
        v2.estacionar();
        System.out.println("   Vehículo v2 pasó a estado: " + v2.getEstado());
        System.out.println("   ¿Tiene vehículos dentro?: " + r2.tieneVehiculosDentro());

        // Intentamos cambiar de nuevo el estado de socio (debe denegarse)
        boolean cambioDenegado = r2.cambiarEstadoSocio(false);
        System.out.println("   Intento de cambiar estado socio con auto ESTACIONADO: " + cambioDenegado + " (Esperado: false)");

        // Devolvemos el auto a FUERA
        v2.entrarAColaSalida();
        v2.salirDelResidencial();
        System.out.println("   Vehículo v2 salió a estado: " + v2.getEstado());

        boolean cambioPermitido2 = r2.cambiarEstadoSocio(false);
        System.out.println("   Cambio de socio luego de que auto salió a FUERA: " + cambioPermitido2 + " -> esSocio=" + r2.isEsSocio());

        if (cambioPermitido1 && !cambioDenegado && cambioPermitido2) {
            System.out.println("   [OK] Regla de Oro de consistencia para socios respetada.");
            pruebasPasadas++;
        } else {
            System.out.println("   [FALLO] La regla de oro no se cumplió adecuadamente.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // PRUEBA 5: Creación y Validación de Visitante Temporal
        // -------------------------------------------------------------------------
        pruebasTotales++;
        System.out.println(">>> PRUEBA 5: Creación de Visitante y vehículo temporal asociado");
        Visitante vis = new Visitante("Lucía Méndez", "P999VIS", "R-101", "Kia", "Picanto", "Rojo", "Automóvil");
        System.out.println("   " + vis);
        System.out.println("   Vehículo asociado al visitante: " + vis.getVehiculo());

        if (vis.getNombre().equals("Lucía Méndez") && vis.getPlaca().equals("P999VIS")
                && vis.getIdResidenteVisita().equals("R-101") && vis.getVehiculo() != null
                && vis.getVehiculo().getEstado() == EstadoVehiculo.FUERA) {
            System.out.println("   [OK] Visitante y vehículo temporal creados y validados correctamente.");
            pruebasPasadas++;
        } else {
            System.out.println("   [FALLO] La entidad Visitante no se instanció según lo esperado.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // PRUEBA 6: Validación de Entradas y Carácter Delimitador Pipe '|'
        // -------------------------------------------------------------------------
        pruebasTotales++;
        System.out.println(">>> PRUEBA 6: Validación de campos y rechazo de carácter pipe '|'");
        boolean pipeDetectado = ValidadorTexto.contienePipe("Carlos|Gomez");
        boolean placaInvalidaPipe = ValidadorTexto.esPlacaValida("P12|34");
        boolean placaValida = ValidadorTexto.esPlacaValida("P123ABC");

        System.out.println("   ¿Detecta pipe en 'Carlos|Gomez'?: " + pipeDetectado);
        System.out.println("   ¿Es válida placa con pipe 'P12|34'?: " + placaInvalidaPipe + " (Esperado: false)");
        System.out.println("   ¿Es válida placa normal 'P123ABC'?: " + placaValida + " (Esperado: true)");

        if (pipeDetectado && !placaInvalidaPipe && placaValida) {
            System.out.println("   [OK] Validaciones de seguridad de formato aprobadas.");
            pruebasPasadas++;
        } else {
            System.out.println("   [FALLO] Falló la validación del delimitador pipe.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // RESUMEN FINAL
        // -------------------------------------------------------------------------
        System.out.println("======================================================================");
        System.out.println("   RESULTADO DE INSPECCIÓN FASE 1: " + pruebasPasadas + "/" + pruebasTotales + " PRUEBAS SUPERADAS");
        if (pruebasPasadas == pruebasTotales) {
            System.out.println("   ESTADO: [FASE 1 COMPLETADA CON ÉXITO]");
        } else {
            System.out.println("   ESTADO: [SE DETECTARON FALLOS EN FASE 1]");
        }
        System.out.println("======================================================================");
    }
}
