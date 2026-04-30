public class GiveStatement extends Statement {
    Expression expression;

    public GiveStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return TreePrinter.line("GiveStatement") +
                TreePrinter.indent(expression.toString(), 4);
    }
}