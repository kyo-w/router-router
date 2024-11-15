package router.handler;

import router.analysts.IAnalysts;
import router.analysts.MapAnalysts;
import router.context.Context;
import router.except.FieldExcept;
import router.mapping.FrameworkMapping;
import router.parse.UrlParse;
import router.publish.*;
import router.type.HandlerType;

public class Struts2Handler implements FrameworkHandler {
    @Override
    public void analystsTargetObject(UrlParse urlParse, IAnalysts objRef, FrameworkMapping strut2, Context context) throws Exception {
        if (objRef.isInstanceof("org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter") ||
                objRef.isInstanceof("org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter")) {
            struts23(urlParse, objRef, strut2, context);
        } else if (objRef.isInstanceof("org.apache.struts2.dispatcher.FilterDispatcher")) {
            struts21(urlParse, objRef, strut2, context);
        }
    }

    @Override
    public HandlerType getHandlerName() {
        return HandlerType.Strut2;
    }

    private void struts21(UrlParse urlParse, IAnalysts objRef, FrameworkMapping strut2, Context context) throws Exception {
        IAnalysts packageContextsRef = null;
        try {
            packageContextsRef = objRef.getObjFields("dispatcher",
                    "configurationManager", "configuration", "packageContexts");
        } catch (FieldExcept e) {
            throw new Exception("package上下文获取失败");
        }
        handlerPackageContexts(urlParse, packageContextsRef, strut2, context);
    }

    private void struts23(UrlParse urlParse, IAnalysts objRef, FrameworkMapping strut2, Context context) throws Exception {
        IAnalysts packageContextsRef = null;

        try {
            packageContextsRef = objRef.getObjFields("execute",
                    "dispatcher", "configurationManager", "configuration", "packageContexts");
        } catch (Exception e) {
            throw new Exception("package上下文获取失败");
        }
        handlerPackageContexts(urlParse, packageContextsRef, strut2, context);
    }

    private void handlerPackageContexts(UrlParse urlParse, IAnalysts packageContextsRef, FrameworkMapping strut2, Context context) throws Exception {
        for (IAnalysts packageContextRef : new MapAnalysts(packageContextsRef).getValues()) {
            MapAnalysts actionConfigsRef = null;
            try {
                actionConfigsRef = new MapAnalysts(packageContextRef.getObjFields("actionConfigs"));
            } catch (FieldExcept e) {
                throw new Exception("action上下文获取失败");
            }
            IAnalysts[] kvRef = actionConfigsRef.getValues();
            context.getPublish().Event(new StartEvent(EventType.Struts2FrameworkCount,
                    actionConfigsRef.getId(), getHandlerName().name(), kvRef.length));
            try {
                String namespace = packageContextRef.getStrFields("namespace");
                for (IAnalysts actionRef : kvRef) {
                    String name = actionRef.getStrFields("name");
                    String className = actionRef.getStrFields("className");
                    String path = UrlParse.concatSubPath(namespace, name);
                    strut2.recordServletMap(className, urlParse.getPathByMiddleware(path));
                    context.getPublish().Event(new EndEvent(EventType.Struts2FrameworkAnalystsComplete,
                            actionConfigsRef.getId(), className));
                }
            } catch (Exception e) {
                context.getPublish().Error(new ErrorEvent(ErrorType.Struts2Error, actionConfigsRef.getId(), e));
            }
        }
    }

}
