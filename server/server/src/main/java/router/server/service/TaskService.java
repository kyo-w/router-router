package router.server.service;

import com.sun.jdi.VirtualMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import router.DebugCore;
import router.middleware.JettyMiddleware;
import router.middleware.TomcatMiddleware;
import router.server.context.DatabaseContext;
import router.server.controller.ApiResponse;

@Service
public class TaskService {
    DebugCore debugThread;
    @Autowired
    ConnectService connectService;
    @Autowired
    DatabaseContext databaseContext;


    public ApiResponse startAnalysts(VirtualMachine vm) {
        if (debugThread != null) {
            return ApiResponse.status400("已存在分析线程");
        }
        debugThread = DebugCore.build(vm, databaseContext);
        debugThread.registryHandler("org.apache.catalina.mapper.Mapper",
                "internalMap", new TomcatMiddleware());
        debugThread.registryHandler("org.eclipse.jetty.servlet.ServletContextHandler",
                "doScope", new JettyMiddleware());
        new Thread(debugThread).start();
        return ApiResponse.status200("分析线程启动");
    }

    public ApiResponse stopAnalysts() {
        if (debugThread != null) {
            debugThread.stopTask();
            debugThread = null;
        }
        return ApiResponse.Ok();
    }
}
