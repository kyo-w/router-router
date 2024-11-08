package router.except;

public class MethodExcept extends RuntimeException {
    public MethodExcept(String message) {
        super("remote call method fail: " + message);
    }
}
