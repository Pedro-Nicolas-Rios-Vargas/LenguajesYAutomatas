package src.alfabetoycadenas;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;

import java.util.ArrayList;

public class ER {
    private char[] alfabet;
    private BufferedWriter bw;

    public ER(char[] alfabet) {
        this.alfabet = alfabet;

        bw = new BufferedWriter(new OutputStreamWriter(System.out));
    }

    //Se asume que el alfabeto esta ordenado correctamente
    public void lexemaEnLaPosicion(int x) {
        int[] lexemaNumerico = tableauLexemaNumerico(x);
        char[] lexema = traductor(lexemaNumerico);

        try {
            bw.write("\nEl lexema en la posicion \"" + x + " es: ");
            imprimirArreglo(lexemaNumerico);
            bw.write(" = ");
            imprimirArreglo(lexema);
            bw.write("\n");
            bw.flush();
        }catch(IOException ioE) {
            ioE.printStackTrace();
        }
    }

    // Se asume que la cadena pertenece al alfabeto
    public void posicionDeLaCadena(String cadena) {
        int res = sumadorDeElevados(cadena);

        try {

            bw.write("\n La posicion que ocupa la cadena \"" + cadena + "\" es: " + res + "\n");
            bw.flush();

        }catch(IOException ioE) {
            ioE.printStackTrace();
        }
    }

    private int[] tableauLexemaNumerico(int x) {
        int cantidadLetras = alfabet.length;
        int[] filaElevados, filaDiagonales;

        filaElevados = filaElevadosLexemaDesconocido(cantidadLetras, x);
        filaDiagonales = filaDiagonales(filaElevados);

        int validador;  // Encargado de realizar la operacion para validar la letra correcta
        int remanente = 0; // Remanente de la operacion sumado al anterior

        int elevado, antecesorElevado;
        int limitFilas = filaDiagonales.length;
        int[] lexemaNumerico = new int[limitFilas]; // Almacenara el resultado de la iteracion
        int index = 0, indexDiag = 1, letraIndex = cantidadLetras;

        try {
            imprimirArregloTabulado(filaDiagonales);
            bw.write("\n");
            imprimirArregloTabulado(filaElevados);
            bw.write(x + "\n");

            while (index < limitFilas) {
                if (letraIndex == 0) {
                    lexemaNumerico[index] = letraIndex;
                    index++;
                    indexDiag++;
                    letraIndex = cantidadLetras;
                    continue;
                }

                elevado = filaElevados[index];

                if (indexDiag >= limitFilas) {
                    antecesorElevado = 0;
                } else {
                    antecesorElevado = filaDiagonales[indexDiag];
                }

                // Operacion
                validador = remanente + letraIndex * elevado + antecesorElevado;

                if (validador > x) {
                    letraIndex--;
                    continue;
                }

                lexemaNumerico[index] = letraIndex;

                remanente += letraIndex * elevado;

                imprimirArregloTabulado(lexemaNumerico);
                bw.write(remanente + "\n");

                index++;
                indexDiag++;
                letraIndex = cantidadLetras;

            }
        }catch(IOException ioE) {
            ioE.printStackTrace();
        }

        return lexemaNumerico;

    }

    private char[] traductor(int[] lexemaNumerico) {
        int lexNumLength = lexemaNumerico.length;
        char[] lexema = new char[lexNumLength];

        int letraCorrespondiente;
        for(int i = 0 ; i < lexNumLength; i++) {
            letraCorrespondiente = lexemaNumerico[i];

            if(letraCorrespondiente == 0) {
                continue;
            }
            lexema[i] = alfabet[letraCorrespondiente - 1];
        }

        return lexema;
    }

    /**
     * Metodo para generar la fila de valores elevados cuando se desconoce el tamaño del lexema
     * @param cantidadLetras
     * @param x
     * @return
     */
    public int[] filaElevadosLexemaDesconocido(int cantidadLetras, int x) {
        int[] powVals;
        int power = 0;
        int powered;

        int limitAdd = 0;

        ArrayList<Integer> poweredValues = new ArrayList<>();

        do {
            // Se eleva la cantidad de letras para obtener el limite inferior
            powered = (int) Math.pow(cantidadLetras, power);
            // Se agrega a la lista el valor obtenido al elevar la cantidad de letras
            poweredValues.add(powered);

            limitAdd += powered;
            power++;
        }while(limitAdd < x);

        powVals = new int[power -1];
        int count = power-2;
        for(int i : poweredValues) {
            if(count >= 0) {
                powVals[count] = i;
                count--;
            }
        }

        return powVals;
    }

    public int[] filaElevadosLexemaConocido(int cantidadLetras, int cantLetrasLexema) {
        int[] powVals = new int[cantLetrasLexema];
        int power = 0;

        for(int i = cantLetrasLexema - 1; i >= 0; i--) {
            powVals[i] = (int) Math.pow(cantidadLetras, power);
            power++;
        }
        return powVals;
    }

    private int[] filaDiagonales(int[] powVals) {
        int powArrayLength = powVals.length;
        int[] diagVals = new int[powArrayLength];


        diagVals[powArrayLength - 1] = powVals[powArrayLength - 1];
        for(int i = powArrayLength - 2; i >= 0; i--) {
            diagVals[i] = powVals[i] + diagVals[i + 1];
        }

        return diagVals;
    }

    private int sumadorDeElevados(String cadena) {
        char[] lexema = cadena.toCharArray();
        int cantidadDeLetras = alfabet.length;
        int cantLetrasLexema = lexema.length;
        int[] filaElevados = filaElevadosLexemaConocido(cantidadDeLetras, cantLetrasLexema);
        int elevado;
        int caracterNum;
        int sumador = 0;

        try {
            for (int i = 0; i < cantLetrasLexema; i++) {
                elevado = filaElevados[i];
                caracterNum = lexema[i] - 96;
                bw.write(elevado + "(" + caracterNum + ") ");
                sumador += elevado * caracterNum;

                if (i == cantLetrasLexema - 1) {
                    bw.write("= ");
                } else
                    bw.write("+ ");
            }
            bw.write(sumador + "\n");
        }catch(IOException ioE) {
            ioE.printStackTrace();
        }

        return sumador;
    }

    private void imprimirArregloTabulado(int[] array) {
        try {
            for (int i : array) {
                if (i == 0) {
                    bw.write("\t");
                } else {
                    bw.write(i + "\t");
                }
            }
        } catch(IOException ioE) {
            ioE.printStackTrace();
        }
    }

    private void imprimirArreglo(char[] array) {
        try {
            for (char c : array) {
                bw.write(c);
            }
        } catch(IOException ioE) {
            ioE.printStackTrace();
        }
    }

    private void imprimirArreglo(int[] array) {
        try {
            for (int i : array) {
                bw.write(i+"");
            }
        } catch(IOException ioE) {
            ioE.printStackTrace();
        }
    }

    public void setAlfabet(char[] alfabet) {
        this.alfabet = alfabet;
    }
}
