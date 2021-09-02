package src.ordevlang.compilador;

import java.util.LinkedList;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class AsmMaker {

    private final BufferedWriter BF;
    private final String SALTO_LN;
    private final String PATH;

    private LinkedList<String> stackAllocations;
    private LinkedList<String> extraAllocations;
    private LinkedList<String> datosAllocations;
    private LinkedList<String> codigoAllocations;

    public AsmMaker(String fileName) throws IOException {
        PATH = "D:\\masm\\compilador\\" + fileName + ".asm";
        BF = new BufferedWriter(new FileWriter(PATH));
        SALTO_LN = System.lineSeparator();
    }

    public void initBuildFile() {
        // TODO: Invoke here the four segments builders
        try {
            addLine("include bBasic.inc");
        } catch (IOException ioE) {
            System.out.println("Error construyendo el documento asm." + ioE.getMessage());
        }
        buildStack();
        buildExtra();
        buildData();
        buildCodigo();
        try {
            BF.flush();
            BF.close();
            System.out.println("\nSe creo el archivo en la ruta: " + PATH);
        } catch (IOException ioE) {
            System.out.println("Error escribiendo el archivo asm. " + ioE.getMessage());
        }
    }

    private void buildStack() {
        try {
            addLine("pila segment para 'stack'");
            addLine("db 1000 dup (?)");

            // TODO: Add here the code that can be in stack segment
            if (stackAllocations != null) {
                while (!stackAllocations.isEmpty()) {
                    addLine(stackAllocations.remove());
                }
            }

            addLine("pila ends");

        } catch(IOException ioE) {
            System.out.println("Error construyendo el segmento Stack" + ioE.getMessage());
        }
    }

    private void buildExtra() {
        try {
            addLine("extra segment para public 'data'");

            // TODO: Add here the code that can be in extra segment
            if (extraAllocations != null) {
                while (!extraAllocations.isEmpty()) {
                    addLine(extraAllocations.remove());
                }
            }

            addLine("extra ends");

        } catch(IOException ioE) {
            System.out.println("Error construyendo el segmento Extra" + ioE.getMessage());
        }
    }

    private void buildData() {
        try {
            addLine("datos segment para public 'data'");

            // TODO: Add here the code that can be in datos segment
            if (datosAllocations != null) {
                while(!datosAllocations.isEmpty()) {
                    addLine(datosAllocations.remove());
                }
            }

            addLine("datos ends");

        } catch(IOException ioE) {
            System.out.println("Error construyendo el segmento Datos" + ioE.getMessage());
        }
    }

    private void buildCodigo() {
        try {
            addLine("codigo segment para public 'code'");
            addLine("assume cs:codigo, ds:datos, es:extra, ss:pila");
            addLine("public p0");
            addLine("p0 proc far");
            addLine("push ds");
            addLine("mov ax,0");
            addLine("push ax");
            addLine("mov ax, datos");
            addLine("mov ds, ax");
            addLine("mov ax, extra");
            addLine("mov es, ax");

            // TODO: Add here the code that can be in codigo segment
            // TODO: Receive strings that call the bBasic macros file
            if (codigoAllocations != null) {
                while(!codigoAllocations.isEmpty()) {
                    addLine(codigoAllocations.remove());
                }
            }

            addLine("ret");
            addLine("p0 endp");
            addLine("codigo ends");
            addLine("end p0");

        } catch(IOException ioE) {
            System.out.println("Error construyendo el segmento Codigo" + ioE.getMessage());
        }
    }

    public void loadStackAllocations(LinkedList<String> stackAllocations) {
        this.stackAllocations = stackAllocations;
    }

    public void loadExtraAllocations(LinkedList<String> extraAllocations) {
        this.extraAllocations = extraAllocations;
    }

    public void loadDatosAllocations(LinkedList<String> datosAllocations) {
        this.datosAllocations = datosAllocations;
    }

    public void loadCodigoAllocations(LinkedList<String> codigoAllocations) {
        this.codigoAllocations = codigoAllocations;
    }

    private void addLine(String line) throws IOException {
        BF.write(line + SALTO_LN);
    }
}
