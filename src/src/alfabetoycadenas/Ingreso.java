package src.alfabetoycadenas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;

public class Ingreso {
    private BufferedReader br;
    private BufferedWriter bw;
    private boolean isDone;
    private char[] alfabeto;
    private ER regex;

    public Ingreso() {
        br = new BufferedReader(new InputStreamReader(System.in));
        bw = new BufferedWriter(new OutputStreamWriter(System.out));
        isDone = false;
        regex = new ER(null);
    }

    public void init() {
        setAlfabeto();
        while(!isDone) {
            menu();
        }
    }

    private void menu() {
        int opc;
        String input;
        String menu =   "[1] Ingresar nuevo alfabeto\n" +
                        "[2] Encontrar el lexema en la posición \"x\"\n" +
                        "[3] Encontrar la posicion que ocupa la cadena\n" +
                        "[4] Salir\n";

        try{

            bw.write("\n--------------------------------------------------\n");
            bw.write("\t\t\t\t\tMENÚ\n");

            bw.write(menu);
            bw.write("Del 1-4 Que desea realizar? ");
            bw.flush();
            input = br.readLine();
            opc = Integer.parseInt(input);

            if(opc == 1) {
                setAlfabeto();
            } else if(opc ==  2) {
                lexemaEnPosicion();
            } else if(opc == 3) {
                posicionCadena();
            } else if(opc == 4) {
                isDone = true;
                bw.write("Hasta luego!");
                bw.write("\n--------------------------------------------------\n");
                bw.write("\t\t\tREALIZADO POR:\n");
                bw.write("Alumno: Pedro Nicolas Rios Vargas NC: \"18290925\"");
                bw.write("\n--------------------------------------------------\n");
                bw.flush();
                br.close();
                bw.close();
            }
        } catch(IOException ioE) {
            ioE.printStackTrace();
        } catch(NumberFormatException nfE) {
            System.out.println("No se admiten cadenas, por favor ingrese un numero valido.");
        }
    }

    private void setAlfabeto() {
        String alfabetoCadena;
        try {

            bw.write("\n--------------------------------------------------\n");
            bw.write("\t\t\tASIGNACIÓN DE ALFABETO\n");
            bw.write("Ingrese el alfabeto: ");
            bw.flush();
            alfabetoCadena = br.readLine();
            alfabeto = alfabetoCadena.toCharArray();
            regex.setAlfabet(alfabeto);

        } catch(IOException ioE) {
            ioE.printStackTrace();
        }
    }

    private void lexemaEnPosicion() {
        int pos;
        try {

            bw.write("\n--------------------------------------------------\n");
            bw.write("\t\tENCONTRAR EL LEXEMA EN LA POSICION \"x\"\n");
            bw.write("Ingrese la posicion: ");
            bw.flush();
            pos = Integer.parseInt(br.readLine());
            regex.lexemaEnLaPosicion(pos);

        } catch(NumberFormatException nFE){
            System.out.println("La posicion que ingreso no es valida, por favor ingrese un valor entero.");
        } catch(IOException ioE) {
            ioE.printStackTrace();
        }
    }

    private void posicionCadena() {
        String cadena;
        try {

            bw.write("\n--------------------------------------------------\n");
            bw.write("\t\tENCONTRAR LA POSICION DE LA CADENA\n");
            bw.write("Ingrese la cadena: ");
            bw.flush();
            cadena = br.readLine();
            regex.posicionDeLaCadena(cadena);

        } catch(IOException ioE) {
            ioE.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Ingreso in = new Ingreso();
        in.init();
    }


}
