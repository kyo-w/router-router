package com.kyodream.end;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.tools.jdi.SocketAttachingConnector;

import java.io.IOException;
import java.util.Map;

public class TestUtils {
    public static VirtualMachine getAttach(){
        SocketAttachingConnector socketAttachingConnector = new SocketAttachingConnector();
        Map<String, Connector.Argument> argumentHashMap = socketAttachingConnector.defaultArguments();
        argumentHashMap.get("hostname").setValue("127.0.0.1");
        argumentHashMap.get("port").setValue("5005");
        argumentHashMap.get("timeout").setValue("3000");
        VirtualMachine attach = null;
        try {
            attach = socketAttachingConnector.attach(argumentHashMap);
        } catch (IllegalConnectorArgumentsException | IOException e) {
        }
        return attach;
    }
}
