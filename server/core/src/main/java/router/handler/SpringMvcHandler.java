package router.handler;

import router.analysts.*;
import router.context.Context;
import router.mapping.FrameworkMapping;
import router.parse.UrlParse;
import router.publish.EventPackage;
import router.publish.EventType;
import router.type.HandlerType;
import router.utils.SpringUtils;

public class SpringMvcHandler implements FrameworkHandler {
    @Override
    public void analystsTargetObject(UrlParse urlParse, IAnalysts objRef, FrameworkMapping spring, Context context) throws Exception {
        ListAnalysts handlerMappingsRef = new ListAnalysts(objRef.getObjFields("handlerMappings"));
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
        MapAnalysts mapValueInstanceRef = new MapAnalysts(handlerMapperRef.getObjFields("mappingRegistry", "registry"));
        IAnalysts[] kvInstanceRef = mapValueInstanceRef.getValues();
        context.getPublish().Event(EventType.SpringFrameworkCount, new EventPackage(mapValueInstanceRef.getId(), kvInstanceRef.length));
        for (IAnalysts handlerRef : kvInstanceRef) {
            SetAnalysts patternsRef = SpringUtils.getPatternsRef(handlerRef);
            String className = handlerRef.getObjFields("handlerMethod", "beanType", "name").convertToString();
            for (IAnalysts path : patternsRef) {
                spring.recordServletMap(className, path.convertToString());
            }
            context.getPublish().Event(EventType.SpringFrameworkAnalystsComplete, new EventPackage(mapValueInstanceRef.getId(), className));
        }
    }

    private void handlerAbstractMap(FrameworkMapping spring, IAnalysts handlerMapperRef) {
    }
}