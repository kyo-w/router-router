package kyodream.controller;

import com.alibaba.bytekit.utils.Decompiler;
import kyodream.AgentManagerImpl;
import kyodream.controller.wrapper.Response;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;


@RequestMapping("/decompile")
@RestController
public class DecompileController {
    @RequestMapping("/class/{className}")
    public Response Decompiler(@PathVariable String className) throws IOException {
        Class classObject = AgentManagerImpl.getClassByClassName(className);
        if (classObject == null) {
            return Response.Fail(null);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream resourceAsStream =
                     classObject.getClassLoader().getResourceAsStream(classObject.getName().replace(".", "/") + ".class");
             BufferedInputStream bufferedInputStream = new BufferedInputStream(resourceAsStream)) {
            byte[] buffer = new byte[1024];
            while (bufferedInputStream.read(buffer) != -1) {
                byteArrayOutputStream.write(buffer);
            }
        }
        byte[] bytes = byteArrayOutputStream.toByteArray();
        if (bytes != null) {
            String decompile = Decompiler.decompile(bytes);
            return Response.OK(decompile);
        }
        return Response.Fail(null);
    }
}
