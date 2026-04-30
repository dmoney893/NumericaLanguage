import java.util.List;

public class ListLiteral extends Expression {
    List<Expression> elements;

    public ListLiteral(List<Expression> elements) {
        this.elements = elements;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(TreePrinter.line("ListLiteral"));
        for (Expression element : elements) {
            sb.append(TreePrinter.indent(element.toString(), 4));
        }
        return sb.toString();
    }
}