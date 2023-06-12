package com.kyodream.debugger.core.category;

import com.kyodream.debugger.service.DebugWebSocket;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

import java.util.List;
import java.util.Set;

/**
 * 每一个中间件或者插件的抽象类
 * <p>
 * 一个基本逻辑： 执行线程遍历目标JVM所有的对象，每一个AbstractDataWrapper子类需要提供
 * 一些类来用于获取自己想要的对象，这个动作由getDiscoveryClass方法支持，
 * 在遍历完成后，执行线程会把找到的对象注入到每一个中间件或者插件，这个操作由addAnalysisObject提供
 * 执行线程在处理完上述操作之后，会向目标JVM下断点，每一个AbstractDataWrapper都要提供
 * 一些类来给执行线程下断点。
 */
public abstract class AbstractDataWrapper implements DataWrapper {
    private boolean isFind = false;

    @Override
    public boolean isFind() {
        return isFind;
    }

    public void hasFind() {
        isFind = true;
    }

    public void clearFindFlag() {
        isFind = false;
    }

    abstract public Set<String> getDiscoveryClass();


    abstract public void addAnalysisObject(Set<ObjectReference> objectReference);


    public void analystsObject(VirtualMachine attach) {
    }

}
