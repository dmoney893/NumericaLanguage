public class IntegerLiteral extends Expression {
    String value;

    public IntegerLiteral(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "IntegerLiteral(" + value + ")\n";
    }
}