package java.kyodream.record;

import java.util.Arrays;

public class Framework {
    private String from;
    private String virtualPath;
    private Object[] HandlerChain;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getVirtualPath() {
        return virtualPath;
    }

    public void setVirtualPath(String virtualPath) {
        this.virtualPath = virtualPath;
    }

    public Object[] getHandlerChain() {
        return HandlerChain;
    }

    public void setHandlerChain(Object[] handlerChain) {
        HandlerChain = handlerChain;
    }

    @Override
    public String toString() {
        return "Framework{" +
                "from='" + from + '\'' +
                ", virtualPath='" + virtualPath + '\'' +
                ", HandlerChain=" + Arrays.toString(HandlerChain) +
                '}';
    }
}
