package src.ordevlang.compilador;

import src.ordevlang.tablatokens.SyntaxException;
import src.ordevlang.depurador.Depurador;
import src.ordevlang.tablatokens.tree.TreeAnalyzer;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        String fileName = "paralop";
        TreeAnalyzer analyzer;
        Compilador compi;
        try {
            new Depurador(fileName + ".ordev");
            analyzer = new TreeAnalyzer(fileName + ".ordep");
            analyzer.buildingAST();
            compi = new Compilador(analyzer.getTokensTable());
            compi.compilar(fileName, false);
        } catch(FileNotFoundException | SyntaxException fNFE) {
            fNFE.printStackTrace();
        }
    }
}
