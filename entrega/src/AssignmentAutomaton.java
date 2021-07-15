public class AssignmentAutomaton implements Automaton {
    public Token eval(Tape tape) {
        char ch;
        String str = "";
        Token tok = new Token();
        int state = 0;
        while(tape.hasNext()) {
            ch = tape.next();
            switch (state) {
                case 0:
                    if (Character.isWhitespace(ch)) {
                        state = 0;
                    } else if (ch == ':') {
                        str = str.concat(":");
                        if (tape.hasNext()) {
                            state = 1;
                        } else {
                            tok.setValid();
                            tok.setValue(str);
                            return tok;
                        }
                    } else {
                        tape.rollback();
                        return null;
                    }
                    break;
                case 1:
                    if (ch == '=') {
                        str = str.concat("=");
                    } else {
                        tape.rollback();
                    }
                    tok.setValid();
                    tok.setValue(str);
                    return tok;
            }
        }
        return null;
    }
}