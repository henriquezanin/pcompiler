import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Compiler {

    private BufferedReader file;
    private final LexicalAnalyzer lexicalAnalyzer;

    public Compiler(String filename) {
        // Tenta abrir o arquivo, se não lança uma exceção
        try {
            this.file = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e){
            System.err.println("File not found");
        }
        // Instancia o analizador léxico
        this.lexicalAnalyzer = new LexicalAnalyzer();
    }

    public void compile(){
        String line;
        // Intancia uma nova string na fita
        Tape tape = new Tape();
        ArrayList<Token> tokens;
        int lineNumber = 0;
        try{
            // Lê todas as linhas do arquivo
            while( (line = this.file.readLine()) != null ) {
                lineNumber++;
                // Substitui a string na fita
                tape.replace(line);
                // Executa o analizador lexico para a fita
                tokens = lexicalAnalyzer.processTape(tape);
                // Caso haja algum caractere inválido uma mensagem de erro é exibida
                if(showTokens(tokens)){
                    System.out.printf("|------------------------------------------------------------|\n", lineNumber);
                    System.out.printf("|ERROR!- Line %d has invalid character or comment isn't closed|\n", lineNumber);
                    System.out.printf("|------------------------------------------------------------|\n", lineNumber);
                }
            }
        }catch (IOException e){
            System.err.println("Failed to read all source code");
            e.printStackTrace();
        }
    }

    // Exibe os tokens processados pelo analizador léxico e verifica os que estão na tabela de símbolos
    // Retorna true caso haja algum token invalido
    private boolean showTokens(ArrayList<Token> tokens){
        boolean hasInvalid = false;
        for (Token token : tokens) {
            System.out.printf("%-20s |", token.getValue());
            if(!token.isValid()){
                hasInvalid = true;
                System.out.printf("Caractere invalido ou comentário não fechado\n");
            } else {
                System.out.println(token.getId());
            }
        }
        return hasInvalid;
    }

}
