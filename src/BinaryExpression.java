public class BinaryExpression extends Expression {
    Expression left;
    Token operator;
    Expression right;

    public BinaryExpression(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return TreePrinter.line("BinaryExpression(" + operator.lexeme + ")") +
                TreePrinter.indent(left.toString(), 4) +
                TreePrinter.indent(right.toString(), 4);
    }
}