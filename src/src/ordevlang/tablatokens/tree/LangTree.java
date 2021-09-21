package src.ordevlang.tablatokens.tree;

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
}
