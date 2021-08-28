package src.compilador;

import java.util.LinkedList;
import src.tablatokens.data.Token;

public class Compilador {

    private LinkedList<Token> tokenTable;

    public Compilador(LinkedList<Token> tokenTable) {
        this.tokenTable = tokenTable;
    }

    public void compilar() {
        int tableLength = tokenTable.size();
        Token token;
        String lexema, tokenType, atrib;
        boolean flagImpri = false;

        for (int i = 0; i < tableLength; i++) {
            token = tokenTable.remove();
            lexema = token.getLexema();
            tokenType = token.getToken();
            atrib = token.getAtrib();

            if(flagImpri) {

            }

            if (tokenType.equals("impri")) {
                flagImpri = true;
                continue;
            }
        }
    }
}
