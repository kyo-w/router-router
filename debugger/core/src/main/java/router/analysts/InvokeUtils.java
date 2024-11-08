package router.analysts;


import com.sun.jdi.*;
import router.except.FieldExcept;
import router.except.MethodExcept;

import java.util.*;

import static java.util.Collections.EMPTY_LIST;

public class InvokeUtils {
    public static IAnalysts invokeMethod(ObjAnalysts objRef, String methodName, String desc, List<? extends Value> args) throws MethodExcept {
        ThreadReference frameThread = objRef.getThreadRawRef();
        return ObjAnalysts.parseObject(frameThread,
                nativeInvokeMethod(objRef.getThreadRawRef(), objRef.objValue, methodName, desc, args));
    }

    public static IAnalysts invokeMethodSingle(ObjAnalysts obj, String methodName, List<Value> args) throws MethodExcept {
        return ObjAnalysts.parseObject(obj.getThreadRawRef(), nativeInvokeMethodSingle(obj.getThreadRawRef(), obj.objValue, methodName, args));
    }

    public static IAnalysts invokeMethodSingleNoArgs(ObjAnalysts obj, String methodName) throws MethodExcept {
        return ObjAnalysts.parseObject(obj.getThreadRawRef(), nativeInvokeMethodSingleNoArgs(obj.getThreadRawRef(), obj.objValue, methodName));
    }

    public static Value nativeInvokeMethodSingle(ThreadReference thread, ObjectReference objRef, String methodName, List<Value> args) throws MethodExcept {
        Method method = objRef.referenceType().methodsByName(methodName).get(0);
        try {
            return objRef.invokeMethod(thread, method, args, ObjectReference.INVOKE_SINGLE_THREADED);
        } catch (Exception e) {
            throw new MethodExcept(methodName);
        }
    }

    public static Value nativeInvokeMethod(ThreadReference thead, ObjectReference objRef, String methodName, String desc, List<? extends Value> args) throws MethodExcept {
        Method method = objRef.referenceType().methodsByName(methodName, desc).get(0);
        if (method == null) {
            return null;
        }
        try {
            return objRef.invokeMethod(thead, method, args, ObjectReference.INVOKE_SINGLE_THREADED);
        } catch (Exception e) {
            throw new MethodExcept(methodName);
        }
    }

    public static Value nativeInvokeMethodSingleNoArgs(ThreadReference thread, ObjectReference objRef, String methodName) throws MethodExcept {
        return nativeInvokeMethodSingle(thread, objRef, methodName, EMPTY_LIST);
    }

    public static Value getFieldObject(ObjectReference value, String fieldString) throws FieldExcept {
        Field field = null;
        try {
            field = value.referenceType().fieldByName(fieldString);
        } catch (Exception e) {
            throw new FieldExcept(value, fieldString);
        }
        return value.getValue(field);
    }

    public static Value getFieldOneByOne(Value objectReference, String... fields) throws FieldExcept {
        ObjectReference tmp = (ObjectReference) objectReference;
        for (String field : fields) {
            tmp = (ObjectReference) getFieldObject(tmp, field);
            if (tmp == null) {
                return null;
            }
        }
        return tmp;
    }

    public static HashMap<Value, Value> getMap(ThreadReference thread, ObjectReference objRef) throws MethodExcept {
        HashMap<Value, Value> result = new HashMap<>();
        Value keySetRef = nativeInvokeMethodSingleNoArgs(thread, objRef, "keySet");
        if (keySetRef == null) {
            return null;
        }
        Value elems = nativeInvokeMethod(thread, (ObjectReference) keySetRef, "toArray", "()[Ljava/lang/Object;", EMPTY_LIST);
        if (elems == null) {
            return null;
        }
        for (Value elem : ((ArrayReference) elems).getValues()) {
            ArrayList<Value> args = new ArrayList<>();
            args.add(elem);
            Value value = nativeInvokeMethod(thread, objRef, "get", "(Ljava/lang/Object;)Ljava/lang/Object;", args);
            result.put(elem, value);
        }
        return result;
    }

    public static ArrayReference getMapValue(ThreadReference thread, ObjectReference objRef) throws MethodExcept {
        Value values = nativeInvokeMethodSingleNoArgs(thread, objRef, "values");
        if (values == null) {
            return null;
        }
        return (ArrayReference) nativeInvokeMethod(thread, (ObjectReference) values, "toArray", "()[Ljava/lang/Object;", EMPTY_LIST);
    }

    public static HashSet<Value> getSet(ThreadReference thread, ObjectReference objRef) throws MethodExcept {
        HashSet<Value> result = new HashSet<>();
        ArrayReference value = (ArrayReference) nativeInvokeMethod(thread, objRef, "toArray", "()[Ljava/lang/Object;", EMPTY_LIST);
        result.addAll(value.getValues());
        return result;
    }

    public static List<Value> getList(ThreadReference thread, ObjectReference objRef) throws MethodExcept {
        ArrayReference value = (ArrayReference) nativeInvokeMethod(thread, objRef, "toArray", "()[Ljava/lang/Object;", EMPTY_LIST);
        if (value == null) {
            return null;
        }
        return value.getValues();
    }

    public static boolean isInstance(String parentClassName, ObjectReference obj) {
        String subClassName = obj.referenceType().name();
        List<ReferenceType> referenceTypes = obj.virtualMachine().classesByName(parentClassName);
        for (ReferenceType referenceType : referenceTypes) {
            if (((ClassType) referenceType).subclasses().stream().anyMatch(e -> e.name().equals(subClassName))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isImplementOf(String interfaceName, ObjectReference obj) {
        if (obj == null) {
            return false;
        }
        List<ReferenceType> referenceTypes = obj.virtualMachine().classesByName(obj.referenceType().name());
        for (ReferenceType referenceType : referenceTypes) {
            if (((ClassType) referenceType).allInterfaces().stream().anyMatch(i -> i.name().equals(interfaceName))) {
                return true;
            }
        }
        return false;
    }
}
