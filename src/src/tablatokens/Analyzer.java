package src.tablatokens;

import src.tablatokens.data.Line;
import src.tablatokens.data.Token;

import java.util.Formatter;
import java.util.LinkedList;

public class Analyzer {
    private final LinkedList<Token> TOKENS_TABLE;
    private String[] keyWords;
    private int attrib;
    private boolean inStringFlag;
    private boolean invalidCharAtBeginVarFlag;
    private boolean invalidCharInVarFlag;
    private boolean numberFlag;
    private boolean decimalFlag;
    private boolean exponentFlag;
    private boolean expSignFlag;
    private boolean numAfterExpSignFlag;

    public Analyzer(LinkedList<Token> tokensTable) {
        initKeyWords();
        this.TOKENS_TABLE = tokensTable;
        attrib = 2;
        inStringFlag = false;
        invalidCharInVarFlag = false;
        invalidCharAtBeginVarFlag = false;
        numberFlag = false;
        decimalFlag = false;
        exponentFlag = false;
        expSignFlag = false;
        numAfterExpSignFlag = false;
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

        Logic operators
                "<",
                ">",
                "<=",
                ">=",
                "==",
                "!="
                
        asignation
                ":="

        arithmetic operators
                "+",
                "-",
                "*",
                "/"

         */
    }

