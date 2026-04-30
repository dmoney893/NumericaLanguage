import java.util.List;

public class FunctionDeclaration extends Statement {
    Identifier name;
    List<Identifier> parameters;
    BlockStatement body;

    public FunctionDeclaration(Identifier name, List<Identifier> parameters, BlockStatement body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(TreePrinter.line("FunctionDeclaration"));
        sb.append(TreePrinter.indent(name.toString(), 4));
        sb.append(TreePrinter.indent(TreePrinter.line("Parameters"), 4));
        for (Identifier parameter : parameters) {
            sb.append(TreePrinter.indent(parameter.toString(), 8));
        }
        sb.append(TreePrinter.indent(body.toString(), 4));
        return sb.toString();
    }
}