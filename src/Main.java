public class Main {
    public static void main(String[] args){
        if(args.length != 1){
            System.out.println("You must pass an p-- language source code or use -h to list parameters");
            return;
        }
        Automaton commandLineParser = new CmdLineAutomaton();
        Tape tape = new Tape(args[0]);
        Token token = commandLineParser.eval(tape);
        String input;
        if(token != null && token.isValid()){
            input = token.getValue();
            if(input.charAt(0) == '-'){
                switch (input){
                    case "-h":
                        showHelpMenu();
                        break;
                    default:
                        System.out.println("Invalid option, use -h to see options");
                        break;
                }
            }
            else {
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
