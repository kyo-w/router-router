package router.handler;


import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.BreakpointEvent;
import router.analysts.IAnalysts;
import router.analysts.ObjAnalysts;
import router.context.Context;
import router.except.MiddleWareError;
import router.publish.EventPackage;
import router.publish.EventType;
import router.type.MiddlewareType;

public interface BreakPointHandler {
    default void handler(BreakpointEvent breakpointEvent, ThreadReference thread, Context context) {
        if (breakpointEvent.thread().isSuspended()) {
            try {
                IAnalysts thisObject = getThisObject(thread);
                context.getPublish().Event(EventType.BreakPointStart, new EventPackage(thisObject.getId(), 1));
                handlerTarget(thisObject, context);
                context.getPublish().Event(EventType.BreakPointEnd, new EventPackage(thisObject.getId(), getHandlerName()));
            } catch (Exception e) {
                context.getPublish().Error(e);
            }
        }
    }

    /**
     * 这里处理的是每一个上下文容器，tomcat中存在多个contextObjectToContextVersionMaps,每一个对象只进行一次分析
     * 这里需要注意一点，在进行handlerTarget调用中，会默认初始化所有的Servlet，这意味着不存在load-on-startup导致的Servlet不存在的问题,这样就确保在第一次访问时，就能完成所有分析
     * 详细初始化问题，在initServlet()中可见
     *
     * @param thisObject
     * @param context
     * @throws MiddleWareError
     */
    void handlerTarget(IAnalysts thisObject, Context context) throws MiddleWareError;

    default IAnalysts getThisObject(ThreadReference ref) throws Exception {
        try {
            return ObjAnalysts.parseObject(ref, ref.frame(0).thisObject());
        } catch (IncompatibleThreadStateException e) {
            throw new Exception("can't get object from thread!");
        }
    }

    MiddlewareType getHandlerName();

}
