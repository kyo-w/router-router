package com.kyodream.debugger.core.thread;

import com.kyodream.debugger.core.DebugManger;
import com.kyodream.debugger.core.common.StackHandler;
import com.kyodream.debugger.core.context.JettyHandler;
import com.kyodream.debugger.core.context.TomcatHandler;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import kyodream.analysts.IPublish;
import kyodream.breakpoint.BreakPointHandler;
import kyodream.breakpoint.BreakPointUtils;
import kyodream.map.DebugClass;
import kyodream.map.AnalystsType;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class DebuggerThread implements Runnable {
    private final IPublish publishService;
    private final VirtualMachine vm;
    private boolean flag = true;

    public DebuggerThread(DebugManger debugManger) {
        this.publishService = debugManger.publisher;
        this.vm = debugManger.getVirtualMachine();
    }

    @Override
    public void run() {
//        初始化断点
        initMiddleWareBreakPoint(vm);
//        初始化
        while (flag) {
            EventQueue eventQueue = vm.eventQueue();
            EventSet events = null;
            try {
                events = eventQueue.remove();
            } catch (Exception e) {
                if (flag) {
                    throw new RuntimeException(e);
                }
            }
            assert events != null;
            Iterator<Event> iterator = events.iterator();
            while (iterator.hasNext()) {
                Event event = iterator.next();
                if (event instanceof BreakpointEvent) {
                    BreakPointHandler handler = (BreakPointHandler) event.request().getProperty("handler");
                    handler.handler((BreakpointEvent) event, ((BreakpointEvent) event).thread(), publishService);
                    vm.resume();
                }
            }
        }
    }

    public void stopDebug() {
        flag = false;
    }

    private void initMiddleWareBreakPoint(VirtualMachine vm) {
        BreakPointUtils.createMethodBreakPointFront(vm, DebugClass.TOMCAT_9.className(), "internalMap", TomcatHandler.getInstance());
        BreakPointUtils.createMethodBreakPointEnd(vm, DebugClass.JETTY_WEB.className(), "doScope", JettyHandler.getInstance());
//        BreakPointUtils.createMethodBreakPointFrontEnd(vm, DebugClass.TOMCAT_STACK_REQUEST.className(), "invoke", StackHandler.getInstance());
//        BreakPointUtils.createMethodBreakPointFront(vm, DebugClass.TOMCAT_STACK_FILTER.className(), "internalDoFilter", StackHandler.getInstance());
    }
}