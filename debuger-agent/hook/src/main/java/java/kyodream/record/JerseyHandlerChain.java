package java.kyodream.record;

public class JerseyHandlerChain {
    private String url;
    private String HandlerClass;

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
}
