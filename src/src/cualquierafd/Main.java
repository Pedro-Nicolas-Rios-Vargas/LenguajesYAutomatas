package src.cualquierafd;

import java.util.Formatter;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;


public class Main {
    private int[][] tableau;
    private char[] alfabeto;
    private int edoInicial;
    private BufferedReader br;
    private BufferedWriter bw;

    public Main() {
        bw = new BufferedWriter(new OutputStreamWriter(System.out));
        br = new BufferedReader(new InputStreamReader(System.in));
        ingresoTabla();
    }

    private void ingresoTabla() {
        IngresoTablaTransicion ingreso = new IngresoTablaTransicion();
        ingreso.ingresoTablaTransiciones();
        tableau = ingreso.getTablaTransiciones();
        alfabeto = ingreso.getAlfabeto();
        edoInicial = ingreso.getEdoInicial();
    }

    public void ingresoDeLexemas() {
        String lexema;
        boolean isDone = false;
        boolean isLexema;
        Procesador proc = new Procesador(alfabeto,tableau);
        try {
            bw.write("\n====================   PROCESADOR DE LENGUAJE   ====================\n");
            while (!isDone) {
                bw.write("Ingrese un lexema: ");
                bw.flush();
                lexema = br.readLine();
                isLexema = proc.evaluarLexema(lexema,edoInicial);

                bw.write(new Formatter().format("El lexema \"%s\" se %s\n\n",
                        lexema, isLexema ? "acepta" : "rechaza").toString());
                bw.flush();
                if(!continuar())
                    isDone = true;

            }
        } catch(IOException ioE) {

        }
    }

    private boolean continuar() throws IOException {
        String line;
        Integer eval;
        do {
            bw.write("Desea ingresar otro lexema?\n");
            bw.write("   0 No\n");
            bw.write("   1 Si\n");
            bw.write("Ingrese la seleccion [0..1] ");
            bw.flush();
            line = br.readLine();
            eval = evalIngreso(line);
        } while(!evalContinuar(eval));
        return eval == 1 ? true : false;
    }

    private Integer evalIngreso(String line) throws IOException {
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

    private boolean evalContinuar(Integer eval) throws IOException {
        if(eval != null && (eval == 0 || eval == 1)) {
            return true;
        } else if(eval == null) {
            // Nothing because eval only will be null if the line evaluated wasn't a number
            // and that case was already check @evalNumero.
        } else {
            bw.write("Su respuesta unicamente puede ser 0 o 1. Porfavor vuelva a intentarlo\n\n");
            bw.flush();
        }
        return false;
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.ingresoDeLexemas();
    }
}
