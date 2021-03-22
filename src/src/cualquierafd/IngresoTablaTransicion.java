/*
    Desarrollar un programa que admita una tabla de transición de algún AFD sobre algún alfabeto Σ, así como algunas cadenas
     para verificar si son admitidas por el lenguaje o no.

    Probar el programa con tres cadenas aceptadas y tres rechazadas.
* */

package src.cualquierafd;

import java.util.Formatter;

import src.datastructures.Sort;
import src.datastructures.BinarySearch;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;

public class IngresoTablaTransicion {
    private int[][] tablaTransiciones;
    private char[] alfabeto;
    private int numSimbols;
    private int numNodos;
    private int edoInicial;
    private BufferedWriter bw;
    private BufferedReader br;

    public IngresoTablaTransicion() {
        bw = new BufferedWriter(new OutputStreamWriter(System.out));
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    public void ingresoTablaTransiciones() {

        try {
            ingresoNumeroDeSimbolos();
            ingresoSimbolos();

            ingresoNumeroDeEstados();
            ingresoEstadoInicial();
            ingresoNodos();

        } catch (IOException ioE) {
            ioE.printStackTrace();
        }
    }

    private void ingresoNumeroDeSimbolos() throws IOException {
        String line;
        Integer val;
        boolean isDone = false;
        do {
            bw.write("Ingrese el numero de simbolos que contiene el alfabeto: ");
            bw.flush();
            line = br.readLine();
            val = evalNumero(line); //Validacion
            if (val == null)
                continue;
            if (val <= 0) {
                bw.write("El alfabeto no puede tener menos numeros que 0." +
                        " Por favor ingrese de nuevo el numero.\n\n");
                bw.flush();
            } else {
                numSimbols = val;
                isDone = true;
            }
        } while (!isDone);
    }

    private void ingresoNumeroDeEstados() throws IOException {
        String line;
        Integer val;
        boolean isDone = false;
        do {
            bw.write("Ingrese el numero de estados: ");
            bw.flush();
            line = br.readLine();
            val = evalNumero(line); //Validacion
            if (val == null)
                continue;
            if (val <= 0) {
                bw.write("Se necesita por lo menos un estado para poder verificar cadenas." +
                        " Por favor ingrese de nuevo el numero.\n\n");
                bw.flush();
            } else {
                numNodos = val;
                isDone = true;
            }

        } while (!isDone);
    }

    @SuppressWarnings("ConstantConditions")
    private void ingresoEstadoInicial() throws IOException {
        String line;
        Integer val;
        boolean nodoValido;
        do {
            bw.write("Ingrese el numero del estado inicial: ");
            bw.flush();
            line = br.readLine();
            val = evalNumero(line);
            nodoValido = evalNodo(val);
            if(nodoValido)
                edoInicial = val;

        } while(!nodoValido);

    }

    private void ingresoSimbolos() throws IOException {
        alfabeto = new char[numSimbols];
        String line;
        int count = 0;
        boolean simboloValido;
        while(count < numSimbols) {
            bw.write(new Formatter().format("Ingrese simbolo %d: ", count + 1).toString());
            bw.flush();
            line = br.readLine();
            simboloValido = evalSimbolo(line, count);
            if(simboloValido)
                count++;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void ingresoNodos() throws IOException {
        tablaTransiciones = new int[numNodos][alfabeto.length + 1];
        String line;
        Integer eval;
        int countEstado = 0;
        int countSimbol;
        boolean nodoValido;
        boolean fdcValido;

        while(countEstado < numNodos) {
            bw.write("======================================================\n\n");
            bw.write(new Formatter().format("\t\t\tTransiciones del nodo q%d\n\n",countEstado).toString());
            bw.write("======================================================\n\n");
            bw.flush();
            countSimbol = 0;
            while(countSimbol < alfabeto.length) {
                bw.write(new Formatter().format("Ingrese el numero del nodo que lleva al " +
                                "simbolo \"%c\": ",alfabeto[countSimbol]).toString());
                bw.flush();
                line = br.readLine();
                eval = evalNumero(line);
                nodoValido = evalNodo(eval);
                 if(nodoValido) {
                    tablaTransiciones[countEstado][countSimbol] = eval;
                    countSimbol++;
                 }
            }
            while(true) {
                bw.write(new Formatter().format("El nodo \"q%d\" es de aceptacion? \n",
                        countEstado).toString());
                bw.write("  0. No\n");
                bw.write("  1. Si\n");
                bw.write("Ingrese la seleccion [0..1] ");
                bw.flush();
                line = br.readLine();
                eval = evalNumero(line);
                fdcValido = evalFDC(eval);
                if(fdcValido) {
                    tablaTransiciones[countEstado][countSimbol] = eval == 0 ? -1 : 1;
                    countEstado++;
                    break;
                }
            }
        }
        printTableau();

    }

    private Integer evalNumero(String line) throws IOException {
        int numero;
        try {
            numero = Integer.parseInt(line);
        } catch(NumberFormatException nfE) {
            bw.write("El valor que ingreso no es un numero. Por favor vuelva a intentarlo\n\n");
            bw.flush();
            return null;
        }
        return numero;
    }

    private boolean evalSimbolo(String line, int index) throws IOException {
        if(line.length() > 1) {
            bw.write("Ingreso más de un simbolo. Por favor intente de nuevo.\n\n");
            bw.flush();
            return false;
        }
        char simbolo = line.charAt(0);

        if(index == 0) {
            alfabeto[index] = simbolo;
        } else if(!BinarySearch.binarySearch(alfabeto, 0, alfabeto.length-1, simbolo)){
            if (alfabeto[index] != 0) {
                index = 0;
                while (alfabeto[index] != 0) index++;
            }
            alfabeto[index] = simbolo;
        } else {
            bw.write("El simbolo \"" + simbolo + "\" ya fue ingresado. Porfavor ingrese otro\n\n");
            bw.flush();
            return false;
        }

        Sort.quick(alfabeto,0, alfabeto.length-1);
        return true;
    }

    private boolean evalNodo(Integer eval) throws IOException {
        if(eval != null && (eval >= 0 && eval < numNodos)) {
            return true;
        } else if(eval == null) {
            // Nothing because eval only will be null if the line evaluated wasn't a number
            // and that case was already check @evalNumero.
        } else if(eval < 0) {
            bw.write("No existen nodos menores a 0. Porfavor vuelva a intentarlo.\n\n");
            bw.flush();
        } else if(eval >= numNodos){
            bw.write(new Formatter().format("No existen nodos arriba del nodo \"q%d\"." +
                    " Porfavor vuelva a intentarlo.\n\n",numNodos-1).toString());
            bw.flush();
        }
        return false;
    }

    private boolean evalFDC(Integer eval) throws IOException {
        if(eval != null && (eval == 0 || eval == 1)) {
            return true;
        } else if(eval == null) {
            // Nothing because eval only will be null if the line evaluated wasn't a number
            // and that case was already check @evalNumero.
        } else {
            bw.write("El valor que se le asigna al FDC unicamente puede ser 0 o 1." +
                    "Porfavor vuelva a intentarlo\n\n");
            bw.flush();
        }
        return false;
    }

    private void printTableau() {
        System.out.println("\n=====================================\n");
        System.out.println("\tTABLA DE TRANSICION INGRESADA");
        System.out.println("\n=====================================\n");
        System.out.print("\t\tEstados\t");
        printAlfabeto();

        for(int i = 0; i < tablaTransiciones.length; i++) {
            String node;
            System.out.printf("\t\t   p%d\t",i);
            for(int j = 0; j < tablaTransiciones[0].length; j++) {
                if(j < tablaTransiciones[0].length-1)
                    node = "p";
                else
                    node = " ";
                System.out.printf("%s%d\t",node,tablaTransiciones[i][j]);
            }
            System.out.println();
        }
    }

    private void printAlfabeto() {
        for(int i = 0; i < alfabeto.length; i++) {
            System.out.printf("%c\t",alfabeto[i]);
        }
        System.out.println("FDC");
    }

    public int[][] getTablaTransiciones() {
        return tablaTransiciones;
    }

    public char[] getAlfabeto() {
        return alfabeto;
    }

    public int getEdoInicial() {
        return edoInicial;
    }

}
