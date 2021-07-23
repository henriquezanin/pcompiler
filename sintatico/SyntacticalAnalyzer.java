public class SyntacticalAnalyzer {

    LexicalAnalyzer lexical;
    public enum PanicLevel {
        Local,
        Father,
        None
    }

    public SyntacticalAnalyzer(LexicalAnalyzer lexical){
        this.lexical = lexical;
    }

    public boolean execute(){
        return programa();
    }

    private boolean checkIfSymbolIsInArray(String symbol, String []symbolArray){
        if(symbolArray == null || symbol == null){
            return false;
        }
        for (String s : symbolArray) {
            if(s.equals(symbol)){
                return true;
            }
        }
        return false;
    }
    private String[] concatStringArrays(String []first, String []second){
        if(first == null){
            return second;
        } else if(second == null){
            return  first;
        }
        int size = first.length + second.length;
        String []newArray = new String[size];
        System.arraycopy(first, 0, newArray, 0, first.length);
        if (size - first.length >= 0)
            System.arraycopy(second, 0, newArray, first.length, second.length);
        return newArray;
    }

    private PanicLevel panic(String []local, String []father){
        Token symbol = lexical.currentSymbol();
        if(symbol != null && !symbol.isValid()){
            symbol = lexical.nextSymbol();
        }
//        if(symbol != null){
//            System.out.println(symbol.getValue());
//        }
        while(symbol != null && symbol.isValid() && !checkIfSymbolIsInArray(symbol.getId(), local) && !checkIfSymbolIsInArray(symbol.getId(), father)){
            symbol = lexical.nextSymbol();
            if(symbol != null && !symbol.isValid()){
                symbol = lexical.nextSymbol();
            }
        }
        if(symbol == null || checkIfSymbolIsInArray(symbol.getId(), father)){
            return PanicLevel.Father;
        }
        else if(checkIfSymbolIsInArray(symbol.getId(), local)){
            return PanicLevel.Local;
        }
        return PanicLevel.None;
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
            // Seguidor(program) = ident | Seguidor(programa) = lambda
            PanicLevel err = panic(new String[] {"ident"}, new String[]{""});
            if(err == PanicLevel.Father) {
                return false;
            }
        }
        // Espera um identificador valido como proximo token
        if(symbol != null && symbol.getId().equals("ident") && symbol.isValid()){
            symbol = lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: identificador esperado ou mal formado\n", lexical.getLineNumber());
            // Seguidor(ident) = ; | Seguidor(programa) = lambda
            PanicLevel err = panic(new String[] {"sym_semicolon"}, new String[] {""});
            if(err == PanicLevel.Father) {
                return false;
            }
        }
        // Espera um ";" como próximo token
        if(symbol != null && symbol.getId().equals("sym_semicolon")){
            lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: \";\" esperado\n", lexical.getLineNumber());
            // Primeiro(dc) = const,var,procedure | Seguidor(programa) = lambda
            PanicLevel err = panic(new String[] {"sym_const", "sym_var", "sym_procedure"}, new String[] {""});
            if(err == PanicLevel.Father) {
                return false;
            }
        }
        // Seguidor(dc) = begin | Seguidor(programa) = lambda
        PanicLevel err = dc(new String[] {"sym_begin"}, new String[] {""});
        if(err == PanicLevel.Father) {
            return false;
        }
        symbol = lexical.currentSymbol();
        // Espera begin como próximo token
        if(symbol != null && symbol.getId().equals("sym_begin")){
            lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: \"begin\" esperado\n", lexical.getLineNumber());
            // Primeiro(comandos) = read, write, while, if, ident, begin | Seguidor(programa) = lambda
            err = panic(new String[] {"sym_read", "sym_write", "sym_while", "sym_if", "ident", "sym_begin", "sym_for"},
                    new String[] {""});
            if(err == PanicLevel.Father) {
                return false;
            }
        }
        // Seguidor(comandos) = end | Seguidor(programa) = lambda
        comandos(new String[] {"sym_end"}, new String[] {""});
        symbol = lexical.currentSymbol();
        // Espera end como próximo token
        if(symbol != null && symbol.getId().equals("sym_end")){
            symbol = lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: \"end\" esperado\n", lexical.getLineNumber());
            // Seguidor(end) = . | Seguidor(programa) = lambda
            err = panic(new String[] {"sym_dot"}, new String[] {""});
            if(err == PanicLevel.Father) {
                return false;
            }
        }
        // Espera o token "ponto" para finalizar a compilação
        if(symbol == null || !symbol.getId().equals("sym_dot")){
            System.out.printf("Linha %d: \".\" esperado\n", lexical.getLineNumber());
            err = panic(new String[] {""}, new String[] {""});
            if(err == PanicLevel.Father) {
                return false;
            }
        }
    return true;
    }

    private PanicLevel dc(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        PanicLevel err = dc_c(new String[] {"sym_var", "sym_procedure"}, newFather);
        if(err == PanicLevel.Father) {
            Token symbol = lexical.currentSymbol();
            if(checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        err = dc_v(new String[] {"sym_procedure"}, newFather);
        if(err == PanicLevel.Father) {
            Token symbol = lexical.currentSymbol();
            if(checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        err = dc_p(null, newFather);
        if(err == PanicLevel.Father) {
            Token symbol = lexical.currentSymbol();
            if(checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        return PanicLevel.None;
    }

    private PanicLevel dc_c(String []local, String []father){
        Token symbol = lexical.currentSymbol();
        if(symbol == null || !symbol.getId().equals("sym_const")){
            return PanicLevel.None;
        }
        String []newFather = concatStringArrays(local, father);
        symbol = lexical.nextSymbol();
        if(symbol != null && symbol.getId().equals("ident") && symbol.isValid()){
            symbol = lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: identificador esperado ou mal formado\n", lexical.getLineNumber());
            PanicLevel err = panic(new String[] {"sym_eq"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        if(symbol != null && symbol.getId().equals("sym_eq")){
            symbol = lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: \"=\" esperado\n", lexical.getLineNumber());
            PanicLevel err = panic(new String[] {"number"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        if(symbol != null && symbol.getId().equals("number")){
            symbol = lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: inteiro ou real esperado\n", lexical.getLineNumber());
            PanicLevel err = panic(new String[] {"sym_semicolon"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        if(symbol != null && symbol.getId().equals("sym_semicolon")){
            lexical.nextSymbol();
            dc_c(local,father);
        }
        else {
            System.out.printf("Linha %d: \";\" esperado\n", lexical.getLineNumber());
            PanicLevel err = panic(null, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        return PanicLevel.None;
    }

    private PanicLevel dc_v(String []local, String []father){
        Token symbol = lexical.currentSymbol();
        if(symbol == null || !symbol.getId().equals("sym_var")){
            return PanicLevel.None;
        }
        String []newFather = concatStringArrays(local, father);
        symbol = lexical.nextSymbol();
        if(symbol != null && symbol.getId().equals("ident") && symbol.isValid()){
            symbol = lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: identificador esperado ou mal formado\n", lexical.getLineNumber());
            PanicLevel err = panic(new String[] {"sym_colon"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        while (symbol != null && symbol.getId().equals("sym_comma")) {
            symbol = lexical.nextSymbol();
            if (symbol != null && symbol.getId().equals("ident") && symbol.isValid()) {
                symbol = lexical.nextSymbol();
            } else {
                System.out.printf("Linha %d: identificador esperado ou mal formado\n", lexical.getLineNumber());
                PanicLevel err = panic(new String[] {"sym_colon"}, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
            }
        }
        if(symbol != null && symbol.getId().equals("sym_colon")){
            symbol = lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: \":\" esperado\n", lexical.getLineNumber());
            PanicLevel err = panic(new String[] {"sym_real", "sym_int"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        if(symbol != null && (symbol.getId().equals("sym_real") || symbol.getId().equals("sym_int"))){
            symbol = lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: inteiro ou real esperado\n", lexical.getLineNumber());
            PanicLevel err = panic(new String[] {"sym_semicolon"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        if(symbol != null && symbol.getId().equals("sym_semicolon")){
            lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: \";\" esperado\n", lexical.getLineNumber());
            PanicLevel err = panic(null, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        return PanicLevel.None;
    }

    private PanicLevel dc_p(String []local, String []father){
        Token symbol = lexical.currentSymbol();
        if(symbol == null || !symbol.getId().equals("sym_procedure")){
            return PanicLevel.None;
        }
        String []newFather = concatStringArrays(local, father);
        symbol = lexical.nextSymbol();
        if(symbol != null && symbol.getId().equals("ident") && symbol.isValid()){
            symbol = lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: identificador esperado ou mal formado\n", lexical.getLineNumber());
            PanicLevel err = panic(new String[] {"sym_leftParenthesis", "sym_semicolon"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        if(symbol != null && symbol.getId().equals("sym_leftParenthesis")){
            lexical.nextSymbol();
            PanicLevel err = lista_par(new String[] {"sym_rightParenthesis"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
            symbol = lexical.currentSymbol();
            if(symbol != null && symbol.getId().equals("sym_rightParenthesis")){
                symbol = lexical.nextSymbol();
            }
            else {
                System.out.printf("Linha %d: \")\" esperado\n", lexical.getLineNumber());
                err = panic(new String[] {"sym_semicolon"}, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
            }
        }
        if(symbol != null && symbol.getId().equals("sym_semicolon")){
            lexical.nextSymbol();
            PanicLevel err = corpo_p(new String[] {"sym_procedure"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
            dc_p(local, father);
        }
        else {
            System.out.printf("Linha %d: \";\" esperado\n", lexical.getLineNumber());
            PanicLevel err = lista_par(new String[] {"sym_var", "sym_begin"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        return PanicLevel.None;
    }

    private PanicLevel lista_par(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        PanicLevel err = variaveis(new String[] {"sym_colon"}, newFather);
        if(err == PanicLevel.Father) {
            Token symbol = lexical.currentSymbol();
            if(checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_colon")){
            lexical.nextSymbol();
            err = tipo_var(new String[] {"sym_semicolon", "sym_rightParenthesis"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        else {
            System.out.printf("Linha %d: \":\" esperado\n", lexical.getLineNumber());
            err = panic(new String[] {"sym_real", "sym_int"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_semicolon")){
            lexical.nextSymbol();
            err = lista_par(local, father);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        return PanicLevel.None;
    }

    private PanicLevel variaveis(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("ident")){
            symbol = lexical.nextSymbol();
        }else {
            System.out.printf("Linha %d: identificador esperado ou mal formado\n", lexical.getLineNumber());
            PanicLevel err = panic(new String[] {"sym_comma", "sym_colon", "sym_rightParenthesis"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        if(symbol != null && symbol.getId() != null && symbol.getId().equals("sym_comma")){
            lexical.nextSymbol();
            PanicLevel err = variaveis(local, father);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        return PanicLevel.None;
    }

    private PanicLevel tipo_var(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        Token symbol = lexical.currentSymbol();
        if(symbol != null && (symbol.getId().equals("sym_real") || symbol.getId().equals("sym_int"))){
            lexical.nextSymbol();
        }
        else {
            System.out.printf("Linha %d: inteiro ou real esperado\n", lexical.getLineNumber());
            PanicLevel err = panic(null, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        return PanicLevel.None;
    }

    private PanicLevel corpo_p(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        PanicLevel err = dc_v(new String[] {"sym_var", "sym_procedure", "sym_begin"}, newFather);
        if(err == PanicLevel.Father) {
            Token symbol = lexical.currentSymbol();
            if(checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_begin")){
            lexical.nextSymbol();
        }else {
            System.out.printf("Linha %d: \"begin\" esperado\n", lexical.getLineNumber());
            err = panic(new String[] {"sym_read", "sym_write", "sym_while", "sym_if", "ident", "sym_begin", "sym_for"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        err = comandos(new String[] {"sym_end"}, newFather);
        if(err == PanicLevel.Father) {
            symbol = lexical.currentSymbol();
            if(checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_end")){
            symbol = lexical.nextSymbol();
        }else {
            System.out.printf("Linha %d: \"end\" esperado\n", lexical.getLineNumber());
            err = panic(new String[] {"sym_semicolon"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        if(symbol != null && symbol.getId().equals("sym_semicolon")){
            lexical.nextSymbol();
        }else {
            System.out.printf("Linha %d: \";\" esperado\n", lexical.getLineNumber());
            err = panic(null, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        return PanicLevel.None;
    }

    private PanicLevel comandos(String []local, String []father){
        Token symbol = lexical.currentSymbol();
        String []newFather = concatStringArrays(local, father);
        if(symbol != null && (symbol.getId().equals("sym_read") || symbol.getId().equals("sym_write") ||
                symbol.getId().equals("sym_while") || symbol.getId().equals("sym_if") || symbol.getId().equals("ident") ||
                symbol.getId().equals("sym_begin") || symbol.getId().equals("sym_for"))) {
            PanicLevel err = cmd(new String[] {"sym_semicolon", "sym_else"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
            symbol = lexical.currentSymbol();
            if(symbol != null && symbol.getId().equals("sym_semicolon")){
                lexical.nextSymbol();
                err = comandos(local, father);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
            } else {
                System.out.printf("Linha %d: \";\" esperado\n", lexical.getLineNumber());
                err = panic(null, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
            }
        }
        return PanicLevel.None;
    }

    private PanicLevel cmd(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        Token symbol;
        PanicLevel err = cmd_read(null, newFather);
        if(err == PanicLevel.Father) {
            symbol = lexical.currentSymbol();
            if(checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        err = cmd_write(null, newFather);
        if(err == PanicLevel.Father) {
            symbol = lexical.currentSymbol();
            if(checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        err = cmd_while(null, newFather);
        if(err == PanicLevel.Father) {
            symbol = lexical.currentSymbol();
            if(checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        err = cmd_if(null, newFather);
        if(err == PanicLevel.Father) {
            symbol = lexical.currentSymbol();
            if(checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        err = cmd_ident(null, newFather);
        if(err == PanicLevel.Father) {
            symbol = lexical.currentSymbol();
            if(checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        err = cmd_begin(null, newFather);
        if(err == PanicLevel.Father) {
            symbol = lexical.currentSymbol();
            if(checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        err = cmd_for(null, newFather);
        if(err == PanicLevel.Father) {
            symbol = lexical.currentSymbol();
            if(checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        return PanicLevel.None;
    }

    private PanicLevel cmd_read(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_read")){
            symbol = lexical.nextSymbol();
            if(symbol != null && symbol.getId().equals("sym_leftParenthesis")){
                lexical.nextSymbol();
                PanicLevel err = variaveis(new String[] {"ident"}, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
                symbol = lexical.currentSymbol();
                if(symbol != null && symbol.getId().equals("sym_rightParenthesis")) {
                    lexical.nextSymbol();
                } else {
                    System.out.printf("Linha %d: \")\" esperado\n", lexical.getLineNumber());
                    err = panic(null, newFather);
                    if(err == PanicLevel.Father) {
                        symbol = lexical.currentSymbol();
                        if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                            return PanicLevel.Father;
                        }
                        return PanicLevel.Local;
                    }
                }
            }else{
                System.out.printf("Linha %d: \"(\" esperado\n", lexical.getLineNumber());
                PanicLevel err = panic(new String[] {"ident"}, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
            }
        }
       return PanicLevel.None;
    }

    private PanicLevel cmd_write(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_write")){
            symbol = lexical.nextSymbol();
            if(symbol != null && symbol.getId().equals("sym_leftParenthesis")){
                lexical.nextSymbol();
                PanicLevel err = variaveis(new String[] {"ident"}, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
                symbol = lexical.currentSymbol();
                if(symbol != null && symbol.getId().equals("sym_rightParenthesis")) {
                    lexical.nextSymbol();
                } else {
                    System.out.printf("Linha %d: \")\" esperado\n", lexical.getLineNumber());
                    err = panic(null, newFather);
                    if(err == PanicLevel.Father) {
                        symbol = lexical.currentSymbol();
                        if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                            return PanicLevel.Father;
                        }
                        return PanicLevel.Local;
                    }
                }
            }else{
                System.out.printf("Linha %d: \"(\" esperado\n", lexical.getLineNumber());
                PanicLevel err = panic(new String[] {"ident"}, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
            }
        }
        return PanicLevel.None;
    }

    private PanicLevel cmd_while(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_while")){
            symbol = lexical.nextSymbol();
            if(symbol != null && symbol.getId().equals("sym_leftParenthesis")){
                lexical.nextSymbol();
                PanicLevel err = condicao(new String[] {"sym_rightParenthesis"}, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
                symbol = lexical.currentSymbol();
                if(symbol != null && symbol.getId().equals("sym_rightParenthesis")) {
                    lexical.nextSymbol();
                } else {
                    System.out.printf("Linha %d: \")\" esperado\n", lexical.getLineNumber());
                    err = panic(new String[] {"sym_do"}, newFather);
                    if(err == PanicLevel.Father) {
                        symbol = lexical.currentSymbol();
                        if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                            return PanicLevel.Father;
                        }
                        return PanicLevel.Local;
                    }
                }
            }else {
                System.out.printf("Linha %d: \"(\" esperado\n", lexical.getLineNumber());
                PanicLevel err = panic(new String[] {"sym_minus", "sym_plus", "ident", "number", "sym_leftParenthesis"}, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
            }
            symbol = lexical.currentSymbol();
            if(symbol != null && symbol.getId().equals("sym_do")){
                lexical.nextSymbol();
                PanicLevel err = cmd(null, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
            }
            else {
                System.out.printf("Linha %d: \"do\" esperado\n", lexical.getLineNumber());
                PanicLevel err = panic(new String[] {"sym_read", "sym_write", "sym_while", "sym_if", "ident", "sym_begin", "sym_for"}, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
            }
        }
        return PanicLevel.None;
    }

    private PanicLevel cmd_if(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_if")){
            lexical.nextSymbol();
            PanicLevel err = condicao(new String[] {"sym_then"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
            symbol = lexical.currentSymbol();
            if(symbol != null && symbol.getId().equals("sym_then")) {
                lexical.nextSymbol();
            }else {
                System.out.printf("Linha %d: \"then\" esperado\n", lexical.getLineNumber());
                err = panic(new String[] {"sym_read", "sym_write", "sym_while", "sym_if", "ident", "sym_begin", "sym_for"}, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
            }
            err = cmd(new String[] {"sym_else"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
            symbol = lexical.currentSymbol();
            if(symbol != null && symbol.getId().equals("sym_else")){
                lexical.nextSymbol();
                err = cmd(null, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
            }
        }
        return PanicLevel.None;
    }

    private PanicLevel cmd_ident(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("ident") && symbol.isValid()){
            symbol = lexical.nextSymbol();
            if(symbol != null && symbol.getId().equals("sym_atrib")){
                lexical.nextSymbol();
                PanicLevel err = expressao(null, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
                return PanicLevel.None;
            }
            //TODO se o identificador não for válido entrar no modo panico
            if(symbol != null && symbol.getId().equals("sym_leftParenthesis")){
                lexical.nextSymbol();
                PanicLevel err = argumentos(new String[] {"sym_rightParenthesis"}, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
                symbol = lexical.currentSymbol();
                if(symbol != null && symbol.getId().equals("sym_rightParenthesis")){
                    lexical.nextSymbol();
                }
                else{
                    System.out.printf("Linha %d: \")\" esperado\n", lexical.getLineNumber());
                    err = panic(null, newFather);
                    if(err == PanicLevel.Father) {
                        symbol = lexical.currentSymbol();
                        if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                            return PanicLevel.Father;
                        }
                        return PanicLevel.Local;
                    }
                }
            }else {
                return PanicLevel.None;
            }
        } else if(symbol != null && symbol.getId().equals("ident") && !symbol.isValid()){
            System.out.printf("Linha %d: identificador esperado ou mal formado\n", lexical.getLineNumber());
            PanicLevel err = panic(new String[] {"sym_plus", "sym_minus", "numero", "sym_leftParenthesis"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        return PanicLevel.None;
    }

    private PanicLevel cmd_begin(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_begin")){
            lexical.nextSymbol();
            PanicLevel err = comandos(new String[] {"sym_end"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
            symbol = lexical.currentSymbol();
            if(symbol != null && symbol.getId().equals("sym_end")){
                lexical.nextSymbol();
            } else{
                System.out.printf("Linha %d: \"end\" esperado\n", lexical.getLineNumber());
                err = panic(null, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
            }
        }
        return PanicLevel.None;
    }

    private PanicLevel argumentos(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("ident")){
            symbol = lexical.nextSymbol();
        } else {
            System.out.printf("Linha %d: identificador esperado ou mal formado\n", lexical.getLineNumber());
            PanicLevel err = panic(new String[] {"sym_semicolon", "sym_rightParenthesis"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        if(symbol != null && symbol.getId().equals("sym_semicolon")){
            lexical.nextSymbol();
            PanicLevel err = argumentos(local, father);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        return PanicLevel.None;
    }

    private PanicLevel condicao(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        PanicLevel err = expressao(new String[] {"sym_eq", "sym_notEq", "sym_greaterOrEq", "sym_lessOrEq", "sym_greater",
                "sym_less"}, newFather);
        if(err == PanicLevel.Father) {
            Token symbol = lexical.currentSymbol();
            if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        Token symbol = lexical.currentSymbol();
        if(symbol != null && (symbol.getId().equals("sym_eq") || symbol.getId().equals("sym_notEq") ||
                symbol.getId().equals("sym_greaterOrEq") || symbol.getId().equals("sym_lessOrEq") ||
                symbol.getId().equals("sym_greater") || symbol.getId().equals("sym_less"))){
            lexical.nextSymbol();
        } else{
            System.out.printf("Linha %d: relação esperada\n", lexical.getLineNumber());
            err = panic(new String[] {"sym_plus", "sym_minus", "ident", "number", "sym_leftParenthesis"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        err = expressao(null, newFather);
        if(err == PanicLevel.Father) {
            symbol = lexical.currentSymbol();
            if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        return PanicLevel.None;
    }

    private PanicLevel expressao(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        PanicLevel err = termo(new String[] {"sym_plus", "sym_minus"}, newFather);
        if(err == PanicLevel.Father) {
            Token symbol = lexical.currentSymbol();
            if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        err = outros_termos(null, newFather);
        if(err == PanicLevel.Father) {
            Token symbol = lexical.currentSymbol();
            if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        return PanicLevel.None;
    }

    private PanicLevel outros_termos(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        Token symbol = lexical.currentSymbol();
        if(symbol != null && (symbol.getId().equals("sym_plus") || symbol.getId().equals("sym_minus"))){
            lexical.nextSymbol();
        } else {
            return PanicLevel.None;
        }
        PanicLevel err = termo(null, newFather);
        if(err == PanicLevel.Father) {
            symbol = lexical.currentSymbol();
            if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        err = outros_termos(local, father);
        if(err == PanicLevel.Father) {
            symbol = lexical.currentSymbol();
            if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        return PanicLevel.None;
    }

    private PanicLevel termo(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        Token symbol = lexical.currentSymbol();
        if(symbol != null && (symbol.getId().equals("sym_plus") || symbol.getId().equals("sym_minus"))){
            lexical.nextSymbol();
        }
        PanicLevel err = fator(new String[] {"sym_mult", "sym_div"}, newFather);
        if(err == PanicLevel.Father) {
            symbol = lexical.currentSymbol();
            if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        err = mais_fatores(null, newFather);
        if(err == PanicLevel.Father) {
            symbol = lexical.currentSymbol();
            if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        return PanicLevel.None;
    }

    private PanicLevel fator(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("ident") && symbol.isValid()){
            lexical.nextSymbol();
            return PanicLevel.None;
        }else if(symbol != null && symbol.getId().equals("ident") && !symbol.isValid()){
            System.out.printf("Linha %d: identificador esperado ou mal formado\n", lexical.getLineNumber());
            PanicLevel err = panic(new String[] {"sym_mult", "sym_div"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        if(symbol != null && symbol.getId().equals("number")){
            lexical.nextSymbol();
            return PanicLevel.None;
        }
        if(symbol != null && symbol.getId().equals("sym_leftParenthesis")){
            lexical.nextSymbol();
        } else {
            System.out.printf("Linha %d: \"(\" esperado\n", lexical.getLineNumber());
            PanicLevel err = panic(new String[] {"sym_plus", "sym_minus", "ident", "number", "sym_leftParenthesis"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        PanicLevel err = expressao(new String[] {"sym_rightParenthesis"}, newFather);
        if(err == PanicLevel.Father) {
            symbol = lexical.currentSymbol();
            if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        if(symbol != null && symbol.getId().equals("sym_rightParenthesis")){
            lexical.nextSymbol();
        } else {
            System.out.printf("Linha %d: \")\" esperado\n", lexical.getLineNumber());
            err = panic(null, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        return PanicLevel.None;
    }

    private PanicLevel mais_fatores(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        Token symbol = lexical.currentSymbol();
        if(symbol != null && (symbol.getId().equals("sym_mult") || symbol.getId().equals("sym_div"))){
            lexical.nextSymbol();
        }
        else {
            return PanicLevel.None;
        }
        PanicLevel err = fator(null, newFather);
        if(err == PanicLevel.Father) {
            symbol = lexical.currentSymbol();
            if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        err = mais_fatores(local, father);
        if(err == PanicLevel.Father) {
            symbol = lexical.currentSymbol();
            if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                return PanicLevel.Father;
            }
            return PanicLevel.Local;
        }
        return PanicLevel.None;
    }

    private PanicLevel cmd_for(String []local, String []father){
        String []newFather = concatStringArrays(local, father);
        Token symbol = lexical.currentSymbol();
        if(symbol != null && symbol.getId().equals("sym_for")) {
            symbol = lexical.nextSymbol();
            if (symbol != null && symbol.getId().equals("ident") && symbol.isValid()) {
                lexical.nextSymbol();
            } else {
                System.out.printf("Linha %d: identificador esperado ou mal formado\n", lexical.getLineNumber());
                PanicLevel err = panic(new String[] {"sym_atrib"}, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
            }
            symbol = lexical.currentSymbol();
            if (symbol != null && symbol.getId().equals("sym_atrib") && symbol.isValid()) {
                lexical.nextSymbol();
            } else {
                System.out.printf("Linha %d: atribuição esperada\n", lexical.getLineNumber());
                PanicLevel err = panic(new String[] {"sym_plus", "sym_minus", "ident", "number", "sym_leftParenthesis"}, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
            }
            PanicLevel err = expressao(new String[] {"sym_to"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
            symbol = lexical.currentSymbol();
            if (symbol != null && symbol.getId().equals("sym_to")) {
                lexical.nextSymbol();
            } else {
                System.out.printf("Linha %d: \"to\" esperado\n", lexical.getLineNumber());
                err = panic(new String[] {"sym_plus", "sym_minus", "ident", "number", "sym_leftParenthesis"}, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
            }
            err = expressao(new String[] {"sym_to"}, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
            symbol = lexical.currentSymbol();
            if (symbol != null && symbol.getId().equals("sym_do") && symbol.isValid()) {
                lexical.nextSymbol();
            } else {
                System.out.printf("Linha %d: \"do\" esperada\n", lexical.getLineNumber());
                err = panic(new String[] {"sym_read", "sym_write", "sym_while", "sym_if", "ident", "sym_begin", "sym_for"}, newFather);
                if(err == PanicLevel.Father) {
                    symbol = lexical.currentSymbol();
                    if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                        return PanicLevel.Father;
                    }
                    return PanicLevel.Local;
                }
            }
            err = cmd(null, newFather);
            if(err == PanicLevel.Father) {
                symbol = lexical.currentSymbol();
                if(symbol != null && checkIfSymbolIsInArray(symbol.getId(), father)){
                    return PanicLevel.Father;
                }
                return PanicLevel.Local;
            }
        }
        return PanicLevel.None;
    }
}
