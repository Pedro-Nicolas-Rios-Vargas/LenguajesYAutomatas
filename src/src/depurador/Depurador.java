package src.depurador;

import src.datastructures.Queue;

import java.io.File;

public class Depurador {
    private Queue<String> lines;
    private final String MAIN_PATH;
    private final String FILE_NAME;
    private final String CODE_PATH = "Codigo\\";
    private final String DEBUG_PATH = "Depurado\\";
    private final String LANG_TYPE = ".ordev";
    private final String DEBUG_TYPE = ".ordep";

    public Depurador(String fileName) {
        lines = new Queue<>();
        MAIN_PATH = System.getProperty("user.dir") + "\\";
        this.FILE_NAME = fileName;
        debug();
    }

    private void debug() {
        File f2r = new File(MAIN_PATH + CODE_PATH + FILE_NAME + LANG_TYPE);
        File f2w = new File(MAIN_PATH + DEBUG_PATH + FILE_NAME + DEBUG_TYPE);
        lines = FileHandler.readFile(f2r);
        FileHandler.writeFile(f2w, debuggingCode());
    }

    private String debuggingCode() {
        String line;
        StringBuilder debuggedCode = new StringBuilder();
        do {
            line = LineDebug.lineDebugging(lines.dequeue());
            debuggedCode.append(line);
            //System.out.println(line);
            if(lines.size() != 0)
                debuggedCode.append("\n");

        }while(lines.size() != 0);
        return debuggedCode.toString();
    }

}
