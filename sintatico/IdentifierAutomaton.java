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
                    if (Character.isWhitespace(ch)) {
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
                        tape.rollback();
                        return null;
                    }
                    break;
                case 1:
                    if (Character.isLetter(ch) || Character.isDigit(ch) || 
                        ch == '_') {
                        str = str.concat(Character.toString(ch));
                        if (!tape.hasNext()) {
                            tok.setValid();
                            tok.setValue(str);
                            return tok;
                        } else {
                            state = 1;
                        }
                    } else if(checkSymbols(ch)) {
                        tape.rollback();
                        tok.setValid();
                        tok.setValue(str);
                        return tok;
                    } else{
                        str = str.concat(Character.toString(ch));
                        state = 3;
                    }
                    break;
                case 3:
                    if (checkSymbols(ch)){
                        tape.rollback();
                        tok.setValue(str);
                        return tok;
                    } else {
                        str = str.concat(Character.toString(ch));
                        state = 3;
                    }
            }
        }
        return null;
    }

    private boolean checkSymbols(char ch){
        return (ch == ';' || ch == ',' || ch == ':' || ch == ')' || ch == '+' || ch == '-' || ch == '>' || ch == '<' ||
                ch == '=' || ch == '*' || ch == '\\' || ch == '(' || ch == '.' ||Character.isWhitespace(ch));
    }
}