package src.tablatokens;

import src.datastructures.Queue;
import src.tablatokens.data.Line;
import src.tablatokens.data.Token;

import java.util.LinkedList;
import java.util.Formatter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;
import java.io.OutputStreamWriter;

public class TablaTokens {
    private final LinkedList<Token> TOKENS_TABLE;
    private final Analyzer analyzer;
    private final BufferedWriter BW;
    private final String DEBUG_PATH;
    private final Queue<Line> LINES;

    public TablaTokens(String fileName) {
        BW = new BufferedWriter(new OutputStreamWriter(System.out));
        TOKENS_TABLE = new LinkedList<>();
        analyzer = new Analyzer(TOKENS_TABLE);
        DEBUG_PATH = System.getProperty("user.dir") + "\\Depurado\\";
        LINES = FileHandler.readFile(new File(DEBUG_PATH + fileName));
    }

    public void fillingTheTable() {
        try {
            for (Line line : LINES) {
                TOKENS_TABLE.addAll(analyzer.tokenizingLine(line));
            }
            printTablaTokens();
        } catch(SyntaxException sE) {
                sE.printStackTrace();
        }
    }

    public void printTablaTokens() {
        try {
            BW.write(new Formatter().format("%s%40s%40s\n", "LEXEMA", "TOKEN", "ATRIBUTOS").toString());
            BW.write("-".repeat(100) + "\n");
            BW.flush();
            for(Token t : TOKENS_TABLE) {
                BW.write(t.toString());
                BW.flush();
            }
            BW.flush();
        } catch(IOException ioE) {

        }
    }
}
