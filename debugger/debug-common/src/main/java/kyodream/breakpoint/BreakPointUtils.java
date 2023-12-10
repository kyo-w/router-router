package kyodream.breakpoint;

import com.sun.jdi.*;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;

import java.util.List;

public class BreakPointUtils {
    private static boolean internalCreateMethodBreakPoint(VirtualMachine vm, MethodPoint methodPoint, int point) {
        String className = methodPoint.getClassName();
        String methodName = methodPoint.getMethodName();
        BreakPointHandler handler = methodPoint.getHandler();

        List<ReferenceType> referenceTypes = vm.classesByName(className);
        if (referenceTypes.isEmpty()) {
            return false;
        }
        ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
        classPrepareRequest.addClassFilter(className);
        classPrepareRequest.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
        classPrepareRequest.enable();

        for (ReferenceType elem : referenceTypes) {
            List<Method> methods = elem.methodsByName(methodName);
            if (!methods.isEmpty()) {
                Method method = methods.get(0);
                try {
                    List<Location> locations = method.allLineLocations();
                    Location location = null;
                    if (point == 0) {
                        location = locations.get(0);
                        BreakpointRequest breakpointRequest = vm.eventRequestManager().createBreakpointRequest(location);
                        breakpointRequest.putProperty("handler", handler);
                        breakpointRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                        breakpointRequest.enable();
                    } else if (point == 1) {
                        location = locations.get(locations.size() - 1);
                        BreakpointRequest breakpointRequest = vm.eventRequestManager().createBreakpointRequest(location);
                        breakpointRequest.putProperty("handler", handler);
                        breakpointRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                        breakpointRequest.enable();
                        location = locations.get(0);
                    } else {
                        Location first = locations.get(0);
                        Location last = locations.get(locations.size() - 1);
                        BreakpointRequest breakpointRequest = vm.eventRequestManager().createBreakpointRequest(first);
                        breakpointRequest.putProperty("handler", handler);
                        breakpointRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                        breakpointRequest.enable();
                        breakpointRequest = vm.eventRequestManager().createBreakpointRequest(last);
                        breakpointRequest.putProperty("handler", handler);
                        breakpointRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                        breakpointRequest.enable();
                    }
                } catch (AbsentInformationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }

    public static boolean createMethodBreakPointEnd(VirtualMachine virtualMachine, String className, String methodName, BreakPointHandler handler) {
        MethodPoint methodPoint = new MethodPoint(className, methodName, handler);
        if (methodPoint != null) {
            return internalCreateMethodBreakPoint(virtualMachine, methodPoint, 1);
        }
        return true;
    }

    public static boolean createMethodBreakPointFront(VirtualMachine virtualMachine, String className, String methodName, BreakPointHandler handler) {
        MethodPoint methodPoint = new MethodPoint(className, methodName, handler);
        if (methodPoint != null) {
            return internalCreateMethodBreakPoint(virtualMachine, methodPoint, 0);
        }
        return true;
    }

    public static boolean createMethodBreakPointFrontEnd(VirtualMachine virtualMachine, String className, String methodName, BreakPointHandler handler) {
        MethodPoint methodPoint = new MethodPoint(className, methodName, handler);
        if (methodPoint != null) {
            return internalCreateMethodBreakPoint(virtualMachine, methodPoint, -1);
        }
        return true;
    }
}
