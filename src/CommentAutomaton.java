public class CommentAutomaton implements Automaton {
    public Token eval(Tape tape) {
        executeRules(tape);
        return null;
    }

    private boolean executeRules(Tape tape) {
        int state = 0;
        char ch;
        while(tape.hasNext()) {
            ch = tape.next();
            switch (state) {
                case 0:
                    if (Character.isWhitespace(ch)) {
                        state = 0;
                    } else if (ch == '{') {
                        state = 1;
                    } else {
                        tape.rollback();
                        return false;
                    }
                    break;
                case 1:
                    if (ch == Character.MIN_VALUE) {
                        tape.rollback();
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
