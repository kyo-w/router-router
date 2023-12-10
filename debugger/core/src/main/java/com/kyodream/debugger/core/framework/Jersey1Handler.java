package com.kyodream.debugger.core.framework;

import kyodream.analysts.ListAnalysts;
import kyodream.analysts.ObjectAnalysts;
import kyodream.analysts.SetAnalysts;
import kyodream.record.ContextUrlRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Jersey1Handler implements FrameworkHandler {
    @Override
    public void analystsTargetObject(ObjectAnalysts objRef, CacheFrameworkRecord cacheFrameworkRecord) {
        HashMap<String, String> servletMap = new HashMap<>();
        cacheFrameworkRecord.setUrlMap(servletMap);
        SetAnalysts abstractRootResourcesRef = new SetAnalysts(objRef.getFieldsRef("webComponent", "application", "abstractRootResources"));
        for (ObjectAnalysts resourceRef : abstractRootResourcesRef) {
            String className = resourceRef.getFieldsRef("resourceClass", "name").getString();
            String path = resourceRef.getFieldsRef("uriPath", "value").getString();
            ListAnalysts subResourceMethodsRef = new ListAnalysts(resourceRef.getFieldsRef("subResourceMethods"));
            for (ObjectAnalysts subResourceMethodRef : subResourceMethodsRef) {
                String subPath = subResourceMethodRef.getFieldsRef("uriPath", "value").getString();
                String servletPath = ContextUrlRecord.concatSubPath(path, subPath);
                servletMap.put(servletPath, className);
            }
        }
    }
}
