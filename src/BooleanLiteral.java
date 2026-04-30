public class BooleanLiteral extends Expression {
    String value;

    public BooleanLiteral(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return TreePrinter.line("BooleanLiteral(" + value + ")");
    }
}