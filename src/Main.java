public class Main {
    public static void main(String[] args){
        // Verifica se a quantidade de argumentos de linha de comando está correta
        if(args.length != 1){
            System.out.println("You must pass an p-- language source code");
            return;
        }
        Compiler p = new Compiler(args[0]);
        p.compile();
    }
}
