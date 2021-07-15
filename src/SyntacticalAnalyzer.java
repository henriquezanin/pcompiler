public class SyntacticalAnalyzer {

    LexicalAnalyzer lexical;

    public SyntacticalAnalyzer(LexicalAnalyzer lexical){
        this.lexical = lexical;
    }

    public boolean execute(){
        return programa();
    }

    private void erro(String funcao){
        System.out.println("Procedimento de erro: "+funcao);
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
            erro("programa -> sym_program");
            return false;
        }
        // Espera um identificador valido como proximo token
        if(symbol != null && symbol.getId().equals("ident") && symbol.isValid()){
            symbol = lexical.nextSymbol();
        }
        else {
            erro("programa -> ident");
            return false;
        }
        // Espera um ";" como próximo token
        if(symbol != null && symbol.getId().equals("sym_semicolon")){
            // Executa o procedimento dc
            lexical.nextSymbol();
            boolean wasExecuted = dc();
            if(!wasExecuted){
                erro("programa -> dc()");
                return false;
            }
        }
        else {
            erro("programa -> sym_semicolon");
            return false;
        }
        symbol = lexical.currentSymbol();
        // Espera begin como próximo token
        if(symbol != null && symbol.getId().equals("sym_begin")){
            // Executa o procedimento comandos
            boolean wasExecuted = comandos();
            if(!wasExecuted){
                erro("programa -> comandos()");
                return false;
            }
        }
        else {
            System.out.println(symbol.getValue());
            erro("programa -> sym_begin");
            return false;
        }
        symbol = lexical.nextSymbol();
        // Espera end como próximo token
        if(symbol != null && symbol.getId().equals("sym_end")){
            symbol = lexical.nextSymbol();
        }
        else {
            erro("programa -> sym_end");
            return false;
        }
        // Espera o token "ponto" para finalizar a compilação
        if(symbol == null || !symbol.getId().equals("sym_dot")){
            erro("programa -> sym_dot");
            return false;
        }
    return true;
    }

    private boolean dc(){
        boolean wasExecuted = dc_c();
        if(!wasExecuted){
            erro("dc_c()");
            return false;
        }
        wasExecuted = dc_v();
        if(!wasExecuted){
            erro("dc_v()");
            return false;
        }
        wasExecuted = dc_p();
        if(!wasExecuted){
            erro("dc_p()");
            return false;
        }
        return true;
    }

    private boolean dc_c(){
        Token symbol = lexical.currentSymbol();
        if(symbol == null || symbol.getId() != "sym_const"){
            return true;
        }
        symbol = lexical.nextSymbol();
        if(symbol != null && symbol.getId() == "ident" && symbol.isValid()){
            symbol = lexical.nextSymbol();
        }
        else {
            erro("dc_c -> ident");
            return false;
        }
        if(symbol != null && symbol.getId() == "sym_eq"){
            symbol = lexical.nextSymbol();
        }
        else {
            erro("dc_c -> sym_eq");
            return false;
        }
        if(symbol != null && symbol.getId() == "number"){
            symbol = lexical.nextSymbol();
        }
        else {
            erro("dc_c -> number");
            return false;
        }
        if(symbol != null && symbol.getId() == "sym_semicolon"){
            lexical.nextSymbol();
            dc_c();
        }
        else {
            erro("dc_c -> sym_semicolon");
            return false;
        }
        return true;
    }

    private boolean dc_v(){
        Token symbol = lexical.currentSymbol();
        if(symbol == null || symbol.getId() != "sym_var"){
            return true;
        }
        symbol = lexical.nextSymbol();
        if(symbol != null && symbol.getId() == "ident" && symbol.isValid()){
            symbol = lexical.nextSymbol();
        }
        else {
            erro("dc_v -> ident");
            return false;
        }
        while (symbol != null && symbol.getId() == "sym_comma") {
            symbol = lexical.nextSymbol();
            if (symbol != null && symbol.getId() == "ident" && symbol.isValid()) {
                symbol = lexical.nextSymbol();
            } else {
                erro("dc_v -> ident");
                return false;
            }
        }
        if(symbol != null && symbol.getId() == "sym_colon"){
            symbol = lexical.nextSymbol();
        }
        else {
            erro("dc_v -> sym_colon");
            return false;
        }
        if(symbol != null && (symbol.getId() == "sym_real" || symbol.getId() == "sym_int")){
            symbol = lexical.nextSymbol();
        }
        else {
            erro("dc_v -> sym_real ou sym_int");
            return false;
        }
        if(symbol != null && symbol.getId() == "sym_semicolon"){
            lexical.nextSymbol();
        }
        else {
            erro("dc_v -> sym_semicolon");
            return false;
        }
        return true;
    }

    private boolean dc_p(){
        Token symbol = lexical.currentSymbol();
        if(symbol == null || symbol.getId() != "sym_procedure"){
            return true;
        }
        symbol = lexical.nextSymbol();
        if(symbol != null && symbol.getId() == "ident" && symbol.isValid()){
            symbol = lexical.nextSymbol();
        }
        else {
            erro("dc_p -> ident");
            return false;
        }
        if(symbol != null && symbol.getId().equals("sym_leftParenthesis")){
            boolean wasExecuted = lista_par();
            if(!wasExecuted){
                erro("lista_par()");
                return false;
            }
            symbol = lexical.nextSymbol();
            if(symbol != null && symbol.getId().equals("sym_rightParenthesis")){
                symbol = lexical.nextSymbol();
            }
            else {
                erro("dc_p -> sym_rightParenthesis");
                return false;
            }
        }
        if(symbol != null && symbol.getId().equals("sym_semicolon")){
            lexical.nextSymbol();
            boolean wasExecuted = corpo_p();
            if(!wasExecuted){
                erro("corpo_p()");
                return false;
            }
        }
        else {
            erro("dc_p -> sym_semicolon");
            return false;
        }
        return true;
    }

    private boolean lista_par(){
        //TODO construir o grafo lista_par
        return true;
    }

    private boolean corpo_p(){
        //TODO construir corpo_p
        return true;
    }

    private boolean comandos(){
        //TODO Contruir o grafo comandos
        return true;
    }

}
