import java.util.List;

public class UserFunction {
    List<Identifier> parameters;
    BlockStatement body;

    public UserFunction(List<Identifier> parameters, BlockStatement body) {
        this.parameters = parameters;
        this.body = body;
    }
}