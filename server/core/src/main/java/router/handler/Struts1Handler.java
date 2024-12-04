package router.handler;

import router.analysts.IAnalysts;
import router.analysts.ListAnalysts;
import router.analysts.MapAnalysts;
import router.context.Context;
import router.except.FieldExcept;
import router.mapping.FrameworkMapping;
import router.parse.UrlParse;
import router.publish.*;
import router.type.HandlerType;


public class Struts1Handler implements FrameworkHandler {
    @Override
    public void analystsTargetObject(UrlParse urlParse, IAnalysts objRef, FrameworkMapping struts1, Context context) throws Exception {
        try {
            IAnalysts refObj = objRef.getObjFields("actionConfigList");
            handlerActionConfigList(refObj, struts1, urlParse, context);
        } catch (FieldExcept e) {
            throw new Exception("上下文获取失败");
        } catch (NullPointerException e) {
            try {
                IAnalysts refObj = objRef.getObjFields("actionConfigs");
                handlerActionConfigs(refObj, struts1, urlParse, context);
            } catch (FieldExcept e2) {
                throw new Exception("上下文获取失败");
            }
        }
    }

    private void handlerActionConfigList(IAnalysts actionConfigListRef, FrameworkMapping struts1, UrlParse urlParse, Context context) {
        ListAnalysts list = new ListAnalysts(actionConfigListRef);
        context.getPublish().Event(new StartEvent(EventType.Struts1FrameworkCount,
                list.getId(), getHandlerName().name(), list.size()));
        try {
            for (IAnalysts actionRef : list) {
                recordHandler(urlParse, actionRef, struts1, list.getId(), context);
            }
        } catch (Exception e) {
            context.getPublish().Error(new ErrorEvent(ErrorType.Struts1Error, list.getId(), e));
        }
    }

    private void handlerActionConfigs(IAnalysts actionConfigListRef, FrameworkMapping struts1, UrlParse urlParse, Context context) {
        MapAnalysts mapAnalysts = new MapAnalysts(actionConfigListRef);
        IAnalysts[] values = mapAnalysts.getValues();
        context.getPublish().Event(new StartEvent(EventType.Struts1FrameworkCount,
                mapAnalysts.getId(), getHandlerName().name(), mapAnalysts.getValues().length));
        try {
            for (IAnalysts actionRef : values) {
                recordHandler(urlParse, actionRef, struts1, mapAnalysts.getId(), context);
            }
        } catch (Exception e) {
            context.getPublish().Error(new ErrorEvent(ErrorType.Struts1Error, mapAnalysts.getId(), e));
        }
    }

    public void recordHandler(UrlParse urlParse, IAnalysts objRef, FrameworkMapping struts1, long id, Context context) throws Exception {
        String path = null;
        try {
            path = urlParse.getPathByMiddleware(objRef.getStrFields("path"));
        } catch (Exception e) {
            throw new Exception("struts1 get path failed");
        }
        String classname = null;
        try {
            classname = objRef.getStrFields("type");
        } catch (Exception e) {
            context.getPublish().Event(new EndEvent(EventType.Struts1FrameworkAnalystsComplete, id, "maybe forward"));
            return;
        }
        struts1.recordServletMap(classname, path);
        context.getPublish().Event(new EndEvent(EventType.Struts1FrameworkAnalystsComplete, id, classname));
    }

    @Override
    public HandlerType getHandlerName() {
        return HandlerType.Strut1;
    }
}
