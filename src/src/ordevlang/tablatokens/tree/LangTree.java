package src.ordevlang.tablatokens.tree;

import src.ordevlang.LangKeyWords;
import src.ordevlang.tablatokens.data.Token;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Formatter;
import java.util.LinkedList;
public class LangTree {
    private final LinkedList<Token> TOKENS_TABLE;
    private LinkedList<InstructionNode> lines;
    private int lineIndex;

    public LangTree() {
        TOKENS_TABLE = new LinkedList<>();
        lines = new LinkedList<>();
        lineIndex = 0;
    }

    /**
     * Add the new InstructionNode line into the langTree lines list.
     * @param line An instructionNode that describes a command line.
     */
    public void addLine(InstructionNode line) {
        lines.add(line);
    }

    /**
     * Return the next line from the lines list. Works as a queue.
     * @return An InstructionNode that describes/contains a line with their tokens.
     */
    public InstructionNode getNextLine() {
        InstructionNode line = lines.get(lineIndex);
        lineIndex++;
        return line;
    }

    /**
     * Method that verifies if exist a next line in the lines list.
     * @return true if exist a next element in the line list, false in another cases.
     */
    public boolean hasNextLine() {
        return lineIndex < lines.size();
    }

    /**
     * resetIndex reset the value of <code><i>lineIndex</i></code>. This value specify the index of search in the lines
     * list.
     */
    public void resetIndex() {
        lineIndex = 0;
    }

    /**
     * Verify if exist a variable with a specific name.
     * @param varName name of the searching variable.
     * @return true if the variable with the specific name exist, false otherwise.
     */
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

    /**
     * Verifies if exist a variable with the same name and same type into lines list.
     * @param tipo data type of the variable.
     * @param varName name of the variable.
     * @return true if the variable have the same type as tipo, false otherwise.
     */
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

    /**
     * Verifies if the variable with the name specified are initialized.
     * @param tipo the type of the variable.
     * @param varName the name of the variable.
     * @return true if the variable with specific name is initialized, false otherwise.
     */
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
                // Add the validation for count a leer instruction as a initialization of a variable
                if (nodeToken.getToken().equals("leer")) {
                    codeLine = codeLine.instructions;
                    nodeToken = codeLine.getToken();
                    if (nodeToken.getLexema().equals(varName)) {
                        this.resetIndex();
                        return true;
                    }
                }
            }
        }
        this.resetIndex();
        return false;
    }

    /**
     * Return the variable Token of the variable that match the name passed as parameter.
     * @param varName the name to match.
     * @return  An Token Object that represent the variable Token (lexeme, token, attribute). If not exist a match
     *          the method returns null.
     */
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

    /**
     * Search the type of the variable that match with the variable name passed as parameter.
     * @param varName The name to match.
     * @return  An Token object that represent the type Token of the variable (lexeme, token, attribute). If not exist a
     *          match the method returns null.
     */
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

    public LinkedList<Token> getTokensTable() {
        return TOKENS_TABLE;
    }

    public void printTablaTokens() {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        try {
            bw.write(new Formatter().format("%s%40s%40s\n", "LEXEMA", "TOKEN", "ATRIBUTOS").toString());
            bw.write("-".repeat(100) + "\n");
            bw.flush();
            for(Token t : TOKENS_TABLE) {
                bw.write(t.toString());
                bw.flush();
            }
            bw.flush();
        } catch(IOException ioE) {

        }
    }

    public void buildTokensTable() {
        InstructionNode lineNode;
        while(hasNextLine()) {
            lineNode = getNextLine();
            tokenizingInstructionNode(lineNode);
            tokenizingBlock(lineNode);
        }
        resetIndex();
    }

    private void tokenizingInstructionNode(InstructionNode node) {
        InstructionNode nodeToken = node;
        while (nodeToken != null) {
            TOKENS_TABLE.add(nodeToken.getToken());
            nodeToken = nodeToken.getNextPartOfInstruction();
        }
        TOKENS_TABLE.add(new Token("endl", "endl", "-"));
    }

    private void tokenizingBlock(InstructionNode lineNode) {
        if (lineNode.isABlock()) {
            InstructionNode blockLineNode;
            while(lineNode.hasMoreBlockLines()) {
                blockLineNode = lineNode.getNextBlockLine();
                tokenizingInstructionNode(blockLineNode);
                tokenizingBlock(blockLineNode);
            }
            lineNode.resetIndex();
        }
    }


}
