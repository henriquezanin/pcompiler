import java.util.ArrayList;
import java.util.Hashtable;

public class LexicalAnalyzer {

    private Tape tape;
    private final ArrayList<Automaton> allAutomatons;
    private Hashtable<String, String> symbolTable;

    public  LexicalAnalyzer(){
        this.initSymbolTable();
        this.allAutomatons = new ArrayList<>();
        this.allAutomatons.add(new WhitelistAutomaton());
        this.allAutomatons.add(new NumbersAutomaton());
        this.allAutomatons.add(new AssignmentAutomaton());
        this.allAutomatons.add(new RelationalAutomaton());
        this.allAutomatons.add(new IdentifierAutomaton());
        this.allAutomatons.add(new CommentAutomaton());
        //this.allAutomatons.add(new OtherSymbolsAutomaton());
    }

    public ArrayList<Token> processTape(Tape tape){
        ArrayList<Token> tokens = new ArrayList<>();
        Token tmpToken;
        while(tape.hasNext()){
            for (Automaton allAutomaton : this.allAutomatons) {
                tmpToken = allAutomaton.eval(tape);
                if (tmpToken != null) {
                    setID(tmpToken);
                    tokens.add(tmpToken);
                }
            }
        }
        return tokens;
    }

    private void initSymbolTable(){
        this.symbolTable = new Hashtable<>();
        this.symbolTable.put("program", "sym_program");
        this.symbolTable.put(";", "sym_semicolon");
        this.symbolTable.put(".", "sym_dot");
        this.symbolTable.put("begin", "sym_begin");
        this.symbolTable.put("end", "sym_end");
        this.symbolTable.put("const", "sym_const");
        this.symbolTable.put("var", "sym_var");
        this.symbolTable.put(":", "sym_colon");
        this.symbolTable.put("real", "sym_real");
        this.symbolTable.put("integer", "sym_int");
        this.symbolTable.put("procedure", "sym_procedure");
        this.symbolTable.put("else", "sym_else");
        this.symbolTable.put("read", "sym_read");
        this.symbolTable.put("write", "sym_write");
        this.symbolTable.put("while", "sym_while");
        this.symbolTable.put("do", "sym_do");
        this.symbolTable.put("if", "sym_if");
        this.symbolTable.put("then", "sym_then");
        this.symbolTable.put("=", "sym_eq");
        this.symbolTable.put("<>", "sym_notEq");
        this.symbolTable.put(">=", "sym_greaterOrEq");
        this.symbolTable.put("<=", "sym_lessOrEq");
        this.symbolTable.put(">", "sym_greater");
        this.symbolTable.put("<", "sym_less");
        this.symbolTable.put("+", "sym_plus");
        this.symbolTable.put("-", "sym_minus");
        this.symbolTable.put("*", "sym_plus");
        this.symbolTable.put("/", "sym_div");
        this.symbolTable.put(":=", "sym_atrib");
        this.symbolTable.put("to", "sym_to");
        this.symbolTable.put(",", "sym_comma");
        this.symbolTable.put("(", "sym_leftParenthesis");
        this.symbolTable.put(")", "sym_rightParenthesis");
    }

    public void setID(Token token){
        String id = this.symbolTable.get(token.getValue());
        if(id != null) {
            token.setId(id);
        } else{
            token.setId("ident");
        }
    }
}
