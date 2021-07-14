import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Compiler {

    private final LexicalAnalyzer lexicalAnalyzer;

    public Compiler(String filename) {
        // Instancia o analizador léxico
        this.lexicalAnalyzer = new LexicalAnalyzer(filename);
    }

    public void compile(){
        Token token = this.lexicalAnalyzer.nextSymbol();
        while (token != null){
            System.out.printf("%-20s |", token.getValue());
            if(!token.isValid()){
                System.out.printf("Caractere invalido ou comentário não fechado\n");
            } else {
                System.out.println(token.getId());
            }
            token = this.lexicalAnalyzer.nextSymbol();
        }
    }
}
