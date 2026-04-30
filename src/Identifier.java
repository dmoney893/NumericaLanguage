public class Identifier extends Expression {
    String name;

    public Identifier(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return TreePrinter.line("Identifier(" + name + ")");
    }
}