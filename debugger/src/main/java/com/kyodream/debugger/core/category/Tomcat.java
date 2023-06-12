package com.kyodream.debugger.core.category;

import com.kyodream.debugger.core.analyse.MapAnalyse;
import com.kyodream.debugger.core.analyse.Utils;
import com.kyodream.debugger.service.DebugWebSocket;
import com.sun.jdi.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * Tomcat支持的解析
 * jersey
 * struts
 */
@Component
@Slf4j
public class Tomcat extends AbstractDataWrapper {
    private static Set<String> filterClass = new HashSet<>();

    static {
        filterClass.add("org.apache.catalina.mapper.Mapper");
        filterClass.add("org.apache.tomcat.util.http.mapper.Mapper");
    }

    @Autowired
    private Jersey jersey;
    @Autowired
    private Struts struts;
    private HashMap<String, String> exactWrappersMap = new HashMap<>();
    private HashMap<String, String> wildcardWrappersMap = new HashMap<>();
    private HashMap<String, String> extensionWrappersMap = new HashMap<>();
    private HashMap<String, String> defaultMap = new HashMap<>();
    private String version;

    private Set<ObjectReference> tomcatObjects = new HashSet<>();

    @Autowired
    private DebugWebSocket debugWebSocket;

    @Override
    public void addAnalysisObject(Set<ObjectReference> objectReference) {
        debugWebSocket.sendInfo("发现tomcat");
        this.tomcatObjects.addAll(objectReference);
    }

    @Override
    public void analystsObject(VirtualMachine attach) {
        handleVersion(attach);
        hasFind();
        debugWebSocket.sendInfo("开始分析tomcat");
        for (ObjectReference tomcatObject : this.tomcatObjects) {
            if (tomcatObject.referenceType().name().equals("org.apache.catalina.mapper.Mapper")) {
                debugWebSocket.sendSuccess("获取Mapper,当前tomcat版本为9/8");
                try {
                    handleTomcat98(tomcatObject);
                } catch (Exception e) {
                    e.printStackTrace();
                    debugWebSocket.sendFail("分析tomcat出现异常错误");
                }
            } else if (tomcatObject.referenceType().name().equals("org.apache.tomcat.util.http.mapper.Mapper")) {
                debugWebSocket.sendSuccess("获取Mapper,当前tomcat版本为7/6");
                try {
                    handleTomcat76(tomcatObject);
                } catch (Exception e) {
                    e.printStackTrace();
                    debugWebSocket.sendFail("分析tomcat出现异常错误");
                }
            }
            if (jersey.isFind()) {
                jersey.analystsObject(attach);
            }
        }
        debugWebSocket.sendInfo("结束分析tomcat");
    }

