package src.PrefijoYSufijo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;

public class Automatas {
    private final BufferedReader br;
    private final BufferedWriter bw;
    private String lexema;

    public Automatas() {
        br = new BufferedReader(new InputStreamReader(System.in));
        bw = new BufferedWriter(new OutputStreamWriter(System.out));
    }

    public static void main(String[] args) {
        Automatas leng = new Automatas();
        leng.lector();
        leng.prefijo();
        leng.sufijo();
        leng.subCadenas();
        leng.inversa();

    }

    public void lector() {
        try{
            bw.write("Ingrese cadena: ");
            bw.flush();
            lexema = br.readLine();

        }catch(IOException ioE) {
            System.out.println(ioE.getMessage());
        }
    }

    public void prefijo() {
        try {
            bw.write("\n--------------------------------------\n");
            bw.write("PREFIJOS DE: " + lexema + "\n");
            for (int i = 1; i <= lexema.length(); i++) {
                bw.write(lexema.substring(0, i) + "\n");
            }
            bw.flush();
        } catch(IOException ioE) {
            System.out.println(ioE.getMessage());
        }
    }

    public void sufijo() {
        try {
            bw.write("\n--------------------------------------\n");
            bw.write("SUFIJOS DE: " + lexema + "\n");

            for(int i = lexema.length()-1; i >= 0; i--) {
                bw.write(lexema.substring(i, lexema.length()) + "\n");
            }
            bw.flush();
        } catch(IOException ioE) {
            System.out.println(ioE.getMessage());
        }
    }

    public void subCadenas() {
        int length = lexema.length();
        try {
            bw.write("\n--------------------------------------\n");
            bw.write("SUBCADENAS DE: " + lexema + "\n");

            for (int width = 0; width < length; width++) {
                for (int height = 0; height < length - width; height++) {
                    bw.write(lexema.substring(width, height + width + 1) + "\t\t");
                }
                bw.write("\n");
            }
            bw.flush();
        }catch(IOException ioE) {

        }
    }

    public void inversa() {
        StringBuilder palabra = new StringBuilder(lexema);
        StringBuilder result = new StringBuilder();
        int length = palabra.length();
        String handler;

        try {
            bw.write("\n--------------------------------------\n");
            bw.write("INVERSA DE: " + lexema + "\n");

            bw.write("W = " + palabra.toString() + " w^I = ? \n");

            for (int i = 0; i < length; i++) {
                result.insert(0,palabra.charAt(0));
                palabra.delete(0,1);

                if(palabra.length() != 0) {
                    handler = palabra.toString();
                }else{
                    handler = "ε";
                }

                bw.write("w^I = (" + handler + ")^I " + result.toString() + "\n");

            }
            bw.write("w^I = " + result.toString() + "\n");
            bw.flush();
        }catch(IOException ioE) {

        }

    }

}
