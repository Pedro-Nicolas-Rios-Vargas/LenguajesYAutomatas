package src.ordevlang.compilador;

import java.util.LinkedList;

import java.io.IOException;

import src.ordevlang.LangKeyWords;
import src.ordevlang.tablatokens.data.Token;

public class Compilador {

    private final LinkedList<Token> tokenTable;

    private final LinkedList<String> stackVars;
    private final LinkedList<String> extraVars;
    private final LinkedList<String> dataVars;
    private final LinkedList<String> codeLines;
    private Variable varDeclarated;
    private boolean flagImpri;
    private boolean flagDeclarationVar; // The compiler detects a declaration an are waiting for the value
    private boolean flagInitializeVar;
    private boolean flagLeer;
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
        this.varDeclarated = null;

        this.flagInitializeVar = false;
        this.flagDeclarationVar = false;
        this.flagImpri = false;
        this.flagLeer = false;
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
            }

            if (tokenType.equals("leer") || flagLeer) {
                defineLeer(tokenType, lexema);
            }

            if (tokenType.equals("impri") || flagImpri) {
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
        if (isVariableDeclaration(tokenType)) {
            directiveGenerator(tokenType);
            flagDeclarationVar = true;
        } else if (tokenType.equals("Id")) {
            line = lexema + directive;
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
            }
        }else if (flagDeclarationVar) {
            line += space;
            dataVars.add(line);
            flagDeclarationVar = false;
        }
    }

    private void defineImpri(String tokenType, String lexema, String attrib) {
        if (tokenType.equals("impri")) {
            line = "imp_cad ";
            flagImpri = true;
        } else if (tokenType.equals("literal")) {
            // TODO: Refactor this code with define Variable code
            String varName;
            varName = "v" + attrib;
            StringBuilder sb = new StringBuilder(lexema);
            sb.insert(sb.length() - 1,'$');
            lexema = sb.toString();
            dataVars.add(varName + " db " + lexema);
            varDeclarated = new Variable(varName, "db");
        } else if (tokenType.equals("Id")) {
            // TODO: Give a real directive to this variable obj declaration
            line = "imp_var ";
            varDeclarated = new Variable(lexema, "db");
        }

        if (flagImpri && varDeclarated != null) {
            line += varDeclarated.getVarID();
            codeLines.add(line);

            moveCursor();

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
            varDeclarated = new Variable(lexema, "db");
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
    }

    static class Variable {
        String varID;
        String directive;
        String content;

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
