package router.server.publish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import router.publish.EventPackage;
import router.publish.EventType;
import router.publish.IPublish;
import router.server.service.ProgressService;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@ServerEndpoint(value = "/ws/event", encoders = {ServerEncoder.class})
public class WsPublish implements IPublish {
    @Autowired
    ProgressService progressService;

    private static final HashMap<Class<?>, CopyOnWriteArraySet<Session>> sessionMap = new HashMap<>();
    protected ExecutorService executorService = Executors.newFixedThreadPool(10);

    @OnOpen
    public void onOpen(Session session) {
        CopyOnWriteArraySet<Session> sessions = sessionMap.computeIfAbsent(this.getClass(), k -> new CopyOnWriteArraySet<>());
        sessions.add(session);
    }

    @OnClose
    public void close(Session session) {
        if (sessionMap.get(this.getClass()) != null) {
            sessionMap.get(this.getClass()).remove(session);
        }
    }


    @Override
    public void Event(EventType eventType, EventPackage eventPackage) {
        executorService.execute(() -> {
            ProgressTask result = null;
            switch (eventType) {
                case BreakPointStart:
                case MiddlewareContextCount:
                case FilterCount:
                case ServletCount:
                case Jersey1FrameworkCount:
                case Jersey2FrameworkCount:
                case SpringFrameworkCount:
                case Struts1FrameworkCount:
                case Struts2FrameworkCount:
                    result = progressService.registryTask(eventType, eventPackage);
                    break;
                case MiddlewareContextAnalystsComplete:
                case FilterAnalystsComplete:
                case ServletAnalystsComplete:
                case Jersey1FrameworkAnalystsComplete:
                case Jersey2FrameworkAnalystsComplete:
                case SpringFrameworkAnalystsComplete:
                case Struts1FrameworkAnalystsComplete:
                case Struts2FrameworkAnalystsComplete:
                case BreakPointEnd:
                    result = progressService.updateTask(eventType, eventPackage);
                    break;
            }
            for (Session session : sessionMap.get(this.getClass())) {
                if (session.isOpen()) {
                    synchronized (session) {
                        try {
                            session.getBasicRemote().sendObject(result);
                        } catch (IOException | EncodeException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void Error(Exception msg) {

    }
}
