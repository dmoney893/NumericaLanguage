public class FloatLiteral extends Expression {
    String value;

    public FloatLiteral(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return TreePrinter.line("FloatLiteral(" + value + ")");
    }
}