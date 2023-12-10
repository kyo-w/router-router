package kyodream.controller;

import kyodream.controller.wrapper.Response;
import kyodream.event.ContextEvent;
import kyodream.event.FrameworkEvent;
import kyodream.event.RequestEvent;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

@RequestMapping("/data")
@RestController
public class DataController {

    @RequestMapping("/clean/request")
    public void cleanRequest() {
        RequestEvent.httpRequestRecords.clear();
    }


    @RequestMapping("/request")
    public Response request() {
        return Response.OK(RequestEvent.httpRequestRecords);
    }

    @RequestMapping("/context")
    public Response context() {
        return Response.OK(ContextEvent.contextSet);
    }

    @RequestMapping("/context/{name}")
    public Response getContextByName(@PathVariable String name) {
        Object[] result = ContextEvent.contextSet.stream().filter(context -> context.getFrom().equals(name)).toArray();
        return Response.OK(result);
    }

    @RequestMapping("/framework")
    public Response framework() {
        return Response.OK(FrameworkEvent.frameworkSet);
    }

    @RequestMapping("/framework/{name}")
    public Response getFrameworkByName(@PathVariable String name) {
        Object[] objects = FrameworkEvent.frameworkSet.stream().filter(framework -> framework.getFrom().equals(name)).toArray();
        return Response.OK(objects);
    }

    @RequestMapping("/context/namespace")
    public Response contextNamespace() {
        Object[] result = ContextEvent.contextSet.stream().map(context -> context.getFrom()).toArray();
        return Response.OK(result);
    }

    @RequestMapping("/framework/namespace")
    public Response frameworkNamespace() {
        Object[] result = FrameworkEvent.frameworkSet.stream().map(framework -> framework.getFrom()).toArray();
        return Response.OK(result);
    }
}
