package src.ordevlang.tablatokens.data;

public class Line {
    private int lineNumber;
    private String code;

    public Line(String line) {
        decompositionOfRawLine(line);
    }

    private void decompositionOfRawLine(String rawLine) {
        StringBuilder sb = new StringBuilder();
        int length = rawLine.length();
        int i = 0;
        char c = 0;
        while(c != 32 && i < length){
            c = rawLine.charAt(i);
            //assume that all first characters of the line are digits before the first whitespace
            if(c == 32){
                i++;
            } else {
                sb.append(c);
                i++;
            }
        }
        lineNumber = Integer.parseInt(sb.toString());
        code = rawLine.substring(i); //the code start before the first whitespace
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getCode() {
        return code;
    }
}