    public LinkedList<Token> tokenizingLine(Line line) throws SyntaxException {
        LinkedList<Token> tokens = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        String code = line.getCode();
        int length = code.length();
        String lexema;
        char actualChar;
        boolean isActualEspace, isActualAlphabetic, isActualDigit, isActualEqualChar;
        boolean isLoopEnding;
        for(int i = 0; i < length; i++) {
            isLoopEnding = i == length - 1;
            actualChar = code.charAt(i);

            if(actualChar == 34) { // c = "
                if(!inStringFlag) {
                    inStringFlag = true;
                } else {
                    inStringFlag = false;
                    sb.append(actualChar);
                    lexema = sb.toString();
                    tokens.add(new Token(lexema,"literal",Integer.toString(attrib)));
                    attrib += 2;
                    sb.delete(0,sb.length());
                    continue;
                }
            }

            if(inStringFlag) {
                sb.append(actualChar);
                continue;
            }

            //VERIFICACION NUMERO
            if(numberFlag || exponentFlag) {

                if (numberFlag) {
                    if (Character.isDigit(actualChar)) {
                        sb.append(actualChar);
                        if(i != code.length() - 1)
                            continue;
                    } else if (actualChar == 69 || actualChar == 101) { //EXPONENTE 'e' 'E'
                        sb.append(actualChar);
                        exponentFlag = true;
                        numberFlag = false;
                        continue;
                    } else if (actualChar == 46 && !decimalFlag) {  //DECIMAL '.'
                        sb.append(actualChar);
                        decimalFlag = true;
                        continue;
                    } else if(!(actualChar == 32 || isArithmeticOperator(actualChar))) {
                        if(actualChar == 46) {
                            throw new SyntaxException(new Formatter().format("Error en la linea %d. Los numeros" +
                                    " no pueden tener mas de un \".\" decimal.", line.getLineNumber()).toString());
                        } else {
                            throw new SyntaxException(new Formatter().format("Error en la linea %d. Los numeros" +
                                    "no pueden contener caracteres que no sean numericos o exponenciales.", line.getLineNumber()).toString());
                        }
                    }
                } else {    // exponentFlag == true
                    if (!expSignFlag) {
                        if (Character.isDigit(actualChar)) {
                            sb.append(actualChar);
                            expSignFlag = true;
                            if(i != code.length()-1)
                                continue;
                        } else if (actualChar == 43 || actualChar == 45) {
                            sb.append(actualChar);
                            expSignFlag = true;
                            continue;
                        }
                    } else if (Character.isDigit(actualChar)) {
                        sb.append(actualChar);
                        if(!numAfterExpSignFlag)
                            numAfterExpSignFlag = true;
                        if(i != code.length()-1)
                            continue;
                    } else if(!(actualChar == 32 || (isArithmeticOperator(actualChar) && numAfterExpSignFlag))) {
                        throw new SyntaxException(new Formatter().format("Error en la linea %d. Los numeros" +
                                " exponenciales no pueden contener \"%c\".", line.getLineNumber(), actualChar).toString());
                    }
                }

                if(actualChar == 32 || isArithmeticOperator(actualChar) || isLoopEnding) {
                    if(numberFlag) numberFlag = false;
                    if(exponentFlag) {
                        exponentFlag = false;
                        numberFlag = false;
                        expSignFlag = false;
                        numAfterExpSignFlag = false;
                    }
                    lexema = sb.toString();
                    tokens.add(new Token(lexema, "numero", Integer.toString(attrib++)));
                    sb.delete(0,sb.length());
                }
            }

            if(sb.length() == 1) {
                char single = sb.charAt(0);
                isActualAlphabetic = Character.isAlphabetic(actualChar);
                isActualEqualChar = actualChar == 61;
                isActualDigit = Character.isDigit(actualChar);
                isActualEspace = actualChar == 32;

                if (single == 60 || single == 62) {
                    if (isActualEspace || isActualAlphabetic || isActualDigit) {
                        lexema = sb.toString();
                        tokens.add(new Token(lexema, "opL", "-"));
                        sb.delete(0, sb.length());
                    } else if (isActualEqualChar) {
                        sb.append(actualChar);
                        lexema = sb.toString();
                        tokens.add(new Token(lexema, "opL", "-"));
                        sb.delete(0, sb.length());
                        continue;
                    }

                } else if (single == 33 || single == 61) {   // actual == '!' || == '='
                    if (isActualEqualChar) {    // siguiente == '='
                        sb.append(actualChar);
                        lexema = sb.toString();
                        tokens.add(new Token(lexema, "opL", "-"));
                        sb.delete(0, sb.length());
                        continue;
                    } else {
                        // Throws an exception because the characters '!' or '=' only can have as next character an
                        // '=' other type of character is an error.
                        throw new SyntaxException(new Formatter().format("Error en la linea %d. El caracter " +
                                "\"%c\" solo puede tener despues de si el caracter \"=\".", line.getLineNumber(), single).toString());
                    }
                //EVALUACION PARA OPERADORES ARITMETICOS
                } else if (isArithmeticOperator(single)) {
                    if (isActualEspace || isActualAlphabetic || isActualDigit) {
                        lexema = sb.toString();
                        tokens.add(new Token(lexema, "op", "-"));
                        sb.delete(0, sb.length());

                        if(isLoopEnding) {
                            if(isActualAlphabetic) {
                                keyWordVariableTokens(line, tokens, sb, actualChar, true);
                            } else if(isActualDigit) {
                                tokens.add(new Token(Character.toString(actualChar),"numero", Integer.toString(attrib++)));
                            }
                        }
                    } else {
                        // Throw an exception because the arithmetic operators only can have as next an number, letter or space,
                        // other type of character is an error.
                        throw new SyntaxException(new Formatter().format("Error en la linea %d. Los operadores aritmeticos" +
                                "solo pueden tener despues de si un espacio, una letra o un digito.", line.getLineNumber()).toString());
                    }
                //EVALUACION PARA NUMEROS
                } else if(Character.isDigit(single)) {
                    if(Character.isDigit(actualChar)) {
                        numberFlag = true;
                    } else if(actualChar == 46) {
                        decimalFlag = true;
                        numberFlag = true;
                    }else if( actualChar == 69 || actualChar == 101) { //EXPONENTE 'e' 'E'
                        exponentFlag = true;
                        numberFlag = false;
                    }
                //EVALUACION PARA ASIGNACION
                } else if (single == 58 && isActualEqualChar) { // sb == ':'
                    sb.append(actualChar);
                    lexema = sb.toString();
                    tokens.add(new Token(lexema, "asign", "-"));
                    sb.delete(0, sb.length());
                    continue;
                } else if(single == 58){
                    // Throw an exception because the character ':' only can have as next an character
                    // '=' other type of character is an error.
                    throw new SyntaxException(new Formatter().format("Error en la linea %d. El caracter " +
                            "\":\" solo puede tener despues de si el caracter \"=\".", line.getLineNumber()).toString());
                } else if(isLoopEnding && Character.isAlphabetic(single)) {
                    keyWordVariableTokens(line, tokens, sb, actualChar, true);
                }
            } else if(sb.length() == 0) {
                if(isLoopEnding && Character.isAlphabetic(actualChar)) {
                    keyWordVariableTokens(line, tokens, sb, actualChar, true);
                }
            }else if(sb.length() > 1) {//EVALUACION PARA PALABRA RESERVADA O VARIABLES
                keyWordVariableTokens(line, tokens, sb, actualChar, isLoopEnding);
            }

            if(actualChar != 32) {
                sb.append(actualChar);
            } else {
                sb.delete(0,sb.length());
            }

        }
        return tokens;
    }

