package com.kyodream.debugger.core;

import com.kyodream.debugger.core.thread.DebuggerThread;
import com.sun.jdi.VirtualMachine;
import org.springframework.stereotype.Component;

@Component
public class TaskThreadManager {
    private DebuggerThread debuggerThread;
    private boolean flag = false;

    public boolean beginAnalysts(DebugManger debugManger) {
        if (debuggerThread == null) {
            debuggerThread = new DebuggerThread(debugManger);
            new Thread(debuggerThread).start();
            flag = true;
            return true;
        } else {
            return false;
        }
    }

    public boolean hasStart() {
        return flag;
    }

    public void stopTask(DebugManger debugManger) {
        debuggerThread.stopDebug();
        debuggerThread = null;
        flag = false;
        VirtualMachine virtualMachine = debugManger.getVirtualMachine();
        if (virtualMachine != null) {
            virtualMachine.suspend();
            virtualMachine.eventRequestManager().deleteAllBreakpoints();
            virtualMachine.resume();
            virtualMachine.dispose();
        }
    }
}
