package kyodream.controller.wrapper;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;

public class LogPrintStream extends PrintStream {
    private static HashMap<String, WebSocketSession> sessionHashMap = new HashMap<>();

    public LogPrintStream(OutputStream out) {
        super(out);
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        super.write(buf, off, len);
        for (WebSocketSession session : sessionHashMap.values()) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(new String(buf, off, len)));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void registrySession(WebSocketSession session) {
        sessionHashMap.put(session.getId(), session);
    }

    public void unRegistrySession(String id) {
        sessionHashMap.remove(id);
    }
}
