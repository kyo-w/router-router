package com.kyodream.debugger.core.category;

import com.kyodream.debugger.core.analyse.ListAnalyse;
import com.kyodream.debugger.core.analyse.MapAnalyse;
import com.kyodream.debugger.core.analyse.Utils;
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

    private String prefix = null;

    private Integer version = 0;

    @Override
    public Set<String> getDiscoveryClass() {
        HashSet<String> result = new HashSet<>();
        result.add("com.opensymphony.xwork2.config.impl.DefaultConfiguration");
        //struts1.3
        result.add("org.apache.struts.config.impl.ModuleConfigImpl");
        return result;
    }

    @Autowired
    DebugWebSocket debugWebSocket;

    @Override
    public void addAnalysisObject(Set<ObjectReference> objectReference) {
        debugWebSocket.sendInfo("发现struts对象");
        objectReference.forEach(refObject -> {
            if (refObject.referenceType().name().equals("org.apache.struts.config.impl.ModuleConfigImpl")) {
                version = 1;
                debugWebSocket.sendInfo("当前struts版本为1.x");
            } else if (refObject.referenceType().name().equals("com.opensymphony.xwork2.config.impl.DefaultConfiguration")) {
                version = 2;
                debugWebSocket.sendInfo("当前struts版本为2.x");
            }
        });
        this.strutsObjects.addAll(objectReference);
        hasFind();
    }

    @Override
    public void analystsObject(VirtualMachine attach) {
        for (ObjectReference strutsObject : this.strutsObjects) {
            if (strutsObject.referenceType().name().equals("org.apache.struts.config.impl.ModuleConfigImpl")) {
                analystsStruts1(strutsObject);
            } else if (strutsObject.referenceType().name().equals("com.opensymphony.xwork2.config.impl.DefaultConfiguration")) {
                analystsStruts2(strutsObject);
            }
        }
    }

    private void analystsStruts1(ObjectReference strutsObject) {
        ObjectReference actionConfigs = null;
        boolean isActionConfigList = true;
        try {
            actionConfigs = Utils.getFieldObject(strutsObject, "actionConfigList");
        }catch (Exception e){
            isActionConfigList = false;
            actionConfigs = Utils.getFieldObject(strutsObject, "actionConfigs");
            debugWebSocket.sendInfo("struts1.x版本过低，从actionConfigs中获取");
        }

        if(isActionConfigList) {
            List<ObjectReference> arrayList = ListAnalyse.getArrayList(actionConfigs);
            arrayList.stream().forEach((ObjectReference actionConfig) -> {
                StringReference pathRef = null;
                StringReference typeRef = null;
                try {
                    pathRef = (StringReference) Utils.getFieldObject(actionConfig, "path");
                } catch (Exception e) {
                    return;
                }
                try {
                    typeRef = (StringReference) Utils.getFieldObject(actionConfig, "type");
                } catch (Exception e) {
                    return;
                }
                String url = this.prefix.replace("*", "/" + pathRef.value());

                if (typeRef == null) {
                    this.maps.put(StrutsFormat.doubleSlash(url), "");
                } else {
                    this.maps.put(StrutsFormat.doubleSlash(url), typeRef.value());
                }
            });
        }else {
        Map<ObjectReference, ObjectReference> hashMapObject = MapAnalyse.getHashMapObject(actionConfigs);
        hashMapObject.values().forEach(elem -> {
            StringReference pathRef = null;
            StringReference typeRef = null;
            try {
                pathRef = (StringReference) Utils.getFieldObject(elem, "path");
            } catch (Exception e) {
                debugWebSocket.sendFail("获取路由路径发现异常！");
            }
            try {
                typeRef = (StringReference) Utils.getFieldObject(elem, "type");
            } catch (Exception e) {
                debugWebSocket.sendFail("获取路由映射对象发现异常！");
            }
            String url = this.prefix.replace("*", "/" + pathRef.value());
            if (pathRef == null) {
                return;
            }
            if (typeRef == null) {
                this.maps.put(StrutsFormat.doubleSlash(url), "");
            } else {
                this.maps.put(StrutsFormat.doubleSlash(url), typeRef.value());
            }
        });
        }
    }

    private void analystsStruts2(ObjectReference strutsObject) {
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
                    String urlRaw = this.prefix.replace("*", "/" + prefixUrl + "/" + ((StringReference) subKey).value());
                    String url = StrutsFormat.doubleSlash(urlRaw);
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

    public void registryPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void clearData() {
        clearFindFlag();
        this.maps = new HashMap<>();
        this.strutsObjects = new HashSet<>();
        this.prefix = null;
    }

    public Integer getStrutsVersion() {
        return version;
    }
}
