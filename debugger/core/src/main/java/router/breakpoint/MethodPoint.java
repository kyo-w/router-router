package router.breakpoint;

import router.handler.BreakPointHandler;

import java.util.Objects;

public class MethodPoint {
    private String className;
    private String methodName;
    private BreakPointHandler handler;

    public MethodPoint(String className, String methodName, BreakPointHandler handler) {
        this.className = className;
        this.methodName = methodName;
        this.handler = handler;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public BreakPointHandler getHandler() {
        return handler;
    }

    public void setHandler(BreakPointHandler handler) {
        this.handler = handler;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodPoint that = (MethodPoint) o;
        return Objects.equals(className, that.className) && Objects.equals(methodName, that.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, methodName);
    }
}
