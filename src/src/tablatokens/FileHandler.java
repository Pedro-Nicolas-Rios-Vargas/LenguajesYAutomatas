package src.tablatokens;

import java.util.Formatter;

import src.datastructures.Queue;
import src.tablatokens.data.Line;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;


public class FileHandler {

    public static Queue<Line> readFile(File file) {
        String rawLine = "";
        Line line;
        Queue<Line> lines = new Queue<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
            bw.write(new Formatter().format("\nExtrayendo lineas de codigo de: \"%s\"\n\n",file.getPath()).toString());
            bw.flush();
            while((rawLine = br.readLine()) != null) {
                if(!rawLine.isEmpty()) {
                    line = new Line(rawLine);
                    lines.enqueue(line);
                }
            }
        } catch(IOException ioE) {
            System.out.println(ioE.getMessage());
            ioE.printStackTrace();
        }
        return lines;
    }

}
