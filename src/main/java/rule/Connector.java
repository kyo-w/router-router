package rule;

import java.util.HashMap;
import java.util.Map;

/**
 * 有些路由是由tomcat + 第三方web容器
 * 路由： tomcat映射路由 + web容器自身路由
 */
public class Connector {
    public static HashMap<String, RecordData> record = new HashMap<>();

    static {
//        Jersey REST服务
        record.put("com.sun.jersey.spi.container.servlet.ServletContainer", new RecordData());
//      Tomcat整合spring框架
        record.put("org.springframework.web.servlet.DispatcherServlet", new RecordData());
        record.put("org.apache.struts.action.ActionServlet", new RecordData());
    }

    public static void clearRouter() {
        for(Map.Entry<String, RecordData> elem: record.entrySet()){
            elem.setValue(new RecordData());
        }
    }
}

class RecordData {
    private String data;
    private Type type;

    public RecordData() {
        this.data = "";
        this.type = Type.NULL;
    }

    public RecordData(Type type, String data) {
        this.type = type;
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public Type getType() {
        return type;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setType(Type type) {
        this.type = type;
    }
}

enum Type {
    Wild, Extension, NULL
}