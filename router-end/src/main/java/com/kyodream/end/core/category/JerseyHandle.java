package com.kyodream.end.core.category;
import com.sun.jdi.*;
import com.sun.jdi.event.MethodEntryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.kyodream.end.core.category.format.*;
import com.kyodream.end.analyse.*;

import java.util.*;

/** Forwarded by tomcat
 * 不是中间件，所以不实现Handle接口
 * It is not middleware, so it does not implement the Handle interface
 */
@Component
@Slf4j
public class JerseyHandle extends AbstractDataWrapper{
    public static final String JerseyName = "org.glassfish.jersey.servlet.ServletContainer";

    public static HashMap jerseyUrl = new HashMap();

    public static Set<String> blackList = new HashSet<>();

    static {
        blackList.add("org.glassfish.jersey.server.wadl.internal.WadlResource");
    }

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
                hasFind();
                log.info("Jersey Handle");
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
                String rawUrl = value.invokeMethod(event.thread(), toString, Collections.EMPTY_LIST, 0).toString().replace("\"", "");
                String jerseyUrl = JerseyFormat.getJerseyUrl(rawUrl);
                if(!blackList.contains(className)) {
                    JerseyHandle.jerseyUrl.put(prefix + jerseyUrl, className);
                }
            }
        }
    }
    @Override
    public HashMap<String, String> getDataWrapper() {
        return jerseyUrl;
    }
    @Override
    public String getVersion() {
        return "";
    }
    @Override
    public void clearData() {
        clearFindFlag();
        jerseyUrl = new HashMap();
    }

}
