package kyodream.analysts;

import com.sun.jdi.*;
import kyodream.record.ContextRecord;
import kyodream.record.FrameworkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThreadAnalysts implements Value {
    private final Logger logger = LoggerFactory.getLogger(ObjectAnalysts.class);
    protected ObjectReference objValue;
    protected ByteValue byteValue;

    protected BooleanValue booleanValue;

    protected CharValue charValue;

    protected DoubleValue doubleValue;

    protected FloatValue floatValue;

    protected IntegerValue integerValue;
    protected LongValue longValue;

    protected ShortValue shortValue;

    protected VoidValue voidValue;

    protected ThreadReference thread;

    protected IPublish publish;

    protected Type type;

    public ThreadAnalysts() {
    }

    protected void init(Value objTarget) {
        if (objTarget == null) {
            return;
        }
        this.type = objTarget.type();
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

    public ThreadReference getFrameThread() {
        return thread;
    }

    public boolean isObject() {
        return objValue != null;
    }


    /**
     * objectRef isEmpty
     *
     * @return
     */
    public boolean isEmpty() {
        return type == null;
    }

    /**
     * objectRef className
     *
     * @return className
     */
    public String className() {
        return type.name();
    }

    /**
     * @param parentClassName
     * @return
     */
    public boolean isEqualsOrInstanceof(String parentClassName) {
        if (className().equals(parentClassName)) {
            return true;
        }
        return InvokeUtils.isInstance(parentClassName, objValue);
    }

    public boolean isImplementOf(String interfaceName) {
        return InvokeUtils.isImplementOf(interfaceName, objValue);
    }

    public ObjectAnalysts getCurrentThreadClassLoader() {
        ClassType referenceType = (ClassType) objValue.virtualMachine().classesByName("java.lang.Thread").get(0);
        Method method = referenceType.methodsByName("currentThread").get(0);

        try {
            ObjectAnalysts threadObject = ObjectAnalysts.getObject(thread, referenceType.invokeMethod(thread, method, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED), publish);
            return threadObject.invokeMethod("getContextClassLoader");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void changeThreadClassLoader(ObjectAnalysts threadClassLoader) {
        ClassType referenceType = (ClassType) objValue.virtualMachine().classesByName("java.lang.Thread").get(0);
        Method method = referenceType.methodsByName("currentThread").get(0);
        try {
            ObjectAnalysts threadObject = ObjectAnalysts.getObject(thread, referenceType.invokeMethod(thread, method, Collections.EMPTY_LIST, ObjectReference.INVOKE_SINGLE_THREADED), publish);
            ArrayList<Value> args = new ArrayList<>();
            args.add(threadClassLoader.getRef());
            threadObject.invokeMethodArgs("setContextClassLoader", args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPackageVersion(String className) {
        List<ReferenceType> referenceTypes = virtualMachine().classesByName(className);
        if (referenceTypes == null || referenceTypes.isEmpty()) {
            return null;
        }
        ObjectAnalysts classObjectRef = ObjectAnalysts.getObject(thread, referenceTypes.get(0).classObject(), publish);
        ObjectAnalysts packageRef = classObjectRef.invokeMethod("getPackage");
        if (packageRef.isEmpty()) {
            return null;
        }
        ObjectAnalysts versionRef = packageRef.invokeMethod("getImplementationVersion");
        if (versionRef != null && versionRef.getRef() instanceof StringReference) {
            return ((StringReference) versionRef.getRef()).value();
        }
        return null;
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public VirtualMachine virtualMachine() {
        return thread.virtualMachine();
    }

    public void debugWarn(Object msg) {
        String info = objValue + msg.toString();
        logger.debug("{}", info);
        publish.debugWarn(info);
    }

    public void debugError(Object msg) {
        String info = objValue + msg.toString();
        logger.error("{}", info);
        publish.debugError(info);
    }

    public void debugSuccess(Object msg) {
        String info = objValue + msg.toString();
        logger.info("{}", info);
        publish.debugSuccess(info);
    }

    public void failServlet(String servlet) {
        debugWarn(servlet + "方法回滚");
        publish.servletMsg(servlet);
    }

    public void stackMsg(Object msg) {
        publish.stackMsg(msg);
    }

    public void contextMsg(ContextRecord contextRecord) {
        publish.contextMsg(contextRecord);
    }

    public void frameworkMsg(FrameworkRecord msg) {
        publish.frameworkMsg(msg);
    }
}
