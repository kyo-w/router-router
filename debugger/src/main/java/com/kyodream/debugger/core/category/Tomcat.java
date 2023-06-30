package com.kyodream.debugger.core.category;

import com.kyodream.debugger.core.analyse.MapAnalyse;
import com.kyodream.debugger.core.category.format.Format;
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
    private static Set<String> discoveryClass = new HashSet<>();

    static {
        discoveryClass.add("org.apache.catalina.mapper.Mapper");
        discoveryClass.add("org.apache.tomcat.util.http.mapper.Mapper");
    }
    private static HashMap<String, String> exactWrappersMap = new HashMap<>();
    private static HashMap<String, String> wildcardWrappersMap = new HashMap<>();
    private static HashMap<String, String> extensionWrappersMap = new HashMap<>();
    private static HashMap<String, String> defaultMap = new HashMap<>();
    private String version;
    private Set<ObjectReference> tomcatObjects = new HashSet<>();
    private VirtualMachine vm;

    @Autowired
    Handler handler;


    @Override
    public void addAnalysisObject(Set<ObjectReference> objectReference) {
        if(objectReference.size() > 0){
            hasFind();
        }
        debugWebSocket.sendInfo("发现tomcat");
        this.tomcatObjects.addAll(objectReference);
    }

    @Override
    public boolean analystsObject(VirtualMachine attach) {
        this.vm = attach;
        handleVersion(attach);
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
            handler.analystsPrefixHandler(vm);
        }
        debugWebSocket.sendSuccess("结束分析tomcat");
        return true;
    }

    @Override
    public void setHandleOrPlugin() {
        this.handleOrPlugin = "tomcat";
    }

    private void handleTomcat98(ObjectReference tomcatObject) {
        ObjectReference containList = getFieldObject(tomcatObject, "contextObjectToContextVersionMap");
        Map<ObjectReference, ObjectReference> hashMapObject = MapAnalyse.getConcurrentHashMapObject(containList);
        Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
            ObjectReference value = next.getValue();
            Field path = value.referenceType().fieldByName("path");
            String prefix = ((StringReference) value.getValue(path)).value();
            handleCategory(prefix, value);
        }
    }

    private void handleTomcat76(ObjectReference tomcatObject) {
        ArrayReference hosts = (ArrayReference) getFieldObject(tomcatObject, "hosts");
        for (int i = 0; i < hosts.length(); i++) {
            ObjectReference mapperHost = (ObjectReference) hosts.getValue(i);
            ObjectReference contextList = getFieldObject(mapperHost, "contextList");
            ArrayReference contexts = (ArrayReference) getFieldObject(contextList, "contexts");
            for (int j = 0; j < contexts.length(); j++) {
                ObjectReference contextOne = (ObjectReference) contexts.getValue(i);
                ArrayReference versions = (ArrayReference) getFieldObject(contextOne, "versions");
                for (int z = 0; z < versions.length(); z++) {
                    ObjectReference context = (ObjectReference) versions.getValue(z);
                    StringReference path = (StringReference) getFieldObject(context, "path");
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
        ObjectReference object = getFieldObject(defaultWrapper, "object");
        StringReference servletClass = (StringReference) getFieldObject(object, "servletClass");
        defaultMap.put("/", servletClass.value());
    }

    private void handleExactWrappers(String prefix, ArrayReference exactWrappers) {
        handleMapperWrappers(prefix, exactWrappers, exactWrappersMap, UrlType.Exact);
    }

    private void handleWildcardWrappers(String prefix, ArrayReference wildcardWrappers) {
        handleMapperWrappers(prefix, wildcardWrappers, wildcardWrappersMap, UrlType.Wild);
    }

    private void handleExtensionWrappers(String prefix, ArrayReference extensionWrappers) {
        handleMapperWrappers(prefix, extensionWrappers, extensionWrappersMap, UrlType.Ext);
    }

    private void handleMapperWrappers(String prefix, ArrayReference objectReference, HashMap<String, String> originMap, UrlType urlType) {
        int length = objectReference.length();
        for (int i = 0; i < length; i++) {
            ObjectReference elem = (ObjectReference) objectReference.getValue(i);
            String url = ((StringReference) getFieldObject(elem, "name")).value();
            ObjectReference object = getFieldObject(elem, "object");
            String servletClass = ((StringReference) getFieldObject(object, "servletClass")).value();
            String fullName = "";
            if (urlType == UrlType.Exact) {
                fullName = Format.doubleSlash(prefix + "/" + url);
            } else if (urlType == UrlType.Wild) {
                fullName = prefix + url + "/*";
            } else if (urlType == UrlType.Ext) {
                fullName = prefix + "/*." + url;
            }
            handler.registryPrefix(fullName, servletClass);
            handler.handlerMagicModificationFramework(vm, fullName, servletClass, servletClass);
            originMap.put(fullName, servletClass);
        }
    }

    @Override
    public HashMap<String, String> getDataWrapper() {
        HashMap<String, String> resultHashMap = new HashMap<>();
        if (defaultMap == null) {
            defaultMap = new HashMap<>();
        }
        if (exactWrappersMap == null) {
            exactWrappersMap = new HashMap<>();
        }
        if (wildcardWrappersMap == null) {
            wildcardWrappersMap = new HashMap<>();
        }
        if (extensionWrappersMap == null) {
            extensionWrappersMap = new HashMap<>();
        }
        resultHashMap.putAll(defaultMap);
        resultHashMap.putAll(exactWrappersMap);
        resultHashMap.putAll(wildcardWrappersMap);
        resultHashMap.putAll(extensionWrappersMap);
        return resultHashMap;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void clearData() {
        this.version = "";
        super.clearData();
        this.tomcatObjects = new HashSet<>();
        this.defaultMap = new HashMap<>();
        this.wildcardWrappersMap = new HashMap<>();
        this.exactWrappersMap = new HashMap<>();
    }

    @Override
    public Set<String> getDiscoveryClass() {
        return discoveryClass;
    }

    private void handleVersion(VirtualMachine attach) {
        List<ReferenceType> referenceTypes = attach.classesByName("org.apache.catalina.util.ServerInfo");
        for (ReferenceType referenceType : referenceTypes) {
            Field serverInfo = referenceType.fieldByName("serverInfo");
            StringReference serverInfoObject = (StringReference) referenceType.getValue(serverInfo);
            version = serverInfoObject.value();
        }
    }


    enum UrlType {
        Exact,
        Wild,
        Ext
    }
}
