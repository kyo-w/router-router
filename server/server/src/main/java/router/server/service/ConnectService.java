package router.server.service;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.tools.jdi.SocketAttachingConnector;
import lombok.Getter;
import org.springframework.stereotype.Service;
import router.server.controller.ApiResponse;
import router.server.entity.ProjectEntity;

import java.io.IOException;
import java.util.Map;

@Getter
@Service
public class ConnectService {

    private VirtualMachine currentVirtualMachine;

    public ApiResponse testConnect(ProjectEntity projectEntity) {
        try {
            VirtualMachine virtualMachine = connectJDWP(projectEntity.getHostname(), projectEntity.getPort());
            if (virtualMachine != null) {
                closeJDWP(virtualMachine);
                return ApiResponse.Ok();
            }
            return ApiResponse.status400("远程对象为空，可能需要重启目标服务");
        } catch (Exception e) {
            return ApiResponse.status400(e.getMessage());
        }
    }

    public VirtualMachine connectJDWP(String hostname, String port) throws IllegalConnectorArgumentsException, IOException {
        SocketAttachingConnector connector = new SocketAttachingConnector();
        Map<String, Connector.Argument> argMap = connector.defaultArguments();
        argMap.get("hostname").setValue(hostname);
        argMap.get("port").setValue(port);
        argMap.get("timeout").setValue("5000");
        return connector.attach(argMap);
    }

    /**
     * 每一个调试连接的关闭操作：
     * 1.取消所有断点
     * 2.无论是否挂起都要进行恢复操作，防止JVM永久性挂起
     * 3.关闭socket层面的连接
     */
    public static void closeJDWP(VirtualMachine vm) {
        if (vm != null) {
            vm.eventRequestManager().deleteAllBreakpoints();
            vm.resume();
            vm.dispose();
        }
    }

    public ApiResponse connectJDWPWithProject(ProjectEntity projectEntity) {
        try {
            VirtualMachine virtualMachine = connectJDWP(projectEntity.getHostname(), projectEntity.getPort());
            if (virtualMachine != null) {
                currentVirtualMachine = virtualMachine;
                return ApiResponse.Ok();
            } else {
                return ApiResponse.status400("远程对象为空，可能需要重启目标服务");
            }
        } catch (Exception e) {
            return ApiResponse.status400(e.getMessage());
        }
    }

    public ApiResponse closeConnect() {
        closeJDWP(currentVirtualMachine);
        currentVirtualMachine = null;
        return ApiResponse.Ok();
    }
}
