package com.kyodream.debugger.core.context;

import com.kyodream.debugger.core.framework.*;
import com.sun.jdi.*;
import com.sun.jdi.event.BreakpointEvent;
import kyodream.analysts.MapAnalysts;
import kyodream.analysts.ObjectAnalysts;
import kyodream.analysts.IPublish;
import kyodream.breakpoint.BreakPointHandler;
import kyodream.map.AnalystsType;
import kyodream.record.ContextUrlRecord;

import java.util.HashSet;

public abstract class ContextBreakPointHandler implements BreakPointHandler {
    private static final HashSet<ObjectAnalysts> objectCaches = new HashSet<>();

    @Override
    public void handler(BreakpointEvent breakpointEvent, ThreadReference thread, IPublish publish) {
//        确保breakpoint的断点是挂起的，因为只有挂起的线程可以进行方法调用
        if (breakpointEvent.thread().isSuspended()) {
            ObjectAnalysts thisObject = getThisObject(thread, publish);
            if (objectCaches.add(thisObject)) {
                thisObject.debugSuccess("已发现" + getContextType() + " 中间件");
//            这里处理的是每一个上下文容器，tomcat中存在多个contextObjectToContextVersionMaps,每一个对象只进行一次分析
//            这里需要注意一点，在进行handlerTarget调用中，会默认初始化所有的Servlet，这意味着不存在load-on-startup导致的Servlet不存在的问题,这样就确保在第一次访问时，就能完成所有分析
//            详细初始化问题，在initServlet()中可见
                handlerTarget(thisObject);
            }
        }
    }

    public abstract CONTEXT_TYPE getContextType();

    public abstract void handlerTarget(ObjectAnalysts thisObject);

    protected ObjectAnalysts initServlet(ObjectAnalysts ref, ObjectAnalysts classLoader) {
        if (getContextType() == CONTEXT_TYPE.TOMCAT) {
//            tomcat的初始化方法： allocate()。需要进行一次线程上下文的切换
            ObjectAnalysts currentThreadClassLoader = ref.getCurrentThreadClassLoader();
            ref.changeThreadClassLoader(classLoader);
            ObjectAnalysts result = ref.invokeMethod("allocate");
            String className = ref.getStringField("servletClass");
            if (result.isEmpty()) {
//                tomcat在无法完成初始化时: 比如某个初始化，需要配置云，在没有配置云的情况下无法进行Servlet初始化
//                这种情况下，需要回退Servlet的状态，防止影响服务的正常运行
                ref.failServlet(className);
                ref.invokeMethod("unload");
            }else{
                ref.debugSuccess(className + "初始化成功");
            }
            ref.changeThreadClassLoader(currentThreadClassLoader);
            return result;
        } else if (getContextType() == CONTEXT_TYPE.JETTY) {
//            jetty9暂时不涉及上下文加载器的问题
            ObjectAnalysts resultRef = ref.invokeMethod("initServlet");
            if (resultRef.isEmpty()) {
                ref.debugWarn(ref.className() + "方法回滚");
            } else {
                ref.debugSuccess(ref.className() + "初始化成功");
            }
            return resultRef;
        } else {
            throw new RuntimeException("未知类型");
        }
    }

    public void handlerFilterInstance(ContextUrlRecord contextUrlRecord, ObjectAnalysts obj) {
        if (obj.isEqualsOrInstanceof("org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter") ||
                obj.isEqualsOrInstanceof("org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter") ||
                obj.isEqualsOrInstanceof("org.apache.struts2.dispatcher.FilterDispatcher")) {
            handlerStruts2(contextUrlRecord, obj);
            obj.debugSuccess(obj.className() + "完成分析");
        }
//        jersey1也可以当filter使用
    }

    private void handlerStruts2(ContextUrlRecord contextUrlRecord, ObjectAnalysts obj) {
        FrameworkHandler.handlerMap.get(AnalystsType.STRUTS_2).handler(contextUrlRecord, obj);
    }

    public void handlerServlet(ContextUrlRecord contextUrlRecord, ObjectAnalysts obj, ObjectAnalysts attribute) {
        if (obj.isEqualsOrInstanceof("org.apache.struts.action.ActionServlet")) {
            handlerStruts1(contextUrlRecord, obj, attribute);
        } else if (obj.isEqualsOrInstanceof("org.springframework.web.servlet.DispatcherServlet")) {
            FrameworkHandler.handlerMap.get(AnalystsType.SPRING).handler(contextUrlRecord, obj);
        } else if (obj.isEqualsOrInstanceof("org.glassfish.jersey.servlet.ServletContainer")) {
            FrameworkHandler.handlerMap.get(AnalystsType.JERSEY_2).handler(contextUrlRecord, obj);
        } else if (obj.isEqualsOrInstanceof("com.sun.jersey.spi.container.servlet.ServletContainer")) {
            FrameworkHandler.handlerMap.get(AnalystsType.JERSEY_1).handler(contextUrlRecord, obj);
        }
    }

    private void handlerStruts1(ContextUrlRecord contextUrlRecord, ObjectAnalysts obj, ObjectAnalysts attributeRef) {
        switch (getContextType()) {
            case JETTY:
            case TOMCAT:
                MapAnalysts.Entry[] mapInstanceRefs = new MapAnalysts(attributeRef).getKV();
                for (MapAnalysts.Entry mapInstanceRef : mapInstanceRefs) {
                    if (mapInstanceRef.getKey().getString().startsWith("org.apache.struts.action.MODULE")) {
                        ObjectAnalysts value = mapInstanceRef.getValue();
                        FrameworkHandler.handlerMap.get(AnalystsType.STRUTS_1).handler(contextUrlRecord, value);
                    }
                }
                return;
            default:
                throw new RuntimeException("未知类型");
        }
    }

    public enum CONTEXT_TYPE {
        TOMCAT,
        JETTY
    }
}
