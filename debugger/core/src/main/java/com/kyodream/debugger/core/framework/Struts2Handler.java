package com.kyodream.debugger.core.framework;

import kyodream.analysts.MapAnalysts;
import kyodream.analysts.ObjectAnalysts;
import kyodream.record.ContextUrlRecord;

import java.util.HashMap;

public class Struts2Handler implements FrameworkHandler {
    @Override
    public void analystsTargetObject(ObjectAnalysts objRef, CacheFrameworkRecord frameworkRecord) {
        if (objRef.isEqualsOrInstanceof("org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter") ||
                objRef.isEqualsOrInstanceof("org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter")) {
            struts23(objRef, frameworkRecord);
        } else if (objRef.isEqualsOrInstanceof("org.apache.struts2.dispatcher.FilterDispatcher")) {
            struts21(objRef, frameworkRecord);
        }
    }

    private void struts21(ObjectAnalysts objRef, CacheFrameworkRecord frameworkRecord) {
        ObjectAnalysts packageContextsRef = objRef.getFieldsRef("dispatcher", "configurationManager", "configuration", "packageContexts");
        handlerPackageContexts(packageContextsRef, frameworkRecord);
    }

    private void struts23(ObjectAnalysts objRef, CacheFrameworkRecord frameworkRecord) {
        ObjectAnalysts packageContextsRef = objRef.getFieldsRef("execute", "dispatcher", "configurationManager", "configuration", "packageContexts");
        handlerPackageContexts(packageContextsRef, frameworkRecord);
    }

    private void handlerPackageContexts(ObjectAnalysts packageContextsRef, CacheFrameworkRecord frameworkRecord) {
        HashMap<String, String> result = new HashMap<>();
        for (ObjectAnalysts packageContextRef : new MapAnalysts(packageContextsRef).getValues()) {
            String namespace = packageContextRef.getStringField("namespace");
            ObjectAnalysts[] actionConfigsRef = new MapAnalysts(packageContextRef.getFieldsRef("actionConfigs")).getValues();
            for (ObjectAnalysts actionRef : actionConfigsRef) {
                String name = actionRef.getStringField("name");
                String className = actionRef.getStringField("className");
                String path = ContextUrlRecord.concatSubPath(namespace, name);
                result.put(path, className);
            }
        }
        frameworkRecord.setUrlMap(result);
    }

}
