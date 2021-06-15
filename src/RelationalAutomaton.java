public class RelationalAutomaton implements Automaton {
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
                    } else if (ch == '<') {
                        str = str.concat(Character.toString(ch));
                        if (!tape.hasNext()) {
                            tok.setValid();
                            tok.setValue(str);
                            return tok;
                        } else {
                            state = 1;
                        }
                    } else if (ch == '>') {
                        str = str.concat(Character.toString(ch));
                        if (!tape.hasNext()) {
                            tok.setValid();
                            tok.setValue(str);
                            return tok;
                        } else {
                            state = 5;
                        }
                    } else if (ch == '=') {
                        str = str.concat(Character.toString(ch));
                        tok.setValid();
                        tok.setValue(str);
                        return tok;
                    } else {
                        tape.rollback();
                        return null;
                    }
                    break;
                case 1:
                    if (ch == '=' || ch == '>') {
                        str = str.concat(Character.toString(ch));
                        tok.setValid();
                        tok.setValue(str);
                        return tok;
                    } else {
                        tape.rollback();
                        tok.setValid();
                        tok.setValue(str);
                        return tok;
                    }
                case 5:
                    if (ch == '=') {
                        str = str.concat(Character.toString(ch));
                        tok.setValid();
                        tok.setValue(str);
                        return tok;
                    } else {
                        tape.rollback();
                        tok.setValid();
                        tok.setValue(str);
                        return tok;
                    }
            }
        }
        return null;
    }
}