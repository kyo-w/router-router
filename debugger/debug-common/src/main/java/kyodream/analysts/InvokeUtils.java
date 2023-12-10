package kyodream.analysts;


import com.sun.jdi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.Collections.EMPTY_LIST;

public class InvokeUtils {
    private static final Logger logger = LoggerFactory.getLogger(InvokeUtils.class);

    public static ObjectAnalysts invokeMethod(ObjectAnalysts objRef, String methodName, String desc, List<? extends Value> args, IPublish publish) {
        ThreadReference frameThread = objRef.getFrameThread();
        return ObjectAnalysts.getObject(frameThread, nativeInvokeMethod(frameThread, objRef.getRef(), methodName, desc, args, publish), publish);
    }

    public static ObjectAnalysts invokeMethodSingle(ObjectAnalysts obj, String methodName, List<Value> args, IPublish publish) {
        return ObjectAnalysts.getObject(obj.getFrameThread(), nativeInvokeMethodSingle(obj.getFrameThread(), obj.getRef(), methodName, args, publish), publish);
    }

    public static ObjectAnalysts invokeMethodSingleNoArgs(ObjectAnalysts obj, String methodName, IPublish publish) {
        return ObjectAnalysts.getObject(obj.getFrameThread(), nativeInvokeMethodSingleNoArgs(obj.getFrameThread(), obj.getRef(), methodName, publish), publish);
    }

    public static <T extends Value> T nativeInvokeMethodSingle(ThreadReference thread, ObjectReference objRef, String methodName, List<Value> args, IPublish publish) {
        Method method = objRef.referenceType().methodsByName(methodName).get(0);
        try {
            return (T) objRef.invokeMethod(thread, method, args, ObjectReference.INVOKE_SINGLE_THREADED);
        } catch (Exception e) {
            logger.error("JDI方法调用异常", e);
            publish.debugError(e.getMessage());
            return null;
        }
    }

    public static <T extends Value> T nativeInvokeMethod(ThreadReference thead, ObjectReference objRef, String methodName, String desc, List<? extends Value> args, IPublish publish) {
        Method method = objRef.referenceType().methodsByName(methodName, desc).get(0);
        if (method == null) {
            return null;
        }
        try {
            return (T) objRef.invokeMethod(thead, method, args, ObjectReference.INVOKE_SINGLE_THREADED);
        } catch (Exception e) {
            logger.error("JDI方法调用异常", e);
            publish.debugError(e.getMessage());
            return null;
        }
    }

    public static <T extends Value> T nativeInvokeMethodSingleNoArgs(ThreadReference thread, ObjectReference objRef, String methodName, IPublish publish) {
        return (T) nativeInvokeMethodSingle(thread, objRef, methodName, EMPTY_LIST, publish);
    }

    public static <T extends Value> T getFieldObject(Value value, String fieldString, IPublish publish) {
        Field field = null;
        if (value instanceof ObjectReference) {
            try {
                field = ((ObjectReference) value).referenceType().fieldByName(fieldString);
            } catch (Exception e) {
                String tarObject = value.type().name();
                logger.error(tarObject + "无法找到" + fieldString, e);
                publish.debugError(e.getMessage());
            }
            return (T) ((ObjectReference) value).getValue(field);
        }
        return null;
    }

    public static <T extends Value> T getFieldOneByOne(Value objectReference, IPublish publish, String... fields) {
        ObjectReference tmp = (ObjectReference) objectReference;
        for (String field : fields) {
            tmp = getFieldObject(tmp, field, publish);
            if (tmp == null) {
                return null;
            }
        }
        return (T) tmp;
    }

    public static HashMap<Value, Value> getMap(ThreadReference thread, ObjectReference objRef, IPublish publish) {
        HashMap<Value, Value> result = new HashMap<>();
        ObjectReference keySetRef = nativeInvokeMethodSingleNoArgs(thread, objRef, "keySet", publish);
        if (keySetRef == null) {
            return null;
        }
        ArrayReference elems = (ArrayReference) nativeInvokeMethod(thread, keySetRef, "toArray", "()[Ljava/lang/Object;", EMPTY_LIST, publish);
        for (Value elem : elems.getValues()) {
            ArrayList<Value> args = new ArrayList<>();
            args.add(elem);
            Value value = nativeInvokeMethod(thread, objRef, "get", "(Ljava/lang/Object;)Ljava/lang/Object;", args, publish);
            result.put(elem, value);
        }
        return result;
    }

    public static ArrayReference getMapValue(ThreadReference thread, ObjectReference objRef, IPublish publish) {
        ObjectReference values = nativeInvokeMethodSingleNoArgs(thread, objRef, "values", publish);
        if (values == null) {
            return null;
        }
        return (ArrayReference) nativeInvokeMethod(thread, values, "toArray", "()[Ljava/lang/Object;", EMPTY_LIST, publish);
    }

    public static HashSet<Value> getSet(ThreadReference thread, ObjectReference objRef, IPublish publish) {
        HashSet<Value> result = new HashSet<>();
        ArrayReference value = (ArrayReference) nativeInvokeMethod(thread, objRef, "toArray", "()[Ljava/lang/Object;", EMPTY_LIST, publish);
        result.addAll(value.getValues());
        return result;
    }

    public static ArrayList<Value> getList(ThreadReference thread, ObjectReference objRef, IPublish publish) {
        ArrayList<Value> result = new ArrayList<>();
        ArrayReference value = (ArrayReference) nativeInvokeMethod(thread, objRef, "toArray", "()[Ljava/lang/Object;", EMPTY_LIST, publish);
        result.addAll(value.getValues());
        return result;
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
