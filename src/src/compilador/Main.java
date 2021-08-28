package src.compilador;

import src.depurador.Depurador;
import src.tablatokens.TablaTokens;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        String fileName = "ingresos";
        TablaTokens tokensTable;
        try {
            new Depurador(fileName + ".ordev");
            tokensTable = new TablaTokens( fileName + ".ordep");
            tokensTable.fillingTheTable();
            new Compilador(tokensTable.getTokensTable());
        } catch(FileNotFoundException fNFE) {
            fNFE.printStackTrace();
        }
    }
}
