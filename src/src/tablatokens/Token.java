package src.tablatokens;

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
        return String.format("%s %40s %40s\n", lexema, token, atrib);
    }
}
