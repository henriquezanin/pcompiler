public class Tape {

    private String line;
    private int head;
    private int lenght;

    public Tape(){
        this.line = null;
        this.head = 0;
        this.lenght = 0;
    }

    public Tape(String line){
        this.replace(line);
    }

    public void replace(String line){
        this.line = line;
        this.head = 0;
        this.lenght = line.length();
    }

    public boolean hasNext(){
        return this.head != this.lenght;
    }

    public char next(){
        if(!this.hasNext()){
            return Character.MIN_VALUE;
        }
        char ch = line.charAt(head);
        this.head++;
        return ch;
    }

    public void rollback(){
        if(this.head > 0) {
            this.head--;
        }
    }

    public char getHead(){
        return line.charAt(head);
    }

    public String getLine(){
        return this.line;
    }
}
