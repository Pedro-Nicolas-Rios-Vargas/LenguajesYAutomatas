package src.ordevlang.tablatokens.tree;

import src.ordevlang.LangKeyWords;
import src.ordevlang.tablatokens.data.Token;

import java.util.LinkedList;
public class LangTree {
    private LinkedList<InstructionNode> lines;
    private int lineIndex;

    public LangTree() {
        lines = new LinkedList<>();
        lineIndex = 0;
    }

    public void addLine(InstructionNode line) {
        lines.add(line);
    }

    public InstructionNode getNextLine() {
        InstructionNode line = lines.get(lineIndex);
        lineIndex++;
        return line;
    }

    public boolean hasNextLine() {
        return lineIndex < lines.size();
    }

    public void resetIndex() {
        lineIndex = 0;
    }

    public boolean existTheVariable(String varName) {
        InstructionNode codeLine;
        while (this.hasNextLine()) {
            codeLine = this.getNextLine();
            Token nodeToken = codeLine.getToken();
            if (LangKeyWords.isDataType(nodeToken.getToken())) {
                codeLine = codeLine.instructions;
                nodeToken = codeLine.getToken();
                if (nodeToken.getToken().equals("Id") &&
                        nodeToken.getLexema().equals(varName)) {
                    this.resetIndex();
                    return true;
                }
            }
        }
        this.resetIndex();
        return false;
    }

    public boolean haveTheVariableTheSameType(String tipo, String varName) {
        InstructionNode codeLine;
        while (this.hasNextLine()) {
            codeLine = this.getNextLine();
            Token nodeToken = codeLine.getToken();
            if (LangKeyWords.isDataType(nodeToken.getToken()) &&
                    nodeToken.getToken().equals(tipo)) {
                codeLine = codeLine.instructions;
                nodeToken = codeLine.getToken();
                if (nodeToken.getToken().equals("Id") &&
                        nodeToken.getLexema().equals(varName)) {
                    this.resetIndex();
                    return true;
                }
            }
        }
        this.resetIndex();
        return false;
    }

    public boolean isTheVariableInitialized(String tipo, String varName) {
        InstructionNode codeLine;
        while (this.hasNextLine()) {
            codeLine = this.getNextLine();
            Token nodeToken = codeLine.getToken();
            if (LangKeyWords.isDataType(nodeToken.getToken())) {
                if (nodeToken.getToken().equals(tipo)) {
                    codeLine = codeLine.instructions;
                    nodeToken = codeLine.getToken();
                    if (nodeToken.getToken().equals("Id") &&
                            nodeToken.getLexema().equals(varName) &&
                            codeLine.instructions != null) {
                        this.resetIndex();
                        return true;
                    }
                }
            } else if (nodeToken.getToken().equals("leer")) {
                // TODO: Add the validation for count a leer instruction as a initialization of a variable
            }
        }
        this.resetIndex();
        return false;
    }

    public Token getVariableToken(String varName) {
        InstructionNode codeLine;
        while (this.hasNextLine()) {
            codeLine = this.getNextLine();
            Token nodeToken = codeLine.getToken();
            if (LangKeyWords.isDataType(nodeToken.getToken())) {
                codeLine = codeLine.instructions;
                nodeToken = codeLine.getToken();
                if (nodeToken.getToken().equals("Id") &&
                        nodeToken.getLexema().equals(varName)) {
                    this.resetIndex();
                    return nodeToken;
                }
            }
        }
        this.resetIndex();
        return null;
    }

    public Token getVariableTypeToken(String varName) {
        InstructionNode codeLine;
        while (this.hasNextLine()) {
            codeLine = this.getNextLine();
            Token nodeToken = codeLine.getToken();
            if (LangKeyWords.isDataType(nodeToken.getToken())) {
                codeLine = codeLine.instructions;
                nodeToken = codeLine.getToken();
                if (nodeToken.getToken().equals("Id") &&
                        nodeToken.getLexema().equals(varName)) {
                    codeLine = codeLine.getParent();
                    nodeToken = codeLine.getToken();
                    this.resetIndex();
                    return nodeToken;
                }
            }
        }
        this.resetIndex();
        return null;
    }

}
