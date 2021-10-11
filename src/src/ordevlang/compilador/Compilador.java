package src.ordevlang.compilador;

import src.ordevlang.LangKeyWords;
import src.ordevlang.compilador.AsmMaker;
import src.ordevlang.tablatokens.data.Token;

import java.io.IOException;
import java.util.LinkedList;

public class Compilador {
    private final LinkedList<Token> TOKEN_TABLE;

    private final LinkedList<String> STACK_VARIABLES;
    private final LinkedList<String> EXTRA_VARIABLES;
    private final LinkedList<String> DATA_VARIABLES;
    private final LinkedList<String> CODE_LINES;
    private final LinkedList<Variable> DECLARED_VARIABLES;
    private final LinkedList<String[]> SI_BLOCK_FLAGS;
    private Variable varDeclared, assignVar;
    private boolean flagNewLine;
    private int printCount, siFlagCount;
    private String line, directive, space;

    public Compilador (LinkedList<Token> tokenTable) {
        this.TOKEN_TABLE = tokenTable;
        this.line = "";
        this.directive = "";
        this.space = "";
        this.printCount = 1;
        this.siFlagCount = 0;

        this.STACK_VARIABLES = new LinkedList<>();
        this.EXTRA_VARIABLES = new LinkedList<>();
        this.DATA_VARIABLES = new LinkedList<>();
        this.CODE_LINES = new LinkedList<>();
        this.DECLARED_VARIABLES = new LinkedList<>();
        this.SI_BLOCK_FLAGS = new LinkedList<>();
        this.varDeclared = null;
        this.assignVar = null;

        this.flagNewLine = false;
    }

    public void compilar(String fileName, boolean inTest) {
        int tokenCount = 0;
        LinkedList<Token> lineTokens = new LinkedList<>();
        Token token;
        String tokenType;
        AsmMaker asmBuilder = null;
        try {
            asmBuilder = new AsmMaker(fileName, inTest);
        } catch (IOException ioE) {
            System.out.println("Hoal " + ioE.getMessage());
        }
        CODE_LINES.add("mov bx, 0h");
        CODE_LINES.add("mov dx, 184Fh");
        CODE_LINES.add("clear_Screen bx,dx");
        CODE_LINES.add("mov dx, 0h");
        CODE_LINES.add("point_Cursor dh, dl");

        while (tokenCount < TOKEN_TABLE.size()) {
            token = TOKEN_TABLE.get(tokenCount);
            tokenType = token.getToken();

            if (tokenType.equals("endl")) {
                lineTokenParser(lineTokens);
                lineTokens.clear();
            } else {
                lineTokens.add(token);
            }
            tokenCount++;
        }

        assert asmBuilder != null;
        asmBuilder.loadStackAllocations(STACK_VARIABLES);
        asmBuilder.loadExtraAllocations(EXTRA_VARIABLES);
        asmBuilder.loadDatosAllocations(DATA_VARIABLES);
        asmBuilder.loadCodigoAllocations(CODE_LINES);

        asmBuilder.initBuildFile();
    }

    private void lineTokenParser(LinkedList<Token> lineTokens) {
        Token token;
        String lexema, tokenType;

        token = lineTokens.get(0);
        lexema = token.getLexema();
        tokenType = token.getToken();

        if (tokenIsDataType(tokenType)) {
            directiveGenerator(tokenType);
            defineVariableDeclaration(lineTokens);
        } else if (tokenType.equals("Id")) {
            assignVar = findVar(lexema);
            defineVarInitialization(lineTokens, assignVar);
        } else if (tokenType.equals("leer")) {
            defineLeerInstruction(lineTokens);
        } else if (tokenType.equals("impri") ||
                tokenType.equals("impriln")) {
            defineImpriInstruction(lineTokens);
        } else if (tokenType.equals("si")) {
            defineSiBlock(lineTokens);
        } else if (tokenType.equals("finsi")) {
            String[] flags = SI_BLOCK_FLAGS.removeLast();
            CODE_LINES.add(flags[1] + ":"); // GralBlockFlag
        }
    }