    private void handleTomcat98(ObjectReference tomcatObject) {
        Field contextObjectToContextVersionMap = null;
        ObjectReference containList = null;
        contextObjectToContextVersionMap = tomcatObject.referenceType().fieldByName("contextObjectToContextVersionMap");
        containList = (ObjectReference) tomcatObject.getValue(contextObjectToContextVersionMap);
        Map<ObjectReference, ObjectReference> hashMapObject = MapAnalyse.getConcurrentHashMapObject(containList);
        Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
//            struts.handleStruts(threadReference, next.getKey());

            ObjectReference value = next.getValue();
            Field path = value.referenceType().fieldByName("path");
            String prefix = ((StringReference) value.getValue(path)).value();
            handleCategory(prefix, value);
        }
    }

    private void handleTomcat76(ObjectReference tomcatObject) {
        ArrayReference hosts = (ArrayReference) Utils.getFieldObject(tomcatObject, "hosts");
        for (int i = 0; i < hosts.length(); i++) {
            ObjectReference mapperHost = (ObjectReference) hosts.getValue(i);
            ObjectReference contextList = Utils.getFieldObject(mapperHost, "contextList");
            ArrayReference contexts = (ArrayReference) Utils.getFieldObject(contextList, "contexts");
            for (int j = 0; j < contexts.length(); j++) {
                ObjectReference contextOne = (ObjectReference) contexts.getValue(i);
                ArrayReference versions = (ArrayReference) Utils.getFieldObject(contextOne, "versions");
                for (int z = 0; z < versions.length(); z++) {
                    ObjectReference context = (ObjectReference) versions.getValue(z);
                    StringReference path = (StringReference) Utils.getFieldObject(context, "path");
                    String prefix = path.value();
                    handleCategory(prefix, context);
                }
            }
        }
    }

    private void handleCategory(String prefix, ObjectReference root) {
        Field defaultWrapper = root.referenceType().fieldByName("defaultWrapper");
        Field exactWrappers = root.referenceType().fieldByName("exactWrappers");
        Field wildcardWrappers = root.referenceType().fieldByName("wildcardWrappers");
        Field extensionWrappers = root.referenceType().fieldByName("extensionWrappers");
        handleDefaultWrapper((ObjectReference) root.getValue(defaultWrapper));
        handleExactWrappers(prefix, (ArrayReference) root.getValue(exactWrappers));
        handleWildcardWrappers(prefix, (ArrayReference) root.getValue(wildcardWrappers));
        handleExtensionWrappers(prefix, (ArrayReference) root.getValue(extensionWrappers));
    }

    private void handleDefaultWrapper(ObjectReference defaultWrapper) {
        ObjectReference object = Utils.getFieldObject(defaultWrapper, "object");
        StringReference servletClass = (StringReference) Utils.getFieldObject(object, "servletClass");
        defaultMap.put("/", servletClass.value());
    }

    private void handleExactWrappers(String prefix, ArrayReference exactWrappers) {
        handleMapperWrappers(prefix, exactWrappers, exactWrappersMap);
    }

    private void handleWildcardWrappers(String prefix, ArrayReference wildcardWrappers) {
        handleMapperWrappers(prefix, wildcardWrappers, wildcardWrappersMap);
    }

    private void handleExtensionWrappers(String prefix, ArrayReference extensionWrappers) {
        handleMapperWrappers(prefix, extensionWrappers, extensionWrappersMap);
    }

    private void handleMapperWrappers(String prefix, ArrayReference objectReference, HashMap<String, String> originMap) {
        int length = objectReference.length();
        for (int i = 0; i < length; i++) {
            ObjectReference elem = (ObjectReference) objectReference.getValue(i);
            String url = ((StringReference) Utils.getFieldObject(elem, "name")).value();
            ObjectReference object = Utils.getFieldObject(elem, "object");
            String servletClass = ((StringReference) Utils.getFieldObject(object, "servletClass")).value();
            if (jersey.getDiscoveryClass().contains(servletClass)) {
                jersey.registryPrefix(prefix + url);
            }
            originMap.put(prefix + url, servletClass);
        }
    }

    @Override
    public HashMap<String, String> getDataWrapper() {
        HashMap<String, String> resultHashMap = new HashMap<>();
        resultHashMap.putAll(defaultMap);
        resultHashMap.putAll(exactWrappersMap);
        resultHashMap.putAll(wildcardWrappersMap);
        resultHashMap.putAll(exactWrappersMap);
        return resultHashMap;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void clearData() {
        this.version = "";
        clearFindFlag();
        this.defaultMap = new HashMap<>();
        this.wildcardWrappersMap = new HashMap<>();
        this.exactWrappersMap = new HashMap<>();
    }

    @Override
    public Set<String> getDiscoveryClass() {
        return filterClass;
    }

    private void handleVersion(VirtualMachine attach) {
        List<ReferenceType> referenceTypes = attach.classesByName("org.apache.catalina.util.ServerInfo");
        for (ReferenceType referenceType : referenceTypes) {
            Field serverInfo = referenceType.fieldByName("serverInfo");
            StringReference serverInfoObject = (StringReference) referenceType.getValue(serverInfo);
            version = serverInfoObject.value();
        }
    }

}
