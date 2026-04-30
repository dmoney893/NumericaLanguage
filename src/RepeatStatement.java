public class RepeatStatement extends Statement {
    Expression condition;
    BlockStatement body;

    public RepeatStatement(Expression condition, BlockStatement body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toString() {
        return TreePrinter.line("RepeatStatement") +
                TreePrinter.indent(condition.toString(), 4) +
                TreePrinter.indent(body.toString(), 4);
    }
}