    private void defineVariableDeclaration(LinkedList<Token> lineTokens) {
        int numberOfTokens = lineTokens.size();
        //String varType; // not used, but probably needed in future updates
        String lexema, tokenType, attrib, varID;
        Variable assignVariable;
        int tokenCount = 1; // The token 0 are evaluated at the start of lineTokenParser
        // The first token of lineTokens are evaluated (is a data type because variable declaration)
        // So, the next token is the name of the variable to be declared
        Token variableName = lineTokens.get(tokenCount);
        lexema = variableName.getLexema();
        attrib = variableName.getAtrib();
        //varType = varDeclarated.getType();
        varID = generateVarID(attrib);
        line = varID + directive; // line = vN db | N the attrib of var

        varDeclared.setVarName(lexema);
        varDeclared.setVarID(varID);
        varDeclared.setDirective(directive);
        assignVariable = varDeclared;
        if (numberOfTokens == 2 || numberOfTokens > 4) {
            addVarToDataList(); // The var is added
            /* This if is for next tier for compiler.
             IDEA
             When a variable of type ent is declared, it needs have 2 states in memory, and string state and
             him pure state, the hexadecimal number of contained value.

             Making use of this second state for store the value in pure hexadecimal make more faster the program
             cause without this state the asm needs make 3 conversions: 1.- for convert a string to integer.
             2.- for conver a hexadecimal value to decimal. 3.- for convert the decimal to string.

             if (varType.equals("ent")) {
                 varDeclarated = new Variable(varType);
                 varID = generateVarID(attrib + 1); // make an variable to contain the hex value of number introduced.
                 directive = " dw ";
                 space = "1, ?, 1 dup(?)";
                 varDeclarated.setVarName(lexema);
                 varDeclarated.setVarID(varID);
                 varDeclarated.setDirective(directive);
                 line = varID + directive;
                 addVarToDataList();
             } */
        } else if (numberOfTokens == 4) {
            tokenCount += 2; // Passing the assign token (assign : 2) value : 3
            Token assignValue = lineTokens.get(tokenCount); // Get the token of value to assign in the variable (variableName)
            lexema = assignValue.getLexema();
            tokenType = assignValue.getToken();
            //attrib = assignValue.getAtrib();

            if (tokenIsVariableValue(tokenType)) {
                line += lexema;
                addVarToDataList();
            } else if (tokenType.equals("Id")) {
                // TODO: Make an method for copy the value from a declared an initialized variable to another
            }
        }
        if (numberOfTokens > 4){ // numberOfTokens > 4
            // If true, the variable of assign is actually declared, the only think to do here is manage the values.
            tokenCount += 2; // Passing the assign token (assign : 2) value : 3
            Token value1 = lineTokens.get(tokenCount);
            Variable variable1;
            tokenCount++; // value : 4
            Token operator = lineTokens.get(tokenCount);
            tokenCount++; // value : 5
            Token value2 = lineTokens.get(tokenCount);
            Variable variable2;

            variable1 = variableOfOperation(value1);
            variable2 = variableOfOperation(value2);
            regOperation(operator, variable1, variable2, assignVariable);
            CODE_LINES.add(line);
        }
    }


    private void defineVarInitialization(LinkedList<Token> lineTokens, Variable assignVar) {
        int numberOfTokens = lineTokens.size();
        int tokenCount = 2; // token[0] = assignVar, token[1] = asignToken, token[2..] = assign values
        String lexema, tokenType;
        Token value1 = lineTokens.get(tokenCount);
        if (numberOfTokens == 3) {
            lexema = value1.getLexema();
            tokenType = value1.getToken();
            //attrib = assignValue.getAtrib();

            if (tokenIsVariableValue(tokenType)) {
                // Only number value token
                // TODO: Modify the number value accepted by analyzer, the ent variables are byte directive (db). So,
                //      the number value accepted need be a 255 number, or max 3 chars per number.
                line = String.format("mov %s+2, %s", assignVar.getVarID(), lexema);
                CODE_LINES.add(line);
            } else if (tokenType.equals("Id")) {
                // TODO: Make an method for copy the value from a declared an initialized variable to another
            }
        } else { // numberOfTokens > 3
            Variable variable1;
            tokenCount++; // value : 3 => op | opL
            Token operator = lineTokens.get(tokenCount);
            tokenCount++; // value : 4 => value | variable
            Token value2 = lineTokens.get(tokenCount);
            Variable variable2;

            variable1 = variableOfOperation(value1);
            variable2 = variableOfOperation(value2);
            regOperation(operator, variable1, variable2, assignVar);
            CODE_LINES.add(line);
        }
    }

