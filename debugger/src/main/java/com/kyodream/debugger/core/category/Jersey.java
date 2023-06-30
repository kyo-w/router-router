package com.kyodream.debugger.core.category;

import com.kyodream.debugger.core.analyse.MapAnalyse;
import com.kyodream.debugger.core.analyse.SetAnalyse;
import com.kyodream.debugger.core.category.format.Format;
import com.kyodream.debugger.service.DebugWebSocket;
import com.sun.jdi.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Forwarded by tomcat
 * 不是中间件，所以不实现Handle接口
 * It is not middleware, so it does not implement the Handle interface
 */
@Component
@Slf4j
public class Jersey extends AbstractDataWrapper implements Plugin {
    public static HashMap jerseyUrl = new HashMap();
    private Set<ObjectReference> jerseyObjects = new HashSet<>();

    private String prefix = null;

    @Autowired
    private DebugWebSocket debugWebSocket;

    @Override
    public void addAnalysisObject(Set<ObjectReference> objectReference) {
        if(objectReference.size() > 0){
            hasFind();
        }
        debugWebSocket.sendInfo("发现jersey插件对象");
        jerseyObjects.addAll(objectReference);
    }

    @Override
    public boolean analystsObject(VirtualMachine attach) {
        if (prefix == null) {
            debugWebSocket.sendInfo("未分析获取jersey前缀,跳过");
            return false;
        }
        debugWebSocket.sendInfo("开始分析jersey");
        for (ObjectReference jerseyObject : jerseyObjects) {
            try {
                if (jerseyObject.referenceType().name().equals("org.glassfish.jersey.servlet.ServletContainer")) {
                    debugWebSocket.sendInfo("当前插件版本为jersey2.x");
                    handleJersey2(jerseyObject);
                } else if (jerseyObject.referenceType().name().equals("com.sun.jersey.spi.container.servlet.ServletContainer")) {
                    debugWebSocket.sendInfo("当前插件版本为jersey1.x");
                    handleJersey(jerseyObject);
                }
            } catch (Exception e) {
                debugWebSocket.sendFail("jersey解析异常");
                e.printStackTrace();
            }
        }
        debugWebSocket.sendSuccess("分析jersey完成");
        return true;
    }

    @Override
    public void setHandleOrPlugin() {
        this.handleOrPlugin = "jersey";
    }

    private void handleJersey(ObjectReference jerseyObject) {
        ObjectReference webComponent = getFieldObject(jerseyObject, "webComponent");
        ObjectReference application = getFieldObject(webComponent, "application");
        ObjectReference abstractRootResources = getFieldObject(application, "abstractRootResources");
        ArrayList<ObjectReference> resources = SetAnalyse.getUnmodifiableSet(abstractRootResources);
        for (ObjectReference resource : resources) {
            ObjectReference resourceClass = getFieldObject(resource, "resourceClass");
            String className = ((StringReference) getFieldObject(resourceClass, "name")).value();
            ObjectReference uriPath = getFieldObject(resource, "uriPath");
            String url = ((StringReference) getFieldObject(uriPath, "value")).value();
            String replace = prefix.replace("*", "/" + url);
            jerseyUrl.put(Format.doubleSlash(replace), className);
        }
    }

    @Override
    public HashMap<String, String> getDataWrapper() {
        return jerseyUrl;
    }

    @Override
    public String getVersion() {
        return "未知版本";
    }

    @Override
    public void clearData() {
        super.clearData();
        jerseyUrl = new HashMap();
        jerseyObjects = new HashSet<>();
        prefix = null;
    }

    @Override
    public Set<String> getDiscoveryClass() {
        HashSet<String> result = new HashSet<>();
        result.add("org.glassfish.jersey.servlet.ServletContainer");
        result.add("com.sun.jersey.spi.container.servlet.ServletContainer");
        return result;
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public void registryPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void handleJersey2(ObjectReference root) {
        /**
         *  jersey2的内部结构
         *  webComponent
         *      appHandler
         *         cachedClasses(HashSet)  包含每一个路由对应的路由对象
         */
        ObjectReference webComponent = getFieldObject(root, "webComponent");
        ObjectReference appHandler = getFieldObject(webComponent, "appHandler");
        ObjectReference application = getFieldObject(appHandler, "application");
        ObjectReference cachedClasses = getFieldObject(application, "cachedClasses");
        ArrayList<ObjectReference> classesList = SetAnalyse.getHashSetObject(cachedClasses);
        classesList.stream().forEach(classObject -> {
            try {
                handleUrlMapping(classObject);
            } catch (Exception e) {
                debugWebSocket.sendFail("jersey解析url异常");
                e.printStackTrace();
            }
        });
    }

    private void handleUrlMapping(ObjectReference classObject) {
        String className = ((StringReference) getFieldObject(classObject, "name")).value();
        String rawUrl = "";
        ObjectReference annotationData = getFieldObject(classObject, "annotationData");
        ObjectReference annotations = getFieldObject(annotationData, "annotations");
        Map<ObjectReference, ObjectReference> hashMapObject = null;
        try {
            hashMapObject = MapAnalyse.getHashMapObject(annotations);
        } catch (Exception e) {
            debugWebSocket.sendInfo(className + "类中不存在路由注解!(请自行检查)");
        }
        if (hashMapObject != null) {
            Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator = hashMapObject.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ObjectReference, ObjectReference> next = iterator.next();
                ObjectReference annotationName = next.getKey();
                ObjectReference urlObject = next.getValue();
                String annotationNameString = ((StringReference) getFieldObject(annotationName, "name")).value();

                /**
                 * annotations子节点的结构
                 *  h
                 *      memberValues
                 [k,v]   (value = 'url')
                 */
                if (annotationNameString.equals("javax.ws.rs.Path")) {
                    Field h = urlObject.referenceType().fieldByName("h");
                    ObjectReference invocationHandler = (ObjectReference) urlObject.getValue(h);
                    ObjectReference memberValues = getFieldObject(invocationHandler, "memberValues");
                    Map<ObjectReference, ObjectReference> kvObject = MapAnalyse.getHashMapObject(memberValues);
                    Iterator<Map.Entry<ObjectReference, ObjectReference>> iterator1 = kvObject.entrySet().iterator();
                    while (iterator1.hasNext()) {
                        Map.Entry<ObjectReference, ObjectReference> next1 = iterator1.next();
                        StringReference keyObject = (StringReference) next1.getKey();
                        if (keyObject.value().equals("value")) {
                            rawUrl = ((StringReference) next1.getValue()).value();
                        }
                    }
                }
            }
            String replace = prefix.replace("*", "/" + rawUrl + "(/*?)");
            jerseyUrl.put(Format.doubleSlash(replace), className);
        }
    }

}
