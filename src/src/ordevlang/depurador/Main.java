package src.ordevlang.depurador;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        try {
            Depurador debug = new Depurador("print.ordev");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
