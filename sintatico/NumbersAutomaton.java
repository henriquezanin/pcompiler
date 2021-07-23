public class NumbersAutomaton implements Automaton {
    public Token eval(Tape tape) {
        char ch;
        String str = "";
        Token tok = new Token();
        int state = 0;

        while(tape.hasNext()) {
            ch = tape.next();
            switch (state) {
                // Estado 0
                case 0:
                    if (Character.isWhitespace(ch)) {
                        state = 0;
                    } else if (Character.isDigit(ch)) {
                        str = str.concat(Character.toString(ch));
                        // Estado 3
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
                    // Estado 1
                    if (Character.isDigit(ch)) {
                        str = str.concat(Character.toString(ch));
                        // Estado 3
                        if (!tape.hasNext()) {
                            tok.setValid();
                            tok.setValue(str);
                            tok.setId("number");
                            return tok;
                        } else {
                            state = 1;
                        }
                        // Vá para o estado 2
                    } else if (ch == '.') {
                        str = str.concat(Character.toString(ch));
                        if (!tape.hasNext()) {
                            tok.setValue(str);
                            tok.setId("ident");
                            return tok;
                        } else {
                            state = 2;
                        }
                        // Estado 3
                    } else if(checkSymbols(ch)){
                        tape.rollback();
                        tok.setValue(str);
                        tok.setId("number");
                        tok.setValid();
                        return tok;
                    } else{
                        str = str.concat(Character.toString(ch));
                        state = 4;
                    }
                    break;
                case 2:
                    if (Character.isDigit(ch)) {
                        str = str.concat(Character.toString(ch));
                        state = 5;
                    }
                    else {
                        str = str.concat(Character.toString(ch));
                        state = 4;
                    }
                    break;
                case 4:
                    if(checkSymbols(ch)){
                        tape.rollback();
                        // Estado 6
                        tok.setValue(str);
                        tok.setId("ident");
                        return tok;
                    } else {
                        str = str.concat(Character.toString(ch));
                        state = 4;
                    }
                    break;
                case 5:
                    if(Character.isDigit(ch)){
                        str = str.concat(Character.toString(ch));
                        state = 5;
                        // Estado 7
                    } else if(checkSymbols(ch)){
                        tape.rollback();
                        tok.setValue(str);
                        tok.setId("number");
                        tok.setValid();
                        return tok;
                    } else {
                        str = str.concat(Character.toString(ch));
                        state = 4;
                    }
                    break;
            }
        }
        return null;
    }
    private boolean checkSymbols(char ch){
        return (ch == ';' || ch == ',' || ch == ':' || ch == ')' || ch == '+' || ch == '-' || ch == '>' || ch == '<' ||
                ch == '=' || ch == '*' || ch == '\\' || Character.isWhitespace(ch));
    }
}
