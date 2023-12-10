package com.kyodream.debugger.core.framework;

import kyodream.analysts.ListAnalysts;
import kyodream.analysts.MapAnalysts;
import kyodream.analysts.ObjectAnalysts;
import kyodream.analysts.SetAnalysts;

import java.util.Arrays;
import java.util.HashMap;

public class SpringMvcHandler implements FrameworkHandler {
    @Override
    public void analystsTargetObject(ObjectAnalysts objRef, CacheFrameworkRecord cacheFrameworkRecord) {
        ListAnalysts handlerMappingsRef = new ListAnalysts(objRef.getFieldsRef("handlerMappings"));
        for (ObjectAnalysts handlerMapperRef : handlerMappingsRef) {
            if (handlerMapperRef.className().equals("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping")) {
                handlerRequestMapper(cacheFrameworkRecord, handlerMapperRef);
            } else {
                handlerAbstractMap(cacheFrameworkRecord, handlerMapperRef);
            }
        }
    }

    private void handlerRequestMapper(CacheFrameworkRecord cacheFrameworkRecord, ObjectAnalysts handlerMapperRef) {
        HashMap<String, String> result = new HashMap<>();
        ObjectAnalysts[] mapValueInstanceRef = new MapAnalysts(handlerMapperRef.getFieldsRef("mappingRegistry", "registry")).getValues();
        for (ObjectAnalysts handlerRef : mapValueInstanceRef) {
            SetAnalysts patternsRef = new SetAnalysts(handlerRef.getFieldsRef("mapping", "patternsCondition", "patterns"));
            String className = handlerRef.getFieldsRef("handlerMethod", "beanType", "name").getString();
            for (ObjectAnalysts path : patternsRef) {
                result.put(path.getString(), className);
            }
        }
        if (cacheFrameworkRecord.getUrlMap() == null) {
            cacheFrameworkRecord.setUrlMap(result);
        } else {
            cacheFrameworkRecord.getUrlMap().putAll(result);
        }
    }

    private void handlerAbstractMap(CacheFrameworkRecord cacheFrameworkRecord, ObjectAnalysts handlerMapperRef) {
//        ObjectAnalysts.MapRef[] handlerMapsRef = handlerMapperRef.getFieldsRef("handlerMap").getMapInstance();
//        for (ObjectAnalysts.MapRef mapRef : handlerMapsRef) {
//            ObjectAnalysts value = mapRef.getKey();
//            String classNameString = mapRef.getValue().getClassNameString();
//        }
    }
}