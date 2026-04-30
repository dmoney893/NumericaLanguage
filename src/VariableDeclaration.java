public class VariableDeclaration extends Statement {
    Token keyword;
    Identifier identifier;
    Expression expression;

    public VariableDeclaration(Token keyword, Identifier identifier, Expression expression) {
        this.keyword = keyword;
        this.identifier = identifier;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return TreePrinter.line("VariableDeclaration(" + keyword.lexeme + ")") +
                TreePrinter.indent(identifier.toString(), 4) +
                TreePrinter.indent(expression.toString(), 4);
    }
}