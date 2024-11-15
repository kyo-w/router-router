package router.handler;


import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.BreakpointEvent;
import router.analysts.IAnalysts;
import router.analysts.ObjAnalysts;
import router.context.Context;
import router.except.MiddleWareError;
import router.publish.*;
import router.publish.Error;
import router.type.MiddlewareType;

public interface BreakPointHandler {
    default void handler(BreakpointEvent breakpointEvent, ThreadReference thread, Context context) {
        if (breakpointEvent.thread().isSuspended()) {
            IAnalysts thisObject = getThisObject(thread);
            try {
                handlerTarget(thisObject, context);
            } catch (Exception e) {
                e.printStackTrace();
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

    default IAnalysts getThisObject(ThreadReference ref) {
        try {
            return ObjAnalysts.parseObject(ref, ref.frame(0).thisObject());
        } catch (IncompatibleThreadStateException e) {
            throw new RuntimeException(e);
        }
    }

    MiddlewareType getHandlerName();

}
