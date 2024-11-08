package router.analysts;

import com.sun.jdi.*;
import router.except.FieldExcept;
import router.except.MethodExcept;

import java.util.*;

public class ObjAnalysts implements IAnalysts {
    public Value rawValue;
    public ObjectReference objValue;
    public ByteValue byteValue;
    public BooleanValue booleanValue;
    public CharValue charValue;
    public DoubleValue doubleValue;
    public FloatValue floatValue;
    public IntegerValue integerValue;
    public LongValue longValue;
    public ShortValue shortValue;
    public VoidValue voidValue;
    public ThreadReference thread;
    public Type type;

    protected ObjAnalysts() {
    }

    public static IAnalysts parseObject(ThreadReference thread, Value obj) {
        ObjAnalysts analystsObject = new ObjAnalysts();
        analystsObject.init(obj);
        analystsObject.thread = thread;
        return analystsObject;
    }

    protected void init(Value objTarget) {
        if (objTarget == null) {
            return;
        }
        this.type = objTarget.type();
        this.rawValue = objTarget;
        if (objTarget instanceof ObjectReference) {
            this.objValue = (ObjectReference) objTarget;
        }
        if (objTarget instanceof VoidValue) {
            this.voidValue = (VoidValue) objTarget;
        }
        if (objTarget instanceof ByteValue) {
            this.byteValue = (ByteValue) objTarget;
        }
        if (objTarget instanceof BooleanValue) {
            this.booleanValue = (BooleanValue) objValue;
        }
        if (objTarget instanceof CharValue) {
            this.charValue = (CharValue) objTarget;
        }
        if (objTarget instanceof IntegerValue) {
            this.integerValue = (IntegerValue) objTarget;
        }
        if (objTarget instanceof FloatValue) {
            this.floatValue = (FloatValue) objTarget;
        }
        if (objTarget instanceof DoubleValue) {
            this.doubleValue = (DoubleValue) objTarget;
        }
        if (objTarget instanceof LongValue) {
            this.longValue = (LongValue) objTarget;
        }
        if (objTarget instanceof ShortValue) {
            this.shortValue = (ShortValue) objTarget;
        }
    }

    public Value getRawValue() {
        return rawValue;
    }

    public boolean isObjRef() {
        return objValue == null;
    }

    public IAnalysts getObjFields(String... field) throws FieldExcept {
        if (objValue == null) {
            return new ObjAnalysts();
        }
        return ObjAnalysts.parseObject(thread, InvokeUtils.getFieldOneByOne(objValue, field));
    }

    public String getClassName() {
        return type.name();
    }

    public ThreadReference getThreadRawRef() {
        return thread;
    }

    public boolean isImplementOf(String interfaceName) {
        return !InvokeUtils.isImplementOf(interfaceName, objValue);
    }

    public String getPackageVersion(String className) {
        List<ReferenceType> referenceTypes = thread.virtualMachine().classesByName(className);
        if (referenceTypes == null || referenceTypes.isEmpty()) {
            return null;
        }
        IAnalysts classObjectRef = ObjAnalysts.parseObject(thread, referenceTypes.get(0).classObject());
        ObjAnalysts packageRef = (ObjAnalysts) classObjectRef.invokeMethod("getPackage");
        if (packageRef.isObjRef()) {
            return null;
        }
        ObjAnalysts versionRef = (ObjAnalysts) packageRef.invokeMethod("getImplementationVersion");
        if (versionRef != null && versionRef.rawValue instanceof StringReference) {
            return ((StringReference) versionRef.rawValue).value();
        }
        return null;
    }

    public boolean isInstanceof(String parentClassName) {
        if (getClassName().equals(parentClassName)) {
            return true;
        }
        return InvokeUtils.isInstance(parentClassName, objValue);
    }

    public boolean existFieldByName(String... field) {
        try {
            return InvokeUtils.getFieldOneByOne(objValue, field) != null;
        } catch (FieldExcept e) {
            return false;
        }
    }

    public String getStrFields(String... fieldNames) throws FieldExcept {
        IAnalysts object = getObjFields(fieldNames);
        if (object.getRawValue() instanceof StringReference) {
            return ((StringReference) object.getRawValue()).value();
        }
        throw new FieldExcept("Field " + fieldNames[fieldNames.length - 1] + " is not a StringReference");
    }

    @Override
    public Integer getIntFields(String... fieldNames) throws FieldExcept {
        IAnalysts object = getObjFields(fieldNames);
        if (object.getRawValue() instanceof IntegerValue) {
            return ((IntegerValue) object.getRawValue()).value();
        }
        throw new FieldExcept("Field " + fieldNames[fieldNames.length - 1] + " is not a IntegerValue");
    }

    @Override
    public byte getByteFields(String... fieldNames) throws FieldExcept {
        IAnalysts object = getObjFields(fieldNames);
        if (object.getRawValue() instanceof ByteValue) {
            return ((ByteValue) object.getRawValue()).value();
        }
        throw new FieldExcept("Field " + fieldNames[fieldNames.length - 1] + " is not a ByteValue");
    }

