public class AssignmentStatement extends Statement {
    Identifier identifier;
    Expression expression;

    public AssignmentStatement(Identifier identifier, Expression expression) {
        this.identifier = identifier;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "AssignmentStatement\n" +
                identifier.toString().indent(4) +
                expression.toString().indent(4);
    }
}