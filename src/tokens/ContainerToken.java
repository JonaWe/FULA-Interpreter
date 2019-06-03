package tokens;


public class ContainerToken<ContentType> extends Token {

    ContentType content;

    public ContainerToken(TokenType type, String text, ContentType content) {
        super(type, text);
        this.content = content;

    }

    public ContentType getContent() {
        return content;
    }
}
