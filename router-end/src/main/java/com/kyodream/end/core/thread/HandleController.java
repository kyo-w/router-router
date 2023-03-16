package com.kyodream.end.core.thread;

import com.kyodream.end.core.AnalyseStatus;
import com.kyodream.end.core.BugManger;
import com.kyodream.end.core.category.Handle;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class HandleController implements Runnable {
    private BugManger.VMWrapper vm;
    private Set<Handle> handles;

    private SurveyThread surveyThread;

    public HandleController(BugManger.VMWrapper vm, Set<Handle> handles, SurveyThread surveyThread) {
        this.vm = vm;
        this.handles = handles;
        this.surveyThread = surveyThread;
    }

    @Override
    public void run() {
        new Thread(surveyThread).start();
        EventQueue eventQueue = vm.getVirtualMachine().eventQueue();
        EventSet eventSet = null;
        try {
            while ((eventSet = eventQueue.remove()) != null) {
                vm.setSingleAnalysisCompleted(AnalyseStatus.NO_OK);
                handleEvent(eventSet, vm.getVirtualMachine());
                vm.getVirtualMachine().resume();
                vm.setSingleAnalysisCompleted(AnalyseStatus.OK);
                surveyThread.stop();
            }
        } catch (VMDisconnectedException| NullPointerException e) {
            surveyThread.stop();
            log.warn("连接端口已经关闭");
            vm.setVirtualMachine(null);
            vm.setExist(false);
        }catch (InterruptedException e){
        }
    }

    private void handleEvent(EventSet eventSet, VirtualMachine attach) {
        for (Handle elem : handles) {
            elem.handleEvent(eventSet, attach);
        }
    }
}
