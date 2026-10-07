    package cris.sic.proyecto2;

import cris.sic.proyecto2.estructuras.ListaDobleResidentes;
import cris.sic.proyecto2.estructuras.ListaSimpleVehiculos;
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

        System.out.println("======================================================================");
        System.out.println("   RESULTADO DE INSPECCIÓN FASE 1: " + pruebasPasadas + "/" + pruebasTotales + " PRUEBAS SUPERADAS");
        if (pruebasPasadas == pruebasTotales) {
            System.out.println("   ESTADO: [FASE 1 COMPLETADA CON ÉXITO]");
        } else {
            System.out.println("   ESTADO: [SE DETECTARON FALLOS EN FASE 1]");
        }
        System.out.println("======================================================================\n");

        // =====================================================================
        // PUNTO DE INSPECCIÓN FASE 2: ESTRUCTURAS DINÁMICAS LINEALES
        // =====================================================================
        System.out.println("======================================================================");
        System.out.println("   RESIPARK - PUNTO DE INSPECCIÓN FASE 2: LISTAS SIMPLES Y DOBLES     ");
        System.out.println("======================================================================\n");

        int pruebasPasadasF2 = 0;
        int pruebasTotalesF2 = 0;

        // -------------------------------------------------------------------------
        // PRUEBA 2.1: Inserción y búsqueda en ListaDobleResidentes
        // -------------------------------------------------------------------------
        pruebasTotalesF2++;
        System.out.println(">>> PRUEBA 2.1: Inserción y búsqueda por ID en ListaDobleResidentes");
        ListaDobleResidentes listaResidentes = new ListaDobleResidentes();
        Residente resA = new Residente("R-201", "Mario Silva", "Casa C-01", true);
        Residente resB = new Residente("R-202", "Elena Morales", "Casa C-02", false);
        Residente resC = new Residente("R-203", "Pedro Ruiz", "Casa C-03", true);

        boolean insResA = listaResidentes.insertar(resA);
        boolean insResB = listaResidentes.insertar(resB);
        boolean insResC = listaResidentes.insertar(resC);
        boolean insDuplicado = listaResidentes.insertar(new Residente("R-201", "Otro", "Casa X", false));

        Residente encontradoB = listaResidentes.buscarPorId("R-202");
        Residente noExiste = listaResidentes.buscarPorId("R-999");

        System.out.println("   Insertados 3 residentes: " + (insResA && insResB && insResC));
        System.out.println("   Intento de insertar ID duplicado R-201: " + insDuplicado + " (Esperado: false)");
        System.out.println("   Búsqueda R-202: " + (encontradoB != null ? encontradoB.getNombre() : "null"));
        System.out.println("   Búsqueda R-999 inexistente: " + noExiste + " (Esperado: null)");

        if (insResA && insResB && insResC && !insDuplicado && encontradoB != null && noExiste == null && listaResidentes.getTamaño() == 3) {
            System.out.println("   [OK] Inserción y búsqueda en ListaDobleResidentes superadas.");
            pruebasPasadasF2++;
        } else {
            System.out.println("   [FALLO] Inserción o búsqueda en ListaDobleResidentes falló.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // PRUEBA 2.2: Recorrido bidireccional (Ambos sentidos) en ListaDobleResidentes
        // -------------------------------------------------------------------------
        pruebasTotalesF2++;
        System.out.println(">>> PRUEBA 2.2: Recorrido bidireccional en ListaDobleResidentes");
        String recorrido = listaResidentes.recorrerAmbosSentidos();
        System.out.print(recorrido);

        if (listaResidentes.getCabeza().getDato().getId().equals("R-201")
                && listaResidentes.getCola().getDato().getId().equals("R-203")
                && listaResidentes.getCola().getAnterior().getDato().getId().equals("R-202")) {
            System.out.println("   [OK] Recorrido bidireccional y punteros anterior/siguiente correctos.");
            pruebasPasadasF2++;
        } else {
            System.out.println("   [FALLO] Error en enlaces dobles.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // PRUEBA 2.3: Restricción estricta de máximo 3 vehículos (Rechazo del 4to)
        // -------------------------------------------------------------------------
        pruebasTotalesF2++;
        System.out.println(">>> PRUEBA 2.3: Límite estricto de vehículos en ListaSimpleVehiculos (Rechazo del 4to)");
        ListaSimpleVehiculos listaAutos = new ListaSimpleVehiculos();
        Vehiculo auto1 = new Vehiculo("P-1111", "Mazda", "3", "Rojo", "Automóvil");
        Vehiculo auto2 = new Vehiculo("P-2222", "Toyota", "Yaris", "Blanco", "Automóvil");
        Vehiculo auto3 = new Vehiculo("P-3333", "Nissan", "Sentra", "Azul", "Automóvil");
        Vehiculo auto4 = new Vehiculo("P-4444", "Hyundai", "Elantra", "Negro", "Automóvil");

        boolean a1 = listaAutos.insertar(auto1);
        boolean a2 = listaAutos.insertar(auto2);
        boolean a3 = listaAutos.insertar(auto3);
        boolean a4 = listaAutos.insertar(auto4); // Debe ser rechazado (límite 3)

        System.out.println("   Auto 1 insertado: " + a1);
        System.out.println("   Auto 2 insertado: " + a2);
        System.out.println("   Auto 3 insertado: " + a3);
        System.out.println("   Auto 4 insertado: " + a4 + " (Esperado: false)");
        System.out.println("   Cantidad en lista: " + listaAutos.getTamaño() + "/3");

        if (a1 && a2 && a3 && !a4 && listaAutos.getTamaño() == 3) {
            System.out.println("   [OK] Restricción de capacidad máxima (3) cumplida estrictamente.");
            pruebasPasadasF2++;
        } else {
            System.out.println("   [FALLO] Falló la restricción de capacidad máxima de vehículos.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // PRUEBA 2.4: Regla de Oro - Bloqueo de eliminación si vehículo está ESTACIONADO
        // -------------------------------------------------------------------------
        pruebasTotalesF2++;
        System.out.println(">>> PRUEBA 2.4: Bloqueo de eliminación de residente con vehículo ESTACIONADO");
        Residente resConAuto = listaResidentes.buscarPorId("R-201");
        Vehiculo vEstacionado = new Vehiculo("P-5555", "Honda", "CRV", "Plateado", "Pickup");
        resConAuto.agregarVehiculo(vEstacionado);

        // Pasamos el vehículo a ESTACIONADO
        vEstacionado.entrarAColaEntrada();
        vEstacionado.estacionar();
        System.out.println("   Vehículo agregado a R-201 con estado: " + vEstacionado.getEstado());

        // Intentamos eliminar al residente (DEBE DENEGARSE)
        boolean eliminadoBloqueado = listaResidentes.eliminar("R-201");
        System.out.println("   Intento de eliminar R-201 con auto ESTACIONADO: " + eliminadoBloqueado + " (Esperado: false)");

        // Intentamos eliminar el auto directamente de la lista simple (DEBE DENEGARSE)
        boolean autoEliminadoBloqueado = resConAuto.getListaVehiculos().eliminar("P-5555");
        System.out.println("   Intento de eliminar auto P-5555 con estado ESTACIONADO: " + autoEliminadoBloqueado + " (Esperado: false)");

        // Ahora sacamos el auto a FUERA
        vEstacionado.entrarAColaSalida();
        vEstacionado.salirDelResidencial();
        System.out.println("   Vehículo salió a estado: " + vEstacionado.getEstado());

        // Con el auto FUERA, ahora sí se debe permitir eliminar
        boolean eliminadoExitoso = listaResidentes.eliminar("R-201");
        System.out.println("   Eliminación de R-201 con autos FUERA: " + eliminadoExitoso + " (Esperado: true)");
        System.out.println("   Tamaño de lista residentes: " + listaResidentes.getTamaño());

        if (!eliminadoBloqueado && !autoEliminadoBloqueado && eliminadoExitoso && listaResidentes.buscarPorId("R-201") == null) {
            System.out.println("   [OK] Bloqueo y descarte condicional de eliminación validado correctamente.");
            pruebasPasadasF2++;
        } else {
            System.out.println("   [FALLO] La regla de oro en eliminación no se cumplió.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // RESUMEN FINAL FASE 2
        // -------------------------------------------------------------------------
        System.out.println("======================================================================");
        System.out.println("   RESULTADO DE INSPECCIÓN FASE 2: " + pruebasPasadasF2 + "/" + pruebasTotalesF2 + " PRUEBAS SUPERADAS");
        if (pruebasPasadasF2 == pruebasTotalesF2) {
            System.out.println("   ESTADO: [FASE 2 COMPLETADA CON ÉXITO]");
        } else {
            System.out.println("   ESTADO: [SE DETECTARON FALLOS EN FASE 2]");
        }
        System.out.println("======================================================================");
    }
}
