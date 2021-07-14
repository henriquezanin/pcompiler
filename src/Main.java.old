public class Main {
    public static void main(String[] args){
        // Verifica se a quantidade de argumentos de linha de comando está correta
        if(args.length != 1){
            System.out.println("You must pass an p-- language source code or use -h to list parameters");
            return;
        }
        // Executa o autômato que verifica a linha do argumento
        Automaton commandLineParser = new CmdLineAutomaton();
        Tape tape = new Tape(args[0]);
        Token token = commandLineParser.eval(tape);
        String input;
        // Se o automato processou a linha veja se o primeiro caractere é um '-' ou uma letra
        // Como argumento, o programa aceita um arquivo com extensão ".p" ou parâmetros de linha de comando
        if(token != null && token.isValid()){
            input = token.getValue();
            if(input.charAt(0) == '-'){
                switch (input){
                    // Menu de ajuda
                    case "-h":
                        showHelpMenu();
                        break;
                    default:
                        System.out.println("Invalid option, use -h to see options");
                        break;
                }
            }
            else {
                // Executa o compilador recebendo como parâmetro o nome do aquivo "*.p"
                Compiler p = new Compiler(input);
                p.compile();
            }
        }
        else{
            System.out.println("Invalid input, use -h to se options");
        }
    }

    private static void showHelpMenu(){
        System.out.println("NOT IMPLEMENTED");
        //TODO fazer um menu de help
    }
}
