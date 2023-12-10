package com.kyodream.debugger.core;

import com.kyodream.debugger.core.thread.DebuggerThread;
import com.kyodream.debugger.pojo.DebuggerArgs;
import com.kyodream.debugger.service.AbstractWS;
import com.kyodream.debugger.service.ContextWebSocket;
import com.kyodream.debugger.service.DebugWebSocket;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.tools.jdi.SocketAttachingConnector;
import kyodream.map.AnalystsType;
import kyodream.record.ContextRecord;
import kyodream.record.FrameworkRecord;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

/**
 * JDI connect/ websocket / Thread / Data
 * JDI连接 / websocket / 服务线程管理/ 数据管理
 */
@Component
public class DebugManger {
    public final PublishService publisher;
    public final TaskThreadManager taskThreadManager;

    @Getter
    private VirtualMachine virtualMachine;
    private final LinkedHashMap<AnalystsType, List<Object>> dataMapping = new LinkedHashMap<>();
    private final HashSet<AbstractWS.AnalystsInfo> logMap = new HashSet<>();

    @Getter
    private final HashSet<String> failServlet = new HashSet<>();

    public DebugManger(PublishService publisher, TaskThreadManager taskThreadManager) {
        this.publisher = publisher;
        this.publisher.setManager(this);
        this.taskThreadManager = taskThreadManager;
    }

    public boolean connectRemoteJVM(DebuggerArgs args) {
        if (args == null) {
            return false;
        } else {
            SocketAttachingConnector socketAttachingConnector = new SocketAttachingConnector();
            Map<String, Connector.Argument> argumentHashMap = socketAttachingConnector.defaultArguments();
            argumentHashMap.get("hostname").setValue(args.getHostname());
            argumentHashMap.get("port").setValue(args.getPort());
            argumentHashMap.get("timeout").setValue(args.getTimeout());
            VirtualMachine attach = null;
            try {
                attach = socketAttachingConnector.attach(argumentHashMap);
            } catch (IOException e) {
                publisher.debugError("网络连接失败");
            } catch (IllegalConnectorArgumentsException e) {
                publisher.debugError("参数格式异常");
            }
            if (attach == null) {
                return false;
            } else {
                this.virtualMachine = attach;
            }
            publisher.debugSuccess("连接成功");
            return true;
        }
    }

    public boolean existConnect() {
        return virtualMachine != null;
    }

    public void closeConnect() {
        if (virtualMachine != null) {
            virtualMachine.eventRequestManager().deleteAllBreakpoints();
            virtualMachine.resume();
            virtualMachine.dispose();
            this.virtualMachine = null;
        }
    }

    public void logDebugInfo(AbstractWS.AnalystsInfo info) {
        logMap.add(info);
    }

    public HashSet<AbstractWS.AnalystsInfo> getLogData() {
        return logMap;
    }

    public void cleanLog() {
        logMap.clear();
    }

    public void
    recordData(AnalystsType type, Object record) {
        if (dataMapping.get(type) == null) {
            List<Object> cache = new ArrayList<>();
            cache.add(record);
            dataMapping.put(type, cache);
        } else {
            dataMapping.get(type).add(record);
        }
    }

    public List<Object> getData(AnalystsType... analystsTypes) {
        List<Object> result = new ArrayList<>();
        for (AnalystsType analystsType : analystsTypes) {
            List<Object> objects = dataMapping.get(analystsType);
            if (objects != null) {
                result.addAll(objects);
            }
        }
        return result;
    }

    public List<AnalystsType> existDataType(AnalystsType... analystsTypes) {
        ArrayList<AnalystsType> result = new ArrayList<>();
        for (AnalystsType analystsType : analystsTypes) {
            List<Object> objects = dataMapping.get(analystsType);
            if (objects != null && !objects.isEmpty()) {
                result.add(analystsType);
            }
        }
        return result;
    }

    public boolean startTask() {
        return taskThreadManager.beginAnalysts(this);
    }

    public boolean stopTask() {
        try {
            taskThreadManager.stopTask(this);
            this.virtualMachine = null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean taskExist() {
        return taskThreadManager.hasStart();
    }

    public void recordFailServlet(String servlet) {
        failServlet.add(servlet);
    }

}
