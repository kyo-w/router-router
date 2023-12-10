package kyodream.analysts;

import kyodream.record.ContextRecord;
import kyodream.record.FrameworkRecord;

public interface IPublish {
    //    调试事件出现警告
    public void debugWarn(Object msg);

    //    调试事件出现错误
    public void debugError(Object msg);

    //    调试事件成功提示
    public void debugSuccess(Object msg);

    public void stackMsg(Object msg);

    //    Tomcat/jetty事件
    public void contextMsg(ContextRecord msg);

    //    框架事件
    public void frameworkMsg(FrameworkRecord msg);

    //    servlet初始化异常事件
    public void servletMsg(String servlet);

}
