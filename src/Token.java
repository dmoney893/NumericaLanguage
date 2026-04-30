public class Token {
    TokenType type;
    String lexeme;
    int line;

    public Token(TokenType type, String lexeme, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
    }

    @Override
    public String toString() {
        if (lexeme == null || lexeme.isEmpty()) {
            return type + " [line " + line + "]";
        }
        return type + "(" + lexeme + ")" + " [line " + line + "]";
    }
}