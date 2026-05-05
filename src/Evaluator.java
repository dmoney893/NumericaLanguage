import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evaluator {
    private Environment environment = new Environment();
    private final Map<String, UserFunction> functions = new HashMap<>();

    public void execute(Program program) {
        try {
            for (Statement statement : program.statements) {
                executeStatement(statement);
            }
        } catch (StopSignal signal) {
            throw new RuntimeException("SyntaxError: 'stop' can only be used inside a loop");
        }
    }

    private void executeStatement(Statement statement) {
        if (statement instanceof FunctionDeclaration) {
            executeFunctionDeclaration((FunctionDeclaration) statement);
        } else if (statement instanceof GiveStatement) {
            executeGive((GiveStatement) statement);
        } else if (statement instanceof StopStatement) {
            executeStop((StopStatement) statement);
        } else if (statement instanceof VariableDeclaration) {
            executeVariableDeclaration((VariableDeclaration) statement);
        } else if (statement instanceof AssignmentStatement) {
            executeAssignment((AssignmentStatement) statement);
        } else if (statement instanceof IndexAssignmentStatement) {
            executeIndexAssignment((IndexAssignmentStatement) statement);
        } else if (statement instanceof PasteStatement) {
            executePaste((PasteStatement) statement);
        } else if (statement instanceof WhenStatement) {
            executeWhen((WhenStatement) statement);
        } else if (statement instanceof RepeatStatement) {
            executeRepeat((RepeatStatement) statement);
        } else if (statement instanceof RangeStatement) {
            executeRange((RangeStatement) statement);
        } else if (statement instanceof BlockStatement) {
            executeBlock((BlockStatement) statement);
        } else {
            throw new RuntimeException("Unknown statement type");
        }
    }

    private void executeFunctionDeclaration(FunctionDeclaration statement) {
        functions.put(statement.name.name, new UserFunction(statement.parameters, statement.body));
    }

    private void executeGive(GiveStatement statement) {
        Object value = evaluateExpression(statement.expression);
        throw new ReturnSignal(value);
    }

    private void executeStop(StopStatement statement) {
        throw new StopSignal();
    }

    private void executeVariableDeclaration(VariableDeclaration statement) {
        Object value = evaluateExpression(statement.expression);
        String category = valueCategory(value);
        environment.declare(statement.identifier.name, value, statement.keyword.type == TokenType.VAR, category);
    }

    private void executeAssignment(AssignmentStatement statement) {
        Object value = evaluateExpression(statement.expression);
        String category = valueCategory(value);
        try {
            environment.assign(statement.identifier.name, value, category);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not defined")) {
                environment.declare(statement.identifier.name, value, true, category);
            } else {
                throw e;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void executeIndexAssignment(IndexAssignmentStatement statement) {
        Object target = environment.get(statement.identifier.name);

        if (!(target instanceof List)) {
            throw new RuntimeException("TypeError: indexed assignment requires a list");
        }

        Object indexValue = evaluateExpression(statement.index);
        if (!(indexValue instanceof Integer)) {
            throw new RuntimeException("TypeError: list index must be an integer");
        }

        int index = (Integer) indexValue;
        List<Object> list = (List<Object>) target;

        if (index < 0 || index >= list.size()) {
            throw new RuntimeException("IndexError: index out of bounds");
        }

        Object value = evaluateExpression(statement.value);

        if (!isNumeric(value)) {
            throw new RuntimeException("TypeError: list elements must be numeric");
        }

        list.set(index, value);
    }

    private void executePaste(PasteStatement statement) {
        Object value = evaluateExpression(statement.expression);
        GUIOutput.print(formatValue(value));
    }

    private void executeWhen(WhenStatement statement) {
        Object conditionValue = evaluateExpression(statement.condition);

        if (!(conditionValue instanceof Boolean)) {
            throw new RuntimeException("TypeError: when condition must be boolean");
        }

        if ((Boolean) conditionValue) {
            executeBlock(statement.thenBlock);
        } else if (statement.otherwiseBlock != null) {
            executeBlock(statement.otherwiseBlock);
        }
    }

    private void executeRepeat(RepeatStatement statement) {
        while (true) {
            Object conditionValue = evaluateExpression(statement.condition);

            if (!(conditionValue instanceof Boolean)) {
                throw new RuntimeException("TypeError: repeat condition must be boolean");
            }

            if (!((Boolean) conditionValue)) {
                break;
            }

            try {
                executeBlock(statement.body);
            } catch (StopSignal signal) {
                break;
            }
        }
    }

    private void executeRange(RangeStatement statement) {
        Object startValue = evaluateExpression(statement.start);
        Object endValue = evaluateExpression(statement.end);
        Object stepValue = evaluateExpression(statement.step);

        if (!isNumeric(startValue) || !isNumeric(endValue) || !isNumeric(stepValue)) {
            throw new RuntimeException("TypeError: range requires numeric start, end, and step values");
        }

        double start = toDouble(startValue, "range");
        double end = toDouble(endValue, "range");
        double step = toDouble(stepValue, "range");

        if (step == 0.0) {
            throw new RuntimeException("ArithmeticError: range step cannot be zero");
        }

        Environment previous = environment;
        environment = new Environment(previous);

        try {
            boolean useIntegers =
                    startValue instanceof Integer &&
                            endValue instanceof Integer &&
                            stepValue instanceof Integer;

            if (useIntegers) {
                int current = (Integer) startValue;
                int finalValue = (Integer) endValue;
                int increment = (Integer) stepValue;

                environment.declare(statement.identifier.name, current, true, "number");

                if (increment > 0) {
                    while (current <= finalValue) {
                        environment.assign(statement.identifier.name, current, "number");
                        try {
                            executeBlockInCurrentScope(statement.body);
                        } catch (StopSignal signal) {
                            break;
                        }
                        current += increment;
                    }
                } else {
                    while (current >= finalValue) {
                        environment.assign(statement.identifier.name, current, "number");
                        try {
                            executeBlockInCurrentScope(statement.body);
                        } catch (StopSignal signal) {
                            break;
                        }
                        current += increment;
                    }
                }
            } else {
                double current = start;
                environment.declare(statement.identifier.name, current, true, "number");

                if (step > 0) {
                    while (current <= end) {
                        environment.assign(statement.identifier.name, current, "number");
                        try {
                            executeBlockInCurrentScope(statement.body);
                        } catch (StopSignal signal) {
                            break;
                        }
                        current += step;
                    }
                } else {
                    while (current >= end) {
                        environment.assign(statement.identifier.name, current, "number");
                        try {
                            executeBlockInCurrentScope(statement.body);
                        } catch (StopSignal signal) {
                            break;
                        }
                        current += step;
                    }
                }
            }
        } finally {
            environment = previous;
        }
    }

    private void executeBlock(BlockStatement block) {
        Environment previous = environment;
        environment = new Environment(previous);

        try {
            for (Statement statement : block.statements) {
                executeStatement(statement);
            }
        } finally {
            environment = previous;
        }
    }

    private void executeBlockInCurrentScope(BlockStatement block) {
        for (Statement statement : block.statements) {
            executeStatement(statement);
        }
    }

    private Object evaluateExpression(Expression expression) {
        if (expression instanceof IntegerLiteral) {
            return Integer.parseInt(((IntegerLiteral) expression).value);
        } else if (expression instanceof FloatLiteral) {
            return Double.parseDouble(((FloatLiteral) expression).value);
        } else if (expression instanceof BooleanLiteral) {
            return Boolean.parseBoolean(((BooleanLiteral) expression).value);
        } else if (expression instanceof NoneLiteral) {
            return null;
        } else if (expression instanceof Identifier) {
            return environment.get(((Identifier) expression).name);
        } else if (expression instanceof UnaryExpression) {
            return evaluateUnaryExpression((UnaryExpression) expression);
        } else if (expression instanceof BinaryExpression) {
            return evaluateBinaryExpression((BinaryExpression) expression);
        } else if (expression instanceof CallExpression) {
            return evaluateCallExpression((CallExpression) expression);
        } else if (expression instanceof ListLiteral) {
            return evaluateListLiteral((ListLiteral) expression);
        } else if (expression instanceof IndexExpression) {
            return evaluateIndexExpression((IndexExpression) expression);
        } else {
            throw new RuntimeException("Unknown expression type");
        }
    }

    private Object evaluateUnaryExpression(UnaryExpression expression) {
        Object right = evaluateExpression(expression.right);

        switch (expression.operator.type) {
            case NOT:
                if (!(right instanceof Boolean)) {
                    throw new RuntimeException("TypeError: operator 'not' requires a boolean");
                }
                return !((Boolean) right);

            case MINUS:
                if (right instanceof Integer) {
                    return -((Integer) right);
                }
                if (right instanceof Double) {
                    return -((Double) right);
                }
                throw new RuntimeException("TypeError: unary '-' requires a numeric value");

            default:
                throw new RuntimeException("Unknown unary operator: " + expression.operator.lexeme);
        }
    }

    private Object evaluateListLiteral(ListLiteral expression) {
        List<Object> values = new ArrayList<>();
        for (Expression element : expression.elements) {
            Object value = evaluateExpression(element);
            if (!isNumeric(value)) {
                throw new RuntimeException("TypeError: list elements must be numeric");
            }
            values.add(value);
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    private Object evaluateIndexExpression(IndexExpression expression) {
        Object target = evaluateExpression(expression.target);
        Object indexValue = evaluateExpression(expression.index);

        if (!(target instanceof List)) {
            throw new RuntimeException("TypeError: indexing requires a list");
        }

        if (!(indexValue instanceof Integer)) {
            throw new RuntimeException("TypeError: list index must be an integer");
        }

        List<Object> list = (List<Object>) target;
        int index = (Integer) indexValue;

        if (index < 0 || index >= list.size()) {
            throw new RuntimeException("IndexError: index out of bounds");
        }

        return list.get(index);
    }

    private Object evaluateCallExpression(CallExpression expression) {
        String name = expression.callee.name;

        if (isBuiltInFunction(name)) {
            return evaluateBuiltInFunction(name, expression.arguments);
        }

        if (!functions.containsKey(name)) {
            throw new RuntimeException("NameError: function '" + name + "' not defined");
        }

        UserFunction function = functions.get(name);

        if (expression.arguments.size() != function.parameters.size()) {
            throw new RuntimeException(
                    "ArgumentError: function '" + name + "' expected " +
                            function.parameters.size() + " arguments but got " +
                            expression.arguments.size()
            );
        }

        List<Object> argumentValues = new ArrayList<>();
        for (Expression argument : expression.arguments) {
            argumentValues.add(evaluateExpression(argument));
        }

        Environment previous = environment;
        environment = new Environment(previous);

        try {
            for (int i = 0; i < function.parameters.size(); i++) {
                environment.declare(
                        function.parameters.get(i).name,
                        argumentValues.get(i),
                        true,
                        valueCategory(argumentValues.get(i))
                );
            }

            try {
                for (Statement statement : function.body.statements) {
                    executeStatement(statement);
                }
            } catch (ReturnSignal signal) {
                return signal.value;
            }

            return null;
        } finally {
            environment = previous;
        }
    }

    private boolean isBuiltInFunction(String name) {
        return name.equals("abs") ||
                name.equals("sqrt") ||
                name.equals("power") ||
                name.equals("max") ||
                name.equals("min");
    }

    private Object evaluateBuiltInFunction(String name, List<Expression> arguments) {
        List<Object> values = new ArrayList<>();
        for (Expression argument : arguments) {
            values.add(evaluateExpression(argument));
        }

        switch (name) {
            case "abs":
                requireArgumentCount(name, values, 1);
                return abs(values.get(0));
            case "sqrt":
                requireArgumentCount(name, values, 1);
                return sqrt(values.get(0));
            case "power":
                requireArgumentCount(name, values, 2);
                return power(values.get(0), values.get(1));
            case "max":
                requireArgumentCount(name, values, 2);
                return max(values.get(0), values.get(1));
            case "min":
                requireArgumentCount(name, values, 2);
                return min(values.get(0), values.get(1));
            default:
                throw new RuntimeException("NameError: built-in function '" + name + "' not defined");
        }
    }

    private void requireArgumentCount(String name, List<Object> values, int expected) {
        if (values.size() != expected) {
            throw new RuntimeException(
                    "ArgumentError: function '" + name + "' expected " +
                            expected + " arguments but got " + values.size()
            );
        }
    }

    private Object abs(Object value) {
        if (value instanceof Integer) return Math.abs((Integer) value);
        if (value instanceof Double) return Math.abs((Double) value);
        throw new RuntimeException("TypeError: function 'abs' requires a numeric value");
    }

    private Object sqrt(Object value) {
        if (!isNumeric(value)) {
            throw new RuntimeException("TypeError: function 'sqrt' requires a numeric value");
        }
        double number = toDouble(value, "sqrt");
        if (number < 0) {
            throw new RuntimeException("ArithmeticError: sqrt requires a non-negative value");
        }
        return Math.sqrt(number);
    }

    private Object power(Object left, Object right) {
        if (!isNumeric(left) || !isNumeric(right)) {
            throw new RuntimeException("TypeError: function 'power' requires numeric values");
        }
        return Math.pow(toDouble(left, "power"), toDouble(right, "power"));
    }

    private Object max(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return Math.max((Integer) left, (Integer) right);
        }
        if (!isNumeric(left) || !isNumeric(right)) {
            throw new RuntimeException("TypeError: function 'max' requires numeric values");
        }
        return Math.max(toDouble(left, "max"), toDouble(right, "max"));
    }

    private Object min(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return Math.min((Integer) left, (Integer) right);
        }
        if (!isNumeric(left) || !isNumeric(right)) {
            throw new RuntimeException("TypeError: function 'min' requires numeric values");
        }
        return Math.min(toDouble(left, "min"), toDouble(right, "min"));
    }

    private Object evaluateBinaryExpression(BinaryExpression expression) {
        Object left = evaluateExpression(expression.left);
        Object right = evaluateExpression(expression.right);

        switch (expression.operator.type) {
            case PLUS:
                return add(left, right);
            case MINUS:
                return subtract(left, right);
            case STAR:
                return multiply(left, right);
            case SLASH:
                return divide(left, right);

            case GREATER:
                return compare(left, right) > 0;
            case GREATER_EQUAL:
                return compare(left, right) >= 0;
            case LESS:
                return compare(left, right) < 0;
            case LESS_EQUAL:
                return compare(left, right) <= 0;

            case EQUAL_EQUAL:
                return isEqual(left, right);
            case BANG_EQUAL:
                return !isEqual(left, right);

            case AND:
                if (!(left instanceof Boolean) || !(right instanceof Boolean)) {
                    throw new RuntimeException("TypeError: operator 'and' requires booleans");
                }
                return (Boolean) left && (Boolean) right;

            case OR:
                if (!(left instanceof Boolean) || !(right instanceof Boolean)) {
                    throw new RuntimeException("TypeError: operator 'or' requires booleans");
                }
                return (Boolean) left || (Boolean) right;

            default:
                throw new RuntimeException("Unknown operator: " + expression.operator.lexeme);
        }
    }

    private Object add(Object left, Object right) {
        if (!isNumeric(left) || !isNumeric(right)) {
            throw new RuntimeException("TypeError: operator '+' requires numeric values");
        }
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left + (Integer) right;
        }
        return toDouble(left, "+") + toDouble(right, "+");
    }

    private Object subtract(Object left, Object right) {
        if (!isNumeric(left) || !isNumeric(right)) {
            throw new RuntimeException("TypeError: operator '-' requires numeric values");
        }
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left - (Integer) right;
        }
        return toDouble(left, "-") - toDouble(right, "-");
    }

    private Object multiply(Object left, Object right) {
        if (!isNumeric(left) || !isNumeric(right)) {
            throw new RuntimeException("TypeError: operator '*' requires numeric values");
        }
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left * (Integer) right;
        }
        return toDouble(left, "*") * toDouble(right, "*");
    }

    private Object divide(Object left, Object right) {
        if (!isNumeric(left) || !isNumeric(right)) {
            throw new RuntimeException("TypeError: operator '/' requires numeric values");
        }
        if (left instanceof Integer && right instanceof Integer) {
            int divisor = (Integer) right;
            if (divisor == 0) {
                throw new RuntimeException("ArithmeticError: division by zero");
            }
            return (Integer) left / divisor;
        }
        double divisor = toDouble(right, "/");
        if (divisor == 0.0) {
            throw new RuntimeException("ArithmeticError: division by zero");
        }
        return toDouble(left, "/") / divisor;
    }

    private int compare(Object left, Object right) {
        if (!isNumeric(left) || !isNumeric(right)) {
            throw new RuntimeException("TypeError: comparison requires numeric values");
        }
        return Double.compare(toDouble(left, "comparison"), toDouble(right, "comparison"));
    }

    private boolean isNumeric(Object value) {
        return value instanceof Integer || value instanceof Double;
    }

    private String valueCategory(Object value) {
        if (value == null) return "none";
        if (isNumeric(value)) return "number";
        if (value instanceof Boolean) return "boolean";
        if (value instanceof List) return "list";
        return "unknown";
    }

    private double toDouble(Object value, String operator) {
        if (value instanceof Integer) return ((Integer) value).doubleValue();
        if (value instanceof Double) return (Double) value;
        throw new RuntimeException("TypeError: operator '" + operator + "' requires numeric values");
    }

    private boolean isEqual(Object left, Object right) {
        if (left == null && right == null) return true;
        if (left == null || right == null) return false;

        if (isNumeric(left) && isNumeric(right)) {
            return Double.compare(toDouble(left, "=="), toDouble(right, "==")) == 0;
        }

        return left.equals(right);
    }

    private String formatValue(Object value) {
        if (value == null) return "none";

        if (value instanceof Double) {
            double d = (Double) value;
            if (d == Math.rint(d)) {
                return String.format("%.1f", d);
            }
        }

        if (value instanceof List) {
            List<?> list = (List<?>) value;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(formatValue(list.get(i)));
            }
            sb.append("]");
            return sb.toString();
        }

        return value.toString();
    }
}