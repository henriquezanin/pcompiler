public class SyntacticalAnalyzer {

    LexicalAnalyzer lexical;
    public enum PanicLevel {
        Local,
        Father,
        Error
    }

    public SyntacticalAnalyzer(LexicalAnalyzer lexical){
        this.lexical = lexical;
    }

    public boolean execute(){
        return programa();
    }

    private void erro(String funcao){
        System.out.println("Procedimento de erro: "+funcao);
    }

    private PanicLevel panic(String local, String father){
        Token symbol = lexical.currentSymbol();
        while(symbol != null && (!symbol.getId().equals(local) || symbol.getId().equals(father))){
            symbol = lexical.nextSymbol();
        }
        if(symbol == null || symbol.getId().equals(father)){
            return PanicLevel.Father;
        }
        if(symbol != null && symbol.getId().equals(local)){
            return PanicLevel.Local;
        }
        return PanicLevel.Error;
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
            System.out.printf("Linha %d: \"program\" esperado\n", lexical.getLineNumber());
//            erro("programa -> sym_program");
            return false;
        }
        // Espera um identificador valido como proximo token
        if(symbol != null && symbol.getId().equals("ident") && symbol.isValid()){
            symbol = lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: identificador esperado ou mal formado\n", lexical.getLineNumber());
//            erro("programa -> ident");
            return false;
        }
        // Espera um ";" como próximo token
        if(symbol != null && symbol.getId().equals("sym_semicolon")){
            // Executa o procedimento dc
            lexical.nextSymbol();
            boolean wasExecuted = dc();
            if(!wasExecuted){
//                erro("programa -> dc()");
                return false;
            }
        }
        else {
            System.out.printf("Linha %d: \";\" esperado\n", lexical.getLineNumber());
//            erro("programa -> sym_semicolon");
            return false;
        }
        symbol = lexical.currentSymbol();
        // Espera begin como próximo token
        if(symbol != null && symbol.getId().equals("sym_begin")){
            lexical.nextSymbol();
            // Executa o procedimento comandos
            boolean wasExecuted = comandos();
            if(!wasExecuted){
//                erro("programa -> comandos()");
                return false;
            }
        }
        else {
            System.out.printf("Linha %d: \"begin\" esperado\n", lexical.getLineNumber());
//            erro("programa -> sym_begin");
            return false;
        }
        symbol = lexical.currentSymbol();
        // Espera end como próximo token
        if(symbol != null && symbol.getId().equals("sym_end")){
            symbol = lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: \"end\" esperado\n", lexical.getLineNumber());
//            erro("programa -> sym_end");
            return false;
        }
        // Espera o token "ponto" para finalizar a compilação
        if(symbol == null || !symbol.getId().equals("sym_dot")){
            System.out.printf("Linha %d: \".\" esperado\n", lexical.getLineNumber());
//            erro("programa -> sym_dot");
            return false;
        }
    return true;
    }

    private boolean dc(){
        boolean wasExecuted = dc_c();
        if(!wasExecuted){
//            erro("dc_c()");
            return false;
        }
        wasExecuted = dc_v();
        if(!wasExecuted){
//            erro("dc_v()");
            return false;
        }
        wasExecuted = dc_p();
        if(!wasExecuted){
//            erro("dc_p()");
            return false;
        }
        return true;
    }

    private boolean dc_c(){
        Token symbol = lexical.currentSymbol();
        if(symbol == null || !symbol.getId().equals("sym_const")){
            return true;
        }
        symbol = lexical.nextSymbol();
        if(symbol != null && symbol.getId().equals("ident") && symbol.isValid()){
            symbol = lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: identificador esperado ou mal formado\n", lexical.getLineNumber());
//            erro("dc_c -> ident");
            return false;
        }
        if(symbol != null && symbol.getId().equals("sym_eq")){
            symbol = lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: \"=\" esperado\n", lexical.getLineNumber());
//            erro("dc_c -> sym_eq");
            return false;
        }
        if(symbol != null && symbol.getId().equals("number")){
            symbol = lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: inteiro ou real esperado\n", lexical.getLineNumber());
//            erro("dc_c -> number");
            return false;
        }
        if(symbol != null && symbol.getId().equals("sym_semicolon")){
            lexical.nextSymbol();
            dc_c();
        }
        else {
            System.out.printf("Linha %d: \";\" esperado\n", lexical.getLineNumber());
//            erro("dc_c -> sym_semicolon");
            return false;
        }
        return true;
    }

    private boolean dc_v(){
        Token symbol = lexical.currentSymbol();
        if(symbol == null || !symbol.getId().equals("sym_var")){
            return true;
        }
        symbol = lexical.nextSymbol();
        if(symbol != null && symbol.getId().equals("ident") && symbol.isValid()){
            symbol = lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: identificador esperado ou mal formado\n", lexical.getLineNumber());
//            erro("dc_v -> ident");
            return false;
        }
        while (symbol != null && symbol.getId().equals("sym_comma")) {
            symbol = lexical.nextSymbol();
            if (symbol != null && symbol.getId().equals("ident") && symbol.isValid()) {
                symbol = lexical.nextSymbol();
            } else {
                System.out.printf("Linha %d: identificador esperado ou mal formado\n", lexical.getLineNumber());
//                erro("dc_v -> ident");
                return false;
            }
        }
        if(symbol != null && symbol.getId().equals("sym_colon")){
            symbol = lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: \":\" esperado\n", lexical.getLineNumber());
//            erro("dc_v -> sym_colon");
            return false;
        }
        if(symbol != null && (symbol.getId().equals("sym_real") || symbol.getId().equals("sym_int"))){
            symbol = lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: inteiro ou real esperado\n", lexical.getLineNumber());
//            erro("dc_v -> sym_real ou sym_int");
            return false;
        }
        if(symbol != null && symbol.getId().equals("sym_semicolon")){
            lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: \";\" esperado\n", lexical.getLineNumber());
