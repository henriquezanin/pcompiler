public class CommentAutomaton implements Automaton {
    public Token eval(Tape tape) {
        if(executeRules(tape)){
            Token token = new Token();
            token.setValue(tape.getLine());
            token.setValid(true);
            return token;
        }
        return null;
    }

    private boolean executeRules(Tape tape) {
        int state = 0;
        char ch;
        while(tape.hasNext()) {
            ch = tape.next();
            switch (state) {
                case 0:
                    if (ch == '\t' || ch == '\n' || ch == '\r' || ch == ' ') {
                        state = 0;
                    } else if (ch == '{') {
                        state = 1;
                    } else {
                        return false;
                    }
                    break;
                case 1:
                    if (ch == '\n' || ch == '\r' || ch == Character.MIN_VALUE) {
                        return false;
                    } else if (ch != '}') {
                        state = 1;
                    } else {
                        return true;
                    }
                    break;
            }
        }
        return false;
    }
}
