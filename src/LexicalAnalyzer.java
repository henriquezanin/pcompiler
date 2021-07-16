import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class LexicalAnalyzer {

    private final ArrayList<Automaton> allAutomatons;
    private Hashtable<String, String> symbolTable;
    private BufferedReader file;
    private Tape tape;
    private Token currentToken;
    private int lineNumber;

    public  LexicalAnalyzer(String filename){
        this.initSymbolTable();
        // Instancia todos os automatos em um ArrayList
        this.allAutomatons = new ArrayList<>();
        this.allAutomatons.add(new WhitelistAutomaton());
        this.allAutomatons.add(new NumbersAutomaton());
        this.allAutomatons.add(new AssignmentAutomaton());
        this.allAutomatons.add(new RelationalAutomaton());
        this.allAutomatons.add(new IdentifierAutomaton());
        this.allAutomatons.add(new CommentAutomaton());
        this.allAutomatons.add(new OtherSymbolsAutomaton());
        // Tenta abrir o arquivo, se não lança uma exceção
        try {
            this.file = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e){
            System.err.println("File not found");
        }
        // Inicializa a fita
        this.tape = new Tape();
        this.lineNumber = 0;
        this.replaceTape();
        this.currentToken = null;
    }

    public Token nextSymbol() {
        Token token = null;
        if (!this.tape.hasNext()) {
            if (!this.replaceTape()) {
                return null;
            }
        }
        for (Automaton allAutomaton : this.allAutomatons) {
            token = allAutomaton.eval(this.tape);
            if (token != null && token.getId() == null) {
                setID(token);
                this.currentToken = token;
                return token;
            } else if (token != null && token.getId() != null && token.getId().equals("comment")){
                this.nextSymbol();
            } else if (token != null && token.getId() != null && token.getId().equals("number")){
                this.currentToken = token;
                return token;
            }
    }
        return null;
    }

    public Token currentSymbol(){
        return this.currentToken;
    }

    private boolean replaceTape(){
        String line;
        try{
            line = this.file.readLine();
            while (line.isEmpty()){
                line = this.file.readLine();
            }
        }catch (
        IOException e){
            System.err.println("Failed to read all source code");
            e.printStackTrace();
            return false;
        }
        if(line == null){
            return false;
        }
        this.lineNumber++;
        tape.replace(line);
        return true;
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
        this.symbolTable.put("*", "sym_mult");
        this.symbolTable.put("/", "sym_div");
        this.symbolTable.put(":=", "sym_atrib");
        this.symbolTable.put("to", "sym_to");
        this.symbolTable.put(",", "sym_comma");
        this.symbolTable.put("(", "sym_leftParenthesis");
        this.symbolTable.put(")", "sym_rightParenthesis");
        this.symbolTable.put("for", "sym_for");
    }

    // Define o id do token processado. Para isso utiliza-se a tabela de simbolos
    public void setID(Token token){
        String id = this.symbolTable.get(token.getValue());
        if(token.getId() != null && !token.getId().isEmpty()){
            return;
        }
        if(id != null) {
            token.setId(id);
        } else if (token.isValid()){
            token.setId("ident");
        }
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
