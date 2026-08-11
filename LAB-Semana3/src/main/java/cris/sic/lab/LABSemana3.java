package cris.sic.lab;

public class LABSemana3 {

    public static void main(String[] args) {
        int nota = 60;
        if (nota >= 90) {
            System.out.print("El alumno es sobresaliente.");
        }else if(nota >= 70){
            System.out.print("Aprobado.");
        }else{
            System.out.print("Reprobado");
        }
        
        String estado = (nota >= 70) ? "Aprobado" : "Reprobado";
        System.out.print("Estado: " + estado);
        
        int diaSemana = 3;
        switch (diaSemana) {
            case 1 -> System.out.println("Lunes");
            case 2 -> System.out.println("Martes");
            case 3 -> System.out.println("Miercoles");
            case 4 -> System.out.println("Jueves");
            case 5 -> System.out.println("Viernes");
            default -> System.out.println("Otro dia");
        }
    }
}
