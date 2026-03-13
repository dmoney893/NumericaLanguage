public class PrintStatement extends Statement {
    Expression expression;

    public PrintStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "PrintStatement\n" +
                expression.toString().indent(4);
    }
}