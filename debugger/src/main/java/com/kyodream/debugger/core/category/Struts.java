package com.kyodream.debugger.core.category;

import com.kyodream.debugger.core.analyse.MapAnalyse;
import com.kyodream.debugger.core.analyse.Utils;
import com.kyodream.debugger.core.category.format.Format;
import com.kyodream.debugger.core.category.format.StrutsFormat;
import com.kyodream.debugger.service.DebugWebSocket;
import com.sun.jdi.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Forwarded by tomcat
 */
@Component
@Slf4j
public class Struts extends AbstractDataWrapper {

    private HashMap<String, String> maps = new HashMap();

    private Set<ObjectReference> strutsObjects = new HashSet<>();

    @Override
    public Set<String> getDiscoveryClass() {
        HashSet<String> result = new HashSet<>();
        result.add("com.opensymphony.xwork2.config.impl.DefaultConfiguration");
        return result;
    }

    @Autowired
    DebugWebSocket debugWebSocket;

    @Override
    public void addAnalysisObject(Set<ObjectReference> objectReference) {
        debugWebSocket.sendInfo("发现struts对象");
        this.strutsObjects.addAll(objectReference);
    }

    @Override
    public void analystsObject(VirtualMachine attach) {
        for (ObjectReference strutsObject : this.strutsObjects) {
            analystsStruts(strutsObject);
        }
    }

    private void analystsStruts(ObjectReference strutsObject) {
        hasFind();
        debugWebSocket.sendSuccess("发现并分析struts路由");
        ObjectReference runtimeConfiguration = Utils.getFieldObject(strutsObject, "runtimeConfiguration");
        ObjectReference namespaceActionConfigs = Utils.getFieldObject(runtimeConfiguration, "namespaceActionConfigs");
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
                    String url = StrutsFormat.doubleSlash(prefixUrl + "/" + ((StringReference) subKey).value());
                    String className = ((StringReference) Utils.getFieldObject(subValue, "className")).value();
                    this.maps.put(url, className);
                });
            });
        }
    }

    @Override
    public HashMap<String, String> getDataWrapper() {
        return maps;
    }

    @Override
    public String getVersion() {
        return "";
    }

    @Override
    public void clearData() {
        clearFindFlag();
        this.maps = new HashMap<>();
        this.strutsObjects = new HashSet<>();
    }
}
