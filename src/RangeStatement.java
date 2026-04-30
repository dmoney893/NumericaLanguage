public class RangeStatement extends Statement {
    Identifier identifier;
    Expression start;
    Expression end;
    Expression step;
    BlockStatement body;

    public RangeStatement(Identifier identifier, Expression start, Expression end, Expression step, BlockStatement body) {
        this.identifier = identifier;
        this.start = start;
        this.end = end;
        this.step = step;
        this.body = body;
    }

    @Override
    public String toString() {
        return TreePrinter.line("RangeStatement") +
                TreePrinter.indent(identifier.toString(), 4) +
                TreePrinter.indent(start.toString(), 4) +
                TreePrinter.indent(end.toString(), 4) +
                TreePrinter.indent(step.toString(), 4) +
                TreePrinter.indent(body.toString(), 4);
    }
}