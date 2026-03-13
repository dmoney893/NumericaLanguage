import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Program parse() {
        List<Statement> statements = new ArrayList<>();

        while (!isAtEnd()) {
            statements.add(statement());
        }

        return new Program(statements);
    }

    private Statement statement() {
        if (match(TokenType.PRINT)) {
            return printStatement();
        }
        return assignmentStatement();
    }

    private Statement printStatement() {
        Expression expr = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after print statement.");
        return new PrintStatement(expr);
    }

    private Statement assignmentStatement() {
        Token name = consume(TokenType.IDENTIFIER, "Expected identifier.");
        consume(TokenType.EQUAL, "Expected '=' after identifier.");
        Expression expr = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after assignment.");
        return new AssignmentStatement(new Identifier(name.lexeme), expr);
    }

    private Expression expression() {
        Expression expr = term();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Expression right = term();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression term() {
        Expression expr = factor();

        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = previous();
            Expression right = factor();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression factor() {
        if (match(TokenType.INTEGER)) {
            return new IntegerLiteral(previous().lexeme);
        }

        if (match(TokenType.IDENTIFIER)) {
            return new Identifier(previous().lexeme);
        }

        if (match(TokenType.LPAREN)) {
            Expression expr = expression();
            consume(TokenType.RPAREN, "Expected ')' after expression.");
            return expr;
        }

        throw new RuntimeException("Expected expression.");
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw new RuntimeException(message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}