package src.ordevlang.depurador;

import src.datastructures.Queue;

import java.io.File;
import java.io.FileNotFoundException;

public class Depurador {
    private Queue<String> lines;
    private final String MAIN_PATH;
    private final String RAW_FILE_NAME;
    private final String CODE_PATH = "Codigo\\";
    private final String DEBUG_PATH = "Depurado\\";
    private final String LANG_TYPE = ".ordev";
    private final String DEBUG_TYPE = ".ordep";

    public Depurador(String fileName) throws FileNotFoundException {
        lines = new Queue<>();
        MAIN_PATH = System.getProperty("user.dir") + "\\";
        this.RAW_FILE_NAME = fileName;
        debug();
    }

    private void debug() throws FileNotFoundException {
        String[] fileNameComponents = RAW_FILE_NAME.split("[.]");
        String fileName = fileNameComponents[0];
        if(!fileNameComponents[1].equals("ordev"))
            throw new FileNotFoundException("El archivo no es de tipo: " + LANG_TYPE);

        File f2r = new File(MAIN_PATH + CODE_PATH + RAW_FILE_NAME);
        File f2w = new File(MAIN_PATH + DEBUG_PATH + fileName + DEBUG_TYPE);
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