    private void keyWordVariableTokens(Line line, LinkedList<Token> tokens, StringBuilder sb,
                                       char actualChar, boolean isLoopEnding) throws SyntaxException {
        String lexema;
        if ((actualChar >= 32 && actualChar <= 34) // ESPACIO, !, "
                || (isArithmeticOperator(actualChar))   // OPERADORES
                || (actualChar >= 58 && actualChar <= 62)    // COMPARACION
                || isLoopEnding
        ) {
            if(isLoopEnding) {
                sb.append(actualChar);
            }
            lexema = sb.toString();
            if (isKeyWord(lexema)) {
                tokens.add(new Token(lexema, lexema, "-"));
                sb.delete(0, sb.length());
            } else if (isVariable(lexema)) {

                Token token = null;
                for (Token t : TOKENS_TABLE) {
                    if (t.getLexema().equals(lexema)) {
                        token = t;
                        break;
                    }
                }
                String lastTokenStr = "";
                if(tokens.size() > 0)
                    lastTokenStr = tokens.getLast().getToken();
                if(isDataType(lastTokenStr)) { // Verifying that the variable is declare
                    if(token == null) { // If token is null indicates that the variable is not registered
                        token = new Token(lexema, "Id", Integer.toString(attrib++));
                    } else { // The variable name exists and is trying of re-create it with a new datatype.
                        // Throw an exception because the variable exists and the user is trying re-create it
                        throw new SyntaxException(new Formatter().format("Error en la linea %d. La variable \"%s\" " +
                                "ya esta declarada.", line.getLineNumber(), lexema).toString());
                    }
                } else if (token == null) { // The last token is not a data type and the variable already not exist in the table
                    // Throw an exception because the variable not exist in the table and the user is trying to use it.
                    throw new SyntaxException(new Formatter().format("Error en la linea %d. La variable \"%s\" " +
                            "no existe en el codigo.", line.getLineNumber(), lexema).toString());
                }
                // The token is not null so it already exist in the table and the user is trying to use it or
                // the token don't exist but the user are declaring it.
                tokens.add(token);
                sb.delete(0, sb.length());
            } else {
                if(invalidCharAtBeginVarFlag) {
                    // Throw an exception because the variable only can starts with alphabetic character
                    throw new SyntaxException(new Formatter().format("Error en la linea %d. La variable \"%s\" " +
                            "solo puede iniciar con una letra", line.getLineNumber(), lexema).toString());
                }
                if(invalidCharInVarFlag) {
                    // Throw an exception because the variable only can contain alphabetic or digit or underscore characters
                    throw new SyntaxException(new Formatter().format("Error en la linea %d. La variable \"%s\" " +
                            "solo puede estar conformada por letras, digitos o \"_\"", line.getLineNumber(), lexema).toString());
                }
            }
        }
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

    private boolean isArithmeticOperator(char caracter) {
        return caracter == 42 || caracter == 43 || caracter == 45 || caracter == 47;
    }

}
