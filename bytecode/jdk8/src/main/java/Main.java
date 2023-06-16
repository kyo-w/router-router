import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.tools.jdi.SocketAttachingConnector;
import javassist.ClassPool;
import javassist.CtClass;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        SocketAttachingConnector socketAttachingConnector = new SocketAttachingConnector();
        Map<String, Connector.Argument> argumentHashMap = socketAttachingConnector.defaultArguments();
        argumentHashMap.get("hostname").setValue("127.0.0.1");
        argumentHashMap.get("port").setValue("5005");

        VirtualMachine attach = null;
        try {
            attach = socketAttachingConnector.attach(argumentHashMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IllegalConnectorArgumentsException e) {
            throw new RuntimeException(e);
        }


        List<ReferenceType> referenceTypes = attach.classesByName("test.Main");
        ReferenceType referenceType = referenceTypes.get(0);
        List<Method> methods = referenceType.classObject().reflectedType().methods();
        Value value = referenceType.classLoader().getValue();

    }
}