//            erro("dc_v -> sym_semicolon");
            return false;
        }
        return true;
    }

    private boolean dc_p(){
        Token symbol = lexical.currentSymbol();
        if(symbol == null || !symbol.getId().equals("sym_procedure")){
            return true;
        }
        symbol = lexical.nextSymbol();
        if(symbol != null && symbol.getId().equals("ident") && symbol.isValid()){
            symbol = lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: identificador esperado ou mal formado\n", lexical.getLineNumber());
//            erro("dc_p -> ident");
            return false;
        }
        if(symbol != null && symbol.getId().equals("sym_leftParenthesis")){
            lexical.nextSymbol();
            boolean wasExecuted = lista_par();
            if(!wasExecuted){
//                erro("lista_par()");
                return false;
            }
            symbol = lexical.currentSymbol();
            if(symbol != null && symbol.getId().equals("sym_rightParenthesis")){
                symbol = lexical.nextSymbol();
            }
            else {
                System.out.printf("Linha %d: \")\" esperado\n", lexical.getLineNumber());
//                erro("dc_p -> sym_rightParenthesis");
                return false;
            }
        }
        if(symbol != null && symbol.getId().equals("sym_semicolon")){
            lexical.nextSymbol();
            boolean wasExecuted = corpo_p();
            if(!wasExecuted){
//                erro("dc_p -> corpo_p()");
                return false;
            }
            dc_p();
        }
        else {
            System.out.printf("Linha %d: \";\" esperado\n", lexical.getLineNumber());
//            erro("dc_p -> sym_semicolon");
            return false;
        }
        return true;
    }

    private boolean lista_par(){
        boolean wasExecuted = variaveis();
        if(!wasExecuted){
//            erro("lista_par -> variaveis()");
            return false;
        }
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_colon")){
            lexical.nextSymbol();
            wasExecuted = tipo_var();
            if(!wasExecuted){
//                erro("lista_par -> tipo_var()");
                return false;
            }
        }
        else {
            System.out.printf("Linha %d: \":\" esperado\n", lexical.getLineNumber());
            //erro()
        }
        symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_semicolon")){
            lexical.nextSymbol();
            lista_par();
        }
        return true;
    }

    private boolean variaveis(){
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("ident")){
            symbol = lexical.nextSymbol();
        }else {
            System.out.printf("Linha %d: identificador esperado ou mal formado\n", lexical.getLineNumber());
//            erro("variaveis() -> ident");
            return false;
        }
        if(symbol != null && symbol.getId().equals("sym_comma")){
            lexical.nextSymbol();
            variaveis();
        }
        return true;
    }

    private boolean tipo_var(){
        Token symbol = lexical.currentSymbol();
        if(symbol != null && (symbol.getId().equals("sym_real") || symbol.getId().equals("sym_int"))){
            lexical.nextSymbol();
            return true;
        }
        else {
            System.out.printf("Linha %d: inteiro ou real esperado\n", lexical.getLineNumber());
//            erro()
        }
        return false;
    }

    private boolean corpo_p(){
        boolean wasExecuted = dc_v();
        if(!wasExecuted){
//            erro("corpo_p -> dc_v()");
            return false;
        }
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_begin")){
            lexical.nextSymbol();
        }else {
            System.out.printf("Linha %d: \"begin\" esperado\n", lexical.getLineNumber());
//            erro("corpo_p -> sym_begin");
            return false;
        }
        wasExecuted = comandos();
        if(!wasExecuted){
//            erro("corpo_p -> comandos()");
            return false;
        }
        symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_end")){
            symbol = lexical.nextSymbol();
        }else {
            System.out.printf("Linha %d: \"end\" esperado\n", lexical.getLineNumber());
//            erro("corpo_p -> sym_end");
            return false;
        }
        if(symbol != null && symbol.getId().equals("sym_semicolon")){
            lexical.nextSymbol();
        }else {
            System.out.printf("Linha %d: \";\" esperado\n", lexical.getLineNumber());
//            erro("corpo_p -> sym_semicolon");
            return false;
        }
        return true;
    }

    private boolean comandos(){
        Token symbol = lexical.currentSymbol();
        if(symbol != null && (symbol.getId().equals("sym_read") || symbol.getId().equals("sym_write") ||
                symbol.getId().equals("sym_while") || symbol.getId().equals("sym_if") || symbol.getId().equals("ident") ||
                symbol.getId().equals("sym_begin"))) {
            boolean wasExecuted = cmd();
            if (!wasExecuted) {
//                erro("comandos -> cmd()");
                return false;
            }
            symbol = lexical.currentSymbol();
            if(symbol != null && symbol.getId().equals("sym_semicolon")){
                lexical.nextSymbol();
                comandos();
            } else {
                System.out.printf("Linha %d: \";\" esperado\n", lexical.getLineNumber());
//                erro("comandos() -> sym_semicolon");
                return false;
            }
        }
        return true;
    }

    private boolean cmd(){
        boolean wasExecuted = cmd_read();
        if(!wasExecuted){
//            erro("cmd -> cmd_read()");
            return false;
        }
        wasExecuted = cmd_write();
        if(!wasExecuted){
//            erro("cmd -> cmd_write()");
            return false;
        }
        wasExecuted = cmd_while();
        if(!wasExecuted){
//            erro("cmd -> cmd_while()");
            return false;
        }
        wasExecuted = cmd_if();
        if(!wasExecuted){
//            erro("cmd -> cmd_if()");
            return false;
        }
        wasExecuted = cmd_ident();
        if(!wasExecuted){
//            erro("cmd -> cmd_if()");
            return false;
        }
        wasExecuted = cmd_begin();
        if(!wasExecuted){
//            erro("cmd -> cmd_begin()");
            return false;
        }
        return true;
    }

    private boolean cmd_read(){
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_read")){
            symbol = lexical.nextSymbol();
            if(symbol != null && symbol.getId().equals("sym_leftParenthesis")){
                lexical.nextSymbol();
                boolean wasExecuted = variaveis();
                if(!wasExecuted){
//                    erro("cmd_read -> variaveis()");
                    return false;
                }
                symbol = lexical.currentSymbol();
                if(symbol != null && symbol.getId().equals("sym_rightParenthesis")) {
                    lexical.nextSymbol();
                    return true;
                } else {
                    System.out.printf("Linha %d: \")\" esperado\n", lexical.getLineNumber());
//                    erro("cmd_read -> sym_rightParenthesis");
                    return false;
                }
            }else{
                System.out.printf("Linha %d: \"(\" esperado\n", lexical.getLineNumber());
//                erro("cmd_read -> sym_leftParenthesis");
                return false;
            }
        }
       return true;
    }

    private boolean cmd_write(){
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_write")){
            symbol = lexical.nextSymbol();
            if(symbol != null && symbol.getId().equals("sym_leftParenthesis")){
                lexical.nextSymbol();
                boolean wasExecuted = variaveis();
                if(!wasExecuted){
//                    erro("cmd_write -> variaveis()");
                    return false;
                }
                symbol = lexical.currentSymbol();
                if(symbol != null && symbol.getId().equals("sym_rightParenthesis")) {
                    lexical.nextSymbol();
                    return true;
                } else {
                    System.out.printf("Linha %d: \")\" esperado\n", lexical.getLineNumber());
//                    erro("cmd_write -> sym_rightParenthesis");
                    return false;
                }
            }else{
                System.out.printf("Linha %d: \"(\" esperado\n", lexical.getLineNumber());
//                erro("cmd_write -> sym_leftParenthesis");
                return false;
            }
        }
        return true;
    }

    private boolean cmd_while(){
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_while")){
            symbol = lexical.nextSymbol();
            if(symbol != null && symbol.getId().equals("sym_leftParenthesis")){
                lexical.nextSymbol();
                boolean wasExecuted = condicao();
                if(!wasExecuted){
//                    erro("cmd_while -> condicao()");
                    return false;
                }
                symbol = lexical.currentSymbol();
                if(symbol != null && symbol.getId().equals("sym_rightParenthesis")) {
                    lexical.nextSymbol();
                } else {
                    System.out.printf("Linha %d: \")\" esperado\n", lexical.getLineNumber());
//                    erro("cmd_while -> sym_rightParenthesis");
                    return false;
                }
            }else {
                System.out.printf("Linha %d: \"(\" esperado\n", lexical.getLineNumber());
//                erro("cmd_while -> sym_leftParenthesis");
                return false;
            }
            symbol = lexical.currentSymbol();
            if(symbol != null && symbol.getId().equals("sym_do")){
                lexical.nextSymbol();
                cmd();
            }
            else {
                System.out.printf("Linha %d: \"(\" esperado\n", lexical.getLineNumber());
//                erro("cmd_while -> sym_do");
                return false;
            }
        }
        return true;
    }

    private boolean cmd_if(){
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_if")){
            lexical.nextSymbol();
            boolean wasExecuted = condicao();
            if(!wasExecuted){
//                erro("cmd_if -> condicao()");
                return false;
            }
            symbol = lexical.currentSymbol();
            if(symbol != null && symbol.getId().equals("sym_then")) {
                lexical.nextSymbol();
            }else {
                System.out.printf("Linha %d: \"then\" esperado\n", lexical.getLineNumber());
//                erro("cmd_if -> sym_then");
                return false;
            }
            cmd();
            symbol = lexical.currentSymbol();
            if(symbol != null && symbol.getId().equals("sym_else")){
                lexical.nextSymbol();
                cmd();
            }
        }
        return true;
    }

    private boolean cmd_ident(){
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("ident") && symbol.isValid()){
            symbol = lexical.nextSymbol();
            if(symbol != null && symbol.getId().equals("sym_atrib")){
                lexical.nextSymbol();
                boolean wasExecuted = expressao();
                if(!wasExecuted){
//                    erro("cmd() -> expressao()");
                    return false;
                }
                return true;
            }
            if(symbol != null && symbol.getId().equals("sym_leftParenthesis")){
                lexical.nextSymbol();
                argumentos();
                symbol = lexical.currentSymbol();
                if(symbol != null && symbol.getId().equals("sym_rightParenthesis")){
                    lexical.nextSymbol();
                    return true;
                }
                else{
                    System.out.printf("Linha %d: \")\" esperado\n", lexical.getLineNumber());
                    return false;
                }
            }else {
                return true;
            }
        }
        return true;
    }

    private boolean cmd_begin(){
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_begin")){
            lexical.nextSymbol();
            boolean wasExecuted = comandos();
            if(!wasExecuted){
//                erro("cmd_begin -> comandos()");
                return false;
            }
            symbol = lexical.currentSymbol();
            if(symbol != null && symbol.getId().equals("sym_end")){
                lexical.nextSymbol();
                return true;
            } else{
                System.out.printf("Linha %d: \"end\" esperado\n", lexical.getLineNumber());
//                erro("cmd_begin -> sym_end");
                return false;
            }
        }
        return true;
    }

    private boolean argumentos(){
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("ident")){
            symbol = lexical.nextSymbol();
        } else {
            System.out.printf("Linha %d: identificador esperado ou mal formado\n", lexical.getLineNumber());
//            erro("argumentos() -> ident");
            return false;
        }
        if(symbol != null && symbol.equals("sym_semicolon")){
            lexical.nextSymbol();
            argumentos();
        }
        return true;
    }

    private boolean condicao(){
        boolean wasExecuted = expressao();
        if(!wasExecuted){
//            erro("condicao -> expressao()");
            return false;
        }
        Token symbol = lexical.currentSymbol();
        if(symbol != null && (symbol.getId().equals("sym_eq") || symbol.getId().equals("sym_notEq") ||
                symbol.getId().equals("sym_greaterOrEq") || symbol.getId().equals("sym_lessOrEq") ||
                symbol.getId().equals("sym_greater") || symbol.getId().equals("sym_less"))){
            lexical.nextSymbol();
        } else{
            System.out.printf("Linha %d: relação esperada\n", lexical.getLineNumber());
//            erro("condicao -> relacao");
            return false;
        }
        wasExecuted = expressao();
        if(!wasExecuted){
//            erro("condicao -> expressao()");
            return false;
        }
        return true;
    }

    private boolean expressao(){
        boolean wasExecuted = termo();
        if(!wasExecuted){
//            erro("expressao -> termo()");
            return false;
        }
        wasExecuted = outros_termos();
        if(!wasExecuted){
//            erro("expressao -> outros_termos()");
            return false;
        }
        return true;
    }

    private boolean outros_termos(){
        Token symbol = lexical.currentSymbol();
        if(symbol != null && (symbol.getId().equals("sym_plus") || symbol.getId().equals("sym_minus"))){
            lexical.nextSymbol();
        } else {
            return true;
        }
        boolean wasExecuted = termo();
        if(!wasExecuted){
//            erro("outros_termos -> termo()");
            return false;
        }
        outros_termos();
        return true;
    }

    private boolean termo(){
        Token symbol = lexical.currentSymbol();
        if(symbol != null && (symbol.getId().equals("sym_plus") || symbol.getId().equals("sym_minus"))){
            lexical.nextSymbol();
        }
        boolean wasExecuted = fator();
        if(!wasExecuted){
//            erro("termo -> fator()");
            return false;
        }
        wasExecuted = mais_fatores();
        if(!wasExecuted){
//            erro("termo -> mais_fatores()");
            return false;
        }
        return true;
    }

    private boolean fator(){
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("ident") && symbol.isValid()){
            lexical.nextSymbol();
            return true;
        }
        if(symbol != null && symbol.getId().equals("number")){
            lexical.nextSymbol();
            return true;
        }
        if(symbol != null && symbol.getId().equals("sym_leftParenthesis")){
            lexical.nextSymbol();
        } else {
            System.out.printf("Linha %d: \"(\" esperado\n", lexical.getLineNumber());
//            erro("fator -> sym_leftParenthesis");
        }
        boolean wasExecuted = expressao();
        if(!wasExecuted){
//            erro("fator -> expressao()");
            return false;
        }
        if(symbol != null && symbol.getId().equals("sym_rightParenthesis")){
            lexical.nextSymbol();
        } else {
            System.out.printf("Linha %d: \")\" esperado\n", lexical.getLineNumber());
//            erro("fator -> sym_rightParenthesis");
            return false;
        }
        return true;
    }

    private boolean mais_fatores(){
        Token symbol = lexical.currentSymbol();
        if(symbol != null && (symbol.getId().equals("sym_mult") || symbol.getId().equals("sym_div"))){
            lexical.nextSymbol();
        }
        else {
            return true;
        }
        boolean wasExecuted = fator();
        if(!wasExecuted){
//            erro("mais_fatores -> fator()");
            return false;
        }
        mais_fatores();
        return true;
    }

    private boolean func_for(){
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("ident") && symbol.isValid()){
            lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: identificador esperado ou mal formado\n", lexical.getLineNumber());
            erro("func_for -> ident");
            return false;
        }
        boolean wasExecuted = expressao();
        if(!wasExecuted){
//            erro("func_for -> expressao()");
            return false;
        }
        symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_to")){
            lexical.nextSymbol();
        } else {
            System.out.printf("Linha %d: \"to\" esperado\n", lexical.getLineNumber());
//            erro("func_for -> sym_to");
            return false;
        }
        wasExecuted = expressao();
        if(!wasExecuted){
//            erro("func_for -> expressao()");
            return false;
        }
        wasExecuted = comandos();
        if(!wasExecuted){
//            erro("func_for -> comandos()");
            return false;
        }
        return true;
    }
}
