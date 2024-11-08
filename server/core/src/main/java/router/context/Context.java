package router.context;

import router.mapping.FrameworkMapping;
import router.mapping.MiddlewareMapping;
import router.publish.IPublish;
import router.type.HandlerType;
import router.type.MiddlewareType;

import java.util.List;

public interface Context {

    void pushMiddleware(MiddlewareMapping middleware);

    void pushFramework(FrameworkMapping framework);

    IPublish getPublish();
}
