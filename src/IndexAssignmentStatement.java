public class IndexAssignmentStatement extends Statement {
    Identifier identifier;
    Expression index;
    Expression value;

    public IndexAssignmentStatement(Identifier identifier, Expression index, Expression value) {
        this.identifier = identifier;
        this.index = index;
        this.value = value;
    }

    @Override
    public String toString() {
        return TreePrinter.line("IndexAssignmentStatement") +
                TreePrinter.indent(identifier.toString(), 4) +
                TreePrinter.indent(index.toString(), 4) +
                TreePrinter.indent(value.toString(), 4);
    }
}