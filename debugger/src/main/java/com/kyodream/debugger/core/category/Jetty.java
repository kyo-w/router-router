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
public class Jetty extends DefaultHandler {
    private static Set<String> blackList = new HashSet<String>();

    static {
        blackList.add("org.eclipse.jetty.servlet.NoJspServlet");
    }

    private VirtualMachine vm;


    @Override
    public void analystsHandlerObject(VirtualMachine vm) {
        this.vm = vm;
        handleVersion(vm);
        debugWebSocket.sendInfo("开始分析jetty");
        for (ObjectReference jettyObject : getAnalystsObject()) {
            handleWebAppContextOrServletContext(jettyObject);
        }
        debugWebSocket.sendSuccess("结束分析jetty");
    }

    @Override
    public void setHandleOrFrameworkName() {
        this.handleOrFrameworkName = "jetty";
    }

    private void handleWebAppContextOrServletContext(ObjectReference handler) {
        String rawContextPath = getFieldObject(handler, "_contextPath").toString();
        String contextPath = Format.doubleDot(rawContextPath);
        ObjectReference servletHandler = getFieldObject(handler, "_servletHandler");
        ArrayReference servletMappings = (ArrayReference) getFieldObject(servletHandler, "_servletMappings");
        debugWebSocket.sendInfo("jetty存在" + servletMappings.length() + "条路由分析记录");
        HashMap<String, ObjectReference> classNameObjectRef = new HashMap<>();
        HashMap<String, String> servletAliasName = getServletAliasName(servletHandler, classNameObjectRef);
        handlerServletMappingObject(servletMappings, contextPath, servletAliasName, classNameObjectRef);
    }

    private HashMap<String, String> getServletAliasName(ObjectReference servletHandler, HashMap<String, ObjectReference> classNameObjectRef) {
        HashMap<String, String> result = new HashMap<>();
        ArrayReference servlets = (ArrayReference) getFieldObject(servletHandler, "_servlets");
        for (Value servlet : servlets.getValues()) {
            ObjectReference servletObject = (ObjectReference) servlet;
            StringReference name = (StringReference) getFieldObject(servletObject, "_name");
            StringReference className = (StringReference) getFieldObject(servletObject, "_className");
            ObjectReference servletRef = getFieldObject(servletObject, "_servlet");
            ObjectReference servletInstanceRef = getFieldObject(servletRef, "_servlet");
            classNameObjectRef.put(className.value(), servletInstanceRef);
            result.put(name.value(), className.value());
        }
        return result;
    }

    private void handlerServletMappingObject(ArrayReference servletMappings,
                                             String prefix,
                                             HashMap<String, String> classNameMapping,
                                             HashMap<String, ObjectReference> classNameObjectRef) {
        for (Value servletMapping : servletMappings.getValues()) {
            String classNameMap = ((StringReference) getFieldObject((ObjectReference) servletMapping, "_servletName")).value();
            String className = classNameMapping.get(classNameMap);
            ArrayReference pathSpecs = (ArrayReference) getFieldObject((ObjectReference) servletMapping, "_pathSpecs");
            for (Value pathSpec : pathSpecs.getValues()) {
                String rawResult = ((StringReference) pathSpec).value();
                String url = Format.doubleSlash(prefix + rawResult);
                String fullName = Format.doubleSlash(prefix + "/" + url);
                RegistryType registryType = registryPrefix(fullName, className);
                if(registryType != RegistryType.None){
                    registryAnalystsObject(registryType, classNameObjectRef.get(className));
                }
//                boolean find = handlerMagicModificationFramework(vm, fullName, className, className);
//                if(find){
//                    springMvc.addAnalystsObject(classNameObjectRef.get(className));
//                }
                if (!blackList.contains(className)) {
                    getDataWrapper().put(url, className);
                }
            }
        }
    }

    @Override
    public Set<String> getDiscoveryClass() {
        if(this.discoveryClass.size() == 0) {
            discoveryClass.add("org.eclipse.jetty.webapp.WebAppContext");
            discoveryClass.add("org.eclipse.jetty.servlet.ServletContextHandler");
        }
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