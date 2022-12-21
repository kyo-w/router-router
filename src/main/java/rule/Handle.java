package rule;

import com.sun.jdi.event.MethodEntryEvent;

import java.util.List;

public interface Handle {

    /**
     * 每一个容器都存在一个路由映射表
     */
    void getRouter(MethodEntryEvent event);

    /**
     * 处理旧路由表
     */
    void clearRouter();

    /**
     * 返回路由表
     *
     * {
     *     {"container", "api", "className"}
     * }
     * @return
     */
    String[][] wrapperRouter();


    /**
     * 每个断点都需要Hook指定的类
     * @return
     */
    List<String> getHookClassName();
}
