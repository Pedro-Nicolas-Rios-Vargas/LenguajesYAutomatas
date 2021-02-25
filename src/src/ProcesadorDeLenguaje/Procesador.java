/*
*   Seleccionar una ER, o bien crear una, y desarrollar un programa que admita o rechace una
*   cadena introducida por teclado para dicha expresión.
*
*   Probar el programa con a menos tres cadenas aceptadas y tres rechazadas.
*
*   ER elegida:
*       (ab)^+a U ba^+b
* */

package src.ProcesadorDeLenguaje;

public class Procesador {
    private int[][] tableau;

    public Procesador() {
        tableauInit();
    }

    private void tableauInit() {
        tableau = new int[][]
                {
               //a,b,FDC
                {1,4,-1},   // Nodo q0
                {7,2,-1},   // Nodo q1
                {3,7,-1},   // Nodo q2
                {7,2, 1},   // Nodo q3
                {5,7,-1},   // Nodo q4
                {5,6,-1},   // Nodo q5
                {7,7, 1},   // Nodo q6
                {7,7,-1},   // Nodo q7
                };
    }

    public boolean evaluarLexema(String lexema) {
        char[] string = lexema.toCharArray();
        int length = string.length;
        int nodo = 0;
        int a,b;
        boolean fdc = false;
        int i = 0;
        do {
            a = tableau[nodo][0];
            b = tableau[nodo][1];

            if(string[i] == 'a') {
                nodo = a;
            }
            else if(string[i] == 'b') {
                nodo = b;
            }

            fdc = tableau[nodo][2] == -1 ? false : true;

            i++;
        } while(i != length);
        return fdc;
    }


}
