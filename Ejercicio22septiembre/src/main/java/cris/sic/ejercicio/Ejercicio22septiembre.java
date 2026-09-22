/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package cris.sic.ejercicio;

import java.util.LinkedList;
import java.util.Queue;

// Clase auxiliar para restringir el uso únicamente a los métodos de una Cola
class ColaAuxiliar {
    private Queue<Integer> cola;
    private int capacidad;

    public ColaAuxiliar(int capacidad) {
        this.cola = new LinkedList<>();
        this.capacidad = capacidad;
    }

    public void encolar(int x) {
        cola.add(x);
    }

    public int desencolar() {
        return cola.poll();
    }

    public boolean estaVacia() {
        return cola.isEmpty();
    }

    public int obtenerTamano() {
        return cola.size();
    }
}

// Clase Pila implementada utilizando únicamente 2 colas
class Pila {
    private ColaAuxiliar cola1;
    private ColaAuxiliar cola2;
    private int capacidad;

    public Pila(int capacidad) {
        this.capacidad = capacidad;
        this.cola1 = new ColaAuxiliar(capacidad);
        this.cola2 = new ColaAuxiliar(capacidad);
    }

    // Inserta un elemento en la pila (método push)
    public void push(int x) {
        if (pilaLlena()) {
            System.out.println("Error: La pila está llena (Overflow). No se puede insertar " + x);
            return;
        }

        // 1. Encolar el nuevo elemento en cola2
        cola2.encolar(x);

        // 2. Mover todos los elementos de cola1 a cola2
        while (!cola1.estaVacia()) {
            cola2.encolar(cola1.desencolar());
        }

        // 3. Intercambiar referencias de cola1 y cola2
        ColaAuxiliar temp = cola1;
        cola1 = cola2;
        cola2 = temp;

        System.out.println("Push(" + x + ") realizado con éxito.");
    }

    // Remueve y retorna el elemento superior de la pila (método pop)
    public int pop() {
        if (pilaVacia()) {
            System.out.println("Error: La pila está vacía (Underflow).");
            return -1;
        }
        return cola1.desencolar();
    }

    // Verifica si la pila está vacía
    public boolean pilaVacia() {
        return cola1.estaVacia();
    }

    // Verifica si la pila está llena
    public boolean pilaLlena() {
        return cola1.obtenerTamano() == capacidad;
    }
}

/**
 *
 * @author cris_sic
 */
public class Ejercicio22septiembre {

    public static void main(String[] args) {
        // Crear una pila con capacidad para 3 elementos
        Pila pila = new Pila(3);

        System.out.println("--- PRUEBAS DE PUSH ---");
        pila.push(10);
        pila.push(20);
        pila.push(30);

        // Intento de insertar en una pila que ya está llena
        pila.push(40);

        System.out.println("\n¿Pila llena?: " + pila.pilaLlena());

        System.out.println("\n--- PRUEBAS DE POP ---");
        System.out.println("Elemento extraído (pop): " + pila.pop()); // Imprime 30
        System.out.println("Elemento extraído (pop): " + pila.pop()); // Imprime 20
        System.out.println("Elemento extraído (pop): " + pila.pop()); // Imprime 10

        // Intento de extraer de una pila que ya está vacía
        System.out.println("Elemento extraído (pop): " + pila.pop());

        System.out.println("\n¿Pila vacía?: " + pila.pilaVacia());
    }
}