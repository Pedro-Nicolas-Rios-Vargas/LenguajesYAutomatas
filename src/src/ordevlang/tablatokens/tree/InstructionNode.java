package src.ordevlang.tablatokens.tree;

import java.util.LinkedList;
import java.util.Objects;
import src.ordevlang.tablatokens.data.Token;

public class InstructionNode {
    protected LinkedList<InstructionNode> lines; //acts as a right node in a tree
    protected InstructionNode instructions; //acts as a left node in a tree
    protected InstructionNode parent; //The parent of the node
    protected Token token;
    private int lineIndex;
    private boolean isBlock;

    public InstructionNode(InstructionNode parent, Token token) {
        this.parent = Objects.requireNonNullElse(parent, this);
        this.token = token;

        lineIndex = 0;

        isBlock = false;
        this.lines = null;
    }

    public InstructionNode(InstructionNode parent, Token token, boolean isBlock) {
        this.parent = Objects.requireNonNullElse(parent, this);
        this.token = token;

        lineIndex = 0;

        this.isBlock = isBlock;
        if (!isBlock) {
            lines = null;
        } else {
            lines = new LinkedList<>();
        }
    }

    public void addInstruction(InstructionNode instruction) {
        instruction.setParent(this);
        if (instructions == null) {
            instructions = instruction;
        } else {
            instructions.addInstruction(instruction);
        }
    }

    public InstructionNode getNextPartOfInstruction() {
        return instructions;
    }

    public void addLine(InstructionNode instructionLine) {
        if (isBlock) {
            instructionLine.setParent(this);
            lines.add(instructionLine);
        } else
            System.out.println("This Instruction is not a Block");
    }

    public boolean hasMoreBlockLines() {
        return lineIndex < lines.size();
    }

    public void resetIndex() {
        lineIndex = 0;
    }

    public InstructionNode getNextBlockLine() {
        if (isBlock) {
            InstructionNode line = (InstructionNode) lines.get(lineIndex);
            lineIndex++;
            return line;
        } else {
            System.out.println("This Instruction is not a Block");
            return null;
        }
    }

    public boolean isABlock() {
        return isBlock;
    }

    public Token getToken() {
        return token;
    }

    public InstructionNode getParent() {
        return parent;
    }

    public void setParent(InstructionNode parent) {
        this.parent = parent;
    }
}
