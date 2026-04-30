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
        if (match(TokenType.FN)) {
            return functionDeclaration();
        }

        if (match(TokenType.GIVE)) {
            return giveStatement();
        }

        if (match(TokenType.STOP)) {
            return stopStatement();
        }

        if (match(TokenType.LET, TokenType.VAR)) {
            return variableDeclaration(previous());
        }

        if (match(TokenType.PASTE)) {
            return pasteStatement();
        }

        if (match(TokenType.WHEN)) {
            return whenStatement();
        }

        if (match(TokenType.REPEAT)) {
            return repeatStatement();
        }

        if (match(TokenType.RANGE)) {
            return rangeStatement();
        }

        return assignmentStatement();
    }

    private Statement functionDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expected function name.");
        consume(TokenType.LPAREN, "Expected '(' after function name.");

        List<Identifier> parameters = new ArrayList<>();
        if (!check(TokenType.RPAREN)) {
            do {
                Token parameter = consume(TokenType.IDENTIFIER, "Expected parameter name.");
                parameters.add(new Identifier(parameter.lexeme));
            } while (match(TokenType.COMMA));
        }

        consume(TokenType.RPAREN, "Expected ')' after parameters.");
        BlockStatement body = block();

        return new FunctionDeclaration(new Identifier(name.lexeme), parameters, body);
    }

    private Statement giveStatement() {
        Expression expr = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after give statement.");
        return new GiveStatement(expr);
    }

    private Statement stopStatement() {
        consume(TokenType.SEMICOLON, "Expected ';' after stop statement.");
        return new StopStatement();
    }

    private Statement variableDeclaration(Token keyword) {
        Token name = consume(TokenType.IDENTIFIER, "Expected identifier after '" + keyword.lexeme + "'.");
        consume(TokenType.EQUAL, "Expected '=' after identifier.");
        Expression expr = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after variable declaration.");
        return new VariableDeclaration(keyword, new Identifier(name.lexeme), expr);
    }

    private Statement pasteStatement() {
        Expression expr = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after paste statement.");
        return new PasteStatement(expr);
    }

    private Statement rangeStatement() {
        Token name = consume(TokenType.IDENTIFIER, "Expected loop variable after 'range'.");
        consume(TokenType.FROM, "Expected 'from' after range variable.");
        Expression start = expression();
        consume(TokenType.TO, "Expected 'to' after range start.");
        Expression end = expression();
        consume(TokenType.BY, "Expected 'by' after range end.");
        Expression step = expression();
        BlockStatement body = block();

        return new RangeStatement(new Identifier(name.lexeme), start, end, step, body);
    }

    private Statement assignmentStatement() {
        Token name = consume(TokenType.IDENTIFIER, "Expected identifier.");

        if (match(TokenType.LBRACKET)) {
            Expression index = expression();
            consume(TokenType.RBRACKET, "Expected ']' after index.");
            consume(TokenType.EQUAL, "Expected '=' after index.");
            Expression value = expression();
            consume(TokenType.SEMICOLON, "Expected ';' after index assignment.");
            return new IndexAssignmentStatement(new Identifier(name.lexeme), index, value);
        }

        consume(TokenType.EQUAL, "Expected '=' after identifier.");
        Expression expr = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after assignment.");
        return new AssignmentStatement(new Identifier(name.lexeme), expr);
    }

    private Statement whenStatement() {
        Expression condition = expression();
        BlockStatement thenBlock = block();

        BlockStatement otherwiseBlock = null;
        if (match(TokenType.OTHERWISE)) {
            otherwiseBlock = block();
        }

        return new WhenStatement(condition, thenBlock, otherwiseBlock);
    }

    private Statement repeatStatement() {
        Expression condition = expression();
        BlockStatement body = block();
        return new RepeatStatement(condition, body);
    }

    private BlockStatement block() {
        consume(TokenType.LBRACE, "Expected '{' to start block.");
        List<Statement> statements = new ArrayList<>();

        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            statements.add(statement());
        }

        consume(TokenType.RBRACE, "Expected '}' after block.");
        return new BlockStatement(statements);
    }

    private Expression expression() {
        return or();
    }

    private Expression or() {
        Expression expr = and();

        while (match(TokenType.OR)) {
            Token operator = previous();
            Expression right = and();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression and() {
        Expression expr = equality();

        while (match(TokenType.AND)) {
            Token operator = previous();
            Expression right = equality();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression equality() {
        Expression expr = comparison();

        while (match(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)) {
            Token operator = previous();
            Expression right = comparison();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression comparison() {
        Expression expr = term();

        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL,
                TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token operator = previous();
            Expression right = term();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression term() {
        Expression expr = factor();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Expression right = factor();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression factor() {
        Expression expr = unary();

        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = previous();
            Expression right = unary();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression unary() {
        if (match(TokenType.NOT, TokenType.MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return new UnaryExpression(operator, right);
        }

        return call();
    }

    private Expression call() {
        Expression expr = primary();

        while (true) {
            if (match(TokenType.LPAREN)) {
                if (!(expr instanceof Identifier)) {
                    throw new RuntimeException("Can only call named functions.");
                }

                List<Expression> arguments = new ArrayList<>();
                if (!check(TokenType.RPAREN)) {
                    do {
                        arguments.add(expression());
                    } while (match(TokenType.COMMA));
                }

                consume(TokenType.RPAREN, "Expected ')' after arguments.");
                expr = new CallExpression((Identifier) expr, arguments);
            } else if (match(TokenType.LBRACKET)) {
                Expression index = expression();
                consume(TokenType.RBRACKET, "Expected ']' after index.");
                expr = new IndexExpression(expr, index);
            } else {
                break;
            }
        }

        return expr;
    }

    private Expression primary() {
        if (match(TokenType.INTEGER)) {
            return new IntegerLiteral(previous().lexeme);
        }

        if (match(TokenType.FLOAT)) {
            return new FloatLiteral(previous().lexeme);
        }

        if (match(TokenType.TRUE)) {
            return new BooleanLiteral("true");
        }

        if (match(TokenType.FALSE)) {
            return new BooleanLiteral("false");
        }

        if (match(TokenType.NONE)) {
            return new NoneLiteral();
        }

        if (match(TokenType.IDENTIFIER)) {
            return new Identifier(previous().lexeme);
        }

        if (match(TokenType.LBRACKET)) {
            List<Expression> elements = new ArrayList<>();

            if (!check(TokenType.RBRACKET)) {
                do {
                    elements.add(expression());
                } while (match(TokenType.COMMA));
            }

            consume(TokenType.RBRACKET, "Expected ']' after list literal.");
            return new ListLiteral(elements);
        }

        if (match(TokenType.LPAREN)) {
            Expression expr = expression();
            consume(TokenType.RPAREN, "Expected ')' after expression.");
            return expr;
        }

        throw new RuntimeException("Error on line " + peek().line + ": Expected expression.");
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

        Token token = peek();
        throw new RuntimeException("Error on line " + token.line + ": " + message);
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