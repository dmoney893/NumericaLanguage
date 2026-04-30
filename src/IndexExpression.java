public class IndexExpression extends Expression {
    Expression target;
    Expression index;

    public IndexExpression(Expression target, Expression index) {
        this.target = target;
        this.index = index;
    }

    @Override
    public String toString() {
        return TreePrinter.line("IndexExpression") +
                TreePrinter.indent(target.toString(), 4) +
                TreePrinter.indent(index.toString(), 4);
    }
}