package router;

import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import router.context.Context;
import router.handler.BreakPointHandler;
import router.breakpoint.BreakPointUtils;
import com.sun.jdi.VirtualMachine;

import java.util.Iterator;

/**
 * DebugCore作为核心驱动
 */
public class DebugCore implements Runnable {
    private VirtualMachine vm;
    private Context context;
    private volatile boolean stop = false; // 退出标志

    public DebugCore() {
    }

    public static DebugCore build(VirtualMachine vm, Context context) {
        DebugCore debugCore = new DebugCore();
        debugCore.vm = vm;
        debugCore.context = context;
        return debugCore;
    }


    /**
     * registryHandler 注册处理器
     *
     * @param className  监听类名
     * @param methodName 监听类名的具体方法
     * @param handler    监视后的事件处理接口
     */
    public void registryHandler(String className, String methodName, BreakPointHandler handler) {
        BreakPointUtils.createMethodBreakPointFront(vm, className, methodName, handler);
    }

    @Override
    public void run() {
        while (!stop) {
            EventQueue eventQueue = vm.eventQueue();
            EventSet events = null;
            try {
                events = eventQueue.remove();
            } catch (Exception e) {
                if (!stop) {
                    continue;
                }
            }
            assert events != null;
            for (Event event : events) {
                if (event instanceof BreakpointEvent) {
                    BreakPointHandler handler = (BreakPointHandler) event.request().getProperty("handler");
                    handler.handler((BreakpointEvent) event, ((BreakpointEvent) event).thread(), context);
                    vm.resume();
                }
            }
        }
    }

    public void stopTask() {
        stop = true;
    }
}
