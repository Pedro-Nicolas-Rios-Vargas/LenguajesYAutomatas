package src.ordevlang.compilador;

import src.ordevlang.tablatokens.SyntaxException;
import src.ordevlang.depurador.Depurador;
import src.ordevlang.tablatokens.TablaTokens;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        String fileName = "operaciones";
        TablaTokens tokensTable;
        Compilador compi;
        try {
            new Depurador(fileName + ".ordev");
            tokensTable = new TablaTokens( fileName + ".ordep");
            tokensTable.fillingTheTable();
            compi = new Compilador(tokensTable.getTokensTable());
            compi.compilar(fileName, true);
        } catch(FileNotFoundException fNFE) {
            fNFE.printStackTrace();
        } catch (SyntaxException sE) {
            sE.printStackTrace();
        }
    }
}
