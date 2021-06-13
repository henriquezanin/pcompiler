import java.util.ArrayList;

public class LexicalAnalyzer {

    private Tape tape;
    private final ArrayList<Automaton> allAutomatons;

    public  LexicalAnalyzer(){
        this.allAutomatons = new ArrayList<>();
        this.allAutomatons.add(new WhitelistAutomaton());
        this.allAutomatons.add(new NumbersAutomaton());
        this.allAutomatons.add(new AssignmentAutomaton());
        this.allAutomatons.add(new RelationalAutomaton());
        this.allAutomatons.add(new IdentifierAutomaton());
        this.allAutomatons.add(new CommentAutomaton());
        this.allAutomatons.add(new OtherSymbolsAutomaton());
    }

    public ArrayList<Token> processTape(Tape tape){
        ArrayList<Token> tokens = new ArrayList<>();
        Token tmpToken;
        while(tape.hasNext()){
            for (Automaton allAutomaton : this.allAutomatons) {
                tmpToken = allAutomaton.eval(tape);
                if (tmpToken != null) {
                    tokens.add(tmpToken);
                }
            }
        }
        return tokens;
    }

}
