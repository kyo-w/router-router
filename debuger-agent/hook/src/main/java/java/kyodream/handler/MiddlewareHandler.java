package java.kyodream.handler;


import java.io.File;
import java.kyodream.AgentManager;
import java.kyodream.record.Context;
import java.kyodream.utils.VirtualPathUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MiddlewareHandler extends BaseHandler {
    public static void dyTransformerServlet(Context context) {
        for (String servletClass : context.getUrlMap().values()) {
            AgentManager.registryServlet(servletClass);
        }
    }


}
