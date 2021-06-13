public class OtherSymbolsAutomaton implements Automaton {
    public Token eval(Tape tape) {

        char ch = 'a';
        if (tape.hasNext()) {
            ch = tape.next();
            if (acceptedSymbols(ch)) {
                Token token = new Token();
                token.setValue(Character.toString(ch));
                token.setValid();
                return token;
            } else {
                tape.rollback();
                return null;
            }

        } else {
            return null;
        }

    }

    private boolean acceptedSymbols(char ch) {
        return switch (ch) {
            case ';', '.', '/', '*', ',', '(', ')', '+', '-' -> true;
            default -> false;
        };
    }
}
