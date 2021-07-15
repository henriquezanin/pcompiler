public class SyntacticalAnalyzer {

    LexicalAnalyzer lexical;

    public SyntacticalAnalyzer(LexicalAnalyzer lexical){
        this.lexical = lexical;
    }

    public boolean execute(){
        return programa();
    }

    private void erro(){
        System.out.println("Procedimento de erro");
    }

    private boolean programa(){
        Token symbol = lexical.nextSymbol();
        // Se não houver simbolo o compilador não retorna erro
        if(symbol == null){
            return true;
        }
        // Se o token for diferente do esperado chama a função de erro e finaliza
        if(symbol.getId().equals("sym_program")){
            symbol = lexical.nextSymbol();
        }
        else {
            erro();
            return false;
        }
        // Espera um identificador valido como proximo token
        if(symbol != null && symbol.getId().equals("ident") && symbol.isValid()){
            symbol = lexical.nextSymbol();
        }
        else {
            erro();
            return false;
        }
        // Espera um ";" como próximo token
        if(symbol != null && symbol.getId().equals("sym_semicolon")){
            // Executa o procedimento dc
            boolean wasExecuted = dc();
            if(!wasExecuted){
                erro();
                return false;
            }
        }
        else {
            erro();
            return false;
        }
        symbol = lexical.nextSymbol();
        // Espera begin como próximo token
        if(symbol != null && symbol.getId().equals("sym_begin")){
            comandos();
        }
        else {
            erro();
            return false;
        }
        symbol = lexical.nextSymbol();
        // Espera end como próximo token
        if(symbol != null && symbol.getId().equals("sym_end")){
            symbol = lexical.nextSymbol();
        }
        else {
            erro();
            return false;
        }
        // Espera o token "ponto" para finalizar a compilação
        if(symbol == null || !symbol.getId().equals("sym_dot")){
            erro();
            return false;
        }
    return true;
    }

    private boolean dc(){
        boolean wasExecuted = dc_c();
        if(!wasExecuted){
            erro();
            return false;
        }
        wasExecuted = dc_v();
        if(!wasExecuted){
            erro();
            return false;
        }
        wasExecuted = dc_p();
        if(!wasExecuted){
            erro();
            return false;
        }
        return true;
    }

    private boolean dc_c(){
        //TODO Contruir o grafo dc_c
        return true;
    }

    private boolean dc_v(){
        //TODO Contruir o grafo dc_v
        return true;
    }

    private boolean dc_p(){
        //TODO Contruir o grafo dc_p
        return true;
    }

    private boolean comandos(){
        //TODO Contruir o grafo comandos
        return true;
    }

}
