package src.ProcesadorDeLenguaje;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
public class Main {

    public void ingresoDeLexemas() {
        BufferedReader br;
        BufferedWriter bw;
        String lexema;
        boolean resultado;
        Procesador proc = new Procesador();
        int count = 0;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(System.out));
            br = new BufferedReader(new InputStreamReader(System.in));

            bw.write("====================   PROCESADOR DE LENGUAJE   ====================\n");
            bw.write("La expresión Regular que analizara los lexemas es: \"(ab)^+a U ba^+b\"\n");
            while (count != 6) {
                bw.write("Ingrese un lexema: ");
                bw.flush();
                lexema = br.readLine();
                resultado = proc.evaluarLexema(lexema);

                if(resultado)
                    bw.write("El lexema \"" + lexema + "\" se acepta.\n\n");
                else
                    bw.write("El lexema \"" + lexema + "\" se rechaza.\n\n");
                bw.flush();
                count++;
            }
        } catch(IOException ioE) {

        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.ingresoDeLexemas();
    }
}
