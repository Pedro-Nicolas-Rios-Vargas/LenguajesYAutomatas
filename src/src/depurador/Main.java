package src.depurador;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        try {
            Depurador debug = new Depurador("codigo2.ordev");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
