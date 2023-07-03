package com.kyodream.debugger.core.category;

import com.kyodream.debugger.core.analyse.ListAnalyse;
import com.kyodream.debugger.core.analyse.MapAnalyse;
import com.kyodream.debugger.core.category.format.Format;
import com.sun.jdi.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Forwarded by tomcat
 */
@Component
@Slf4j
public class Struts extends DefaultFramework {

    private Integer version = 0;


    @Override
    public void addAnalystsObject(ObjectReference objectReference) {
    }


    @Override
    public boolean analystsFrameworkObject(VirtualMachine vm) {
        if (getPrefix() == null) {
            debugWebSocket.sendInfo("struts未获取路由前缀先跳过");
            return false;
        }
        List<ReferenceType> referenceTypes = vm.classesByName("org.apache.struts.config.impl.ModuleConfigImpl");
        for(ReferenceType referenceType: referenceTypes){
            version = 1;
            debugWebSocket.sendInfo("当前struts版本为1.x");
            List<ObjectReference> instances = referenceType.instances(0);
            instances.forEach(elem-> super.addAnalystsObject(elem));
        }
        referenceTypes = vm.classesByName("com.opensymphony.xwork2.config.impl.DefaultConfiguration");
        for(ReferenceType referenceType: referenceTypes){
            version = 1;
            debugWebSocket.sendInfo("当前struts版本为2.x");
            List<ObjectReference> instances = referenceType.instances(0);
            instances.forEach(elem-> super.addAnalystsObject(elem));
        }

        debugWebSocket.sendInfo("开始分析struts");
        for (ObjectReference strutsObject : getAnalystsObject()) {
            if (strutsObject.referenceType().name().equals("org.apache.struts.config.impl.ModuleConfigImpl")) {
                analystsStruts1(strutsObject);
            } else if (strutsObject.referenceType().name().equals("com.opensymphony.xwork2.config.impl.DefaultConfiguration")) {
                analystsStruts2(strutsObject);
            }
        }
        debugWebSocket.sendSuccess("结束分析struts");
        return true;
    }

    @Override
    public void setHandleOrFrameworkName() {
        this.handleOrFrameworkName = "struts";
    }

    private void analystsStruts1(ObjectReference strutsObject) {
        ObjectReference actionConfigs = null;
        boolean isActionConfigList = true;
        try {
            actionConfigs = getFieldObject(strutsObject, "actionConfigList");
        } catch (Exception e) {
            isActionConfigList = false;
            actionConfigs = getFieldObject(strutsObject, "actionConfigs");
            debugWebSocket.sendInfo("struts1.x版本过低，从actionConfigs中获取");
        }

        if (isActionConfigList) {
            List<ObjectReference> arrayList = ListAnalyse.getArrayList(actionConfigs);
            arrayList.stream().forEach((ObjectReference actionConfig) -> {
                StringReference pathRef = null;
                StringReference typeRef = null;
                try {
                    pathRef = (StringReference) getFieldObject(actionConfig, "path");
                } catch (Exception e) {
                    return;
                }
                try {
                    typeRef = (StringReference) getFieldObject(actionConfig, "type");
                } catch (Exception e) {
                    return;
                }
                String url = getPrefix().replace("*", "/" + pathRef.value());

                if (typeRef == null) {
                    getDataWrapper().put(Format.doubleSlash(url), "");
                } else {
                    getDataWrapper().put(Format.doubleSlash(url), typeRef.value());
                }
            });
        } else {
            Map<ObjectReference, ObjectReference> hashMapObject = MapAnalyse.getHashMapObject(actionConfigs);
            hashMapObject.values().forEach(elem -> {
                StringReference pathRef = null;
                StringReference typeRef = null;
                try {
                    pathRef = (StringReference) getFieldObject(elem, "path");
                } catch (Exception e) {
                    debugWebSocket.sendFail("获取路由路径发现异常！");
                }
                try {
                    typeRef = (StringReference) getFieldObject(elem, "type");
                } catch (Exception e) {
                    debugWebSocket.sendFail("获取路由映射对象发现异常！");
                }
                String url = getPrefix().replace("*", "/" + pathRef.value());
                if (pathRef == null) {
                    return;
                }
                if (typeRef == null) {
                    getDataWrapper().put(Format.doubleSlash(url), "");
                } else {
                    getDataWrapper().put(Format.doubleSlash(url), typeRef.value());
                }
            });
        }
    }

    private void analystsStruts2(ObjectReference strutsObject) {
        debugWebSocket.sendSuccess("发现并分析struts路由");
        ObjectReference runtimeConfiguration = getFieldObject(strutsObject, "runtimeConfiguration");
        ObjectReference namespaceActionConfigs = getFieldObject(runtimeConfiguration, "namespaceActionConfigs");
        Map<ObjectReference, ObjectReference> unmodifiableMap = null;
        try {
            unmodifiableMap = MapAnalyse.getUnmodifiableMap(namespaceActionConfigs);
        } catch (Exception e) {
            debugWebSocket.sendInfo("目标struts版本小于2.5");
            unmodifiableMap = MapAnalyse.getHashMapObject(namespaceActionConfigs);
        }
        if (unmodifiableMap != null) {
            unmodifiableMap.forEach((ObjectReference key, ObjectReference value) -> {
                String prefixUrl = ((StringReference) key).value();
                Map<ObjectReference, ObjectReference> subUrlMapping = MapAnalyse.getHashMapObject(value);
                subUrlMapping.forEach((ObjectReference subKey, ObjectReference subValue) -> {
                    String urlRaw = getPrefix().replace("*", "/" + prefixUrl + "/" + ((StringReference) subKey).value());
                    String url = Format.doubleSlash(urlRaw);
                    String className = ((StringReference) getFieldObject(subValue, "className")).value();
                    getDataWrapper().put(url, className);
                });
            });
        }
    }

    public Integer getStrutsVersion() {
        return version;
    }
}