    private void defineLeerInstruction(LinkedList<Token> lineTokens) {
        String varName, varID;
        Token toReadVariable = lineTokens.get(1); // 0 is the leer instruction, so token in 1 is the variable to read.
        varName = toReadVariable.getLexema();
        varDeclared = findVar(varName);
        assert varDeclared != null;
        varID = varDeclared.getVarID();
        line = String.format("read_Cad %s", varID);
        CODE_LINES.add(line);
        CODE_LINES.add(String.format("fix_Read %s", varID));
        moveCursor();
        varDeclared = null;
    }

    private void defineImpriInstruction(LinkedList<Token> lineTokens) {
        line = "imp_cad ";
        Token instruction, valueToPrint;
        instruction = lineTokens.get(0);
        valueToPrint = lineTokens.get(1);
        if (instruction.getToken().equals("impriln")) {
            flagNewLine = true;
        }
        String lexema, tokenType, attrib;
        lexema = valueToPrint.getLexema();
        tokenType = valueToPrint.getToken();
        attrib = valueToPrint.getAtrib();
        if (tokenType.equals("literal")) {
            String varID = generateVarID(attrib);
            StringBuilder sb = new StringBuilder(lexema);
            sb.insert(sb.length() - 1, '$');
            lexema = sb.toString();
            DATA_VARIABLES.add(varID + " db " + lexema);
            varDeclared = new Variable(varID, " db ", tokenType);
            DECLARED_VARIABLES.add(varDeclared);
        } else if (tokenType.equals("Id")) {
            line = "imp_var ";
            varDeclared = findVar(lexema);
        }

        assert varDeclared != null;
        line += varDeclared.getVarID();
        CODE_LINES.add(line);

        if (flagNewLine) {
            moveCursor();
            flagNewLine = false;
        }

        varDeclared = null;
    }

    private void defineSiBlock(LinkedList<Token> lineTokens) {
        flagsGenerator(); // cause the first token was the si instruction
        String lexema, sisiFlag, sinoFlag;
        String[] flags = SI_BLOCK_FLAGS.peekLast();
        assert flags != null;
        sisiFlag = flags[0];
        sinoFlag = flags[1];
        int numberOfTokens = lineTokens.size(); // max: 5
        int tokenCount = 1;
        Token value1;
        value1 = lineTokens.get(tokenCount);
        lexema = value1.getLexema();
        if (numberOfTokens == 3) {
            // cuando es un unico valor, se espera que este sea unicamennte 1 o 0,
            // si es 1 será verdadero, falso si es 0.
            CODE_LINES.add("mov dx, " + lexema);
            CODE_LINES.add("mov ax, 1");
            CODE_LINES.add("cmp dx, ax");
            CODE_LINES.add("je " + sisiFlag); // SiBlockFlag
        } else { // numberOfTokens > 3
            Variable variable1;
            tokenCount++; // value : 2 => opL
            Token operator = lineTokens.get(tokenCount);
            tokenCount++; // value : 3 => value | variable
            Token value2 = lineTokens.get(tokenCount);
            Variable variable2;
            String operationLex = "";
            lexema = operator.getLexema();

            variable1 = variableOfOperation(value1);
            variable2 = variableOfOperation(value2);
            CODE_LINES.add(String.format("comparar %s, %s", variable1.getVarID(), variable2.getVarID()));
            CODE_LINES.add("sahf");
            switch (lexema) {
                case "<":
                    operationLex = "jl";
                    break;
                case "<=":
                    operationLex = "jle";
                    break;
                case ">":
                    operationLex = "jg";
                    break;
                case ">=":
                    operationLex = "jge";
                    break;
                case "==":
                    operationLex = "je";
                    break;
                case "!=":
                    operationLex = "jne";
                    break;
            }
            line = String.format("%s %s", operationLex, sisiFlag);
            CODE_LINES.add(line);
        }
        CODE_LINES.add("jmp " + sinoFlag); // GralBlockFlag
        CODE_LINES.add(sisiFlag + ":");
    }

    private String generateVarID(String attrib) {
        return "v" + attrib;
    }

    private void addVarToDataList() {
        line += space;
        DATA_VARIABLES.add(line);
        DECLARED_VARIABLES.add(varDeclared);
        varDeclared = null;
    }

