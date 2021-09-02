package src.ordevlang.compilador;

import java.util.HashMap;
import java.util.LinkedList;

import java.io.IOException;

import src.ordevlang.LangKeyWords;
import src.ordevlang.tablatokens.data.Token;

public class Compilador {

    private LinkedList<Token> tokenTable;

    private LinkedList<String> stackVars;
    private LinkedList<String> extraVars;
    private LinkedList<String> dataVars;
    private LinkedList<String> codeLines;
    private int floatVarCount;
    private int entVarCount;
    private int dobleVarCount;
    private int cadenaVarCount;
    private boolean flagImpri;
    private boolean flagDeclarationVar; // The compiler detects a declaration an are waiting for the value

    public Compilador(LinkedList<Token> tokenTable) {
        this.tokenTable = tokenTable;
        this.floatVarCount = 0;
        this.entVarCount = 0;
        this.dobleVarCount = 0;
        this.cadenaVarCount = 0;

        this.stackVars = new LinkedList<>();
        this.extraVars = new LinkedList<>();
        this.dataVars = new LinkedList<>();
        this.codeLines = new LinkedList<>();

        this.flagImpri = false;
        this.flagDeclarationVar = false;
    }

    public void compilar(String fileName) {
        HashMap<String, Variable> variables = new HashMap<>();
        Token token;
        String lexema, tokenType, attrib;
        String varName = "";
        String varDirective = "";
        String line = "";
        Variable varDeclarated = null;
        int printCount = 1;
        AsmMaker asmBuilder = null;
        try {
            asmBuilder = new AsmMaker(fileName);
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

            if (tokenType.equals("impri")) {
                line = "imp_cad ";
                flagImpri = true;
            }

            if (tokenType.equals("literal")) {
                varName = "v" + attrib;
                StringBuilder sb = new StringBuilder(lexema);
                sb.insert(sb.length() - 1,'$');
                lexema = sb.toString();
                dataVars.add(varName + " db " + lexema);
                varDeclarated = new Variable(varName, "db");
            }

            if (flagImpri && varDeclarated != null) {
                line += varDeclarated.getVarID();
                codeLines.add(line);

                String hexCount = Integer.toHexString(printCount);
                String dhValue = (hexCount.length() == 2) ? hexCount : "0".repeat(2 - hexCount.length()) + hexCount;
                codeLines.add("mov dh, " + dhValue + "h");
                codeLines.add("mov dl, 00");
                codeLines.add("point_Cursor dh, dl");
                printCount++;

                flagImpri = false;
                varDeclarated = null;
            }

        }
        assert asmBuilder != null;
        asmBuilder.loadStackAllocations(stackVars);
        asmBuilder.loadExtraAllocations(extraVars);
        asmBuilder.loadDatosAllocations(dataVars);
        asmBuilder.loadCodigoAllocations(codeLines);

        asmBuilder.initBuildFile();

    }

    private boolean isVariableDeclaration(String tokenValue) {
        if (LangKeyWords.isDataType(tokenValue)) {
            return true;
        }
        return false;
    }

    private boolean isInitializationOfVariable(String tokenValue) {
        if (tokenValue.equals("number") || tokenValue.equals("literal")) {
            return true;
        }
        return false;
    }

    private String[] varNameGenerator(String tokenValue, String attrib) {
        String varID = "v" + attrib;
        String directive = "";
        boolean isLiteral = tokenValue.equals("literal");
        if (tokenValue.equals("ent")) {
            directive = "db";

        } else if(tokenValue.equals("float")) {
            directive = "db";
        } else if(tokenValue.equals("doble")) {
            directive = "dw";
        } else if(tokenValue.equals("cadena") || isLiteral) {
            directive = "db";
        }

        return new String[]{varID, directive, Boolean.toString(isLiteral)};
    }

    class Variable {
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
