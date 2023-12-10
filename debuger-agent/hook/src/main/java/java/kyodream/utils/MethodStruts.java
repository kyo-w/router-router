package java.kyodream.utils;

public class MethodStruts {
    public String methodName;
    public String methodDesc;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public void setMethodDesc(String methodDesc) {
        this.methodDesc = methodDesc;
    }

    @Override
    public String toString() {
        return "MethodStruts{" +
                "methodName='" + methodName + '\'' +
                ", methodDesc='" + methodDesc + '\'' +
                '}';
    }
}
