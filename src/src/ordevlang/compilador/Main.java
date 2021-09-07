package src.ordevlang.compilador;

import src.ordevlang.tablatokens.SyntaxException;
import src.ordevlang.depurador.Depurador;
import src.ordevlang.tablatokens.TablaTokens;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        // TODO: Fix the print error in DosBox.
        //  Solution a) make a LEA read +2 for skip the descriptive bytes.
        //  Solution b) make a macro for fix this.
        //  Solution c) make a loop that print every character in the variable. (NOT RECOMMENDED)
        //  Solution d) change the existing macros (NOT RECOMMENDED).
        String fileName = "input";
        TablaTokens tokensTable;
        Compilador compi;
        try {
            new Depurador(fileName + ".ordev");
            tokensTable = new TablaTokens( fileName + ".ordep");
            tokensTable.fillingTheTable();
            compi = new Compilador(tokensTable.getTokensTable());
            compi.compilar(fileName, false);
        } catch(FileNotFoundException fNFE) {
            fNFE.printStackTrace();
        } catch (SyntaxException sE) {
            sE.printStackTrace();
        }
    }
}
