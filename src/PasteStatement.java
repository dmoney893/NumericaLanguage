public class PasteStatement extends Statement {
    Expression expression;

    public PasteStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return TreePrinter.line("PasteStatement") +
                TreePrinter.indent(expression.toString(), 4);
    }
}