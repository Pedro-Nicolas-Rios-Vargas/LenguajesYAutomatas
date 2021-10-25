package src.ordevlang.tablatokens.tree;

import com.sun.source.tree.Tree;
import src.datastructures.Queue;
import src.ordevlang.tablatokens.FileHandler;
import src.ordevlang.tablatokens.SyntaxException;
import src.ordevlang.tablatokens.data.Line;
import src.ordevlang.tablatokens.data.Token;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TreeAnalyzer {

    private final LangTree tree;
    private InstructionNode blockNode;
    private final String[] LANG_PATTERNS;
    private final BufferedWriter BW;
    private final String DEBUG_PATH;
    private final Queue<Line> LINES;
    private int attrib, blockCount;
    private boolean flagInBlock;

    public TreeAnalyzer(String fileName) {
        blockCount = 0;
        attrib = 2;
        flagInBlock = false;
        tree = new LangTree();
        BW = new BufferedWriter(new OutputStreamWriter(System.out));
        DEBUG_PATH = System.getProperty("user.dir") + "\\Depurado\\";
        LINES = FileHandler.readFile(new File(DEBUG_PATH + fileName));

        LANG_PATTERNS = new String[]{
                "(?<tipo>ent)\\s(?<variable>([a-zA-Z][a-zA-Z\\d_]*))((([\\s]?)(?<assign>(:=))([\\s]?)((?<valor1>(\\d{1,5}))|(?<var1>([a-zA-Z][a-zA-Z\\d_]*)))([\\s]?)((?<oper>([+*/-]))([\\s]?)((?<valor2>(\\d{1,5}))|(?<var2>([a-zA-Z][a-zA-Z\\d_]*))))?))?",
                "(?<tipo>cadena)\\s(?<variable>([a-zA-Z][a-zA-Z\\d_]*))((([\\s]?)(?<assign>(:=))([\\s]?)((?<valor1>\".+\")|(?<var1>([a-zA-Z][a-zA-Z\\d_]*)))))?",
                "(?<tipo>booleano)\\s(?<variable>([a-zA-Z][a-zA-Z\\d_]*))(([\\s]?)(?<assign>:=)([\\s]?)((?<valor>([01]))|(((?<valor1>(\\d{1,5}))|(?<var1>([a-zA-Z][a-zA-Z\\d_]*)))([\\s]?)(?<opL>(([><][=]?)|([!=]=)))([\\s]?)((?<valor2>(\\d{1,5}))|(?<var2>([a-zA-Z][a-zA-Z\\d_]*))))))?",
                "(?<inst>leer)\\s(?<variable>([a-zA-Z][a-zA-Z\\d_]*))",
                "(?<inst>(impri(ln)?))\\s((?<valor>(\\d{1,5}))|(?<cad>(\".+\"))|(?<variable>([a-zA-Z][a-zA-Z\\d_]*)))",
                "(?<variable>([a-zA-Z][a-zA-Z\\d_]*))([\\s]?)(?<assign>(:=))([\\s]?)((?<valor1>(\\d{1,5}))|(?<cad1>(\".+\"))|(?<var1>([a-zA-Z][a-zA-Z\\d_]*)))(([\\s]?)((?<oper>([+*/-]))|(?<opL>(([><][=]?)|([!=]=))))([\\s]?)((?<valor2>(\\d{1,5}))|(?<cad2>(\".+\"))|(?<var2>([a-zA-Z][a-zA-Z\\d_]*))))?",
                "(?<inst>si)\\s((?<valor>([01]))|(((?<valor1>(\\d{1,5}))|(?<var1>([a-zA-Z][a-zA-Z\\d_]*)))([\\s]?)(?<opL>(([><][=]?)|([!=]=)))([\\s]?)((?<valor2>(\\d{1,5}))|(?<var2>([a-zA-Z][a-zA-Z\\d_]*)))))\\s(?<inst1>tons:)",
                "(?<inst>finsi)",
                "(?<inst>para)\\s(?<variable>([a-zA-Z][a-zA-Z\\d_]*));([\\s]?)\\k<variable>([\\s]?)(?<opL>(([><][=]?)|([!=]=)))([\\s]?)(?<valor>(\\d{1,5}));([\\s]?)\\k<variable>(?<modi>[+]{2}|[-]{2}):",
                "(?<inst>finpara)"

        };
    }

    /*public static void main(String[] args) {
        String[] codeLines = new String[] {
                "1 si 43 > 12 tons:",
                "2 impri \"Hoal\"",
                "3 si 13 == 43 tons:",
                "4 impriln \"Si es igual\"",
                "5 finsi",
                "6 impriln \"Como andamios?\"",
                "7 finsi",
                "8 ent dasd12 := 43",
                "9 impri dasd12"
        };
        LinkedList<Line> lines = new LinkedList<>();
        for (String rawLine : codeLines) {
            lines.add(new Line(rawLine));
        }
        TreeAnalyzer analyzer = new TreeAnalyzer();
        try {
            for (Line line : lines) {
                analyzer.analizeLine(line);
            }
            LangTree arbol = analyzer.getLangTree();
            arbol.buildTokensTable();
        } catch (SyntaxException sE) {
            System.out.println(sE.getMessage());
        }
        analyzer.tree.printTablaTokens();
    }*/

    public void buildingAST() throws SyntaxException {
        for (Line line : LINES) {
            analizeLine(line);
        }
    }

    public LinkedList<Token> getTokensTable() {
        tree.buildTokensTable();
        tree.printTablaTokens();
        return tree.getTokensTable();
    }

    public void analizeLine(Line line) throws SyntaxException {
        InstructionNode lineNode;
        boolean flagNewBlock = false;
        boolean flagEndBlock = false;
        String codeLine = line.getCode();
        int numberLine = line.getLineNumber();

        if ((lineNode = entVarDeclaration(codeLine, numberLine)) != null) {
            //lineNode = entVarDeclaration(codeLine, numberLine);
        } else if ((lineNode = cadVarDeclaration(codeLine, numberLine)) != null) {
            //lineNode = cadVarDeclaration(codeLine, numberLine);
        } else if ((lineNode = boolVarDeclaration(codeLine, numberLine)) != null) {
            //lineNode = boolVarDeclaration(codeLine, numberLine);
        } else if ((lineNode = instLeerCall(codeLine, numberLine)) != null) {
            //lineNode = instLeerCall(codeLine, numberLine)
        } else if ((lineNode = instImprCall(codeLine, numberLine)) != null) {
            //lineNode = instImprCall(codeLine, numberLine);
        } else if ((lineNode = varAssign(codeLine, numberLine)) != null) {
            //lineNode = varAssign(codeLine, numberLine);
        } else if ((lineNode = instSiCall(codeLine, numberLine)) != null) {
            //lineNode = instSiCall(codeLine, numberLine);
            //blockNode = lineNode;
            flagNewBlock = true;
        } else if ((lineNode = instFinSiCall(codeLine, numberLine)) != null) {
            //lineNode = instFinSiCall(codeLine, numberLine);
            flagEndBlock = true;
        } else if ((lineNode = instParaCall(codeLine, numberLine)) != null) {
            // Is a for loop
            flagNewBlock = true;
        } else if ((lineNode = instFinParaCall(codeLine, numberLine)) != null) {
            // Is the end of a for loop
            flagEndBlock = true;
        } else {
            throw new SyntaxException(String.format("Error en la línea: %d. " +
                    "Error de sintaxis. Línea fuera del lenguaje.", numberLine));
        }
        if (flagInBlock) {
            if (flagNewBlock) {
                blockCount++;
                blockNode.addLine(lineNode);
                blockNode = lineNode;
            } else if (flagEndBlock) {
                blockCount--;
                blockNode.addLine(lineNode);
                if (blockCount == 0) {
                    flagInBlock = false;
                } else {
                    blockNode = blockNode.getParent(); // It might be return the parent if block
                }
            } else
                blockNode.addLine(lineNode);
        } else {
            if (flagNewBlock) {
                flagInBlock = true;
                blockCount++;
                blockNode = lineNode;
            }
            tree.addLine(lineNode);
        }

    }

    private InstructionNode entVarDeclaration(String line, int lineNumber) throws SyntaxException {
        LinkedList<Token> tokens = new LinkedList<>();
        Pattern pattern = Pattern.compile(LANG_PATTERNS[0]);
        Matcher matchCodeLine = pattern.matcher(line);
        boolean isValid = matchCodeLine.matches();

        if (isValid) {
            String tipo = matchCodeLine.group("tipo");
            String variable = matchCodeLine.group("variable");
            String assign = matchCodeLine.group("assign");
            String valor1 = matchCodeLine.group("valor1");
            String var1 = matchCodeLine.group("var1");
            String oper = matchCodeLine.group("oper");
            String valor2 = matchCodeLine.group("valor2");
            String var2 = matchCodeLine.group("var2");

            // These two exist if the line matches the pattern
            tokens.add(tokenizingType(tipo));
            tokens.add(tokenizingVariable(lineNumber, variable));


            tokens.add(tokenizingAssign(assign));
            if (tokens.peekLast() != null) { // Checks if the last token (assign) exist if it didn't the other tokens don't exist
                tokens.add(tokenizingValue(lineNumber, tipo, null, valor1, var1));
                tokens.add(tokenizingArithmeticOperator(oper));
                if (tokens.peekLast() != null) // if opers was null the execution of eval second var is unnecessary
                    tokens.add(tokenizingValue(lineNumber, tipo, null, valor2, var2));
            }

            InstructionNode instNode;
            // The first element in the stack always are the type of the variable
            instNode = new InstructionNode(null, tokens.remove());

            for (Token token : tokens) {
                if (token != null)
                    instNode.addInstruction(new InstructionNode(null, token));
            }
            return instNode;

        }
        return null;
    }

    private InstructionNode cadVarDeclaration(String line, int lineNumber) throws SyntaxException {
        LinkedList<Token> tokens = new LinkedList<>();
        Pattern pattern = Pattern.compile(LANG_PATTERNS[1]);
        Matcher matchCodeLine = pattern.matcher(line);
        boolean isValid = matchCodeLine.matches();

        if (isValid) {
            String tipo = matchCodeLine.group("tipo");
            String variable = matchCodeLine.group("variable");
            String assign = matchCodeLine.group("assign");
            String valor1 = matchCodeLine.group("valor1");
            String var1 = matchCodeLine.group("var1");

            tokens.add(tokenizingType(tipo));
            tokens.add(tokenizingVariable(lineNumber, variable));

            tokens.add(tokenizingAssign(assign));
            if (tokens.peekLast() != null) { // Checks if the last token (assign) exist if it didn't the other tokens don't exist
                tokens.add(tokenizingValue(lineNumber, tipo, null, valor1, var1));
            }

            InstructionNode instNode;
            // The first element in the stack always are the type of the variable
            instNode = new InstructionNode(null, tokens.remove());

            for (Token token : tokens) {
                if (token != null)
                    instNode.addInstruction(new InstructionNode(null, token));
            }
            return instNode;
        }
        return null;
    }

    private InstructionNode boolVarDeclaration(String line, int lineNumber) throws SyntaxException {
        LinkedList<Token> tokens = new LinkedList<>();
        Pattern pattern = Pattern.compile(LANG_PATTERNS[2]);
        Matcher matchCodeLine = pattern.matcher(line);
        boolean isValid = matchCodeLine.matches();

        if (isValid) {
            String tipo = matchCodeLine.group("tipo");
            String variable = matchCodeLine.group("variable");
            String assign = matchCodeLine.group("assign");
            String valor = matchCodeLine.group("valor");
            String valor1 = matchCodeLine.group("valor1");
            String var1 = matchCodeLine.group("var1");
            String opL = matchCodeLine.group("opL");
            String valor2 = matchCodeLine.group("valor2");
            String var2 = matchCodeLine.group("var2");

            // These two exist if the line matches the pattern
            tokens.add(tokenizingType(tipo));
            tokens.add(tokenizingVariable(lineNumber, variable));

            tokens.add(tokenizingAssign(assign));
            if (tokens.peekLast() != null) { // Checks if the last token (assign) exist if it didn't the other tokens don't exist
                tokens.add(tokenizingValue(lineNumber, tipo, valor, valor1, var1));
                tokens.add(tokenizingLogicOperator(opL));
                if (tokens.peekLast() != null)
                    tokens.add(tokenizingValue(lineNumber, tipo, null, valor2, var2));
            }

            InstructionNode instNode;
            // The first element in the stack always are the type of the variable
            instNode = new InstructionNode(null, tokens.remove());

            for (Token token : tokens) {
                if (token != null)
                    instNode.addInstruction(new InstructionNode(null, token));
            }
            return instNode;
        }
        return null;
    }

    private InstructionNode instLeerCall(String line, int lineNumber) throws SyntaxException {
        LinkedList<Token> tokens = new LinkedList<>();
        Pattern pattern = Pattern.compile(LANG_PATTERNS[3]);
        Matcher matchCodeLine = pattern.matcher(line);
        boolean isValid = matchCodeLine.matches();

        if (isValid) {
            String inst = matchCodeLine.group("inst");
            String variable = matchCodeLine.group("variable");

            tokens.add(tokenizingInstructionCall(inst));
            if (tree.existTheVariable(variable)) {
                Token varToken = tree.getVariableToken(variable);
                tokens.add(varToken);
            } else {
                throw varDoesNotExist(lineNumber, variable);
            }

            InstructionNode instNode;
            // The first element in the stack always are the type of the variable
            instNode = new InstructionNode(null, tokens.remove());

            for (Token token : tokens) {
                if (token != null)
                    instNode.addInstruction(new InstructionNode(null, token));
            }
            return instNode;
        }
        return null;
    }

    private InstructionNode instImprCall(String line, int lineNumber) throws SyntaxException {
        LinkedList<Token> tokens = new LinkedList<>();
        Pattern pattern = Pattern.compile(LANG_PATTERNS[4]);
        Matcher matchCodeLine = pattern.matcher(line);
        boolean isValid = matchCodeLine.matches();

        if (isValid) {
            String inst = matchCodeLine.group("inst");
            String valor = matchCodeLine.group("valor");
            String cad = matchCodeLine.group("cad");
            String variable = matchCodeLine.group("variable");

            tokens.add(tokenizingInstructionCall(inst));
            if (valor != null) {
                tokens.add(new Token(valor, "number", String.valueOf(attrib)));
                attrib += 2;
            } else if (cad != null) {
                tokens.add(new Token(cad, "literal", String.valueOf(attrib)));
                attrib += 2;
            } else if (variable != null) {
                if (tree.existTheVariable(variable)) {
                    Token variableRef = tree.getVariableToken(variable);
                    String tipo = tree.getVariableTypeToken(variable).getLexema();
                    if (tree.isTheVariableInitialized(tipo, variable)) {
                        tokens.add(variableRef);
                    } else {
                        throw varNotInitialized(lineNumber, variable);
                    }
                } else {
                    throw varDoesNotExist(lineNumber, variable);
                }

            }

            InstructionNode instNode;
            // The first element in the stack always are the type of the variable
            instNode = new InstructionNode(null, tokens.remove());

            for (Token token : tokens) {
                if (token != null)
                    instNode.addInstruction(new InstructionNode(null, token));
            }
            return instNode;

        }
        return null;
    }

    private InstructionNode varAssign(String line, int lineNumber) throws SyntaxException {
        LinkedList<Token> tokens = new LinkedList<>();
        Pattern pattern = Pattern.compile(LANG_PATTERNS[5]);
        Matcher matchCodeLine = pattern.matcher(line);
        boolean isValid = matchCodeLine.matches();

        if (isValid) {
            String variable = matchCodeLine.group("variable");
            String assign = matchCodeLine.group("assign");
            String valor1 = matchCodeLine.group("valor1");
            String cad1 = matchCodeLine.group("cad1");
            String var1 = matchCodeLine.group("var1");
            String oper = matchCodeLine.group("oper");
            String opL = matchCodeLine.group("opL");
            String valor2 = matchCodeLine.group("valor2");
            String cad2 = matchCodeLine.group("cad2");
            String var2 = matchCodeLine.group("var2");

            if (tree.existTheVariable(variable)) {
                Token variableRef = tree.getVariableToken(variable);
                String tipo = tree.getVariableTypeToken(variable).getLexema();
                tokens.add(variableRef);
                tokens.add(new Token(assign, "asign", "-"));
                tokens.add(tokenizingValueAssign(lineNumber, tipo, valor1, cad1, var1));
                if (oper != null || opL != null) {
                    if (oper != null) {
                        if (tipo.equals("ent")) {
                            tokens.add(tokenizingArithmeticOperator(oper));
                        } else {
                            throw new SyntaxException(String.format("Error en la línea %d. " +
                                    "El operador \"%s\" unicamente puede ser usado en valores enteros.",
                                    lineNumber, oper));
                        }
                    } else { //if (opL != null)
                        if (tipo.equals("booleano")) {
                            tokens.add(tokenizingLogicOperator(opL));
                        } else {
                            throw new SyntaxException(String.format("Error en la línea %d. " +
                                            "El operador \"%s\" unicamente puede ser usado en valores booleanos.",
                                    lineNumber, opL));
                        }
                    }
                    tokens.add(tokenizingValueAssign(lineNumber, tipo, valor2, cad2, var2));
                }

                InstructionNode instNode;
                instNode = new InstructionNode(null, tokens.remove());

                for (Token token : tokens) {
                    if (token != null)
                        instNode.addInstruction(new InstructionNode(null, token));
                }
                return instNode;

            } else {
                throw varDoesNotExist(lineNumber, variable);
            }

        }
        return null;
    }

    private InstructionNode instSiCall(String line, int lineNumber) throws SyntaxException {
        LinkedList<Token> tokens = new LinkedList<>();
        Pattern pattern = Pattern.compile(LANG_PATTERNS[6]);
        Matcher matchCodeLine = pattern.matcher(line);
        boolean isValid = matchCodeLine.matches();

        if (isValid) {
            String inst = matchCodeLine.group("inst");
            String valor = matchCodeLine.group("valor");
            String valor1 = matchCodeLine.group("valor1");
            String var1 = matchCodeLine.group("var1");
            String opL = matchCodeLine.group("opL");
            String valor2 = matchCodeLine.group("valor2");
            String var2 = matchCodeLine.group("var2");
            String inst1 = matchCodeLine.group("inst1");

            tokens.add(tokenizingInstructionCall(inst));
            if (valor != null) {
                tokens.add(new Token(valor, "bool", String.valueOf(attrib)));
                attrib += 2;
            } else {
                tokens.add(tokenizingValue(lineNumber, "ent", null, valor1, var1));
                tokens.add(tokenizingLogicOperator(opL));
                tokens.add(tokenizingValue(lineNumber, "ent", null, valor2, var2));
            }
            tokens.add(tokenizingInstructionCall(inst1));

            InstructionNode instNode;
            // The first element in the stack always are the type of the variable
            instNode = new InstructionNode(null, tokens.remove(), true);

            for (Token token : tokens) {
                if (token != null)
                    instNode.addInstruction(new InstructionNode(null, token));
            }
            return instNode;
        }
        return null;
    }

    private InstructionNode instFinSiCall(String line, int lineNumber) {
        Pattern pattern = Pattern.compile(LANG_PATTERNS[7]);
        Matcher matchCodeLine = pattern.matcher(line);
        boolean isValid = matchCodeLine.matches();

        if (isValid) {
            String inst = matchCodeLine.group("inst");
            Token instruction = tokenizingInstructionCall(inst);

            return new InstructionNode(null, instruction);
        }
        return null;
    }

    private InstructionNode instParaCall(String line, int lineNumber) throws SyntaxException {
        LinkedList<Token> tokens = new LinkedList<>();
        Pattern pattern = Pattern.compile(LANG_PATTERNS[8]);
        Matcher matchCodeLine = pattern.matcher(line);
        boolean isValid = matchCodeLine.matches();

        if (isValid) {
            String inst = matchCodeLine.group("inst");
            String variable = matchCodeLine.group("variable");
            String operador = matchCodeLine.group("opL");
            String valor = matchCodeLine.group("valor");
            String modificador = matchCodeLine.group("modi");

            tokens.add(tokenizingInstructionCall(inst));
            if (tree.existTheVariable(variable)) {
                Token varRef = tree.getVariableToken(variable);
                String tipo = tree.getVariableTypeToken(variable).getLexema();

                if (tipo.equals("ent")) {
                    if (tree.isTheVariableInitialized(tipo, variable)) {
                        tokens.add(varRef);
                        tokens.add(tokenizingLogicOperator(operador));
                        tokens.add(tokenizingValueAssign(lineNumber, tipo, valor, null, null));
                        //tokens.add(varRef); // Unnecessary addition
                        tokens.add(tokenizingArithmeticOperator(modificador));
                    } else {
                        throw varNotInitialized(lineNumber, variable);
                    }
                } else {
                    throw varsDifferentType(lineNumber, variable);
                }

            } else {
                throw varDoesNotExist(lineNumber, variable);
            }

            InstructionNode instNode;
            instNode = new InstructionNode(null, tokens.remove(), true);

            for(Token token : tokens) {
                if (token != null) {
                    instNode.addInstruction(new InstructionNode(null, token));
                }
            }
            return instNode;

        }
        return null;
    }

    private InstructionNode instFinParaCall(String line, int lineNumber) {
        Pattern pattern = Pattern.compile(LANG_PATTERNS[9]);
        Matcher matchCodeLine = pattern.matcher(line);
        boolean isValid = matchCodeLine.matches();

        if (isValid) {
            String inst = matchCodeLine.group("inst");
            Token instruction = tokenizingInstructionCall(inst);

            return new InstructionNode(null, instruction);
        }
        return null;
    }

    private Token tokenizingType(String tipo) {
        return new Token(tipo, tipo, "-");
    }

    private Token tokenizingInstructionCall(String inst) {
        return new Token(inst, inst, "-");
    }

    private Token tokenizingVariable(int lineNumber, String variable) throws SyntaxException {
        if (!tree.existTheVariable(variable)) {
            Token token = new Token(variable, "Id", String.valueOf(attrib));
            attrib += 2;
            return token;
        } else {
            // Throws an exception because the variable all ready exist
            throw varAlreadyInUse(lineNumber, variable);
        }
    }

    private Token tokenizingAssign(String assign) {
        if (assign != null) {
            return new Token(assign, "asign", "-");
        }
        return null;
    }

    private Token tokenizingValue(int lineNumber, String tipo, String valor, String valor1, String var) throws SyntaxException {
        if (valor != null) {
            Token token = new Token(valor, "bool", String.valueOf(attrib));
            attrib += 2;
            return token;
        } else if (valor1 != null) {
            String tokensillo = "";
            int incrementoAttrib = 2;
            if (tipo.equals("ent")) {
                tokensillo = "number";
            } else if (tipo.equals("cadena")) {
                tokensillo = "literal";
            } else if (tipo.equals("booleano")) {
                tokensillo = "bool";
            }
            Token token = new Token(valor1, tokensillo, String.valueOf(attrib));
            attrib += incrementoAttrib;
            return token;
        } else if (tree.existTheVariable(var)) {
            if (tree.haveTheVariableTheSameType(tipo, var)) {
                if (tree.isTheVariableInitialized(tipo, var)) {
                    return tree.getVariableToken(var);
                } else {
                    throw varNotInitialized(lineNumber, var);
                }
            } else {
                throw varsDifferentType(lineNumber, var);
            }
        } else {
            throw varDoesNotExist(lineNumber, var);
        }
    }

    public Token tokenizingValueAssign(int lineNumber, String tipo, String valor, String cad, String var) throws SyntaxException {
        if (valor != null) {
            if (tipo.equals("ent") || tipo.equals("booleano")) {
                if (tipo.equals("ent")) {
                    Token token =  new Token(valor, "number", String.valueOf(attrib));
                    attrib += 2;
                    return token;
                } else {
                    Token token = new Token(valor, "booleano", String.valueOf(attrib));
                    attrib += 2;
                    return token;
                }
            } else {
                throw valueDifferType(lineNumber, tipo);
            }
        } else if (cad != null) {
            if (tipo.equals("cadena")) {
                Token token = new Token(cad, "literal", String.valueOf(attrib));
                attrib += 2;
                return token;
            } else {
                // Throws an exception because the valor1 only can be not null with variable of same type
                throw valueDifferType(lineNumber, tipo);
            }
        } else if (var != null) {
            if (tree.existTheVariable(var)) {
                Token variable1Ref = tree.getVariableToken(var);
                String var1Tipo = tree.getVariableTypeToken(var).getLexema();
                if (tipo.equals(var1Tipo)) {
                    if (tree.isTheVariableInitialized(var1Tipo, var)) {
                        return variable1Ref;
                    } else {
                        // Throws an exception because the var1 is not initialized
                        throw varNotInitialized(lineNumber, var);
                    }
                }  else {
                    // Throws an exception because the variable type and var1 type aren't equals
                    throw varsDifferentType(lineNumber, var);
                }
            } else {
                // Throws an exception because the var1 doesn't exist
                throw varDoesNotExist(lineNumber, var);
            }
        }
        return null;
    }

    private Token tokenizingArithmeticOperator(String oper) {
        if (oper != null) {
            return new Token(oper, "op", "-");
        }
        return null;
    }

    private Token tokenizingLogicOperator(String opL) {
        if (opL != null) {
            return new Token(opL, "opL", "-");
        }
        return null;
    }

    public LangTree getLangTree() {
        return tree;
    }

    private SyntaxException varDoesNotExist(int lineNumber, String variable) {
        return new SyntaxException(String.format("Error en la línea %d. " +
                " La variable \"%s\" no existe.", lineNumber, variable));
    }

    private SyntaxException varAlreadyInUse(int lineNumber, String variable) {
        return new SyntaxException(String.format("Error en la línea %d. " +
                "La variable \"%s\" ya esta siendo usada", lineNumber, variable));
    }

    private SyntaxException varNotInitialized(int lineNumber, String variable) {
        return new SyntaxException(String.format("Error en la línea %d. " +
                "La variable \"%s\" no esta inicializada.", lineNumber, variable));
    }

    private SyntaxException varsDifferentType(int lineNumber, String variable) {
        return new SyntaxException(String.format("Error en la línea %d. " +
                "La variable \"%s\" no es del mismo tipo que la variable de asignación.",
                lineNumber, variable));
    }

    private SyntaxException valueDifferType(int lineNumber, String tipo) {
        return new SyntaxException(String.format("Error en la línea %d. " +
                "El valor a asignar no es del mismo tipo del de la variable (\"%s\")", lineNumber, tipo));
    }


}
