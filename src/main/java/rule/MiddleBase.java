package rule;

import com.sun.jdi.event.MethodEntryEvent;

import java.util.*;

public abstract class MiddleBase implements Handle {

    private List<String> className = new ArrayList<>();
    private Set<String> routerMethod = new HashSet<>();
    private HashMap<String, String> exactMap = new HashMap<>();
    private HashMap<String, String> wildMap = new HashMap<>();
    private HashMap<String, String> extensionMap = new HashMap<>();

    abstract String getTitle();

    //    初始化className/routerMethod变量
    abstract void init();

    //中间件注册了其他的web容器，需要修改Connector连接器设置
    void connectionWild() {
        for (Map.Entry<String, String> elem : getWildMap().entrySet()) {
            if (Connector.record.containsKey(elem.getValue())) {
                Connector.record.get(elem.getValue()).setData(elem.getKey());
                Connector.record.get(elem.getValue()).setType(Type.Wild);
            }
        }
    }
    void connectionExtension(){
        for (Map.Entry<String, String> elem : getWildMap().entrySet()) {
            if (Connector.record.containsKey(elem.getValue())) {
                Connector.record.get(elem.getValue()).setData(elem.getKey());
                Connector.record.get(elem.getValue()).setType(Type.Extension);
            }
        }
    }

    abstract void handleEvent(MethodEntryEvent event);

    @Override
    public void getRouter(MethodEntryEvent event) {
        routerMethod.forEach(elem -> {
            if (event.method().name().equals(elem)) {
                handleEvent(event);
            }
        });
    }

    @Override
    public void clearRouter() {
        exactMap = null;
        wildMap = null;
        extensionMap = null;
        exactMap = new HashMap<>();
        wildMap = new HashMap<>();
        extensionMap = new HashMap<>();
    }

    @Override
    public String[][] wrapperRouter() {
        int length = exactMap.size() + extensionMap.size() + wildMap.size();
        String[][] result = new String[length][3];
        int point = 0;
        for (Map.Entry<String, String> item : exactMap.entrySet()) {
            result[point++] = new String[]{getTitle() + "(exact)", item.getKey(), item.getValue()};
        }

        for (Map.Entry<String, String> item : wildMap.entrySet()) {
            result[point++] = new String[]{getTitle() + "(wild)", item.getKey(), item.getValue()};
        }

        for (Map.Entry<String, String> item : extensionMap.entrySet()) {
            result[point++] = new String[]{getTitle() + "(extension)", item.getKey(), item.getValue()};
        }
        return result;

    }

    @Override
    public List<String> getHookClassName() {
        return className;
    }

    public void registryClassName(List<String> list) {
        this.className = list;
    }

    public void registryMethodName(Set<String> list) {
        this.routerMethod = list;
    }

    public HashMap<String, String> getExactMap() {
        return exactMap;
    }

    public HashMap<String, String> getWildMap() {
        return wildMap;
    }

    public HashMap<String, String> getExtensionMap() {
        return extensionMap;
    }

    public List<String> getClassList() {
        return className;
    }

    public Set<String> getRouterMethod() {
        return routerMethod;
    }
}
