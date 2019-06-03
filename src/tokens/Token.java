package tokens;

public class Token {

    private TokenType type;
    private String text;

    public Token(TokenType type, String text) {
        this.text = text;
        this.type = type;
    }

    public TokenType getType() {
        return type;
    }

    public String toString(){
        return text;
    }

}
