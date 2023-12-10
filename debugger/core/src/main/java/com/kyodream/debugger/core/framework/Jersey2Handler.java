package com.kyodream.debugger.core.framework;

import kyodream.analysts.MapAnalysts;
import kyodream.analysts.ObjectAnalysts;
import kyodream.analysts.SetAnalysts;

import java.util.HashMap;

public class Jersey2Handler implements FrameworkHandler {
    @Override
    public void analystsTargetObject(ObjectAnalysts objRef, CacheFrameworkRecord cacheFrameworkRecord) {
        HashMap<String, String> result = new HashMap<>();
        SetAnalysts cachedClassesRef = new SetAnalysts(objRef.getFieldsRef("webComponent", "appHandler", "application", "cachedClasses"));
        for (ObjectAnalysts classObjectRef : cachedClassesRef) {
            String className = classObjectRef.getStringField("name");
            MapAnalysts mapInstancesRef = new MapAnalysts(classObjectRef.getFieldsRef("annotationData", "annotations"));
            for (MapAnalysts.Entry mapInstanceRef : mapInstancesRef.getKV()) {
                String name = mapInstanceRef.getKey().getStringField("name");
                if (name.equals("javax.ws.rs.Path")) {
                    MapAnalysts.Entry[] memberValuesRef = new MapAnalysts(mapInstanceRef.getValue().getFieldsRef("h", "memberValues")).getKV();
                    for (MapAnalysts.Entry memberValueRef : memberValuesRef) {
                        if (memberValueRef.getKey().getString().equals("value")) {
                            String path = memberValueRef.getValue().getString();
                            result.put(path, className);
                        }
                    }
                }
            }
        }
        cacheFrameworkRecord.setUrlMap(result);
    }
}
