import java.util.List;

public class BlockStatement extends Statement {
    List<Statement> statements;

    public BlockStatement(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(TreePrinter.line("BlockStatement"));
        for (Statement statement : statements) {
            sb.append(TreePrinter.indent(statement.toString(), 4));
        }
        return sb.toString();
    }
}