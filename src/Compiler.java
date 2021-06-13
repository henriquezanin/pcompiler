import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class Compiler {

    private BufferedReader file;
    private final LexicalAnalyzer lexicalAnalyzer;

    public Compiler(String filename) {
        try {
            this.file = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e){
            System.err.println("File not found");
        }
        this.lexicalAnalyzer = new LexicalAnalyzer();
    }

    public void compile(){
        String line;
        Tape tape = new Tape();
        ArrayList<Token> tokens;
        int lineNumber = 0;
        System.out.println("Token | ID | Valid?");
        try{
            while( (line = this.file.readLine()) != null ) {
                lineNumber++;
                tape.replace(line);
                tokens = lexicalAnalyzer.processTape(tape);
                if(showTokens(tokens)){
                    System.out.printf("Line %d: Has invalid character\n", lineNumber);
                }
            }
        }catch (IOException e){
            System.err.println("Failed to read all source code");
            e.printStackTrace();
        }
    }

    private boolean showTokens(ArrayList<Token> tokens){
        boolean hasInvalid = false;
        for (Token token : tokens) {
            if(!token.isValid()){
                hasInvalid = true;
            }
            System.out.println(token.getValue() + " | " + token.getId() + " | " + token.isValid());
        }
        return hasInvalid;
    }

}
