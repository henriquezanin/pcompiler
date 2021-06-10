public class IdentifierAutomaton implements Automaton {
    public Token eval(Tape tape) {
        char ch;
        String str = "";
        Token tok = new Token();
        int state = 0;

        while(tape.hasNext()) {
            ch = tape.next();

            switch (state) {
                case 0:
                    if (Character.isWhitespace(ch) || ch == '\n' || ch == '\t') {
                        state = 0;
                    } else if (Character.isLetter(ch)) {
                        str = str.concat(Character.toString(ch));

                        if (!tape.hasNext()) {
                            tok.setValid();
                            tok.setValue(str);
                    
                            return tok;
                        } else {
                            state = 1;
                        }
                    } else {
                        return null;
                    }
                    break;
                case 1:
                    if (Character.isLetter(ch) || Character.isDigit(ch) || 
                        ch == '_' || ch == '-') {
                        str = str.concat(Character.toString(ch));

                        if (!tape.hasNext()) {
                            tok.setValid();
                            tok.setValue(str);

                            return tok;
                        } else {
                            state = 1;
                        }
                    } else {
                        tape.rollback();

                        tok.setValid();
                        tok.setValue(str);

                        return tok;
                    }
                    break;
            }
        }
        
        return null;
    }
}