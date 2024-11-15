package router.handler;

import router.analysts.*;
import router.context.Context;
import router.except.FieldExcept;
import router.mapping.FrameworkMapping;
import router.parse.UrlParse;
import router.publish.*;
import router.type.HandlerType;
import router.utils.SpringUtils;

public class SpringMvcHandler implements FrameworkHandler {
    @Override
    public void analystsTargetObject(UrlParse urlParse, IAnalysts objRef, FrameworkMapping spring, Context context) throws Exception {
        ListAnalysts handlerMappingsRef = null;
        try {
            handlerMappingsRef = new ListAnalysts(objRef.getObjFields("handlerMappings"));
        } catch (Exception e) {
            throw new Exception("spring上下文获取失败");
        }
        for (IAnalysts handlerMapperRef : handlerMappingsRef) {
            if (handlerMapperRef.getClassName().equals("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping")) {
                handlerRequestMapper(spring, handlerMapperRef, context);
            } else {
                handlerAbstractMap(spring, handlerMapperRef);
            }
        }
    }

    @Override
    public HandlerType getHandlerName() {
        return HandlerType.Spring;
    }

    private void handlerRequestMapper(FrameworkMapping spring, IAnalysts handlerMapperRef, Context context) throws Exception {
        MapAnalysts mapValueInstanceRef = null;
        try {
            mapValueInstanceRef = new MapAnalysts(handlerMapperRef.getObjFields("mappingRegistry", "registry"));
        } catch (FieldExcept e) {
            throw new Exception("registry对象获取失败");
        }
        IAnalysts[] kvInstanceRef = mapValueInstanceRef.getValues();
        context.getPublish().Event(new StartEvent(
                EventType.SpringFrameworkCount, mapValueInstanceRef.getId(), getHandlerName().name(), kvInstanceRef.length));
        try {
            for (IAnalysts handlerRef : kvInstanceRef) {
                SetAnalysts patternsRef = SpringUtils.getPatternsRef(handlerRef);
                String className = handlerRef.getObjFields("handlerMethod", "beanType", "name").convertToString();
                for (IAnalysts path : patternsRef) {
                    spring.recordServletMap(className, path.convertToString());
                }
                context.getPublish().Event(new EndEvent(EventType.SpringFrameworkAnalystsComplete, mapValueInstanceRef.getId(), className));
            }
        } catch (Exception e) {
            context.getPublish().Error(new ErrorEvent(ErrorType.SpringError, mapValueInstanceRef.getId(), e));
        }
    }

    private void handlerAbstractMap(FrameworkMapping spring, IAnalysts handlerMapperRef) {
    }
}