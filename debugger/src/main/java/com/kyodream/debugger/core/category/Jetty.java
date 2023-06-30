package com.kyodream.debugger.core.category;

import com.kyodream.debugger.core.category.format.Format;
import com.kyodream.debugger.service.DebugWebSocket;
import com.sun.jdi.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * jetty支持
 * jersey
 * struts
 */
@Component
@Slf4j
public class Jetty extends AbstractDataWrapper {
    private static Set<String> discoveryClass = new HashSet<>();
    private static Set<String> blackList = new HashSet<String>();

    static {
        discoveryClass.add("org.eclipse.jetty.webapp.WebAppContext");
        discoveryClass.add("org.eclipse.jetty.servlet.ServletContextHandler");
        blackList.add("org.eclipse.jetty.servlet.NoJspServlet");
    }

    private HashMap<String, String> maps = new HashMap<>();
    private String version;
    private Set<ObjectReference> jettyObjects = new HashSet<>();

    @Autowired
    private DebugWebSocket debugWebSocket;

    private VirtualMachine vm;

    @Autowired
    private Handler handler;

    @Override
    public void addAnalysisObject(Set<ObjectReference> objectReference) {
        if (objectReference.size() > 0) {
            hasFind();
        }
        debugWebSocket.sendInfo("发现jetty对象");
        this.jettyObjects.addAll(objectReference);
    }

    @Override
    public boolean analystsObject(VirtualMachine attach) {
        vm = attach;
        handleVersion(attach);
        debugWebSocket.sendInfo("开始分析jetty");
        for (ObjectReference jettyObject : jettyObjects) {
            handleWebAppContextOrServletContext(jettyObject);
        }
        handler.analystsPrefixHandler(vm);
        debugWebSocket.sendSuccess("结束分析jetty");
        return true;
    }

    @Override
    public void setHandleOrPlugin() {
        this.handleOrPlugin = "jetty";
    }

    private void handleWebAppContextOrServletContext(ObjectReference handler) {
        String rawContextPath = getFieldObject(handler, "_contextPath").toString();
        String contextPath = Format.doubleDot(rawContextPath);
        ObjectReference servletHandler = getFieldObject(handler, "_servletHandler");
        ArrayReference servletMappings = (ArrayReference) getFieldObject(servletHandler, "_servletMappings");
        HashMap<String, String> servletAliasName = getServletAliasName(servletHandler);
        handlerServletMappingObject(servletMappings, contextPath, servletAliasName, servletHandler);
    }

    private HashMap<String, String> getServletAliasName(ObjectReference servletHandler) {
        HashMap<String, String> result = new HashMap<>();
        ArrayReference servlets = (ArrayReference) getFieldObject(servletHandler, "_servlets");
        for (Value servlet : servlets.getValues()) {
            ObjectReference servletObject = (ObjectReference) servlet;
            StringReference name = (StringReference) getFieldObject(servletObject, "_name");
            StringReference className = (StringReference) getFieldObject(servletObject, "_className");
            result.put(name.value(), className.value());
        }
        return result;
    }

    private void handlerServletMappingObject(ArrayReference servletMappings, String prefix, HashMap<String, String> classNameMapping, ObjectReference servletHandler) {

        for (Value servletMapping : servletMappings.getValues()) {
            String classNameMap = ((StringReference) getFieldObject((ObjectReference) servletMapping, "_servletName")).value();
            String className = classNameMapping.get(classNameMap);
            ArrayReference pathSpecs = (ArrayReference) getFieldObject((ObjectReference) servletMapping, "_pathSpecs");
            for (Value pathSpec : pathSpecs.getValues()) {
                String rawResult = ((StringReference) pathSpec).value();
                String url = Format.doubleSlash(prefix + rawResult);
                String fullName = Format.doubleSlash(prefix + "/" + url);
                handler.registryPrefix(fullName, className);
                handler.handlerMagicModificationFramework(vm, fullName, className, classNameMap);
                if (!blackList.contains(className)) {
                    maps.put(url, className);
                }
            }
        }
    }

    @Override
    public HashMap<String, String> getDataWrapper() {
        return maps;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void clearData() {
        super.clearData();
        maps = new HashMap<>();
        this.jettyObjects = new HashSet<>();
        version = "";
    }

    @Override
    public Set<String> getDiscoveryClass() {
        return discoveryClass;
    }

    private void handleVersion(VirtualMachine attach) {
        List<ReferenceType> referenceTypes = attach.classesByName("org.eclipse.jetty.util.Jetty");
        for (ReferenceType referenceType : referenceTypes) {
            Field version = referenceType.fieldByName("VERSION");
            StringReference jettyVersion = (StringReference) referenceType.getValue(version);
            this.version = jettyVersion.value();
        }
    }

}