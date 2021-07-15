public class NumbersAutomaton implements Automaton {
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
                    } else if (Character.isDigit(ch)) {
                        str = str.concat(Character.toString(ch));
                        if (!tape.hasNext()) {
                            tok.setValid();
                            tok.setValue(str);
                            tok.setId("number");
                            return tok;
                        } else {
                            state = 1;
                        }
                    } else {
                        tape.rollback();
                        return null;
                    }
                    break;
                case 1:
                    if (Character.isDigit(ch)) {
                        str = str.concat(Character.toString(ch));
                        if (!tape.hasNext()) {
                            tok.setValid();
                            tok.setValue(str);
                            tok.setId("number");
                            return tok;
                        } else {
                            state = 1;
                        }
                    } else if (ch == '.') {
                        if (!tape.hasNext()) {
                            tape.rollback();
                            tok.setValid();
                            tok.setValue(str);
                            tok.setId("number");
                            return tok;
                        } else {
                            str = str.concat(Character.toString(ch));
                            state = 2;
                        }
                    } else {
                        tape.rollback();
                        tok.setValid();
                        tok.setValue(str);
                        tok.setId("number");
                        return tok;
                    }
                    break;
                case 2:
                    if (Character.isDigit(ch)) {
                        str = str.concat(Character.toString(ch));

                        if (!tape.hasNext()) {
                            tok.setValid();
                            tok.setValue(str);
                            tok.setId("number");
                            return tok;
                        } else {
                            state = 5;
                        }                        
                    } else {
                        str = str.substring(0, str.length() - 1);
                        tape.rollback();
                        tape.rollback();
                        tok.setValid();
                        tok.setValue(str);
                        tok.setId("number");
                        return tok;
                    }
                    break;
                case 5:
                    if (Character.isDigit(ch)) {
                        str = str.concat(Character.toString(ch));
                        if (!tape.hasNext()) {
                            tok.setValid();
                            tok.setValue(str);
                            tok.setId("number");
                            return tok;
                        } else {
                            state = 5;
                        }                        
                    } else {
                        tape.rollback();
                        tok.setValid();
                        tok.setValue(str);
                        tok.setId("number");
                        return tok;
                    }
                    break;
            }
        }
        return null;
    }
}