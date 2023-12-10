package java.kyodream.record;

public class StrutsHandlerChain {
    private String url;
    private String HandlerClass;
    private String[] interceptor;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHandlerClass() {
        return HandlerClass;
    }

    public void setHandlerClass(String handlerClass) {
        HandlerClass = handlerClass;
    }

    public String[] getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(String[] interceptor) {
        this.interceptor = interceptor;
    }
}
