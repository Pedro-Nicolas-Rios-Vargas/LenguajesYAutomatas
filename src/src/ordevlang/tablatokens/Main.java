package src.ordevlang.tablatokens;

import src.ordevlang.depurador.Depurador;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        String fileName = "ingresos";
        try {
            new Depurador(fileName + ".ordev");
            new TablaTokens( fileName + ".ordep").fillingTheTable();
        } catch(FileNotFoundException fNFE) {
            fNFE.printStackTrace();
        } catch (SyntaxException sE) {
            sE.printStackTrace();
        }

    }
}
