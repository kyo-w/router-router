package com.kyodream.end;

import com.kyodream.end.analyse.HashMapAnalyse;
import com.kyodream.end.analyse.SetAnalyse;
import com.kyodream.end.analyse.Utils;
import com.sun.jdi.*;
import com.sun.jdi.event.MethodEntryEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class Jersey {

    public static final String JerseyName = "org.glassfish.jersey.servlet.ServletContainer";

    public Boolean isJersey(MethodEntryEvent methodEntryEvent, ObjectReference root) throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        Boolean isJersey = false;
        ObjectReference initializers = Utils.getFieldObject(root, "initializers");
        Map<ObjectReference, ObjectReference> hashMapObject = HashMapAnalyse.getHashMapObject(methodEntryEvent.thread(), initializers);
        Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
            ObjectReference key = next.getKey();
            Method getClass = key.referenceType().methodsByName("getClass").get(0);
            String className = key.invokeMethod(methodEntryEvent.thread(), getClass, Collections.EMPTY_LIST, 0).toString();
            if(className.contains("org.glassfish.jersey.servlet.init.JerseyServletContainerInitializer")){
                isJersey = true;
            }
        }
        return isJersey;
    }

    public void handleJersey(MethodEntryEvent methodEntryEvent, ObjectReference root, String prefix) throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        ObjectReference initializers = Utils.getFieldObject(root, "initializers");
        Map<ObjectReference, ObjectReference> hashMapObject = HashMapAnalyse.getHashMapObject(methodEntryEvent.thread(), initializers);
        Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
            ObjectReference key = next.getKey();
            Method getClass = key.referenceType().methodsByName("getClass").get(0);
            String className = key.invokeMethod(methodEntryEvent.thread(), getClass, Collections.EMPTY_LIST, 0).toString();
            if(className.contains("org.glassfish.jersey.servlet.init.JerseyServletContainerInitializer")){
                handleUrlMapping(methodEntryEvent, next.getValue(), prefix);
            }
        }

    }

    private void handleUrlMapping(MethodEntryEvent event, ObjectReference root, String prefix) throws ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        ArrayList<ObjectReference> setObject = SetAnalyse.getSetObject(event.thread(), root);
        for(ObjectReference elem: setObject){
            String className = Utils.getFieldObject(elem, "name").toString().replace("\"", "");
            ObjectReference annotationData = Utils.getFieldObject(elem, "annotationData");
            ObjectReference declaredAnnotations = Utils.getFieldObject(annotationData, "declaredAnnotations");
            Map<ObjectReference, ObjectReference> hashMapObject = HashMapAnalyse.getHashMapObject(event.thread(), declaredAnnotations);
            Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
                ObjectReference value = next.getValue();
                Method toString = value.referenceType().methodsByName("toString").get(0);
                String url = value.invokeMethod(event.thread(), toString, Collections.EMPTY_LIST, 0).toString().replace("\"", "");
                System.out.println(prefix + url);
                System.out.println(className);
            }
        }
    }
}
