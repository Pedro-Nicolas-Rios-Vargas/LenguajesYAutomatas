package src.depurador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

import src.datastructures.Queue;

public class FileHandler {
    public static Queue<String> readFile(File file) {
        String line = "";
        String formattedLine;
        Queue<String> lines = new Queue<>();
        int lineNumber = 1;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while(line != null) {
                line = br.readLine();
                formattedLine = LineDebug.StartOfLineValidation(line);
                if(!formattedLine.isEmpty()) {
                    lines.enqueue(lineNumber + " " + formattedLine);
                    //System.out.println(formattedLine);
                }
                lineNumber++;
            }

        } catch(IOException ioE) {
            ioE.printStackTrace();
        }
        return lines;
    }

    public static void writeFile(File file, String debuggedCode) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(debuggedCode);
            bw.flush();
            bw.close();
        } catch(IOException ioE) {
            ioE.printStackTrace();
        }
    }

}
