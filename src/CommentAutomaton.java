public class CommentAutomaton implements Automaton {

    public Token eval(Tape tape) {
        Token token = new Token();
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
                        return null;
                    }
                    break;
                case 1:
                    if (ch != '}') {
                        state = 1;
                    } else {
                        //token.setValue(tape.getLine());
                        token.setId("comment");
                        return token;
                    }
                    break;
            }
        }
        if(state == 1) {
            token.setValue(tape.getLine());
            return token;
        }
        return null;
    }
}
