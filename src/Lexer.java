import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;
    private int position = 0;

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (position < source.length()) {
            char c = source.charAt(position);

            if (Character.isWhitespace(c)) {
                position++;
                continue;
            }

            if (Character.isDigit(c)) {
                tokens.add(readNumber());
                continue;
            }

            if (Character.isLetter(c)) {
                tokens.add(readIdentifier());
                continue;
            }

            switch (c) {
                case '+':
                    tokens.add(new Token(TokenType.PLUS, "+"));
                    position++;
                    break;
                case '-':
                    tokens.add(new Token(TokenType.MINUS, "-"));
                    position++;
                    break;
                case '*':
                    tokens.add(new Token(TokenType.STAR, "*"));
                    position++;
                    break;
                case '/':
                    tokens.add(new Token(TokenType.SLASH, "/"));
                    position++;
                    break;
                case '=':
                    tokens.add(new Token(TokenType.EQUAL, "="));
                    position++;
                    break;
                case ';':
                    tokens.add(new Token(TokenType.SEMICOLON, ";"));
                    position++;
                    break;
                case '(':
                    tokens.add(new Token(TokenType.LPAREN, "("));
                    position++;
                    break;
                case ')':
                    tokens.add(new Token(TokenType.RPAREN, ")"));
                    position++;
                    break;
                default:
                    throw new RuntimeException("Unknown character: " + c);
            }
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    private Token readNumber() {
        int start = position;

        while (position < source.length() &&
                Character.isDigit(source.charAt(position))) {
            position++;
        }

        String number = source.substring(start, position);
        return new Token(TokenType.INTEGER, number);
    }

    private Token readIdentifier() {
        int start = position;

        while (position < source.length() &&
                Character.isLetterOrDigit(source.charAt(position))) {
            position++;
        }

        String text = source.substring(start, position);

        if (text.equals("print")) {
            return new Token(TokenType.PRINT, text);
        }

        return new Token(TokenType.IDENTIFIER, text);
    }
}