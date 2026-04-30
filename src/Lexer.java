import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;
    private int position = 0;
    private int line = 1;

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (position < source.length()) {
            char c = source.charAt(position);

            if (c == '\n') {
                line++;
                position++;
                continue;
            }

            if (Character.isWhitespace(c)) {
                position++;
                continue;
            }

            if (c == '#') {
                skipComment();
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
                    tokens.add(new Token(TokenType.PLUS, "+", line));
                    position++;
                    break;
                case '-':
                    tokens.add(new Token(TokenType.MINUS, "-", line));
                    position++;
                    break;
                case '*':
                    tokens.add(new Token(TokenType.STAR, "*", line));
                    position++;
                    break;
                case '/':
                    tokens.add(new Token(TokenType.SLASH, "/", line));
                    position++;
                    break;
                case ';':
                    tokens.add(new Token(TokenType.SEMICOLON, ";", line));
                    position++;
                    break;
                case ',':
                    tokens.add(new Token(TokenType.COMMA, ",", line));
                    position++;
                    break;
                case '(':
                    tokens.add(new Token(TokenType.LPAREN, "(", line));
                    position++;
                    break;
                case ')':
                    tokens.add(new Token(TokenType.RPAREN, ")", line));
                    position++;
                    break;
                case '{':
                    tokens.add(new Token(TokenType.LBRACE, "{", line));
                    position++;
                    break;
                case '}':
                    tokens.add(new Token(TokenType.RBRACE, "}", line));
                    position++;
                    break;
                case '[':
                    tokens.add(new Token(TokenType.LBRACKET, "[", line));
                    position++;
                    break;
                case ']':
                    tokens.add(new Token(TokenType.RBRACKET, "]", line));
                    position++;
                    break;
                case '=':
                    if (matchNext('=')) {
                        tokens.add(new Token(TokenType.EQUAL_EQUAL, "==", line));
                        position += 2;
                    } else {
                        tokens.add(new Token(TokenType.EQUAL, "=", line));
                        position++;
                    }
                    break;
                case '!':
                    if (matchNext('=')) {
                        tokens.add(new Token(TokenType.BANG_EQUAL, "!=", line));
                        position += 2;
                    } else {
                        throw new RuntimeException("Error on line " + line + ": Unknown character: " + c);
                    }
                    break;
                case '>':
                    if (matchNext('=')) {
                        tokens.add(new Token(TokenType.GREATER_EQUAL, ">=", line));
                        position += 2;
                    } else {
                        tokens.add(new Token(TokenType.GREATER, ">", line));
                        position++;
                    }
                    break;
                case '<':
                    if (matchNext('=')) {
                        tokens.add(new Token(TokenType.LESS_EQUAL, "<=", line));
                        position += 2;
                    } else {
                        tokens.add(new Token(TokenType.LESS, "<", line));
                        position++;
                    }
                    break;
                default:
                    throw new RuntimeException("Error on line " + line + ": Unknown character: " + c);
            }
        }

        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }

    private void skipComment() {
        while (position < source.length() && source.charAt(position) != '\n') {
            position++;
        }
    }

    private boolean matchNext(char expected) {
        return position + 1 < source.length() && source.charAt(position + 1) == expected;
    }

    private Token readNumber() {
        int start = position;
        boolean hasDot = false;

        while (position < source.length()) {
            char c = source.charAt(position);

            if (Character.isDigit(c)) {
                position++;
            } else if (c == '.' && !hasDot) {
                hasDot = true;
                position++;
            } else {
                break;
            }
        }

        String number = source.substring(start, position);

        if (hasDot) {
            return new Token(TokenType.FLOAT, number, line);
        } else {
            return new Token(TokenType.INTEGER, number, line);
        }
    }

    private Token readIdentifier() {
        int start = position;

        while (position < source.length() &&
                Character.isLetterOrDigit(source.charAt(position))) {
            position++;
        }

        String text = source.substring(start, position);

        switch (text) {
            case "paste":
                return new Token(TokenType.PASTE, text, line);
            case "let":
                return new Token(TokenType.LET, text, line);
            case "var":
                return new Token(TokenType.VAR, text, line);
            case "when":
                return new Token(TokenType.WHEN, text, line);
            case "otherwise":
                return new Token(TokenType.OTHERWISE, text, line);
            case "repeat":
                return new Token(TokenType.REPEAT, text, line);
            case "range":
                return new Token(TokenType.RANGE, text, line);
            case "from":
                return new Token(TokenType.FROM, text, line);
            case "to":
                return new Token(TokenType.TO, text, line);
            case "by":
                return new Token(TokenType.BY, text, line);
            case "stop":
                return new Token(TokenType.STOP, text, line);
            case "true":
                return new Token(TokenType.TRUE, text, line);
            case "false":
                return new Token(TokenType.FALSE, text, line);
            case "and":
                return new Token(TokenType.AND, text, line);
            case "or":
                return new Token(TokenType.OR, text, line);
            case "not":
                return new Token(TokenType.NOT, text, line);
            case "fn":
                return new Token(TokenType.FN, text, line);
            case "give":
                return new Token(TokenType.GIVE, text, line);
            case "none":
                return new Token(TokenType.NONE, text, line);
            default:
                return new Token(TokenType.IDENTIFIER, text, line);
        }
    }
}