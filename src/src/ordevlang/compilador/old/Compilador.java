package src.ordevlang.compilador.old;

import java.util.LinkedList;

import java.io.IOException;

import src.ordevlang.LangKeyWords;
import src.ordevlang.compilador.AsmMaker;
import src.ordevlang.tablatokens.data.Token;

public class Compilador {

    private final LinkedList<Token> tokenTable;

    private final LinkedList<String> stackVars;
    private final LinkedList<String> extraVars;
    private final LinkedList<String> dataVars;
    private final LinkedList<String> codeLines;
    private final LinkedList<Variable> declaredVars;
    LinkedList<Variable> varsUsedOnInitialization;
    private Variable varDeclarated;
    private boolean flagImpri;
    private boolean flagNewLine;
    private boolean flagDeclarationVar; // The compiler detects a declaration an are waiting for the value
    private boolean flagInitializeVar;
    private boolean flagLeer;
    private boolean flagOperationInCourse;
    private boolean flagSumOperation;
    private boolean flagSubOperation;
    private boolean flagMulOperation;
    private boolean flagDivOperation;
    private int printCount;
    private String line;
    private String directive;
    private String space;

    public Compilador(LinkedList<Token> tokenTable) {
        this.tokenTable = tokenTable;
        this.line = "";
        this.directive = "";
        this.space = "";
        this.printCount = 1;

        this.stackVars = new LinkedList<>();
        this.extraVars = new LinkedList<>();
        this.dataVars = new LinkedList<>();
        this.codeLines = new LinkedList<>();
        this.declaredVars = new LinkedList<>();
        this.varsUsedOnInitialization = new LinkedList<>();
        this.varDeclarated = null;

        this.flagInitializeVar = false;
        this.flagDeclarationVar = false;
        this.flagImpri = false;
        this.flagLeer = false;
        this.flagNewLine = false;
        this.flagOperationInCourse = false;
        this.flagSumOperation = false;
        this.flagSubOperation = false;
        this.flagMulOperation = false;
        this.flagDivOperation = false;
    }

    public void compilar(String fileName, boolean inTest) {
        Token token;
        String lexema, tokenType, attrib;
        AsmMaker asmBuilder = null;
        try {
            asmBuilder = new AsmMaker(fileName, inTest);
        } catch (IOException ioE) {
            System.out.println("Hoal " + ioE.getMessage());
        }
        codeLines.add("mov bx, 0h");
        codeLines.add("mov dx, 184Fh");
        codeLines.add("clear_Screen bx,dx");
        codeLines.add("mov dx, 0h");
        codeLines.add("point_Cursor dh, dl");

        while(!tokenTable.isEmpty()) {
            token = tokenTable.remove();
            lexema = token.getLexema();
            tokenType = token.getToken();
            attrib = token.getAtrib();

            if(isVariableDeclaration(tokenType) || flagDeclarationVar) {
                defineVariable(tokenType, lexema);
            } else if (flagInitializeVar) {
                defineInitialization(tokenType, lexema);
            }

            if (tokenType.equals("leer") || flagLeer) {
                defineLeer(tokenType, lexema);
            }

            if (tokenType.equals("impri") || tokenType.equals("impriln") || flagImpri) {
                defineImpri(tokenType, lexema, attrib);
            }


        }
        assert asmBuilder != null;
        asmBuilder.loadStackAllocations(stackVars);
        asmBuilder.loadExtraAllocations(extraVars);
        asmBuilder.loadDatosAllocations(dataVars);
        asmBuilder.loadCodigoAllocations(codeLines);

        asmBuilder.initBuildFile();

    }

    private void defineVariable(String tokenType, String lexema) {
        // TODO: Problema, cuando la variable solo es declarada (no inicializada) no se ejecuta el codigo para
        //      agregar al segmento de datos la variable sin inicializar.
        if (isVariableDeclaration(tokenType)) {
            if (flagDeclarationVar) {
                addVarToDataList();
            } else if (flagInitializeVar) {
                defineInitialization(tokenType, lexema);
            }
            directiveGenerator(tokenType);
            flagDeclarationVar = true;
        } else if (!flagInitializeVar && tokenType.equals("Id")) {
            line = lexema + directive;
            varDeclarated.setVarID(lexema);
            varDeclarated.setDirective(directive);
        } else if (tokenType.equals("asign")) {
            flagInitializeVar = true;
        } else if (flagInitializeVar) {
            if (isInitializationOfVariable(tokenType)) {
                line += lexema;
                StringBuilder sb = new StringBuilder(line);
                sb.insert(sb.length() - 1, '$');
                line = sb.toString();
                dataVars.add(line);
                flagInitializeVar = false;
                flagDeclarationVar = false;
            } else {
                // NOTE: This else is executed when the flagDeclarationVar is active with flagInitializeVar
                // so, we spect that the token that falls here (in the else) is an Id token, so, the declared
                // var, the var that going to get the value of the asign is defined in de data segment.
                // Also, the defineInitialization is called, for add the actual token (an Id token) to asm
                addVarToDataList();
                defineInitialization(tokenType, lexema);
            }
        } else if (flagDeclarationVar) {
            addVarToDataList();
        }
    }

    private void addVarToDataList() {
        line += space;
        dataVars.add(line);
        flagDeclarationVar = false;
        declaredVars.add(varDeclarated);
        varDeclarated = null;
    }

