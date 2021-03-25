package src.tablatokens;

import src.tablatokens.data.Line;
import src.tablatokens.data.Token;

import java.util.LinkedList;

public class Analyzer {
    private final LinkedList<Token> TOKENS_TABLE;
    private String[] keyWords;
    private int atrib;
    private boolean inStringFlag;
    private boolean invalidCharAtBeginVarFlag;
    private boolean invalidCharInVarFlag;

    public Analyzer(LinkedList<Token> tokensTable) {
        initKeyWords();
        this.TOKENS_TABLE = tokensTable;
        atrib = 2;
        inStringFlag = false;
        invalidCharInVarFlag = false;
        invalidCharAtBeginVarFlag = false;
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
        boolean isActualEspace, isActualAlphabetic, isActualDigit, isActualEqualChar;

        for(int i = 0; i < code.length(); i++) {
            actualChar = code.charAt(i);

            if(actualChar == 34) { // c = "
                if(!inStringFlag) {
                    inStringFlag = true;
                } else {
                    inStringFlag = false;
                    sb.append(actualChar);
                    lexema = sb.toString();
                    tokens.add(new Token(lexema,"literal",Integer.toString(atrib)));
                    atrib += 2;
                    sb.delete(0,sb.length());
                    continue;
                }
            }

            if(inStringFlag) {
                sb.append(actualChar);
                continue;
            }

            if(sb.length() == 1) {
                char single = sb.charAt(0);
                isActualAlphabetic = Character.isAlphabetic(actualChar);
                isActualEqualChar = actualChar == 61;
                isActualDigit = Character.isDigit(actualChar);
                isActualEspace = actualChar == 32;

                if (single == 33 || single == 61) {   // actual == '!' || == '='
                    if (isActualEqualChar) {    // siguiente == '='
                        sb.append(actualChar);
                        lexema = sb.toString();
                        tokens.add(new Token(lexema, "opL", "-"));
                        sb.delete(0, sb.length());
                    } else {
                        //TODO: Throws an exception because the characters '!' or '=' only can have as next character an
                        // '=' other type of character is an error.
                    }
                } else if (single == 60 || single == 62) {
                    if (isActualEspace || isActualAlphabetic || isActualDigit) {
                        lexema = sb.toString();
                        tokens.add(new Token(lexema, "opL", "-"));
                        sb.delete(0, sb.length());
                    } else if (isActualEqualChar) {
                        sb.append(actualChar);
                        lexema = sb.toString();
                        tokens.add(new Token(lexema, "opL", "-"));
                        sb.delete(0, sb.length());
                    }
                    //EVALUACION PARA OPERADORES ARITMETICOS
                } else if (single >= 42 && single <= 47) {
                    if (isActualEspace || isActualAlphabetic || isActualDigit) {
                        lexema = sb.toString();
                        tokens.add(new Token(lexema, "op", "-"));
                        sb.delete(0, sb.length());
                    } else {
                        //TODO: Throw an exception because the arithmetic operators only can have as next an number, letter or space,
                        // other type of character is an error.
                    }
                    //EVALUACION PARA ASIGNACION
                } else if (single == 58 && isActualEqualChar) { // sb == ':'
                    sb.append(actualChar);
                    lexema = sb.toString();
                    tokens.add(new Token(lexema, "asign", "-"));
                    sb.delete(0, sb.length());
                } else {
                    //TODO: Throw an exception because the character ':' only can have as next an character
                    // '=' other type of character is an error.
                }
            } else if(sb.length() > 1) {//EVALUACION PARA PALABRA RESERVADA O VARIABLES
                if ((actualChar >= 32 && actualChar <= 34) // ESPACIO, !, "
                        || (actualChar >= 42 && actualChar <= 47)   // OPERADORES
                        || (actualChar >= 58 && actualChar <= 62)    // COMPARACION
                        || (i == code.length() - 1)
                ) {
                    if(i == code.length() - 1) {
                        sb.append(actualChar);
                    }
                    lexema = sb.toString();
                    if (isKeyWord(lexema)) {
                        tokens.add(new Token(lexema, lexema, "-"));
                        sb.delete(0, sb.length());
                    } else if (isVariable(lexema)) {
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
                        for (Token t : TOKENS_TABLE) {
                            if (t.getLexema().equals(lexema)) {
                                token = t;
                                break;
                            }
                        }
                        String lastTokenStr = tokens.getLast().getToken();
                        if(isDataType(lastTokenStr)) { // Verifying that the variable is declare
                            if(token == null) { // If token is null indicates that the variable is not registered
                                token = new Token(lexema, "Id", Integer.toString(atrib++));
                            } else { // The variable name exists and is trying of re-create it with a new datatype.
                                //TODO:  throw an exception because the variable exists and the user is trying re-create it
                            }
                        } else if (token == null) { // The last token is not a data type and the variable already not exist in the table
                            //TODO: throw an exception because the variable not exist in the table and the user is trying to use it.
                            /*token = new Token(lexema, "Id", Integer.toString(atrib++));*/
                        }
                        // The token is not null so it already exist in the table and the user is trying to use it or
                        // the token don't exist but the user are declaring it.
                        tokens.add(token);
                        sb.delete(0, sb.length());
                    } else {
                        if(invalidCharAtBeginVarFlag) {
                            //TODO: throw an exception because the variable only can starts with alphabetic character
                        }
                        if(invalidCharAtBeginVarFlag) {
                            //TODO: throw an exception because the variable only can contain alphabetic or digit or underscore characters
                        }
                    }
                }
            }

            if(actualChar != 32) {
                sb.append(actualChar);
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
        if(!Character.isAlphabetic(word.charAt(0))){
            invalidCharAtBeginVarFlag = true;
            return false;
        }

        for(int i = 1; i < length; i++) {
            c = word.charAt(i);
            if(!(Character.isAlphabetic(c) || Character.isDigit(c) || c == 95)) {
                invalidCharInVarFlag = true;
                return false;
            }
        }

        return true;
    }

    private boolean isDataType(String token) {
        return token.equals("ent") || token.equals("float") ||
                token.equals("doble") || token.equals("cadena");
    }

}
