package com.kyodream.end.core.category;

import com.kyodream.end.analyse.*;
import com.kyodream.end.core.category.format.Format;
import com.kyodream.end.core.category.format.StrutsFormat;
import com.sun.jdi.ArrayReference;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.Value;
import com.sun.jdi.event.MethodEntryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Forwarded by tomcat
 */
@Component
@Slf4j
public class Struts extends AbstractDataWrapper {

    private HashMap<String, String> maps = new HashMap();

    private String modulePatternName = "";


    /**
     * 根据struts的设计，从tomcat中的filter链找到struts拦截器
     * Find the filter chain in tomcat
     *
     * @param methodEntryEvent
     * @param root
     */
    public void handleStruts(MethodEntryEvent methodEntryEvent, ObjectReference root) {
        ObjectReference filterConfigs = Utils.getFieldObject(root, "filterConfigs");


        Map<ObjectReference, ObjectReference> hashMapObject = HashMapAnalyse.getHashMapObject(methodEntryEvent.thread(), filterConfigs);
        Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
            ObjectReference value = next.getValue();
            String string = Utils.getString(methodEntryEvent.thread(), value);
            if (string.contains("org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter")) {
                hasFind();
                log.info("Struts2 exist");
                //        获取匹配模式
                findMouedles(methodEntryEvent, root, Format.doubleDot(next.getKey().toString()));
                getUrlAndClass(methodEntryEvent, value);
            }
        }
    }

    private void findMouedles(MethodEntryEvent methodEntryEvent, ObjectReference root, String name) {
        ObjectReference filterMaps = Utils.getFieldObject(root, "filterMaps");
        ArrayReference array = (ArrayReference) Utils.getFieldObject(filterMaps, "array");
        for (int i = 0; i < array.length(); i++) {
            ObjectReference value = (ObjectReference) array.getValue(i);
            String string = Utils.getString(methodEntryEvent.thread(), value);
            if (string.contains(name)) {
                ArrayReference urlPatterns = (ArrayReference) Utils.getFieldObject(value, "urlPatterns");
                modulePatternName = Format.doubleDot(urlPatterns.getValue(0).toString());
            }
        }
    }

    /**
     * 复杂的参数处理，主要完成namespace/url/className的关系
     *
     * @param methodEntryEvent
     * @param value
     */
    private void getUrlAndClass(MethodEntryEvent methodEntryEvent, ObjectReference value) {
        ObjectReference filter = Utils.getFieldObject(value, "filter");
        ObjectReference prepare = Utils.getFieldObject(filter, "prepare");
        ObjectReference dispatcher = Utils.getFieldObject(prepare, "dispatcher");
        ObjectReference configurationManager = Utils.getFieldObject(dispatcher, "configurationManager");
        ObjectReference configuration = Utils.getFieldObject(configurationManager, "configuration");
        ObjectReference packageContexts = Utils.getFieldObject(configuration, "packageContexts");
        Map<ObjectReference, ObjectReference> hashMapObject = HashMapAnalyse.getHashMapObject(methodEntryEvent.thread(), packageContexts);
        Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
            ObjectReference value1 = next.getValue();

            String rawNamespace = Utils.getFieldObject(value1, "namespace").toString();
            String namespace = StrutsFormat.doubleDot(rawNamespace);

            ObjectReference actionConfigs = Utils.getFieldObject(value1, "actionConfigs");
            Map<ObjectReference, ObjectReference> hashMapObject1 = HashMapAnalyse.getHashMapObject(methodEntryEvent.thread(), actionConfigs);
            Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator1 = hashMapObject1.entrySet().iterator();
            while (iterator1.hasNext()) {
                Map.Entry<ObjectReference, ObjectReference> next1 = iterator1.next();
                String rawUrl = next1.getKey().toString();
                String url = StrutsFormat.doubleDot(rawUrl);
                ObjectReference value2 = next1.getValue();
                String rawClassName = Utils.getFieldObject(value2, "className").toString();
                String className = StrutsFormat.doubleDot(rawClassName);
                maps.put(namespace + url, className);
            }
        }
    }

    @Override
    public HashMap<String, String> getDataWrapper() {
        return maps;
    }

    @Override
    public String getVersion() {
        return "";
    }

    @Override
    public void clearData() {
        clearFindFlag();
        this.maps = new HashMap<>();
    }

    public String getModulePatternName() {
        return this.modulePatternName;
    }
}
