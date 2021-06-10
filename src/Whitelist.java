public class Whitelist implements Automaton{

    public Token eval(Tape tape){
        char ch = tape.next();
        if(Character.isLetter(ch)){
            tape.rollback();
            return null;
        }
        else if(acceptedNonCharacter(ch)){
            tape.rollback();
            return null;
        }
        else {
            String str = "";
            str += ch;
            return new Token(str);
        }
    }

    private boolean acceptedNonCharacter(char ch){
        switch(ch) {
            case ';':
            case '.':
            case ':':
            case '=':
            case '<':
            case '>':
            case '+':
            case '-':
            case '*':
            case '/':
            case ',':
            case '(':
            case ')':
            case '{':
            case '}':
                return true;
            default:
                return false;
        }
    }

}
