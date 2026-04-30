import java.util.HashMap;
import java.util.Map;

public class Environment {
    private static class Variable {
        Object value;
        boolean mutable;
        String category;

        Variable(Object value, boolean mutable, String category) {
            this.value = value;
            this.mutable = mutable;
            this.category = category;
        }
    }

    private final Map<String, Variable> values = new HashMap<>();
    private final Environment parent;

    public Environment() {
        this.parent = null;
    }

    public Environment(Environment parent) {
        this.parent = parent;
    }

    public void declare(String name, Object value, boolean mutable, String category) {
        if (values.containsKey(name)) {
            throw new RuntimeException("NameError: variable '" + name + "' already declared");
        }
        values.put(name, new Variable(value, mutable, category));
    }

    public void assign(String name, Object value, String category) {
        if (values.containsKey(name)) {
            Variable variable = values.get(name);

            if (!variable.mutable) {
                throw new RuntimeException("MutabilityError: cannot reassign immutable variable '" + name + "'");
            }

            if (!variable.category.equals(category)) {
                throw new RuntimeException(
                        "TypeError: cannot assign " + category +
                                " to " + variable.category + " variable '" + name + "'"
                );
            }

            variable.value = value;
            return;
        }

        if (parent != null) {
            parent.assign(name, value, category);
            return;
        }

        throw new RuntimeException("NameError: variable '" + name + "' not defined");
    }

    public Object get(String name) {
        if (values.containsKey(name)) {
            return values.get(name).value;
        }

        if (parent != null) {
            return parent.get(name);
        }

        throw new RuntimeException("NameError: variable '" + name + "' not defined");
    }
}