package src.tablatokens;

import src.datastructures.Queue;
import src.tablatokens.data.Line;

import java.io.BufferedReader;
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
