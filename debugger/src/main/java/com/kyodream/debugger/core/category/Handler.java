package com.kyodream.debugger.core.category;

import com.kyodream.debugger.service.DebugWebSocket;
import com.sun.jdi.ClassType;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Tomcat/Jetty辅助类
 */
@Component
public class Handler {
    @Autowired
    Jersey jersey;

    @Autowired
    SpringMvc springMvc;

    @Autowired
    Struts struts;

    @Autowired
    DebugWebSocket debugWebSocket;

    public void handlerMagicModificationFramework(VirtualMachine vm, String url, String className, String baseName) {
        List<ReferenceType> referenceTypes = vm.classesByName(className);
        referenceTypes.forEach(referenceType -> {
            ClassType superclass = ((ClassType) referenceType).superclass();
            if (superclass != null) {
                if (superclass.name().equals("org.springframework.web.servlet.DispatcherServlet")) {
                    debugWebSocket.sendInfo("spring的类: " + baseName);
                    if (url.endsWith("*")) {
                        springMvc.registryPrefix(url.substring(0, url.length() - 1));
                    } else {
                        springMvc.registryPrefix(url);
                    }
                    springMvc.hasModify();
                } else {
                    handlerMagicModificationFramework(vm, url, superclass.name(), baseName);
                }
            }
        });
    }

    public void analystsPrefixHandler(VirtualMachine virtualMachine){
        if(struts.getPrefix()!= null){
            struts.startAnalysts(virtualMachine);
        }
        if(springMvc.getPrefix() != null){
            springMvc.startAnalysts(virtualMachine);
        }

        if(jersey.getPrefix() != null){
            jersey.startAnalysts(virtualMachine);
        }
    }

    public void registryPrefix(String prefix, String className) {
        if (jersey.getDiscoveryClass().contains(className)) {
            jersey.registryPrefix(prefix);
        }
        if (className.equals("org.apache.struts.action.ActionServlet")) {
            struts.registryPrefix(prefix);
        }
        if (className.equals("org.springframework.web.servlet.DispatcherServlet")) {
            springMvc.registryPrefix(prefix);
        }
    }
}
