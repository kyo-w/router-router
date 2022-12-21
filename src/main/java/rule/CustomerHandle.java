package rule;

import com.sun.jdi.event.MethodEntryEvent;

import java.util.List;


/**
 * 自定义的路由解析器
 */
public class CustomerHandle implements Handle {

    /**
     * 核心方法，在设置完类断点后，如何解析断点的信息
     * @param event
     */
    @Override
    public void getRouter(MethodEntryEvent event) {

    }

    /**
     * 每次socket请求都会清理一次路由表缓存
     */
    @Override
    public void clearRouter() {

    }

    /**
     * 用于将路由表封装到table中
     * @return
     */
    @Override
    public String[][] wrapperRouter() {
        return new String[0][];
    }


    /**
     * 每一个容器都存在一个路由表，方法返回路由表查询必经过的class类
     * @return
     */
    @Override
    public List<String> getHookClassName() {
        return null;
    }
}
