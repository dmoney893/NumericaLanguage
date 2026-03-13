import java.util.List;

public class Program extends ASTNode {
    List<Statement> statements;

    public Program(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Program\n");
        for (Statement stmt : statements) {
            sb.append(stmt.toString().indent(2));
        }
        return sb.toString();
    }
}