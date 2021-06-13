public class Token {

    private String value;
    private boolean isValid;
    private String id;

    public Token(){
        this.isValid = false;
    }

    public Token(String string){
        this.isValid = false;
        value = string;
    }

    public String getValue() {
        return value;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setValid() {
        isValid = true;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
