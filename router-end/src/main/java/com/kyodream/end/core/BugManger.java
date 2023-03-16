package com.kyodream.end.core;

import com.kyodream.end.core.category.*;
import com.kyodream.end.core.thread.HandleController;
import com.kyodream.end.core.thread.SurveyThread;
import com.kyodream.end.pojo.Args;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.tools.jdi.SocketAttachingConnector;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class BugManger {
    private VMWrapper vm = new VMWrapper(null);
    private Args currentArgs;
    private Set<Handle> handles;

    private Struts struts;
    private HashMap<String, AbstractDataWrapper> dataWrappers;

    public Args getCurrentArgs() {
        return currentArgs;
    }

    public BugManger(Spring spring, Tomcat tomcat, Jetty jetty, JerseyHandle jerseyHandle, Struts struts) {
        handles = new HashSet();
        handles.add(spring);
        handles.add(tomcat);
        handles.add(jetty);

        this.struts = struts;

        dataWrappers = new HashMap<>();
        dataWrappers.put("spring", spring);
        dataWrappers.put("tomcat", tomcat);
        dataWrappers.put("jetty", jetty);
        dataWrappers.put("jersey", jerseyHandle);
        dataWrappers.put("struts", struts);
    }

    public boolean existConnect() {
        return vm.vmExist();
    }

    public void settingArg(Args args) {
        this.currentArgs = args;
    }

    public void cleanArg() {
        currentArgs = null;
    }

    public void stopDebug() {
        vm.stop();
    }

    public void runDebug() throws Exception {
        if(vm.getVirtualMachine() == null){
            throw new Exception("未设置");
        }
        registryFilterClass();
//        The control thread is responsible for completing handle every breakpoint
        SurveyThread surveyThread = new SurveyThread(currentArgs.getUrl());
        HandleController handleController = new HandleController(vm, handles, surveyThread);
        Thread handleControllerThread = new Thread(handleController);
        handleControllerThread.setDaemon(true);
        handleControllerThread.start();
    }

    private void registryFilterClass() {
        EventRequestManager eventRequestManager = vm.getVirtualMachine().eventRequestManager();
        for (Handle elem : handles) {
            Set<String> filterClassName = elem.getFilterClassName();
            if(filterClassName != null) {
                for (String className : filterClassName) {
                    MethodEntryRequest methodEntryRequest = eventRequestManager.createMethodEntryRequest();
                    methodEntryRequest.addClassFilter(className);
                    methodEntryRequest.enable();
                    vm.registryEventRequest(methodEntryRequest);
                }
            }
        }
    }

    public void connectTarget() throws IllegalConnectorArgumentsException, IOException, RepeatConnectError {
        if(vm.getVirtualMachine() != null){
            throw new RepeatConnectError("repeat connect");
        }
        SocketAttachingConnector socketAttachingConnector = new SocketAttachingConnector();
        Map<String, Connector.Argument> argumentHashMap = socketAttachingConnector.defaultArguments();
        argumentHashMap.get("hostname").setValue(currentArgs.getAddress());
        argumentHashMap.get("port").setValue(currentArgs.getPort().toString());
        argumentHashMap.get("timeout").setValue("3000");
        VirtualMachine virtualMachine = null;
        virtualMachine = socketAttachingConnector.attach(argumentHashMap);
        vm = new VMWrapper(virtualMachine);
    }

    public AnalyseStatus analyseStatus(){
        return this.vm.isSingleAnalysisCompleted();
    }

    public HashMap<String, AbstractDataWrapper> getAllDataStatus(){
        return dataWrappers;
    }

    public void cleanData() {
//        clear every data and flag
        vm.setSingleAnalysisCompleted(AnalyseStatus.NO_ANALYSE);
        Iterator<Map.Entry<String, AbstractDataWrapper>> iterator = dataWrappers.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, AbstractDataWrapper> next = iterator.next();
            next.getValue().clearData();
        }
    }


    public String getStrutsModuleName(){
        return struts.getModulePatternName();
    }

    public static class VMWrapper{
        private boolean exist;
        private VirtualMachine virtualMachine;
        private AnalyseStatus singleAnalysisCompleted = AnalyseStatus.NO_ANALYSE;
        private List<EventRequest> listEventRequest = new ArrayList();
        public AnalyseStatus isSingleAnalysisCompleted() {
            return singleAnalysisCompleted;
        }

        public void setSingleAnalysisCompleted(AnalyseStatus status) {
            this.singleAnalysisCompleted = status;
        }


        public VMWrapper(VirtualMachine attach){
            if(attach != null) {
                this.virtualMachine = attach;
                exist = true;
            }else {
                exist = false;
            }
        }
        public boolean vmExist(){
            return exist;
        }
        public void setExist(boolean flag){
            exist = flag;
        }

        public VirtualMachine getVirtualMachine() {
            return virtualMachine;
        }

        public void setVirtualMachine(VirtualMachine virtualMachine) {
            this.virtualMachine = virtualMachine;
        }

        public void stop(){
            if(virtualMachine != null) {
                virtualMachine.dispose();
            }
        }
        public void registryEventRequest(EventRequest request){
            this.listEventRequest.add(request);
        }

        public List<EventRequest> getEventRequests(){
            return this.listEventRequest;
        }

        public void clearEventRequests(){
            this.listEventRequest = new ArrayList<>();
        }
    }
}
