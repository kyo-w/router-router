package router.context;

import router.analysts.IAnalysts;
import router.mapping.FrameworkMapping;
import router.mapping.MiddlewareMapping;
import router.publish.IPublish;
import router.type.HandlerType;
import router.type.MiddlewareType;

import java.rmi.server.RemoteObject;
import java.util.List;


/**
 * Context代表一个调试过程中相关的内容
 */
public interface Context {

    /**
     * 存储中间件对象
     *
     * @param middleware
     */
    void pushMiddleware(MiddlewareMapping middleware);

    /**
     * 存储框架对象
     *
     * @param framework
     */
    void pushFramework(FrameworkMapping framework);

    /**
     * 标志一个任务的结束
     */
    void completeTask();

    /**
     * 获取一个公布器
     *
     * @return
     */
    IPublish getPublish();

    /**
     * 存储一个远程对象
     */
    void pushAnalysts(IAnalysts analysts);

    IAnalysts getAnalystsByUniqId(Long id);
}
