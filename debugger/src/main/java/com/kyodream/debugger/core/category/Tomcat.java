package com.kyodream.debugger.core.category;

import com.kyodream.debugger.core.analyse.MapAnalyse;
import com.kyodream.debugger.core.category.format.Format;
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
public class Tomcat extends DefaultHandler {

    private static HashMap<String, String> exactWrappersMap = new HashMap<>();
    private static HashMap<String, String> wildcardWrappersMap = new HashMap<>();
    private static HashMap<String, String> extensionWrappersMap = new HashMap<>();
    private static HashMap<String, String> defaultMap = new HashMap<>();
    private VirtualMachine vm;


    @Override
    public void analystsHandlerObject(VirtualMachine vm) {
        this.vm = vm;
        handleVersion(vm);
        for (ObjectReference tomcatObject : getAnalystsObject()) {
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
        }
    }

    @Override
    public Set<String> getDiscoveryClass() {
        if(this.discoveryClass.size() == 0){
            discoveryClass.add("org.apache.catalina.mapper.Mapper");
            discoveryClass.add("org.apache.tomcat.util.http.mapper.Mapper");
        }
        return discoveryClass;
    }

    @Override
    public void setHandleOrFrameworkName() {
        this.handleOrFrameworkName = "tomcat";
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
            ObjectReference objectRef = getFieldObject(elem, "object");
            String servletClass = ((StringReference) getFieldObject(objectRef, "servletClass")).value();
            String fullName = "";
            if (urlType == UrlType.Exact) {
                fullName = Format.doubleSlash(prefix + "/" + url);
            } else if (urlType == UrlType.Wild) {
                fullName = prefix + url + "/*";
            } else if (urlType == UrlType.Ext) {
                fullName = prefix + "/*." + url;
            }
            RegistryType registryType = registryPrefix(fullName, servletClass);
            if(registryType != RegistryType.None){
                registryAnalystsObject(registryType, getFieldObject(objectRef, "instance"));
            }
            boolean find = handlerMagicModificationFramework(vm, fullName, servletClass, servletClass);
            if(find){
                springMvc.addAnalystsObject(getFieldObject(objectRef, "instance"));
            }
            originMap.put(fullName, servletClass);
        }
    }

    @Override
    public HashMap<String, String> getDataWrapper() {
        HashMap<String, String> dataWrapper = super.getDataWrapper();
        dataWrapper.putAll(defaultMap);
        dataWrapper.putAll(exactWrappersMap);
        dataWrapper.putAll(wildcardWrappersMap);
        dataWrapper.putAll(extensionWrappersMap);
        return dataWrapper;
    }
    @Override
    public void clearAny() {
        super.clearAny();
        defaultMap.clear();
        wildcardWrappersMap.clear();
        exactWrappersMap.clear();
        extensionWrappersMap.clear();
    }

    private void handleVersion(VirtualMachine attach) {
        List<ReferenceType> referenceTypes = attach.classesByName("org.apache.catalina.util.ServerInfo");
        for (ReferenceType referenceType : referenceTypes) {
            Field serverInfo = referenceType.fieldByName("serverInfo");
            StringReference serverInfoObject = (StringReference) referenceType.getValue(serverInfo);
           setVersion(serverInfoObject.value());
        }
    }


    enum UrlType {
        Exact,
        Wild,
        Ext
    }
}
