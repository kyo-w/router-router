package com.kyodream.debugger.core.framework;

import kyodream.analysts.ListAnalysts;
import kyodream.analysts.ObjectAnalysts;

import java.util.HashMap;

public class Struts1Handler implements FrameworkHandler {
    @Override
    public void analystsTargetObject(ObjectAnalysts objRef, CacheFrameworkRecord cacheFrameworkRecord) {
        ObjectAnalysts actionConfigListRef = objRef.getFieldsRef("actionConfigList");
        setUrlMap(actionConfigListRef, cacheFrameworkRecord);
    }

    private void setUrlMap(ObjectAnalysts actionConfigListRef, CacheFrameworkRecord frameworkRecord) {
        HashMap<String, String> result = new HashMap<>();
        ListAnalysts list = new ListAnalysts(actionConfigListRef);
        for (ObjectAnalysts actionRef : list) {
            String subPath = actionRef.getStringField("path");
            String type = actionRef.getStringField("type");
            result.put(subPath, type);
        }
        frameworkRecord.setUrlMap(result);
    }
}
