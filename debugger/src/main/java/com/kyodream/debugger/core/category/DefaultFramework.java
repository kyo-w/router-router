package com.kyodream.debugger.core.category;


import com.sun.jdi.ObjectReference;
import com.sun.jdi.VirtualMachine;

import java.util.Set;

/**
 * 负责框架的处理类：每一个框架都需要一个前缀
 */
public abstract class DefaultFramework extends DefaultHandlerFramework {
    private String prefix = null;

    public String getPrefix() {
        return prefix;
    }

    public void registryPrefix(String prefix) {
        this.prefix = prefix;
    }


    @Override
    public void addAnalystsObjectSet(Set<ObjectReference> objectReferences) throws Exception {
        throw new Exception("框架不支持集合操作");
    }
    @Override
    public void addAnalystsObject(ObjectReference objectReference) {
        if (objectReference != null) {
            handlerFindAnalystsObject();
        }
        debugWebSocket.sendInfo("发现" + this.getName() + "对象");
        boolean ifAddSuccess = this.analystsObject.add(objectReference);
        if(ifAddSuccess){
            analystsHasChangeFlag();
        }
    }

    @Override
    public void analystsHandlerObject(VirtualMachine vm) throws Exception {
        throw new Exception("框架无法处理中间件");
    }

    @Override
    abstract public boolean analystsFrameworkObject(VirtualMachine vm);

    @Override
    public void startAnalysts(VirtualMachine vm) {
        this.clearCompleteFlag();
        if(analystsFrameworkObject(vm)){
            this.completeAnalysts();
        }
    }

    /**
     * 负责prefix/data/flag/complete/analysts的记录清空
     */
    @Override
    public void clearAny() {
        this.prefix = null;
        super.clearAny();
    }
}
