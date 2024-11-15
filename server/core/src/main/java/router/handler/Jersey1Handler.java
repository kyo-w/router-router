package router.handler;

import router.analysts.IAnalysts;
import router.analysts.ListAnalysts;
import router.analysts.SetAnalysts;
import router.context.Context;
import router.mapping.FrameworkMapping;
import router.parse.UrlParse;
import router.publish.*;
import router.type.HandlerType;

public class Jersey1Handler implements FrameworkHandler {
    @Override
    public void analystsTargetObject(UrlParse urlParse, IAnalysts objRef, FrameworkMapping jersey1, Context context) throws Exception {
        SetAnalysts abstractRootResourcesRef = null;
        try {
            abstractRootResourcesRef = new SetAnalysts(objRef.getObjFields("webComponent",
                    "application", "abstractRootResources"));
        } catch (Exception e) {
            throw new Exception("Jersey1上下文获取失败");
        }
        context.getPublish().Event(new StartEvent(EventType.Jersey1FrameworkCount,
                abstractRootResourcesRef.getId(), getHandlerName().name(), abstractRootResourcesRef.size()));
        try {
            for (IAnalysts resourceRef : abstractRootResourcesRef) {
                String className = resourceRef.getObjFields("resourceClass", "name").convertToString();
                String path = resourceRef.getObjFields("uriPath", "value").convertToString();
                ListAnalysts subResourceMethodsRef = new ListAnalysts(resourceRef.getObjFields("subResourceMethods"));
                for (IAnalysts subResourceMethodRef : subResourceMethodsRef) {
                    String subPath = subResourceMethodRef.getObjFields("uriPath", "value").convertToString();
                    String servletPath = UrlParse.concatSubPath(path, subPath);
                    jersey1.recordServletMap(className, urlParse.getPathByMiddleware(servletPath));
                }
                context.getPublish().Event(
                        new EndEvent(EventType.Jersey1FrameworkAnalystsComplete, abstractRootResourcesRef.getId(), className));
            }
        } catch (Exception e) {
            context.getPublish().Error(new ErrorEvent(ErrorType.Jersey1Error, abstractRootResourcesRef.getId(), e));
        }
    }

    @Override
    public HandlerType getHandlerName() {
        return HandlerType.Jersey1;
    }
}
