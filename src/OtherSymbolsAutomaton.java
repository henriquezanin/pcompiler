public class OtherSymbolsAutomaton implements Automaton {
    public Token eval(Tape tape) {
        if(executeRules(tape)){
            Token token = new Token();
            token.setValue(tape.getLine());
            token.setValid();
            return token;
        }
        return null;
    }

    private boolean executeRules(Tape tape) {
        char ch;
        if (tape.hasNext()) {
            ch = tape.next();
            if (acceptedSymbols(ch)) {
                return true;
            } else {
                tape.rollback();
                return false;
            }

        } else {
            return false;
        }
    }

    private boolean acceptedSymbols(char ch) {
        return switch (ch) {
            case ';', '.', '/', '*', ',', '(', ')' -> true;
            default -> false;
        };
    }
}
