package src.ordevlang.tablatokens.data;

public class Token {
    private String lexema;
    private String token;
    private String atrib;

    public Token(String lexema, String token, String atrib) {
        this.lexema = lexema;
        this.token = token;
        this.atrib = atrib;
    }

    public String getLexema() {
        return lexema;
    }

    public String getToken() {
        return token;
    }

    public String getAtrib() {
        return atrib;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setAtrib(String atrib) {
        this.atrib = atrib;
    }

    @Override
    public String toString() {
        int spacesBtwColumns = 40;
        int lexemaLength = lexema.length();
        int tokenLength = token.length();
        String whiteSpacesLexema = " ".repeat(Math.abs(spacesBtwColumns - lexemaLength));
        String whiteSpacesToken = " ".repeat(Math.abs(spacesBtwColumns - tokenLength));

        return String.format("%s %s %s %s %s\n", lexema, whiteSpacesLexema, token, whiteSpacesToken, atrib);
    }
}
