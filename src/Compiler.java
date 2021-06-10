import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

public class Compiler {

    private BufferedReader file;
    private Hashtable<String, String> symbolTable;

    public Compiler(String filename) {
        try {
            this.file = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e){
            System.err.println("File not found");
        }
        this.initSymbolTable();
    }

    public void compile(){
        String line;
        Tape tape = new Tape();
        try{
            while( (line = this.file.readLine()) != null ) {
                tape.replace(line);
                //TODO analizador lexico
            }
        }catch (IOException e){
            System.err.println("Failed to read all source code");
            e.printStackTrace();
        }
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

}
