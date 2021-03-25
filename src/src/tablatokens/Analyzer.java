package src.tablatokens;

import java.util.LinkedList;

public class Analyzer {
    private String[] keyWords;

    public Analyzer() {
        initKeyWords();
    }

    private void initKeyWords() {
        keyWords = new String[] {
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
                "leer",
                "mientras",
                "para",
                "si",
                "sino",
                "tons",
        };
        /*

        opL = new String[] {
                "<",
                ">",
                "<=",
                ">=",
                "==",
                "!="
        };
        asig = new String[] {
                ":="
        };
        op = new String[] {
                "+",
                "-",
                "*",
                "/"
        };

         */
    }

    public LinkedList<Token> tokenizingLine(Line line) {
        LinkedList<Token> tokens = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        String code = line.getCode();
        String lexema;
        char actualChar;
        char nextChar;
        int atrib = 2;
        boolean inString = false;
        boolean isNextEspace, isNextAlphabetic, isNextDigit, isNextEqualChar;

        for(int i = 1; i < code.length(); i++) {
            actualChar = code.charAt(i-1);
            nextChar = code.charAt(i);

            if(actualChar != 32)
                sb.append(actualChar);
            else if(inString)
                sb.append(actualChar);

            if(actualChar == 34) { // c = "
                if(!inString) {
                    inString = true;
                } else {
                    inString = false;
                    lexema = sb.toString();
                    tokens.add(new Token(lexema,"literal",Integer.toString(atrib)));
                    atrib += 2;
                    sb.delete(0,sb.length());
                }
            }

            if(inString) {
                continue;
            }

            if(actualChar == 32)
                continue;

            isNextAlphabetic = Character.isAlphabetic(nextChar);
            isNextEqualChar = nextChar == 61;
            isNextDigit = Character.isDigit(nextChar);
            isNextEspace = nextChar == 32;

            if (actualChar == 33 || actualChar == 61) {   // actual == '!' || == '='
                if (isNextEqualChar) {    // siguiente == '='
                    sb.append(nextChar);
                    lexema = sb.toString();
                    tokens.add(new Token(lexema, "opL", "-"));
                    sb.delete(0, sb.length());
                    i++; //Se incrementa i debido a que se va a ingresar un caracter adelantado.
                } else {
                    // Generar un error
                }
            } else if (actualChar == 60 || actualChar == 62) {
                if (isNextEspace || isNextAlphabetic || isNextDigit) {
                    lexema = sb.toString();
                    tokens.add(new Token(lexema, "opL", "-"));
                    sb.delete(0, sb.length());
                } else if (isNextEqualChar) {
                    sb.append(nextChar);
                    lexema = sb.toString();
                    tokens.add(new Token(lexema, "opL", "-"));
                    sb.delete(0, sb.length());
                    i++; //Se incrementa i debido a que se va a ingresar un caracter adelantado.
                }
                //EVALUACION PARA OPERADORES ARITMETICOS
            } else if (actualChar >= 42 && actualChar <= 47) {
                if (isNextEspace || isNextAlphabetic || isNextDigit) {
                    lexema = sb.toString();
                    tokens.add(new Token(lexema, "op", "-"));
                } else {
                    // Generar un error
                }
                //EVALUACION PARA ASIGNACION
            } else if (actualChar == 58 && isNextEqualChar) {
                sb.append(nextChar);
                lexema = sb.toString();
                tokens.add(new Token(lexema, "asign", "-"));
                sb.delete(0,sb.length());
                i++; //Se incrementa i debido a que se va a ingresar un caracter adelantado.
            } else {
                // Generar un error
            }

            //EVALUACION PARA PALABRA RESERVADA O VARIABLES
            if((nextChar >= 32 && nextChar <=34) // ESPACIO, !, "
                    || (nextChar >= 42 && nextChar <= 47)   // OPERADORES
                    || (nextChar >= 58 && nextChar <=62)    // COMPARACION
            ) {
                lexema = sb.toString();
                if(isKeyWord(lexema)) {
                    tokens.add(new Token(lexema, lexema,"-"));
                    sb.delete(0,sb.length());
                } else if(isVariable(lexema)) {
                    //=============================================================
                    //
                    //                          FALTA
                    //
                    //=============================================================
                    // Se debe buscar la variable dentro de la tabla de tokens
                    // en caso de encontrarse el token vuelve a ser ingresado con el mismo atributo.
                    //
                    // Si la variable no esta registrada en la tabla de tokens y el ultimo token
                    // ingresado no es un tipo (cadena, ent, float, doble) generar un error.
                    Token token = null;
                    for(Token t : tokens) {
                        if(t.getLexema().equals(lexema)) {
                            token = t;
                            break;
                        }
                    }
                    if(token == null) {
                         token = new Token(lexema,"Id",Integer.toString(atrib++));
                    }
                    tokens.add(token);
                    sb.delete(0,sb.length());
                }
            //EVALUACION PARA OPERADORES LOGICOS
            } else {

            }
        }
        return tokens;
    }

    private boolean isKeyWord(String word) {
        for (String keyWord : keyWords) {
            if (word.equals(keyWord))
                return true;
        }
        return false;
    }

    private boolean isVariable(String word) {
        int length = word.length();
        char c;
        System.out.println(word);
        if(!Character.isAlphabetic(word.charAt(0))){
            return false;
        }

        for(int i = 1; i < length; i++) {
            c = word.charAt(i);
            if(!(Character.isAlphabetic(c) || Character.isDigit(c) || c == 95))
                return false;
        }

        return true;
    }

}
