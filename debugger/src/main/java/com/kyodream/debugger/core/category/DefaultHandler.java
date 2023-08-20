package com.kyodream.debugger.core.category;

import com.sun.jdi.ClassType;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 中间件的处理类：负责为框架类注册路由前缀以及过滤器的处理
 */
@Component
public abstract class DefaultHandler extends DefaultHandlerFramework {
    @Autowired
    Jersey jersey;
    @Autowired
    SpringMvc springMvc;
    @Autowired
    Struts struts;

    @Autowired
    Filter filter;

    protected Set<String> discoveryClass = new HashSet<>();

    public void addAnalystsObjectSet(Set<ObjectReference> objectReference) {
        if (objectReference.size() > 0) {
            handlerFindAnalystsObject();
        }
        debugWebSocket.sendInfo("发现" + this.getName() + "对象");
        boolean analystsObjectChange = this.analystsObject.addAll(objectReference);
        if (analystsObjectChange) {
            this.analystsHasChangeFlag();
        }
        boolean filterChange = filter.analystsObject.addAll(objectReference);
        if (filterChange) {
            filter.analystsHasChangeFlag();
        }
    }

    @Override
    public void addAnalystsObject(ObjectReference objectReference) throws Exception {
        throw new Exception("中间件不支持单个对象注册");
    }

    abstract public void analystsHandlerObject(VirtualMachine vm);

    @Override
    public boolean analystsFrameworkObject(VirtualMachine vm) {
        return false;
    }

    @Override
    public void startAnalysts(VirtualMachine vm) {
        this.clearCompleteFlag();
        debugWebSocket.sendInfo("开始分析" + getName());
        this.analystsHandlerObject(vm);
        analystsPrefixHandler(vm);
        filter.startAnalysts(vm);
        debugWebSocket.sendInfo("结束分析" + getName());
        this.completeAnalysts();
    }

    abstract public Set<String> getDiscoveryClass();

    public void analystsPrefixHandler(VirtualMachine virtualMachine) {
        if (struts.getPrefix() != null) {
            struts.startAnalysts(virtualMachine);
        }
        if (springMvc.getPrefix() != null) {
            springMvc.startAnalysts(virtualMachine);
        }

        if (jersey.getPrefix() != null) {
            jersey.startAnalysts(virtualMachine);
        }
    }

    public RegistryType registryPrefix(String prefix, String className) {
        if (className.equals("org.glassfish.jersey.servlet.ServletContainer") || className.equals("com.sun.jersey.spi.container.servlet.ServletContainer")) {
            jersey.registryPrefix(prefix);
            return RegistryType.Jersey;
        }
        if (className.equals("org.apache.struts.action.ActionServlet")) {
            struts.registryPrefix(prefix);
            return RegistryType.Struts;
        }
        if (className.equals("org.springframework.web.servlet.DispatcherServlet")) {
            springMvc.registryPrefix(prefix);
            return RegistryType.SpringMvc;
        }
        return RegistryType.None;
    }

    public void registryAnalystsObject(RegistryType registryType, ObjectReference instance) {
        if (registryType == RegistryType.Jersey) {
            jersey.addAnalystsObject(instance);
        }
        if (registryType == RegistryType.Struts) {
            struts.addAnalystsObject(instance);
        }

        if (registryType == RegistryType.SpringMvc) {
            springMvc.addAnalystsObject(instance);
        }
    }

    enum RegistryType {
        SpringMvc,
        Jersey,
        Struts,
        None
    }
}
