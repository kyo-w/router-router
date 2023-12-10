package com.kyodream.debugger.core.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.BreakpointEvent;
import kyodream.analysts.ArrayAnalysts;
import kyodream.analysts.IPublish;
import kyodream.analysts.MapAnalysts;
import kyodream.analysts.ObjectAnalysts;
import kyodream.breakpoint.BreakPointHandler;

import java.util.*;


public class StackHandler implements BreakPointHandler {

    private StackHandler() {
    }

    //    当前堆栈
    public HashMap<ThreadReference, StackInfo> stackMap = new HashMap<>();

    public ArrayAnalysts filterRefCache;

    private static StackHandler instance = new StackHandler();
    private ObjectMapper mapper = new ObjectMapper();

    public static StackHandler getInstance() {
        return instance;
    }

    @Override
    public void handler(BreakpointEvent breakpointEvent, ThreadReference thread, IPublish publish) {
        try {
            ObjectAnalysts thisObject = getThisObject(thread, publish);
            if (stackMap.get(thread) == null) {
                recordRequest(thisObject);
            } else {
//                if (thisObject.className().equals("org.apache.catalina.core.ApplicationFilterChain")) {
//                    recordFilterAndServlet(thisObject);
//                }
                if (thisObject.className().equals("org.apache.catalina.core.StandardWrapperValve")) {
                    sendRequest(thisObject);
                }
            }
        } catch (Exception e) {
            stackMap.remove(thread);
            filterRefCache = null;
            e.printStackTrace();
        }
    }

    private void sendRequest(ObjectAnalysts thisObject) {
        filterRefCache = null;
        StackInfo stackInfo = stackMap.get(thisObject.getFrameThread());
        try {
            System.out.println(mapper.writeValueAsString(stackInfo));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        stackMap.remove(thisObject.getFrameThread());
    }

    private void recordFilterAndServlet(ObjectAnalysts thisObject) {
        IntegerValue posRef = thisObject.getBaseValue("pos");
        IntegerValue endRef = thisObject.getBaseValue("n");
        int value = posRef.value();
        int end = endRef.value();
        if (value < end) {
            StackInfo stackInfo = stackMap.get(thisObject.getFrameThread());
            filterRefCache = new ArrayAnalysts(thisObject.getFieldsRef("filters"));
            if (stackInfo.getFilters() == null) {
                stackInfo.setFilters(new LinkedList<>());
            }
            if (stackInfo.getServlets() == null) {
                stackInfo.setServlets(new HashSet<>());
            }
            String filterName = filterRefCache.get(value).getFieldsRef("filter").className();
            String servletName = thisObject.getFieldsRef("servlet").className();
            stackInfo.getServlets().add(servletName);
            stackInfo.getFilters().add(filterName);
        }
    }

    private void recordRequest(ObjectAnalysts thisObject) {
        ////        栈顶第一个变量
        ObjectAnalysts requestRef = thisObject.getThreadVariables(0).get(0);
        StackInfo stackInfo = new StackInfo();
        try {
            //        请求方法
            String method = requestRef.invokeMethod("getMethod").getString();
            stackInfo.setMethod(method);
//        请求路由
            String url = requestRef.invokeMethod("getRequestURI").getString();
            stackInfo.setUrl(url);
//        请求参数
            MapAnalysts.Entry[] maps = new MapAnalysts(requestRef.invokeMethod("getParameterMap")).getKV();
            HashMap<String, String[]> requestParams = getRequestParams(maps);
            stackInfo.setQueryParams(requestParams);

//        请求头
            ObjectAnalysts headersRef = requestRef.invokeMethod("getHeaderNames").getFieldsRef("headers", "headers");
            HashMap<String, String> requestHeaders = getRequestHeaders(headersRef);
            stackInfo.setHeaders(requestHeaders);
            stackMap.put(thisObject.getFrameThread(), stackInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> getRequestHeaders(ObjectAnalysts headersRef) {
        HashMap<String, String> result = new HashMap<>();
        ArrayAnalysts objectAnalysts = new ArrayAnalysts(headersRef);
        for (ObjectAnalysts headerRef : objectAnalysts) {
            String key = headerRef.getFieldsRef("nameB").invokeMethod("toString").getString();
            if (key != null) {
                String value = headerRef.getFieldsRef("valueB").invokeMethod("toString").getString();
                result.put(key, value);
            }
        }
        return result;
    }

    private HashMap<String, String[]> getRequestParams(MapAnalysts.Entry[] maps) {
        HashMap<String, String[]> result = new HashMap<>();
        for (MapAnalysts.Entry elem : maps) {
            String headerKey = elem.getKey().getString();
            ArrayList<String> headerValues = new ArrayList<>();
            for (ObjectAnalysts headerValueRef : new ArrayAnalysts(elem.getValue())) {
                headerValues.add(headerValueRef.getString());
            }
            result.put(headerKey, headerValues.toArray(String[]::new));
        }
        return result;
    }


    /**
     * 堆栈信息
     */
    public class StackInfo {
        private String url;
        private String method;
        private HashMap<String, String[]> queryParams;
        private HashMap<String, String> headers;
        //        private HashMap<String, String> stacks;
        private LinkedList<String> filters;
        private HashSet<String> servlets;

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

        public HashMap<String, String[]> getQueryParams() {
            return queryParams;
        }

        public void setQueryParams(HashMap<String, String[]> queryParams) {
            this.queryParams = queryParams;
        }

        public HashMap<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(HashMap<String, String> headers) {
            this.headers = headers;
        }

//        public HashMap<String, String> getStacks() {
//            return stacks;
//        }
//
//        public void setStacks(HashMap<String, String> stacks) {
//            this.stacks = stacks;
//        }

        public LinkedList<String> getFilters() {
            return filters;
        }

        public void setFilters(LinkedList<String> filters) {
            this.filters = filters;
        }

        public HashSet<String> getServlets() {
            return servlets;
        }

        public void setServlets(HashSet<String> servlets) {
            this.servlets = servlets;
        }
    }
}
