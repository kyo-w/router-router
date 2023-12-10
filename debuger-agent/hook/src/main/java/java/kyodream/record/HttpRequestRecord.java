package java.kyodream.record;

import java.util.LinkedList;


/**
 * 每一个请求的记录
 */
public class HttpRequestRecord {
    public String url;
    public String method;
    public LinkedList<String> filterList;
    public String Servlet;

    public StackTrace[] stackTraces;

    public HttpRequestRecord(String url, String method, LinkedList<String> filterList, String servlet) {
        this.url = url;
        this.method = method;
        this.filterList = filterList;
        Servlet = servlet;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public LinkedList<String> getFilterList() {
        return filterList;
    }

    public void setFilterList(LinkedList<String> filterList) {
        this.filterList = filterList;
    }

    public String getServlet() {
        return Servlet;
    }

    public void setServlet(String servlet) {
        Servlet = servlet;
    }

    public StackTrace[] getStackTraces() {
        return stackTraces;
    }

    public void setStackTraces(StackTrace[] stackTraces) {
        this.stackTraces = stackTraces;
    }
}
