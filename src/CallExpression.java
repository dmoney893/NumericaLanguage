import java.util.List;

public class CallExpression extends Expression {
    Identifier callee;
    List<Expression> arguments;

    public CallExpression(Identifier callee, List<Expression> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(TreePrinter.line("CallExpression"));
        sb.append(TreePrinter.indent(callee.toString(), 4));
        for (Expression argument : arguments) {
            sb.append(TreePrinter.indent(argument.toString(), 4));
        }
        return sb.toString();
    }
}