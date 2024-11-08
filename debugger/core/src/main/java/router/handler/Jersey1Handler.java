package router.handler;

import router.analysts.IAnalysts;
import router.analysts.ListAnalysts;
import router.analysts.SetAnalysts;
import router.context.Context;
import router.mapping.FrameworkMapping;
import router.parse.UrlParse;
import router.publish.EventPackage;
import router.publish.EventType;
import router.type.HandlerType;

public class Jersey1Handler implements FrameworkHandler {
    @Override
    public  void analystsTargetObject(UrlParse urlParse, IAnalysts objRef, FrameworkMapping jersey1, Context context) throws Exception {
        SetAnalysts abstractRootResourcesRef = new SetAnalysts(objRef.getObjFields("webComponent",
                "application", "abstractRootResources"));
        context.getPublish().Event(EventType.Jersey1FrameworkCount,
                new EventPackage(abstractRootResourcesRef.getId(), abstractRootResourcesRef.size()));
        for (IAnalysts resourceRef : abstractRootResourcesRef) {
            String className = resourceRef.getObjFields("resourceClass", "name").convertToString();
            String path = resourceRef.getObjFields("uriPath", "value").convertToString();
            ListAnalysts subResourceMethodsRef = new ListAnalysts(resourceRef.getObjFields("subResourceMethods"));
            for (IAnalysts subResourceMethodRef : subResourceMethodsRef) {
                String subPath = subResourceMethodRef.getObjFields("uriPath", "value").convertToString();
                String servletPath = UrlParse.concatSubPath(path, subPath);
                jersey1.recordServletMap(className, urlParse.getPathByMiddleware(servletPath));
            }
            context.getPublish().Event(EventType.Jersey1FrameworkAnalystsComplete,
                    new EventPackage(abstractRootResourcesRef.getId(), className));
        }
    }

    @Override
    public HandlerType getHandlerName() {
        return HandlerType.Jersey1;
    }
}