    private Variable variableOfOperation(Token value) {
        String lexema, tokenType, attrib, varType, varID;
        lexema = value.getLexema();
        tokenType = value.getToken();
        attrib = value.getAtrib();
        Variable variable = null;

        if (tokenIsVariableValue(tokenType)) { // IS number, the language don't have implemented arithmetic operations for strings
            varType = "ent";
            varDeclared = new Variable(varType);
            varID = generateVarID(attrib);
            directive = " db ";
            StringBuilder sb = new StringBuilder(lexema);
            sb.insert(sb.length() - 1, '$');
            space = sb.toString();
            varDeclared.setVarID(varID);
            varDeclared.setDirective(directive);
            variable = varDeclared;
            line = varID + directive; // vN dw numberValue
            addVarToDataList();
        } else  if (tokenType.equals("Id")) {
            variable = findVar(lexema);
        }
        return variable;
    }

    private void regOperation(Token operator, Variable var1, Variable var2, Variable assignVar) {
        String operationType = operator.getToken();
        String operation = operator.getLexema();
        String operatorLex = "";
        if (operationType.equals("op")) {
            switch (operation) {
                case "+":
                    operatorLex = "sumar";
                    break;
                case "-":
                    operatorLex = "restar";
                    break;
                case "*":
                    operatorLex = "mult";
                    break;
                case "/":
                    operatorLex = "divi";
                    break;
            }
            line = String.format("%s %s, %s, %s",
                    operatorLex,
                    var1.getVarID(),
                    var2.getVarID(),
                    assignVar.getVarID()
            );
        } /*else { // opL Logic operation
            if (operation.equals("<")) {

            } else if (operation.equals("<=")) {

            } else if (operation.equals(">")) {

            } else if (operation.equals(">=")) {

            } else if (operation.equals("==")) {

            } else if (operation.equals("!=")) {

            }
        }*/
    }

    private Variable findVar(String varName) {
        // TODO : Verifies if all findVar usages are related with varName instead of varID
        for (Variable variable : DECLARED_VARIABLES) {
            String declaredVarName = variable.getVarName();
            if (declaredVarName != null && declaredVarName.equals(varName)) {
                return variable;
            }
        }
        return null;
    }

    private void flagsGenerator() {
        String flagSiBlock = "f" + siFlagCount + 1;
        String flagGralBlock = "f" + siFlagCount + 2;
        String[] flags = new String[] {
                flagSiBlock,
                flagGralBlock
        };
        SI_BLOCK_FLAGS.add(flags);
        siFlagCount += 2;
    }

    private void moveCursor() {
        String hexCount = Integer.toHexString(printCount);
        String dhValue = (hexCount.length() == 2) ? hexCount : "0".repeat(2 - hexCount.length()) + hexCount;
        CODE_LINES.add("mov dh, " + dhValue + "h");
        CODE_LINES.add("mov dl, 00");
        CODE_LINES.add("point_Cursor dh, dl");
        printCount++;
    }

    private boolean tokenIsDataType(String tokenType) {
        return LangKeyWords.isDataType(tokenType);
    }

    private boolean tokenIsVariableValue(String tokenType) {
        return tokenType.equals("number") || tokenType.equals("literal");
    }

    private void directiveGenerator(String variableType) {
        if (variableType.equals("ent")) {
            directive = " db ";
            space = "6, ?, 6 dup(?)";
        } else if (variableType.equals("cadena")) {
            directive = " db ";
            space = "21, ?, 21 dup(?)";
        }
        varDeclared = new Variable(variableType);
    }

    static class Variable {
        private String varName;
        private String varID;
        private String directive;
        private String type;

        public Variable(String type) {
            this.varName = null;
            this.varID = null;
            this.directive = null;
            this.type = type;
        }

        public Variable(String varID, String directive, String type) {
            this.varID = varID;
            this.directive = directive;
            this.type = type;
        }

        protected void setVarID(String varID) {
            this.varID = varID;
        }

        protected String getVarID() {
            return varID;
        }

        protected void setType(String type) {
            this.type = type;
        }

        protected String getType() {
            return type;
        }

        protected void setDirective(String directive) {
            this.directive = directive;
        }

        protected String getDirective() {
            return directive;
        }

        protected  void setVarName(String varName) {
            this.varName = varName;
        }

        protected  String getVarName() {
            return varName;
        }
    }

}
