package router.except;

public class MiddleWareError extends Exception{
    public MiddleWareError(String msg){
        super(msg);
    }

    public static MiddleWareError TomcatError(String msg){
        return new MiddleWareError("Tomcat Error Event:[ " +  msg + " ]");
    }

    public static MiddleWareError JettyError(String msg){
        return new MiddleWareError("Jetty Error Event:[ " +  msg + " ]");
    }
}
