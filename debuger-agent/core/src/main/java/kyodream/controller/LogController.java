package kyodream.controller;

import kyodream.controller.wrapper.LogPrintStream;
import kyodream.controller.wrapper.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/log")
public class LogController {
    private static ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    public static LogPrintStream printStream = new LogPrintStream(buffer);

    @RequestMapping("/data")
    public Response getLogData() {
        return Response.OK(new String(buffer.toByteArray()));
    }

    @RequestMapping("/clean")
    public Response cleanLogData() {
        buffer.reset();
        return Response.OK(null);
    }

}
