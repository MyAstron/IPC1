package cris.sic;


import java.util.ArrayList;
import java.util.Scanner;
/**
 *
 * @author cris_sic
 */
public class Ejercicio25agosto {
    public static void main(String[] args) {
        ArrayList<String> lsitaNombres = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        int n = 3;
        
        for (int i = 0; i<n;i++){
            System.out.print("Ingresa el nombre "+ i +"\': ");
            String nommbre = sc.nextLine();
            lsitaNombres.add(nommbre);
        }
        System.out.println("Lista "+lsitaNombres);
    } 
}
