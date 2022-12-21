package debugger;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.tools.jdi.SocketAttachingConnector;
import logger.LogOutput;
import monitor.MonitorComponent;
import rule.*;
import rule.Spring;
import utils.StatusInfo;

import javax.swing.*;
import java.io.IOException;
import java.util.*;

public class DebuggerCore {
    private String hostname;
    private String port;
    private LogOutput stdout;
    private JLabel status;
    private VirtualMachine attach;
    private List<Handle> handle = new ArrayList<>();
    private MonitorComponent monitor;

    public DebuggerCore(String hostname, String port, LogOutput logOutput, JLabel status, MonitorComponent monitor) {
        this.hostname = hostname;
        this.port = port;
        this.stdout = logOutput;
        this.status =status;
        this.monitor = monitor;
        initHandle();
    }

    public void initHandle(){
        handle.add(new Tomcat());
        handle.add(new Spring());
        handle.add(new Jersey());
        handle.add(new Jetty());
        handle.add(new Resin());
        handle.add(new Struts());
    }

    public VirtualMachine initConnect(){
        SocketAttachingConnector socketAttachingConnector = new SocketAttachingConnector();
        Map<String, Connector.Argument> argumentHashMap = socketAttachingConnector.defaultArguments();
        argumentHashMap.get("hostname").setValue(hostname);
        argumentHashMap.get("port").setValue(port);
        argumentHashMap.get("timeout").setValue("3000");
        VirtualMachine attach = null;
        try {
            attach = socketAttachingConnector.attach(argumentHashMap);
        } catch (IllegalConnectorArgumentsException | IOException e) {
        }

        this.attach = attach;
        return attach;
    }

    public boolean start() {
        try {
            attach.suspend();
        }catch (NullPointerException e){
            status.setText(StatusInfo.WAIT_CONNECTION);
            stdout.printSocketInfo("无法连接", hostname, port);
            return false;
        }
//      清空连接器记录
        rule.Connector.clearRouter();
//      清空路由表记录
        handle.forEach(handleSingle ->{
            handleSingle.clearRouter();
        });

        EventRequestManager eventRequestManager = attach.eventRequestManager();
        setFilterClass(eventRequestManager);
        attach.resume();

        stdout.printSocketInfo("已连接成功!需要发起请求触发断点", hostname, port);
        status.setText(StatusInfo.WAIT_POINT);
        EventQueue eventQueue = attach.eventQueue();
        EventSet eventSet = null;
        while (true){
            try {
//            等待断点连接
                EventSet remove = eventQueue.remove();
                status.setText(StatusInfo.CONNECTION);
                stdout.printSocketInfo("开始收集", hostname, port);
                handleEvent(remove, attach);

//            遍历堆栈信息
                while ((eventSet = eventQueue.remove(2000)) != null) {
                    handleEvent(eventSet, attach);
                }
            } catch (InterruptedException e) {
                stdout.printSocketInfo("debug结束", hostname, port);
            }
            stdout.printSocketInfo("收集完成", hostname, port);
            stdout.printSocketInfo("等待下一个路由断点", hostname, port);
            monitor.refresh(getWrapperData());
        }
    }

    public void handleEvent(EventSet eventSet, VirtualMachine attach){
        for(Event event: eventSet){
            if(event instanceof MethodEntryEvent){
                handle.forEach(handleSingle->{
                    handleSingle.getRouter((MethodEntryEvent) event);
                });
            }
            attach.resume();
        }
    }

    /**
     * 添加Class断点
     * @param methodEntryRequest
     */
    public void setFilterClass(EventRequestManager methodEntryRequest){
        Iterator<Handle> iterator = handle.iterator();
        while (iterator.hasNext()){
            Handle next = iterator.next();
            List<String> hookClassName = next.getHookClassName();
            for(String className: hookClassName) {
                MethodEntryRequest entryRequestMethodEntryRequest = methodEntryRequest.createMethodEntryRequest();
                entryRequestMethodEntryRequest.addClassFilter(className);
                entryRequestMethodEntryRequest.enable();
            }
        }
    }



    public String[][] getWrapperData(){
        String[][] result = new String[][]{};
        Iterator<Handle> iterator = handle.iterator();
        while (iterator.hasNext()){
            Handle next = iterator.next();
            String[][] strings = next.wrapperRouter();
            result = unite(result, strings);
        }
        return result;
    }

    public static String[][] unite(String[][] os1, String[][] os2) {
        List<String[]> list = new ArrayList<String[]>(Arrays.<String[]>asList(os1));
        list.addAll(Arrays.<String[]>asList(os2));
        return list.toArray(os1);
    }
}
