public class CmdLineAutomaton implements Automaton {
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
        int state = 0;
        char ch;
        while(tape.hasNext()) {
            ch = tape.next();
            switch (state) {
                case 0:
                    if(Character.isLetter(ch)) {
                        state = 1;
                    }
                    else if(ch == '-') {
                        state = 2;
                    }
                    else {
                        return false;
                    }
                    break;
                case 1:
                    if(Character.isLetter(ch) || ch == '-' || ch == '_') {
                        state = 1;
                    }
                    else if(ch == '.'){
                        state = 4;
                    }
                    else {
                        return false;
                    }
                    break;
                case 2:
                    if(tape.hasNext()){
                        return false;
                    }
                    return Character.isLetter(ch);
                case 4:
                    return ch == 'p' && !tape.hasNext();
            }
        }
        return false;
    }
}
