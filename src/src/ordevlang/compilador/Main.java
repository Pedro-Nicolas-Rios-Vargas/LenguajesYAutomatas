package src.ordevlang.compilador;

import src.ordevlang.depurador.Depurador;
import src.ordevlang.tablatokens.TablaTokens;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        String fileName = "print";
        TablaTokens tokensTable;
        Compilador compi;
        try {
            new Depurador(fileName + ".ordev");
            tokensTable = new TablaTokens( fileName + ".ordep");
            tokensTable.fillingTheTable();
        } catch(FileNotFoundException fNFE) {
            fNFE.printStackTrace();
            return;
        }
        compi = new Compilador(tokensTable.getTokensTable());
        compi.compilar(fileName);
    }
}
