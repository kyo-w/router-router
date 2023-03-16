package com.kyodream.end.analyse;

import com.sun.jdi.*;

import java.util.ArrayList;
import java.util.Collections;

public class Utils {
    /**
     * call method of toString of target object
     * @param thread current thread
     * @param objectReference target object
     * @return result of call, type: String
     */
    public static String getString(ThreadReference thread, ObjectReference objectReference) {
        Value value = null;
        try {
            Method toString = objectReference.referenceType().methodsByName("toString").get(0);
            value = objectReference.invokeMethod(thread, toString, Collections.EMPTY_LIST, 0);
        }catch (Exception e){
            e.printStackTrace();
        }
        return value.toString();
    }

    /**
     * get field object of Target Object
     * @param objectReference target Object
     * @param fieldString field name
     * @return field object
     */
    public static ObjectReference getFieldObject(ObjectReference objectReference, String fieldString){
        Field field = objectReference.referenceType().fieldByName(fieldString);
        return (ObjectReference) objectReference.getValue(field);
    }


    //Class.forName
//    Object.method()

    public static ObjectReference invokeStaticMethod(ThreadReference thread, VirtualMachine attach, String className, String methodName){
        classforName(thread, attach, className);
        ClassType referenceType = (ClassType) attach.classesByName(className).get(0);
        Method getVersion = referenceType.methodsByName(methodName).get(0);
        ObjectReference result = null;
        try {
            result = (ObjectReference) referenceType.invokeMethod(thread, getVersion, Collections.EMPTY_LIST, 0);
        } catch (InvalidTypeException e) {
            throw new RuntimeException(e);
        } catch (ClassNotLoadedException e) {
            throw new RuntimeException(e);
        } catch (IncompatibleThreadStateException e) {
            throw new RuntimeException(e);
        } catch (InvocationException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    public static ObjectReference getStaticField(ThreadReference thread, VirtualMachine attach, String className, String field){
        classforName(thread, attach, className);
        ClassType referenceType = (ClassType) attach.classesByName(className).get(0);

        Field field1 = referenceType.fieldByName(field);
        ObjectReference result = (ObjectReference) referenceType.getValue(field1);
        return result;
    }

    private static void classforName(ThreadReference thread, VirtualMachine attach, String className){
        ClassType referenceType = (ClassType) attach.classesByName("java.lang.Class").get(0);
        Method forName = referenceType.methodsByName("forName").get(0);
        StringReference stringReference = attach.mirrorOf(className);
        ArrayList<ObjectReference> objectReferences = new ArrayList<>();
        objectReferences.add(stringReference);
        try {
            ClassObjectReference value = (ClassObjectReference) referenceType.invokeMethod(thread,
                    forName, objectReferences, 0);
        } catch (InvalidTypeException e) {
            throw new RuntimeException(e);
        } catch (ClassNotLoadedException e) {
            throw new RuntimeException(e);
        } catch (IncompatibleThreadStateException e) {
            throw new RuntimeException(e);
        } catch (InvocationException e) {
            throw new RuntimeException(e);
        }
    }
}
