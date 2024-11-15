package router.handler;

import router.analysts.IAnalysts;
import router.context.Context;
import router.mapping.FrameworkMapping;
import router.except.FrameworkExcept;
import router.parse.UrlParse;
import router.publish.*;
import router.type.HandlerType;

public interface FrameworkHandler {
    default void handler(UrlParse urlParse, IAnalysts objRef, Context context) {
        if (context.getAnalystsByUniqId(objRef.getId()) ==null){
            context.pushAnalysts(objRef);
        }else{
            return;
        }
        FrameworkMapping frameworkMapping = new FrameworkMapping();
        String packageVersion = objRef.getPackageVersion(objRef.getClassName());
        frameworkMapping.setVersion(packageVersion);
        frameworkMapping.setType(getHandlerName());
        frameworkMapping.setContextPath(urlParse.getVirtualPath());
        context.getPublish().Event(new StartEvent(EventType.FrameworkContextCount, objRef.getId(), getHandlerName().name(), 1));
        try {
            analystsTargetObject(urlParse, objRef, frameworkMapping, context);
            context.getPublish().Event(new EndEvent(EventType.FrameworkContextCount, objRef.getId(),"framework完成分析"));
        } catch (Exception e) {
            context.getPublish().Error(new ErrorEvent(ErrorType.FrameworkError, objRef.getId(), e));
        }
        context.pushFramework(frameworkMapping);
    }

    void analystsTargetObject(UrlParse urlParse, IAnalysts objRef, FrameworkMapping frameworkRecord, Context context) throws Exception;

    HandlerType getHandlerName();
}

