public class ReturnSignal extends RuntimeException {
    Object value;

    public ReturnSignal(Object value) {
        this.value = value;
    }
}