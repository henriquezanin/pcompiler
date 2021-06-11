import java.util.ArrayList;

public class LexicalAnalyzer {

    private Tape tape;
    private ArrayList<Token> tokens;

    public  LexicalAnalyzer(){
        tokens = new ArrayList<>();
    }

    public ArrayList<Token> processLine(Tape tape){
        while(tape.hasNext()){

        }
        return tokens;
    }

}
