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
        return "BinaryExpression(" + operator.lexeme + ")\n" +
                left.toString().indent(4) +
                right.toString().indent(4);
    }
}