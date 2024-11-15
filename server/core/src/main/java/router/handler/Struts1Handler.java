package router.handler;

import router.analysts.IAnalysts;
import router.analysts.ListAnalysts;
import router.context.Context;
import router.except.FieldExcept;
import router.mapping.FrameworkMapping;
import router.parse.UrlParse;
import router.publish.*;
import router.type.HandlerType;

public class Struts1Handler implements FrameworkHandler {
    @Override
    public void analystsTargetObject(UrlParse urlParse, IAnalysts objRef, FrameworkMapping struts1, Context context) throws Exception {
        IAnalysts actionConfigListRef = null;
        try {
            actionConfigListRef = objRef.getObjFields("actionConfigList");
        } catch (FieldExcept e) {
            throw new Exception("上下文获取失败");
        }
        ListAnalysts list = new ListAnalysts(actionConfigListRef);
        context.getPublish().Event(new StartEvent(EventType.Struts1FrameworkCount,
                list.getId(), getHandlerName().name(), list.size()));
        try {
            for (IAnalysts actionRef : list) {
                String path = urlParse.getPathByMiddleware(actionRef.getStrFields("path"));
                String classname = null;
                try {
                    classname = actionRef.getStrFields("type");
                }catch (FieldExcept e) {
                    context.getPublish().Event(new EndEvent(EventType.Struts1FrameworkAnalystsComplete, list.getId(), "maybe forward"));
                    continue;
                }
                struts1.recordServletMap(classname, path);
                context.getPublish().Event(new EndEvent(EventType.Struts1FrameworkAnalystsComplete, list.getId(), classname));
            }
        } catch (Exception e) {
            context.getPublish().Error(new ErrorEvent(ErrorType.Struts1Error, list.getId(), e));
        }
    }

    @Override
    public HandlerType getHandlerName() {
        return HandlerType.Strut1;
    }
}
