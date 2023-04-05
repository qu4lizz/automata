package qu4lizz.automata.loader;

public class Token {
    private String type;
    private String value;

    public Token(String type) {
        this.type = type;
        value = null;
    }
    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }
    public String getValue() {
        return value;
    }
}
