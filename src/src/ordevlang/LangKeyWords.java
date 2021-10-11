package src.ordevlang;

public class LangKeyWords {
    public static String[] keyWords = new String[] {
            "cadena",
            "const",
            "doble",
            "ent",
            "float",
            "finmientras",
            "finpara",
            "finsi",
            "finsino",
            "hacer", //do
            "impri",
            "impriln",
            "leer",
            "mientras",
            "para",
            "si",
            "sino",
            "tons",
    };

    public static String[] dataTypes = new String[] {
            "cadena",
            "ent",
            "booleano"
    };

    public static String[] instructionKeyWords = new String[] {
            "leer",
            "impri",
            "impriln",
            "si",
            "finsi"
    };

    public static boolean isDataType(String token) {
        return token.equals("ent") || token.equals("float") ||
                token.equals("doble") || token.equals("cadena") ||
                token.equals("booleano");
    }

    public static boolean isArithmeticOperator(char caracter) {
        return caracter == 42 || caracter == 43 || caracter == 45 || caracter == 47;
    }
}
