package kyodream.breakpoint;


import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.BreakpointEvent;
import kyodream.analysts.ObjectAnalysts;
import kyodream.analysts.IPublish;

public interface BreakPointHandler {

    /**
     * @param breakpointEvent current breakpoint
     * @param thread          breakpoint thread
     */
    public void handler(BreakpointEvent breakpointEvent, ThreadReference thread, IPublish publish);

    default ObjectAnalysts getThisObject(ThreadReference threadReference, IPublish publish) {
        try {
            ObjectReference objectReference = threadReference.frame(0).thisObject();
            return ObjectAnalysts.getObject(threadReference, objectReference, publish);
        } catch (IncompatibleThreadStateException e) {
            throw new RuntimeException(e);
        }
    }
}
