package router.handler;

import router.analysts.IAnalysts;
import router.analysts.ListAnalysts;
import router.context.Context;
import router.mapping.FrameworkMapping;
import router.parse.UrlParse;
import router.publish.EventPackage;
import router.publish.EventType;
import router.type.HandlerType;

public class Struts1Handler implements FrameworkHandler {
    @Override
    public void analystsTargetObject(UrlParse urlParse, IAnalysts objRef, FrameworkMapping struts1, Context context) throws Exception {
        IAnalysts actionConfigListRef = objRef.getObjFields("actionConfigList");
        ListAnalysts list = new ListAnalysts(actionConfigListRef);
        context.getPublish().Event(EventType.Struts1FrameworkCount, new EventPackage(list.getId(), list.size()));
        for (IAnalysts actionRef : list) {
            String path = urlParse.getPathByMiddleware(actionRef.getStrFields("path"));
            String classname = actionRef.getStrFields("type");
            struts1.recordServletMap(classname,path);
            context.getPublish().Event(EventType.Struts1FrameworkAnalystsComplete, new EventPackage(list.getId(), classname));
        }
    }

    @Override
    public HandlerType getHandlerName() {
        return HandlerType.Strut1;
    }
}
