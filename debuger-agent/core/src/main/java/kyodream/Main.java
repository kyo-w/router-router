package kyodream;

import com.sun.tools.attach.*;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("获取数据");
        HashMap<String, String> argMap = new HashMap<>();
        for (int i = 0, j = 1; j < args.length; i = i + 2, j = j + 2) {
            argMap.put(args[i], args[j]);
        }
        String workPath = argMap.get("--work-path");
        VirtualMachine attach = null;

        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (VirtualMachineDescriptor descriptor : list) {
            System.out.println(descriptor.displayName());
        }

        try {
            attach = VirtualMachine.attach("3680");
        } catch (AttachNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        String agentArgs = transformerArgs(argMap);
        String agentPath = Paths.get(workPath, "agent.jar").toString();
        try {
            attach.loadAgent(agentPath, agentArgs);
        } catch (AgentLoadException e) {
            e.printStackTrace();
        } catch (AgentInitializationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String transformerArgs(HashMap<String, String> argMap) {
        Base64 base64 = new Base64();
        StringBuffer buffer = new StringBuffer();
        for (Map.Entry<String, String> elem : argMap.entrySet()) {
            buffer.append("|" + elem.getKey() + "@" + elem.getValue() + "|");
        }
        return new String(base64.encode(buffer.toString().getBytes()));
    }
}