    private void defineInitialization(String tokenType, String lexema) {
        if (tokenType.equals("Id")) {
            varDeclarated = findVar(lexema);
            varsUsedOnInitialization.add(varDeclarated);
            varDeclarated = null;
            // TODO: Add an action when find an "Id token" in an initialization
        } else if (tokenType.equals("op")) {
            flagOperationInCourse = true;
            if (lexema.equals("+")) {
                flagSumOperation = true;
            } else if (lexema.equals("-")) {
                flagSubOperation = true;
            } else if (lexema.equals("*")) {
                flagMulOperation = true;
            } else if (lexema.equals("/")) {
                flagDivOperation = true;
            }
        } else {
            flagInitializeVar = false;
        }

        if (!flagInitializeVar) {
            Variable asignVar = declaredVars.getLast();
            if (flagOperationInCourse) {
                flagOperationInCourse = false;
                if (varsUsedOnInitialization.size() == 2) {
                    Variable operando1 = varsUsedOnInitialization.remove();
                    Variable operando2 = varsUsedOnInitialization.remove();
                    if (flagSumOperation) {
                        flagSumOperation = false;
                        line = String.format("sumar %s, %s, %s", operando1.varID, operando2.varID, asignVar.varID);
                    } else if (flagSubOperation) {
                        flagSubOperation = false;
                        line = String.format("restar %s, %s, %s", operando1.varID, operando2.varID, asignVar.varID);
                    } else if (flagMulOperation) {
                        flagMulOperation = false;
                        line = String.format("mult %s, %s, %s", operando1.varID, operando2.varID, asignVar.varID);
                    } else if (flagDivOperation) {
                        flagDivOperation = false;
                        line = String.format("divi %s, %s, %s", operando1.varID, operando2.varID, asignVar.varID);
                    }

                    codeLines.add(line);
                } else {
                    System.out.println("Porra olha isso");
                }
            } else {
                System.out.println("Porra olha isso 2");
            }
        }
    }

    private void defineImpri(String tokenType, String lexema, String attrib) {
        if (tokenType.equals("impri") || tokenType.equals("impriln")) {
            line = "imp_cad ";
            flagImpri = true;
            if (tokenType.equals("impriln")) {
                flagNewLine = true;
            }
        } else if (tokenType.equals("literal")) {
            // TODO: Refactor this code with define Variable code
            String varName;
            varName = "v" + attrib;
            StringBuilder sb = new StringBuilder(lexema);
            sb.insert(sb.length() - 1,'$');
            lexema = sb.toString();
            dataVars.add(varName + " db " + lexema);
            varDeclarated = new Variable(varName, "db", null, tokenType);
            declaredVars.add(varDeclarated);
        } else if (tokenType.equals("Id")) {
            line = "imp_var ";
            varDeclarated = findVar(lexema);
            varDeclarated = (varDeclarated != null) ? varDeclarated : new Variable(lexema, "db");
            //varDeclarated = new Variable(lexema, "db");
        }

        if (flagImpri && varDeclarated != null) {
            line += varDeclarated.getVarID();
            codeLines.add(line);

            if (flagNewLine) {
                moveCursor();
                flagNewLine = false;
            }

            flagImpri = false;
            varDeclarated = null;
        }
    }

    private void moveCursor() {
        String hexCount = Integer.toHexString(printCount);
        String dhValue = (hexCount.length() == 2) ? hexCount : "0".repeat(2 - hexCount.length()) + hexCount;
        codeLines.add("mov dh, " + dhValue + "h");
        codeLines.add("mov dl, 00");
        codeLines.add("point_Cursor dh, dl");
        printCount++;
    }

    private void defineLeer(String tokenType, String lexema) {
        if (tokenType.equals("leer")) {
            line = "read_Cad ";
            flagLeer = true;
        } else if (tokenType.equals("Id")) {
            varDeclarated = findVar(lexema);
            varDeclarated = (varDeclarated != null) ? varDeclarated : new Variable(lexema, "db");
        }

        if (flagLeer && varDeclarated != null) {
            line += varDeclarated.getVarID();
            codeLines.add(line);
            line = String.format("fix_Read %s", varDeclarated.getVarID());
            codeLines.add(line);

            moveCursor();

            flagLeer = false;
            varDeclarated = null;
        }
    }

    private boolean isVariableDeclaration(String tokenValue) {
        return LangKeyWords.isDataType(tokenValue);
    }

    private boolean isInitializationOfVariable(String tokenValue) {
        return tokenValue.equals("number") || tokenValue.equals("literal");
    }

    private void directiveGenerator(String tokenValue) {
        if (tokenValue.equals("ent") || tokenValue.equals("float") || tokenValue.equals("doble")) {
            if (tokenValue.equals("doble"))
                directive = " dw ";
            else
                directive = " db ";

            space = "6, ?, 6 dup(?)";
        } else if(tokenValue.equals("cadena")) {
            directive = " db ";
            space = "21, ?, 21 dup(?)";
        }
        varDeclarated = new Variable(tokenValue);
    }

    private Variable findVar(String varID) {
        for (Variable variable : declaredVars) {
            if (variable.getVarID().equals(varID)) {
                return variable;
            }
        }
        return null;
    }

    static class Variable {
        String varID;
        String directive;
        String content;
        String type;

        public Variable(String type) {
            this.varID = null;
            this.directive = null;
            this.content = null;
            this.type = type;
        }

        public Variable(String varID, String directive, String content, String type) {
            this.varID = varID;
            this.directive = directive;
            this.content = content;
            this.type = type;
        }

        public Variable(String varID,String directive,String content) {
            this.varID = varID;
            this.directive = directive;
            this.content = content;
        }

        public Variable (String varID, String directive) {
            this.varID = varID;
            this.directive = directive;
        }

        protected void setContent(String content) {
            this.content = content;
        }

        protected String getContent() {
            return content;
        }

        protected void setVarID(String varID) {
            this.varID = varID;
        }

        protected String getVarID() {
            return varID;
        }

        protected void setDirective(String directive) {
            this.directive = directive;
        }

        protected String getDirective() {
            return directive;
        }
    }
}
