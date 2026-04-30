public class WhenStatement extends Statement {
    Expression condition;
    BlockStatement thenBlock;
    BlockStatement otherwiseBlock;

    public WhenStatement(Expression condition, BlockStatement thenBlock, BlockStatement otherwiseBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.otherwiseBlock = otherwiseBlock;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(TreePrinter.line("WhenStatement"));
        sb.append(TreePrinter.indent(condition.toString(), 4));
        sb.append(TreePrinter.indent(thenBlock.toString(), 4));
        if (otherwiseBlock != null) {
            sb.append(TreePrinter.indent(otherwiseBlock.toString(), 4));
        }
        return sb.toString();
    }
}