public class Compiler {

    private final LexicalAnalyzer lexicalAnalyzer;
    private final SyntacticalAnalyzer syntacticalAnalyzer;

    public Compiler(String filename) {
        // Instancia o analizador léxico
        this.lexicalAnalyzer = new LexicalAnalyzer(filename);
        this.syntacticalAnalyzer = new SyntacticalAnalyzer(lexicalAnalyzer);
    }

    public void compile(){
        boolean wasExecuted = syntacticalAnalyzer.execute();
        if(!wasExecuted){
            System.out.println("Falha na compilação!");
        }
        else{
            System.out.println("Compilação concluida com sucesso");
        }
        /*Token token = this.lexicalAnalyzer.nextSymbol();
        while (token != null){
            System.out.printf("%-20s |", token.getValue());
            if(!token.isValid()){
                System.out.printf("Caractere invalido ou comentário não fechado\n");
            } else {
                System.out.println(token.getId());
            }
            token = this.lexicalAnalyzer.nextSymbol();
        }*/
    }
}
