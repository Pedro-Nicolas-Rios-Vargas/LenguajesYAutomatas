package src.depurador;

public class LineDebug {
    /**
     * Debug the start of the line passed by argument.
     * @param line  The line to analyze
     * @return  If the line starts with '#' character, returns an empty String.
     *          If the line begins with an whitespace character, the method returns the substring
     *          of the original line with no leading whitespace.
     */
    public static String StartOfLineValidation(String line) {
        if(line == null || line.isEmpty()) // The line is empty or null.
            return "";

        char firstChar = line.charAt(0);

        if(firstChar == 35) { // The line init with '#' character
            return "";
        }
        if(firstChar == 32 || firstChar == 9) {   // The line init with ' ' character
            return StartOfLineValidation(line.substring(1));
        }
        return line;    // If the line don't init with some of this characters return the line.
    }

    public static String lineDebugging(String line) {
        String debugLine = "";
        char[] chars = line.toCharArray();
        boolean blank = false; //exist an blank waiting a letter
        boolean charInString = false;
        for(char c : chars) {
            //When a line contain a '#' and is not inside a string, the rest of line is a comment
            if(c == 35 && !charInString)
                break;

            if(c == 34) {   // The '"' character remark the start or end of a string declaration
                if(charInString){ //the string end's
                    charInString = false;
                } else {    //the string begin's
                    charInString = true;
                }
            } else if(!charInString) {
                if (c == 32) {    // A space character marks the end of a key word,
                    // but is not necessary more than 1.
                    if (blank) {  //When the flag blank is true is not necessary append other blank.
                        continue;
                    } else {
                        blank = true;
                        continue;
                    }
                } else if (c == 9) {
                    blank = true;
                    continue;
                }
            }

            if(blank) {
                debugLine += " ";
                blank = false;
            }
            debugLine += c;
        }

        return debugLine;
    }
}
