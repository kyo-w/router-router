package com.kyodream.debugger.core.category;

import com.kyodream.debugger.service.DebugWebSocket;
import com.kyodream.debugger.service.DiscoverWebSocket;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.VirtualMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 任何中间件和框架的抽象父类
 * 处理的方法都从startAnalysts方法开始
 */
@Component
public abstract class DefaultHandlerFramework extends HandlerFrameworkAncestor {

    protected Set<ObjectReference> analystsObject = new HashSet<>();
    @Autowired
    DebugWebSocket debugWebSocket;

    @Autowired
    DiscoverWebSocket discoverWebSocket;

    /**
     * 中间件分析对象添加
     * @param objectReferences
     * @throws Exception
     */
    public void addAnalystsObjectSet(Set<ObjectReference> objectReferences){
        discoverWebSocket.discoveryMiddlewareOrFramework(handleOrFrameworkName);
    }

    /**
     * 框架分析对象添加方法
     * @param objectReference
     * @throws Exception
     */
    public void addAnalystsObject(ObjectReference objectReference){
        discoverWebSocket.discoveryMiddlewareOrFramework(handleOrFrameworkName);
    }

    public Set<ObjectReference> getAnalystsObject() {
        return this.analystsObject;
    }

    /**
     * 中间件分析处理方法
     * @param vm
     * @throws Exception
     */
    abstract public void analystsHandlerObject(VirtualMachine vm) throws Exception;

    /**
     * 框架分析处理方法
     * @param vm
     * @return
     */
    abstract public boolean analystsFrameworkObject(VirtualMachine vm);

    abstract public void startAnalysts(VirtualMachine vm);

    protected ObjectReference getFieldObject(ObjectReference objectReference, String fieldString) {
        ObjectReference result = null;
        try {
            Field field = objectReference.referenceType().fieldByName(fieldString);
            result = (ObjectReference) objectReference.getValue(field);
        } catch (Exception e) {
            debugWebSocket.sendFail(this.getName() + ": " + fieldString + "字段获取失败");
        }
        return result;
    }


    /**
     * 负责data/find/complete/analysts的记录清空
     */
    public void clearAny() {
        this.clearData();
        this.clearAllFlag();
        this.analystsObject.clear();
        discoverWebSocket.cleanCache();
    }

}
