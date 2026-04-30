public class UnaryExpression extends Expression {
    Token operator;
    Expression right;

    public UnaryExpression(Token operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return TreePrinter.line("UnaryExpression(" + operator.lexeme + ")") +
                TreePrinter.indent(right.toString(), 4);
    }
}