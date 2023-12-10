package kyodream.analysts;

import com.sun.jdi.*;
import java.util.*;

public class ObjectAnalysts extends ThreadAnalysts {
    protected ObjectAnalysts() {
    }

    public static ObjectAnalysts getObject(ThreadReference thread, Value obj, IPublish publish) {
        ObjectAnalysts analystsObject = new ObjectAnalysts();
        analystsObject.init(obj);
        analystsObject.thread = thread;
        analystsObject.publish = publish;
        return analystsObject;
    }

    public ObjectAnalysts getFieldsRef(String... field) {
        if (objValue == null) {
            return new ObjectAnalysts();
        }
        return ObjectAnalysts.getObject(thread, InvokeUtils.getFieldOneByOne(objValue, publish, field), publish);
    }

    public String getStringField(String fieldName) {
        ObjectAnalysts object = getFieldsRef(fieldName);
        if (object.getRef() instanceof StringReference) {
            return ((StringReference) object.getRef()).value();
        }
        return null;
    }

    /**
     * if objectRef instance String
     *
     * @return null or objectRef String value
     */
    public String getString() {
        if (objValue instanceof StringReference) {
            return ((StringReference) objValue).value();
        }
        return null;
    }

    /**
     * 获取线程当前的局部变量
     *
     * @param index 当前的堆栈的层级，0表示栈顶
     * @return 局部变量
     */
    public LinkedList<ObjectAnalysts> getThreadVariables(int index) {
        try {
            LinkedList<ObjectAnalysts> result = new LinkedList<>();
            StackFrame frame = thread.frame(index);
            List<LocalVariable> localVariables = frame.visibleVariables();
            for (LocalVariable var : localVariables) {
                Value value = frame.getValue(var);
                result.add(ObjectAnalysts.getObject(thread, value, publish));
            }
            return result;
        } catch (AbsentInformationException | IncompatibleThreadStateException e) {
            return new LinkedList<>();
        }
    }

    public ObjectReference getRef() {
        return objValue;
    }

    public ObjectAnalysts invokeMethodDesc(String method, String desc) {
        return InvokeUtils.invokeMethod(this, method, desc, Collections.EMPTY_LIST, publish);
    }

    public ObjectAnalysts invokeMethodDescArgs(String method, String desc, List<Value> args) {
        return InvokeUtils.invokeMethod(this, method, desc, args, publish);
    }

    public ObjectAnalysts invokeMethod(String method) {
        return InvokeUtils.invokeMethodSingleNoArgs(this, method, publish);
    }

    public ObjectAnalysts invokeMethodArgs(String method, List<Value> args) {
        return InvokeUtils.invokeMethodSingle(this, method, args, publish);
    }

    @Override
    public boolean equals(Object target) {
        if (target instanceof ObjectAnalysts) {
            long uniqueID = ((ObjectAnalysts) target).getRef().uniqueID();
            return objValue.uniqueID() == uniqueID;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(objValue.uniqueID());
    }


    public <T extends Value> T getBaseValue(String fieldName){
        Field field = getRef().referenceType().fieldByName(fieldName);
        Value value = getRef().getValue(field);
        return (T) value;
    }


    public static class ObjectIter implements Iterator<ObjectAnalysts> {
        private final ObjectAnalysts[] data;
        private int point;

        public ObjectIter(ObjectAnalysts[] data) {
            this.data = data;
        }

        @Override
        public boolean hasNext() {
            return point < data.length;
        }

        @Override
        public ObjectAnalysts next() {
            ObjectAnalysts result = data[point];
            point++;
            return result;
        }
    }
}
