package src.cualquierafd;

import src.datastructures.BinarySearch;

public class Procesador {
    private final char[] ALFABETO;
    private final int[][] TABLEAU;

    public Procesador(char[] alfabeto, int[][] tableau) {
        this.ALFABETO = alfabeto;
        this.TABLEAU = tableau;
    }

    public boolean evaluarLexema(String lexema, int nodoInicial) {
        char[] symbols = lexema.toCharArray();
        int lexemaLength = symbols.length;
        int node = nodoInicial;
        int idxSymbol;
        boolean fdc = false;
        int i = 0;

        do {
            idxSymbol = BinarySearch.binarySearchOfIndex(ALFABETO,0,ALFABETO.length-1,symbols[i]);
            node = TABLEAU[node][idxSymbol];

            if(i == lexemaLength-1)
                fdc = TABLEAU[node][TABLEAU[0].length-1] == 1 ? true : false;

            i++;

        } while(i < lexemaLength);
        return fdc;
    }
}
