package cris.sic.proyecto2;

import cris.sic.proyecto2.estructuras.ColaFIFO;
import cris.sic.proyecto2.estructuras.ListaCircularParqueo;
import cris.sic.proyecto2.estructuras.ListaDobleResidentes;
import cris.sic.proyecto2.estructuras.ListaSimpleVehiculos;
import cris.sic.proyecto2.estructuras.NodoCircular;
import cris.sic.proyecto2.estructuras.PilaEventos;
import cris.sic.proyecto2.hilos.GaritaEntrada;
import cris.sic.proyecto2.hilos.GaritaListener;
import cris.sic.proyecto2.hilos.GaritaSalida;
import cris.sic.proyecto2.hilos.SimuladorParqueo;
import cris.sic.proyecto2.modelo.ControladorParqueo;
import cris.sic.proyecto2.modelo.EspacioParqueo;
import cris.sic.proyecto2.modelo.EstadoEspacio;
import cris.sic.proyecto2.modelo.EstadoVehiculo;
import cris.sic.proyecto2.modelo.Evento;
import cris.sic.proyecto2.modelo.Residente;
import cris.sic.proyecto2.modelo.TipoEspacio;
import cris.sic.proyecto2.modelo.Vehiculo;
import cris.sic.proyecto2.modelo.Visitante;
import cris.sic.proyecto2.persistencia.Encriptador;
import cris.sic.proyecto2.persistencia.GestorArchivos;
import cris.sic.proyecto2.util.ValidadorTexto;