    @Override
    public boolean getBoolFields(String... fieldNames) throws FieldExcept {
        IAnalysts object = getObjFields(fieldNames);
        if (object.getRawValue() instanceof BooleanValue) {
            return ((BooleanValue) object.getRawValue()).value();
        }
        throw new FieldExcept("Field " + fieldNames[fieldNames.length - 1] + " is not a BooleanValue");
    }

    @Override
    public char getCharFields(String... fieldNames) throws FieldExcept {
        IAnalysts object = getObjFields(fieldNames);
        if (object.getRawValue() instanceof CharValue) {
            return ((CharValue) object.getRawValue()).value();
        }
        throw new FieldExcept("Field " + fieldNames[fieldNames.length - 1] + " is not a CharValue");
    }

    @Override
    public double getDoubleFields(String... fieldNames) throws FieldExcept {
        IAnalysts object = getObjFields(fieldNames);
        if (object.getRawValue() instanceof DoubleValue) {
            return ((DoubleValue) object.getRawValue()).value();
        }
        throw new FieldExcept("Field " + fieldNames[fieldNames.length - 1] + " is not a DoubleValue");
    }

    @Override
    public float getFloatFields(String... fieldNames) throws FieldExcept {
        IAnalysts object = getObjFields(fieldNames);
        if (object.getRawValue() instanceof FloatValue) {
            return ((FloatValue) object.getRawValue()).value();
        }
        throw new FieldExcept("Field " + fieldNames[fieldNames.length - 1] + " is not a FloatValue");
    }

    @Override
    public long getLongFields(String... fieldNames) throws FieldExcept {
        IAnalysts object = getObjFields(fieldNames);
        if (object.getRawValue() instanceof LongValue) {
            return ((LongValue) object.getRawValue()).value();
        }
        throw new FieldExcept("Field " + fieldNames[fieldNames.length - 1] + " is not a LongValue");
    }

    @Override
    public short getShortFields(String... fieldNames) throws FieldExcept {
        IAnalysts object = getObjFields(fieldNames);
        if (object.getRawValue() instanceof ShortValue) {
            return ((ShortValue) object.getRawValue()).value();
        }
        throw new FieldExcept("Field " + fieldNames[fieldNames.length - 1] + " is not a ShortValue");
    }

    public String convertToString() throws Exception {
        if (objValue instanceof StringReference) {
            return ((StringReference) objValue).value();
        }
        throw new Exception(rawValue.toString() + " can't convert to string");
    }

    public IAnalysts invokeMethodDesc(String method, String desc) {
        return InvokeUtils.invokeMethod(this, method, desc, Collections.EMPTY_LIST);
    }

    public IAnalysts invokeMethodDescArgs(String method, String desc, List<Value> args) {
        return InvokeUtils.invokeMethod(this, method, desc, args);
    }

    public IAnalysts invokeMethod(String method) throws MethodExcept {
        return InvokeUtils.invokeMethodSingleNoArgs(this, method);
    }

    public IAnalysts invokeMethodArgs(String method, List<Value> args) throws MethodExcept {
        return InvokeUtils.invokeMethodSingle(this, method, args);
    }

    public IAnalysts getCurrentThreadClassLoader() throws Exception {
        ClassType referenceType = (ClassType) objValue.virtualMachine().classesByName("java.lang.Thread").get(0);
        Method method = referenceType.methodsByName("currentThread").get(0);
        IAnalysts threadObject = null;
        try {
            threadObject = ObjAnalysts.parseObject(thread, referenceType.invokeMethod(thread, method, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return threadObject.invokeMethod("getContextClassLoader");
    }

    public Long getId(){
        return objValue.uniqueID();
    }

    public void changeThreadClassLoader(IAnalysts threadClassLoader) throws Exception {
        ClassType referenceType = (ClassType) objValue.virtualMachine().classesByName("java.lang.Thread").get(0);
        Method method = referenceType.methodsByName("currentThread").get(0);
        try {
            IAnalysts threadObject = ObjAnalysts.parseObject(thread, referenceType.invokeMethod(thread, method, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED));
            ArrayList<Value> args = new ArrayList<>();
            args.add(threadClassLoader.getRawValue());
            threadObject.invokeMethodArgs("setContextClassLoader", args);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public boolean equals(Object target) {
        if (target instanceof ObjAnalysts) {
            long uniqueID = ((ObjAnalysts) target).objValue.uniqueID();
            return objValue.uniqueID() == uniqueID;
        }
        return false;
    }

    public int hashCode() {
        return Long.hashCode(objValue.uniqueID());
    }

    public static class ObjectIter implements Iterator<IAnalysts> {
        private final IAnalysts[] data;
        private int point;

        public ObjectIter(IAnalysts[] data) {
            this.data = data;
        }

        @Override
        public boolean hasNext() {
            return point < data.length;
        }

        @Override
        public IAnalysts next() {
            IAnalysts result = data[point];
            point++;
            return result;
        }
    }
}
