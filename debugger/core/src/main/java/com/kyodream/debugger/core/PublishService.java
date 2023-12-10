package com.kyodream.debugger.core;

import com.kyodream.debugger.pojo.ApiStack;
import com.kyodream.debugger.service.*;
import kyodream.analysts.IPublish;
import kyodream.record.ContextRecord;
import kyodream.record.FrameworkRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PublishService implements IPublish {
    private DebugManger debugManger;

    @Autowired
    DebugWebSocket debugWebSocket;

    @Autowired
    StackWebSocket stackWebSocket;

    @Autowired
    ContextWebSocket contextWebSocket;

    @Autowired
    FrameworkWebSocket frameworkWebSocket;

    @Autowired
    DiscoverWebSocket discoverWebSocket;

    @Autowired
    ServletFailWebSocket failWebSocket;

    public void debugWarn(Object msg) {
        AbstractWS.AnalystsInfo analystsInfo = new AbstractWS.AnalystsInfo(AbstractWS.InfoType.Warn, msg);
        debugManger.logDebugInfo(analystsInfo);
        debugWebSocket.info(analystsInfo);
    }

    public void debugError(Object msg) {
        AbstractWS.AnalystsInfo analystsInfo = new AbstractWS.AnalystsInfo(AbstractWS.InfoType.Error, msg);
        debugManger.logDebugInfo(analystsInfo);
        debugWebSocket.info(analystsInfo);
    }

    public void debugSuccess(Object msg) {
        AbstractWS.AnalystsInfo analystsInfo = new AbstractWS.AnalystsInfo(AbstractWS.InfoType.Success, msg);
        debugManger.logDebugInfo(analystsInfo);
        debugWebSocket.info(analystsInfo);
    }

    public void setManager(DebugManger debugManger) {
        this.debugManger = debugManger;
    }

    @Override
    public void stackMsg(Object msg) {
    }

    public void stackMsg(ApiStack msg) {
        stackWebSocket.sendStack(msg);
    }

    public void contextMsg(ContextRecord msg) {
        debugManger.recordData(msg.getType(), msg);
        discoverWebSocket.discoveryMiddleWare(msg.getType());
        contextWebSocket.sendContextData(msg);
    }

    public void frameworkMsg(FrameworkRecord msg) {
        debugManger.recordData(msg.getType(), msg);
        discoverWebSocket.discoveryFramework(msg.getType());
        frameworkWebSocket.sendFrameworkData(msg);
    }

    @Override
    public void servletMsg(String servlet) {
        debugManger.recordFailServlet(servlet);
        AbstractWS.AnalystsInfo analystsInfo = new AbstractWS.AnalystsInfo(AbstractWS.InfoType.Warn, servlet);
        failWebSocket.servletFail(analystsInfo);
    }
}