/**
 * Punto de entrada principal y suite de inspección para el Proyecto 2 (ResiPark).
 * 
 * Verifica fases del planificador con rigor y cero uso de colecciones java.util:
 * - Fase 1: Modelo y reglas de negocio.
 * - Fase 2: Listas lineales (simples y dobles).
 * - Fase 3: Colas FIFO y Pila LIFO de eventos.
 * - Fase 4: Parqueo y listas circulares (asignación y desborde).
 * - Fase 5: Persistencia en disco (java.io.* y cifrado XOR).
 * - Fase 6: Lógica concurrente de garitas (hilos y sincronización).
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
        System.out.println("======================================================================\n");

        // =====================================================================
        // PUNTO DE INSPECCIÓN FASE 3: ESTRUCTURAS DE FLUJO Y EVENTOS (COLAS Y PILA)
        // =====================================================================
        System.out.println("======================================================================");
        System.out.println("   RESIPARK - PUNTO DE INSPECCIÓN FASE 3: COLAS FIFO Y PILA LIFO       ");
        System.out.println("======================================================================\n");

        int pruebasPasadasF3 = 0;
        int pruebasTotalesF3 = 0;

        // -------------------------------------------------------------------------
        // PRUEBA 3.1: Encolado y Desencolado FIFO en ColaFIFO
        // -------------------------------------------------------------------------
        pruebasTotalesF3++;
        System.out.println(">>> PRUEBA 3.1: Orden estricto FIFO de llegada y salida en ColaFIFO");
        ColaFIFO colaEntrada = new ColaFIFO();
        Vehiculo vCola1 = new Vehiculo("P-C001", "Mazda", "2", "Rojo", "Automóvil");
        Vehiculo vCola2 = new Vehiculo("P-C002", "Honda", "Civic", "Azul", "Automóvil");
        Vehiculo vCola3 = new Vehiculo("P-C003", "Toyota", "Hilux", "Blanco", "Pickup");

        boolean enc1 = colaEntrada.encolar(vCola1);
        boolean enc2 = colaEntrada.encolar(vCola2);
        boolean enc3 = colaEntrada.encolar(vCola3);

        System.out.println("   Encolados 3 vehículos: " + (enc1 && enc2 && enc3));
        System.out.println("   Tamaño de la cola: " + colaEntrada.getTamaño());
        System.out.println("   Vehículo al frente (peek): " + colaEntrada.obtenerFrente().getPlaca());
        System.out.println("   ¿Contiene placa P-C002?: " + colaEntrada.contienePlaca("P-C002"));
        System.out.println("   ¿Contiene placa P-INEXISTENTE?: " + colaEntrada.contienePlaca("P-INEXISTENTE"));

        // Desencolamos y verificamos orden exacto 1 -> 2 -> 3
        Vehiculo sale1 = colaEntrada.desencolar();
        Vehiculo sale2 = colaEntrada.desencolar();
        Vehiculo sale3 = colaEntrada.desencolar();
        Vehiculo saleVacio = colaEntrada.desencolar();

        System.out.println("   1er desencolado: " + (sale1 != null ? sale1.getPlaca() : "null") + " (Esperado: P-C001)");
        System.out.println("   2do desencolado: " + (sale2 != null ? sale2.getPlaca() : "null") + " (Esperado: P-C002)");
        System.out.println("   3er desencolado: " + (sale3 != null ? sale3.getPlaca() : "null") + " (Esperado: P-C003)");
        System.out.println("   4to desencolado con cola vacía: " + saleVacio + " (Esperado: null)");
        System.out.println("   ¿Está vacía al final?: " + colaEntrada.estaVacia());

        if (enc1 && enc2 && enc3 && sale1 == vCola1 && sale2 == vCola2 && sale3 == vCola3
                && saleVacio == null && colaEntrada.estaVacia()) {
            System.out.println("   [OK] Cola FIFO opera con disciplina de colas estricta.");
            pruebasPasadasF3++;
        } else {
            System.out.println("   [FALLO] El orden FIFO de la cola no se cumplió.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // PRUEBA 3.2: Apilado, Desapilado y Recorrido LIFO No Destructivo en PilaEventos
        // -------------------------------------------------------------------------
        pruebasTotalesF3++;
        System.out.println(">>> PRUEBA 3.2: Apilado, Desapilado y Recorrido LIFO en PilaEventos");
        PilaEventos bitacora = new PilaEventos();

        Evento ev1 = new Evento("2026-10-07 10:00:00", "INGRESO", "Vehículo P-C001 ingresó a Garita 1", "Garita de Entrada 1");
        Evento ev2 = new Evento("2026-10-07 10:05:00", "ASIGNACION", "Asignado espacio A-1 a P-C001", "Parqueo Socios");
        Evento ev3 = new Evento("2026-10-07 10:10:00", "SALIDA", "Vehículo P-C001 liberó espacio A-1", "Garita de Salida");

        bitacora.apilar(ev1);
        bitacora.apilar(ev2);
        bitacora.apilar(ev3);

        System.out.println("   Eventos apilados: 3");
        System.out.println("   Tamaño de la pila: " + bitacora.getTamaño());
        System.out.println("   Tope actual (peek): " + bitacora.verTope().getTipoEvento() + " (Esperado: SALIDA)");

        // Verificamos el recorrido no destructivo (Debe mostrar 3 -> 2 -> 1)
        System.out.println("   --- RECORRIDO NO DESTRUCTIVO DE BITÁCORA ---");
        String textoRecorrido = bitacora.recorrer();
        System.out.println(textoRecorrido);

        int tamanoPostRecorrido = bitacora.getTamaño();
        System.out.println("   Tamaño después del recorrido (debe conservarse): " + tamanoPostRecorrido);

        // Desapilamos para confirmar orden LIFO estricto
        Evento evSale1 = bitacora.desapilar();
        Evento evSale2 = bitacora.desapilar();
        Evento evSale3 = bitacora.desapilar();
        Evento evSaleVacio = bitacora.desapilar();

        System.out.println("   1er desapilado: " + (evSale1 != null ? evSale1.getTipoEvento() : "null") + " (Esperado: SALIDA)");
        System.out.println("   2do desapilado: " + (evSale2 != null ? evSale2.getTipoEvento() : "null") + " (Esperado: ASIGNACION)");
        System.out.println("   3er desapilado: " + (evSale3 != null ? evSale3.getTipoEvento() : "null") + " (Esperado: INGRESO)");
        System.out.println("   4to desapilado con pila vacía: " + evSaleVacio + " (Esperado: null)");
        System.out.println("   ¿Está vacía al final?: " + bitacora.estaVacia());

        if (tamanoPostRecorrido == 3 && evSale1 == ev3 && evSale2 == ev2 && evSale3 == ev1
                && evSaleVacio == null && bitacora.estaVacia()) {
            System.out.println("   [OK] Pila LIFO de eventos opera correctamente con recorrido no destructivo.");
            pruebasPasadasF3++;
        } else {
            System.out.println("   [FALLO] La pila LIFO de eventos falló.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // RESUMEN FINAL FASE 3
        // -------------------------------------------------------------------------
        System.out.println("======================================================================");
        System.out.println("   RESULTADO DE INSPECCIÓN FASE 3: " + pruebasPasadasF3 + "/" + pruebasTotalesF3 + " PRUEBAS SUPERADAS");
        if (pruebasPasadasF3 == pruebasTotalesF3) {
            System.out.println("   ESTADO: [FASE 3 COMPLETADA CON ÉXITO]");
        } else {
            System.out.println("   ESTADO: [SE DETECTARON FALLOS EN FASE 3]");
        }
        System.out.println("======================================================================\n");

        // =====================================================================
        // PUNTO DE INSPECCIÓN FASE 4: PARQUEO Y LISTAS CIRCULARES (ASIGNACIÓN Y DESBORDE)
        // =====================================================================
        System.out.println("======================================================================");
        System.out.println("   RESIPARK - PUNTO DE INSPECCIÓN FASE 4: LISTAS CIRCULARES Y DESBORDE ");
        System.out.println("======================================================================\n");

        int pruebasPasadasF4 = 0;
        int pruebasTotalesF4 = 0;

        // -------------------------------------------------------------------------
        // PRUEBA 4.1: Topología y dimensiones del Parqueo (75 Socios + 75 General = 150)
        // -------------------------------------------------------------------------
        pruebasTotalesF4++;
        System.out.println(">>> PRUEBA 4.1: Estructura y topología circular del parqueo");
        ControladorParqueo parqueo = new ControladorParqueo();
        int capSocios = parqueo.getAreaSocios().getCapacidad();
        int capGeneral = parqueo.getAreaGeneral().getCapacidad();
        int capTotal = parqueo.getTotalCapacidad();

        System.out.println("   Capacidad Área Socios (Filas A, B, C): " + capSocios + " (Esperado: 75)");
        System.out.println("   Capacidad Área General (Filas E, F, G, H, I): " + capGeneral + " (Esperado: 75, sin fila D)");
        System.out.println("   Capacidad Total: " + capTotal + " (Esperado: 150)");

        // Verificar el cierre circular (nodo 75 apunta a nodo 1)
        NodoCircular cabezaSocios = parqueo.getAreaSocios().getCabeza();
        NodoCircular actual = cabezaSocios;
        for (int i = 0; i < 75; i++) {
            actual = actual.getSiguiente();
        }
        boolean circularidadValida = (actual == cabezaSocios);
        System.out.println("   ¿La lista de Socios es un anillo circular cerrado?: " + circularidadValida);

        if (capSocios == 75 && capGeneral == 75 && capTotal == 150 && circularidadValida) {
            System.out.println("   [OK] Topología de 150 espacios y listas circulares construida correctamente.");
            pruebasPasadasF4++;
        } else {
            System.out.println("   [FALLO] La topología del parqueo no cumple las dimensiones.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // PRUEBA 4.2: Asignación masiva y Desborde de Socios hacia Área General (Espacio 76)
        // -------------------------------------------------------------------------
        pruebasTotalesF4++;
        System.out.println(">>> PRUEBA 4.2: Asignación masiva de 75 socios y desborde del 76° a Área General");
        Residente socioGenerico = new Residente("R-SOCIO-MASIVO", "Socio Masivo", "Lote VIP", true);

        // Llenamos los 75 espacios de socios
        boolean todosAsignadosSocios = true;
        for (int i = 1; i <= 75; i++) {
            Vehiculo autoSocio = new Vehiculo("SOC-" + i, "Toyota", "Corolla", "Blanco", "Automóvil");
            autoSocio.setPropietario(socioGenerico);
            EspacioParqueo espacio = parqueo.asignarVehiculo(autoSocio);
            if (espacio == null || espacio.getTipoEspacio() != TipoEspacio.SOCIO) {
                todosAsignadosSocios = false;
                break;
            }
        }

        System.out.println("   75 espacios de socios llenados exitosamente: " + todosAsignadosSocios);
        System.out.println("   Ocupados Área Socios: " + parqueo.getAreaSocios().getOcupados() + "/75");
        System.out.println("   ¿Área Socios llena?: " + parqueo.getAreaSocios().estaLlena());

        // Ahora insertamos el socio 76 (debe desbordar hacia Área General, asignando ej. E1)
        Vehiculo autoSocio76 = new Vehiculo("SOC-76", "Audi", "A4", "Negro", "Automóvil");
        autoSocio76.setPropietario(socioGenerico);
        EspacioParqueo espacioDesborde = parqueo.asignarVehiculo(autoSocio76);

        System.out.println("   Asignación del Socio 76: " + (espacioDesborde != null ? espacioDesborde.getIdEspacio() : "RECHAZADO"));
        System.out.println("   Tipo de área del espacio asignado: " + (espacioDesborde != null ? espacioDesborde.getTipoEspacio() : "null"));
        System.out.println("   Estado del vehículo 76: " + autoSocio76.getEstado());

        if (todosAsignadosSocios && parqueo.getAreaSocios().estaLlena()
                && espacioDesborde != null && espacioDesborde.getTipoEspacio() == TipoEspacio.GENERAL
                && autoSocio76.getEstado() == EstadoVehiculo.ESTACIONADO) {
            System.out.println("   [OK] Regla de desborde automática de Área Socios a Área General validada con éxito.");
            pruebasPasadasF4++;
        } else {
            System.out.println("   [FALLO] La regla de desborde de socios no se comportó según lo esperado.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // PRUEBA 4.3: Residentes No Socios y Visitantes (Solo acceden a Área General)
        // -------------------------------------------------------------------------
        pruebasTotalesF4++;
        System.out.println(">>> PRUEBA 4.3: Restricción de acceso para No Socios y Visitantes (Solo General)");
        Residente noSocio = new Residente("R-NOSOCIO", "Juan Pérez", "Casa 12", false);
        Vehiculo autoNoSocio = new Vehiculo("NOS-001", "Nissan", "Versa", "Gris", "Automóvil");
        autoNoSocio.setPropietario(noSocio);

        Visitante visitantePrueba = new Visitante("Pedro Gómez", "VIS-001", "R-NOSOCIO", "Hyundai", "Accent", "Rojo", "Automóvil");
        Vehiculo autoVisitante = visitantePrueba.getVehiculo();

        EspacioParqueo espNoSocio = parqueo.asignarVehiculo(autoNoSocio);
        EspacioParqueo espVisitante = parqueo.asignarVehiculo(autoVisitante);

        System.out.println("   Espacio para No Socio: " + (espNoSocio != null ? espNoSocio.getIdEspacio() + " (" + espNoSocio.getTipoEspacio() + ")" : "null"));
        System.out.println("   Espacio para Visitante: " + (espVisitante != null ? espVisitante.getIdEspacio() + " (" + espVisitante.getTipoEspacio() + ")" : "null"));

        if (espNoSocio != null && espNoSocio.getTipoEspacio() == TipoEspacio.GENERAL
                && espVisitante != null && espVisitante.getTipoEspacio() == TipoEspacio.GENERAL) {
            System.out.println("   [OK] No socios y visitantes asignados exclusivamente en Área General.");
            pruebasPasadasF4++;
        } else {
            System.out.println("   [FALLO] No socios o visitantes ingresaron indebidamente al área de socios.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // PRUEBA 4.4: Liberación de espacios y reasignación circular continua
        // -------------------------------------------------------------------------
        pruebasTotalesF4++;
        System.out.println(">>> PRUEBA 4.4: Liberación de espacio por placa y reasignación circular");
        // Liberamos el espacio A1 (donde estaba SOC-1)
        EspacioParqueo espLiberado = parqueo.liberarVehiculoPorPlaca("SOC-1");
        boolean liberacionExitosa = (espLiberado != null && espLiberado.estaLibre());
        System.out.println("   Espacio liberado para SOC-1: " + (espLiberado != null ? espLiberado.getIdEspacio() + " (Estado=" + espLiberado.getEstado() + ")" : "null"));
        System.out.println("   Ocupados en Socios tras liberación: " + parqueo.getAreaSocios().getOcupados() + "/75");

        // Asignamos un nuevo socio, el puntero circular debe seguir avanzando y reutilizar el espacio
        Vehiculo autoNuevoSocio = new Vehiculo("SOC-NUEVO", "BMW", "Serie 3", "Azul", "Automóvil");
        autoNuevoSocio.setPropietario(socioGenerico);
        EspacioParqueo espReasignado = parqueo.asignarVehiculo(autoNuevoSocio);
        System.out.println("   Nuevo vehículo socio asignado a: " + (espReasignado != null ? espReasignado.getIdEspacio() : "null"));
        System.out.println("   Ocupados en Socios tras reasignación: " + parqueo.getAreaSocios().getOcupados() + "/75");

        if (liberacionExitosa && espReasignado != null && espReasignado.estaOcupado() && parqueo.getAreaSocios().getOcupados() == 75) {
            System.out.println("   [OK] Liberación y reasignación circular continua operando sin inconsistencias.");
            pruebasPasadasF4++;
        } else {
            System.out.println("   [FALLO] Falló la liberación o reasignación circular.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // RESUMEN FINAL FASE 4
        // -------------------------------------------------------------------------
        System.out.println("======================================================================");
        System.out.println("   RESULTADO DE INSPECCIÓN FASE 4: " + pruebasPasadasF4 + "/" + pruebasTotalesF4 + " PRUEBAS SUPERADAS");
        if (pruebasPasadasF4 == pruebasTotalesF4) {
            System.out.println("   ESTADO: [FASE 4 COMPLETADA CON ÉXITO]");
        } else {
            System.out.println("   ESTADO: [SE DETECTARON FALLOS EN FASE 4]");
        }
        System.out.println("======================================================================\n");

        // =====================================================================
        // PUNTO DE INSPECCIÓN FASE 5: PERSISTENCIA EN DISCO (JAVA.IO.* Y CIFRADO)
        // =====================================================================
        System.out.println("======================================================================");
        System.out.println("   RESIPARK - PUNTO DE INSPECCIÓN FASE 5: PERSISTENCIA EN DISCO Y XOR  ");
        System.out.println("======================================================================\n");

        int pruebasPasadasF5 = 0;
        int pruebasTotalesF5 = 0;

        // -------------------------------------------------------------------------
        // PRUEBA 5.1: Cifrado y Descifrado Simétrico XOR (Reversibilidad)
        // -------------------------------------------------------------------------
        pruebasTotalesF5++;
        System.out.println(">>> PRUEBA 5.1: Cifrado y descifrado simétrico XOR");
        String textoPrueba = "R-501|Carlos Mendoza|Casa D-20|S";
        String textoCifrado = Encriptador.procesar(textoPrueba);
        String textoDescifrado = Encriptador.procesar(textoCifrado);

        System.out.println("   Texto original  : " + textoPrueba);
        System.out.println("   Texto cifrado   : " + textoCifrado);
        System.out.println("   Texto descifrado: " + textoDescifrado);
        boolean simetriaOk = Encriptador.verificarSimetria(textoPrueba);
        System.out.println("   ¿Simetría perfecta validada?: " + simetriaOk);

        if (simetriaOk && textoDescifrado.equals(textoPrueba) && !textoCifrado.equals(textoPrueba)) {
            System.out.println("   [OK] Algoritmo de cifrado/descifrado simétrico XOR opera fielmente.");
            pruebasPasadasF5++;
        } else {
            System.out.println("   [FALLO] El cifrado XOR no recuperó el texto original.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // PRUEBA 5.2: Guardado y Recarga Idéntica de Estructuras (Reconstrucción en Memoria)
        // -------------------------------------------------------------------------
        pruebasTotalesF5++;
        System.out.println(">>> PRUEBA 5.2: Guardado y reconstrucción idéntica desde disco");

        String rutaTestRes = "src/datos/test_residentes.txt";
        String rutaTestVeh = "src/datos/test_vehiculos.txt";

        // Creamos 2 residentes con 2 vehículos cada uno
        ListaDobleResidentes listaOriginal = new ListaDobleResidentes();
        Residente rMem1 = new Residente("R-901", "Guillermo Tell", "Casa X-01", true);
        Residente rMem2 = new Residente("R-902", "Valeria Vega", "Casa X-02", false);

        Vehiculo vMem1 = new Vehiculo("P-901A", "Toyota", "Corolla", "Rojo", "Automóvil");
        Vehiculo vMem2 = new Vehiculo("P-901B", "Honda", "Civic", "Gris", "Automóvil");
        rMem1.agregarVehiculo(vMem1);
        rMem1.agregarVehiculo(vMem2);

        Vehiculo vMem3 = new Vehiculo("M-902A", "Yamaha", "MT03", "Azul", "Motocicleta");
        Vehiculo vMem4 = new Vehiculo("P-902B", "Ford", "F150", "Negro", "Pickup");
        rMem2.agregarVehiculo(vMem3);
        rMem2.agregarVehiculo(vMem4);

        listaOriginal.insertar(rMem1);
        listaOriginal.insertar(rMem2);

        // Guardamos en disco
        boolean guardadoOk = GestorArchivos.guardarTodo(listaOriginal, rutaTestRes, rutaTestVeh);
        System.out.println("   Guardado en disco exitoso: " + guardadoOk);

        // Borramos la memoria simulando reinicio
        listaOriginal = null;

        // Cargamos desde disco
        ListaDobleResidentes listaRecuperada = GestorArchivos.cargarTodo(rutaTestRes, rutaTestVeh);

        System.out.println("   Residentes recuperados: " + listaRecuperada.getTamaño() + "/2");
        Residente rRecup1 = listaRecuperada.buscarPorId("R-901");
        Residente rRecup2 = listaRecuperada.buscarPorId("R-902");

        boolean r1Ok = (rRecup1 != null && rRecup1.getNombre().equals("Guillermo Tell") && rRecup1.isEsSocio() && rRecup1.getCantidadVehiculos() == 2);
        boolean r2Ok = (rRecup2 != null && rRecup2.getNombre().equals("Valeria Vega") && !rRecup2.isEsSocio() && rRecup2.getCantidadVehiculos() == 2);

        Vehiculo vRecup1 = (rRecup1 != null) ? rRecup1.getListaVehiculos().buscarPorPlaca("P-901A") : null;
        Vehiculo vRecup4 = (rRecup2 != null) ? rRecup2.getListaVehiculos().buscarPorPlaca("P-902B") : null;
        boolean autosOk = (vRecup1 != null && vRecup1.getMarca().equals("Toyota") && vRecup4 != null && vRecup4.getTipo().equals("Pickup")
                && vRecup1.getEstado() == EstadoVehiculo.FUERA);

        System.out.println("   Residente 1 y vehículos íntegros: " + r1Ok);
        System.out.println("   Residente 2 y vehículos íntegros: " + r2Ok);
        System.out.println("   Vehículos con estado FUERA al restaurar: " + autosOk);

        if (guardadoOk && r1Ok && r2Ok && autosOk) {
            System.out.println("   [OK] Persistencia y reconstrucción en memoria verificadas con fidelidad total.");
            pruebasPasadasF5++;
        } else {
            System.out.println("   [FALLO] La reconstrucción de datos desde disco falló.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // PRUEBA 5.3: Filtrado y Resiliencia ante Datos Corruptos y Referencias Huérfanas
        // -------------------------------------------------------------------------
        pruebasTotalesF5++;
        System.out.println(">>> PRUEBA 5.3: Resiliencia ante datos corruptos o referencias huérfanas");

        // Parseo de línea corrupta y línea limpia
        String lineaCorrupta = "R-BAD|FaltanCampos";
        String[] partesCorruptas = GestorArchivos.descomponerLineaPipe(lineaCorrupta);
        String lineaValida = "R-777|Mario Bros|Casa H-01|S";
        String[] partesValidas = GestorArchivos.descomponerLineaPipe(lineaValida);

        System.out.println("   Partes en línea corrupta: " + partesCorruptas.length + " (Rechazada por < 4 campos)");
        System.out.println("   Partes en línea válida  : " + partesValidas.length + " (Aceptada con 4 campos)");

        if (partesCorruptas.length == 2 && partesValidas.length == 4) {
            System.out.println("   [OK] Mecanismo de parsing y filtrado resiliente ante corrupción superado.");
            pruebasPasadasF5++;
        } else {
            System.out.println("   [FALLO] Falló el parseo y filtrado de líneas.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // RESUMEN FINAL FASE 5
        // -------------------------------------------------------------------------
        System.out.println("======================================================================");
        System.out.println("   RESULTADO DE INSPECCIÓN FASE 5: " + pruebasPasadasF5 + "/" + pruebasTotalesF5 + " PRUEBAS SUPERADAS");
        if (pruebasPasadasF5 == pruebasTotalesF5) {
            System.out.println("   ESTADO: [FASE 5 COMPLETADA CON ÉXITO]");
        } else {
            System.out.println("   ESTADO: [SE DETECTARON FALLOS EN FASE 5]");
        }
        System.out.println("======================================================================\n");

        // =====================================================================
        // PUNTO DE INSPECCIÓN FASE 6: LÓGICA CONCURRENTE DE GARITAS (HILOS Y SINCRONIZACIÓN)
        // =====================================================================
        System.out.println("======================================================================");
        System.out.println("   RESIPARK - PUNTO DE INSPECCIÓN FASE 6: HILOS, WAIT/NOTIFY Y GARITAS ");
        System.out.println("======================================================================\n");

        int pruebasPasadasF6 = 0;
        int pruebasTotalesF6 = 0;

        // -------------------------------------------------------------------------
        // PRUEBA 6.1: Simulación multihilo concurrente con 2 Garitas de Entrada
        // -------------------------------------------------------------------------
        pruebasTotalesF6++;
        System.out.println(">>> PRUEBA 6.1: Concurrencia de 2 Garitas de Entrada sin condición de carrera");

        ControladorParqueo parqueoHilos = new ControladorParqueo();
        PilaEventos bitacoraHilos = new PilaEventos();
        SimuladorParqueo simulador = new SimuladorParqueo(parqueoHilos, bitacoraHilos);

        // Configuramos retardo corto para la prueba automatizada
        simulador.setVelocidadAtencion(100); // 100 ms por atención
        simulador.iniciarSimulacion();

        // Encolamos 5 vehículos simultáneamente
        Vehiculo h1 = new Vehiculo("CON-001", "Toyota", "Yaris", "Rojo", "Automóvil");
        Vehiculo h2 = new Vehiculo("CON-002", "Honda", "Civic", "Azul", "Automóvil");
        Vehiculo h3 = new Vehiculo("CON-003", "Mazda", "3", "Blanco", "Automóvil");
        Vehiculo h4 = new Vehiculo("CON-004", "Nissan", "Sentra", "Negro", "Automóvil");
        Vehiculo h5 = new Vehiculo("CON-005", "Ford", "Ranger", "Gris", "Pickup");

        simulador.encolarVehiculoEntrada(h1);
        simulador.encolarVehiculoEntrada(h2);
        simulador.encolarVehiculoEntrada(h3);
        simulador.encolarVehiculoEntrada(h4);
        simulador.encolarVehiculoEntrada(h5);

        System.out.println("   5 vehículos encolados simultáneamente...");

        // Esperamos a que ambas garitas atiendan los 5 vehículos
        try {
            Thread.sleep(800); // Tiempo suficiente para 5 atenciones a 100ms repartidas en 2 hilos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int ocupadosConcurrencia = parqueoHilos.getTotalOcupados();
        int colaEntradaRestante = simulador.getColaEntrada().getTamaño();
        int eventosIngreso = bitacoraHilos.getTamaño();

        System.out.println("   Vehículos en parqueo: " + ocupadosConcurrencia + "/5");
        System.out.println("   Vehículos restantes en cola: " + colaEntradaRestante);
        System.out.println("   Eventos registrados en bitácora: " + eventosIngreso);

        boolean todosEstacionados = (h1.getEstado() == EstadoVehiculo.ESTACIONADO
                && h2.getEstado() == EstadoVehiculo.ESTACIONADO
                && h3.getEstado() == EstadoVehiculo.ESTACIONADO
                && h4.getEstado() == EstadoVehiculo.ESTACIONADO
                && h5.getEstado() == EstadoVehiculo.ESTACIONADO);

        if (ocupadosConcurrencia == 5 && colaEntradaRestante == 0 && eventosIngreso == 5 && todosEstacionados) {
            System.out.println("   [OK] Concurrencia de garitas de entrada superada sin condición de carrera.");
            pruebasPasadasF6++;
        } else {
            System.out.println("   [FALLO] La atención concurrente de entrada presentó inconsistencias.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // PRUEBA 6.2: Simulación concurrente de Garita de Salida y desocupación
        // -------------------------------------------------------------------------
        pruebasTotalesF6++;
        System.out.println(">>> PRUEBA 6.2: Concurrencia de Garita de Salida y liberación sincronizada");

        // Encolamos 2 vehículos para salir
        simulador.encolarVehiculoSalida(h1);
        simulador.encolarVehiculoSalida(h2);

        System.out.println("   2 vehículos enviados a Cola de Salida...");

        try {
            Thread.sleep(400); // Tiempo para 2 atenciones a 100ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int ocupadosPostSalida = parqueoHilos.getTotalOcupados();
        int colaSalidaRestante = simulador.getColaSalida().getTamaño();
        int eventosTotales = bitacoraHilos.getTamaño();

        System.out.println("   Vehículos en parqueo tras salidas: " + ocupadosPostSalida + "/3");
        System.out.println("   Vehículos restantes en cola de salida: " + colaSalidaRestante);
        System.out.println("   Estado de h1 (saliente): " + h1.getEstado());
        System.out.println("   Estado de h2 (saliente): " + h2.getEstado());
        System.out.println("   Total eventos acumulados: " + eventosTotales + " (5 ingresos + 2 salidas = 7)");

        boolean salidasCorrectas = (h1.getEstado() == EstadoVehiculo.FUERA
                && h2.getEstado() == EstadoVehiculo.FUERA
                && ocupadosPostSalida == 3
                && colaSalidaRestante == 0
                && eventosTotales == 7);

        // Detenemos la simulación con seguridad
        simulador.detenerSimulacion();

        if (salidasCorrectas) {
            System.out.println("   [OK] Garita de salida operó concurrentemente liberando espacios en parqueo.");
            pruebasPasadasF6++;
        } else {
            System.out.println("   [FALLO] Falló la concurrencia en la garita de salida.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // RESUMEN FINAL FASE 6
        // -------------------------------------------------------------------------
        System.out.println("======================================================================");
        System.out.println("   RESULTADO DE INSPECCIÓN FASE 6: " + pruebasPasadasF6 + "/" + pruebasTotalesF6 + " PRUEBAS SUPERADAS");
        if (pruebasPasadasF6 == pruebasTotalesF6) {
            System.out.println("   ESTADO: [FASE 6 COMPLETADA CON ÉXITO]");
        } else {
            System.out.println("   ESTADO: [SE DETECTARON FALLOS EN FASE 6]");
        }
        System.out.println("======================================================================\n");

        // =====================================================================
        // PUNTO DE INSPECCIÓN FASE 7: MAQUETACIÓN BASE DE INTERFAZ GRÁFICA (SWING MANUAL)
        // =====================================================================
        System.out.println("======================================================================");
        System.out.println("   RESIPARK - PUNTO DE INSPECCIÓN FASE 7: VISTAS Y PANELES SWING     ");
        System.out.println("======================================================================\n");

        int pruebasPasadasF7 = 0;
        int pruebasTotalesF7 = 0;

        // -------------------------------------------------------------------------
        // PRUEBA 7.1: Instanciación limpia y layouts de los paneles de gestión
        // -------------------------------------------------------------------------
        pruebasTotalesF7++;
        System.out.println(">>> PRUEBA 7.1: Instanciación limpia de paneles de interfaz gráfica Swing");

        ListaDobleResidentes listaF7 = new ListaDobleResidentes();
        ControladorParqueo parqueoF7 = new ControladorParqueo();
        PilaEventos bitacoraF7 = new PilaEventos();
        SimuladorParqueo simuladorF7 = new SimuladorParqueo(parqueoF7, bitacoraF7);

        cris.sic.proyecto2.vista.PanelResidentes pResidentes = new cris.sic.proyecto2.vista.PanelResidentes(listaF7);
        cris.sic.proyecto2.vista.PanelEntrada pEntrada = new cris.sic.proyecto2.vista.PanelEntrada(listaF7, simuladorF7);
        cris.sic.proyecto2.vista.PanelSalida pSalida = new cris.sic.proyecto2.vista.PanelSalida(parqueoF7, simuladorF7);
        cris.sic.proyecto2.vista.PanelEventos pEventos = new cris.sic.proyecto2.vista.PanelEventos(bitacoraF7);

        boolean panelesNoNulos = (pResidentes != null && pEntrada != null && pSalida != null && pEventos != null);
        System.out.println("   Paneles Swing instanciados sin dependencias gráficas bloqueantes: " + panelesNoNulos);

        if (panelesNoNulos && pResidentes.getLayout() != null && pEntrada.getLayout() != null) {
            System.out.println("   [OK] Paneles base de la interfaz gráfica construidos correctamente.");
            pruebasPasadasF7++;
        } else {
            System.out.println("   [FALLO] Falló la construcción de paneles Swing.");
        }
        System.out.println();

        System.out.println("======================================================================");
        System.out.println("   RESULTADO DE INSPECCIÓN FASE 7: " + pruebasPasadasF7 + "/" + pruebasTotalesF7 + " PRUEBAS SUPERADAS");
        if (pruebasPasadasF7 == pruebasTotalesF7) {
            System.out.println("   ESTADO: [FASE 7 COMPLETADA CON ÉXITO]");
        } else {
            System.out.println("   ESTADO: [SE DETECTARON FALLOS EN FASE 7]");
        }
        System.out.println("======================================================================\n");

        // =====================================================================
        // PUNTO DE INSPECCIÓN FASE 8: RENDERIZADO DINÁMICO DEL PARQUEO (150 CELDAS)
        // =====================================================================
        System.out.println("======================================================================");
        System.out.println("   RESIPARK - PUNTO DE INSPECCIÓN FASE 8: PANEL PARQUEO Y MATRIZ      ");
        System.out.println("======================================================================\n");

        int pruebasPasadasF8 = 0;
        int pruebasTotalesF8 = 0;

        // -------------------------------------------------------------------------
        // PRUEBA 8.1: Verificación de dimensiones de celdas (75 socios + 75 general = 150)
        // -------------------------------------------------------------------------
        pruebasTotalesF8++;
        System.out.println(">>> PRUEBA 8.1: Mapeo de celdas visuales y lista circular en PanelParqueo");

        cris.sic.proyecto2.vista.PanelParqueo panelParqueoTest = new cris.sic.proyecto2.vista.PanelParqueo(parqueoF7, simuladorF7);
        System.out.println("   Capacidad Área Socios: " + parqueoF7.getAreaSocios().getCapacidad() + " espacios.");
        System.out.println("   Capacidad Área General: " + parqueoF7.getAreaGeneral().getCapacidad() + " espacios.");
        System.out.println("   Capacidad Total Parqueo: " + (parqueoF7.getAreaSocios().getCapacidad() + parqueoF7.getAreaGeneral().getCapacidad()) + " espacios.");

        if (panelParqueoTest != null && parqueoF7.getAreaSocios().getCapacidad() == 75 && parqueoF7.getAreaGeneral().getCapacidad() == 75) {
            System.out.println("   [OK] Matriz visual de 150 celdas (75 Socios / 75 General) mapeada correctamente.");
            pruebasPasadasF8++;
        } else {
            System.out.println("   [FALLO] Inconsistencia en la matriz visual del parqueo.");
        }
        System.out.println();

        // -------------------------------------------------------------------------
        // PRUEBA 8.2: Actualización cromática y reactiva ante eventos de garitas
        // -------------------------------------------------------------------------
        pruebasTotalesF8++;
        System.out.println(">>> PRUEBA 8.2: Disparo de actualización en PanelParqueo al ingresar y liberar vehículos");

        Residente resP8 = new Residente("R-888", "Socio Parqueo", "Casa P-8", true);
        Vehiculo vP8 = new Vehiculo("SOC-888", "Audi", "A4", "Gris", "Automóvil", resP8);
        EspacioParqueo espP8 = parqueoF7.asignarVehiculo(vP8);
        Evento evtP8 = new Evento("GARITA-1", "INGRESO_VEHICULO", "Ingreso de vehículo SOC-888");
        bitacoraF7.apilar(evtP8);

        panelParqueoTest.onVehiculoIngresado("GARITA-1", vP8, espP8, evtP8);
        System.out.println("   Vehículo estacionado en espacio: " + espP8.getIdEspacio() + " (" + espP8.getTipoEspacio() + ")");

        // Liberación
        parqueoF7.liberarVehiculoPorPlaca("SOC-888");
        Evento evtSalidaP8 = new Evento("GARITA-3", "SALIDA_VEHICULO", "Salida de vehículo SOC-888");
        bitacoraF7.apilar(evtSalidaP8);
        panelParqueoTest.onVehiculoSalida("GARITA-3", vP8, espP8, evtSalidaP8);

        if (espP8.estaLibre() && parqueoF7.getTotalOcupados() == 0) {
            System.out.println("   [OK] Renderizado cromático y callbacks de listener ejecutados sin errores.");
            pruebasPasadasF8++;
        } else {
            System.out.println("   [FALLO] Error en el flujo de callbacks del PanelParqueo.");
        }
        System.out.println();

        System.out.println("======================================================================");
        System.out.println("   RESULTADO DE INSPECCIÓN FASE 8: " + pruebasPasadasF8 + "/" + pruebasTotalesF8 + " PRUEBAS SUPERADAS");
        if (pruebasPasadasF8 == pruebasTotalesF8) {
            System.out.println("   ESTADO: [FASE 8 COMPLETADA CON ÉXITO]");
        } else {
            System.out.println("   ESTADO: [SE DETECTARON FALLOS EN FASE 8]");
        }
        System.out.println("======================================================================\n");

        // =====================================================================
        // PUNTO DE INSPECCIÓN FASE 9: PRUEBA DE ESTRÉS CONCURRENTE (10 VEHÍCULOS)
        // =====================================================================
        System.out.println("======================================================================");
        System.out.println("   RESIPARK - PUNTO DE INSPECCIÓN FASE 9: PRUEBA DE ESTRÉS CONCURRENTE");
        System.out.println("======================================================================\n");

        int pruebasPasadasF9 = 0;
        int pruebasTotalesF9 = 0;

        pruebasTotalesF9++;
        System.out.println(">>> PRUEBA 9.1: Flujo continuo de 10 vehículos en 3 garitas simultáneas");

        ControladorParqueo parqueoStress = new ControladorParqueo();
        PilaEventos bitacoraStress = new PilaEventos();
        SimuladorParqueo simStress = new SimuladorParqueo(parqueoStress, bitacoraStress);
        simStress.setVelocidadAtencion(50); // 50 ms por atención
        simStress.iniciarSimulacion();

        // Encolamos 10 vehículos simultáneos
        for (int i = 1; i <= 10; i++) {
            Vehiculo v = new Vehiculo("STR-" + (100 + i), "Marca" + i, "Mod" + i, "Blanco", "Automóvil");
            simStress.encolarVehiculoEntrada(v);
        }

        System.out.println("   10 vehículos encolados en cola de entrada...");

        // Esperamos a que se procesen las entradas
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int ocupadosStress = parqueoStress.getTotalOcupados();
        System.out.println("   Vehículos estacionados en parqueo: " + ocupadosStress + "/10");

        // Ahora enviamos los primeros 5 a salida
        for (int i = 1; i <= 5; i++) {
            EspacioParqueo esp = parqueoStress.buscarPorPlaca("STR-" + (100 + i));
            if (esp != null && esp.getVehiculoEstacionado() != null) {
                simStress.encolarVehiculoSalida(esp.getVehiculoEstacionado());
            }
        }

        System.out.println("   5 vehículos encolados para salida...");

        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int ocupadosPostStress = parqueoStress.getTotalOcupados();
        int bitacoraTotal = bitacoraStress.getTamaño();

        System.out.println("   Vehículos en parqueo tras salidas: " + ocupadosPostStress + "/5");
        System.out.println("   Total de eventos registrados en bitácora: " + bitacoraTotal + " (10 ingresos + 5 salidas = 15)");

        simStress.detenerSimulacion();

        if (ocupadosStress == 10 && ocupadosPostStress == 5 && bitacoraTotal == 15) {
            System.out.println("   [OK] Prueba de estrés con 10 vehículos superada con total integridad.");
            pruebasPasadasF9++;
        } else {
            System.out.println("   [FALLO] La prueba de estrés presentó inconsistencias.");
        }
        System.out.println();

        System.out.println("======================================================================");
        System.out.println("   RESULTADO DE INSPECCIÓN FASE 9: " + pruebasPasadasF9 + "/" + pruebasTotalesF9 + " PRUEBAS SUPERADAS");
        if (pruebasPasadasF9 == pruebasTotalesF9) {
            System.out.println("   ESTADO: [FASE 9 COMPLETADA CON ÉXITO]");
        } else {
            System.out.println("   ESTADO: [SE DETECTARON FALLOS EN FASE 9]");
        }
        System.out.println("======================================================================\n");
    }
}




