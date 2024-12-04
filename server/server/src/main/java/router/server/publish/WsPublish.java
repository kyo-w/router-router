package router.server.publish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import router.publish.*;
import router.publish.Error;
import router.server.service.ProgressService;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@ServerEndpoint(value = "/ws/event", encoders = {ServerEncoder.class})
public class WsPublish implements IPublish {
    @Autowired
    ProgressService progressService;

    private static final HashSet<Session> sessionMap = new HashSet<>();
    protected ExecutorService executorService = Executors.newFixedThreadPool(10);

    @OnOpen
    public void onOpen(Session session) {
        sessionMap.add(session);
    }

    @OnClose
    public void close(Session session) {
        sessionMap.remove(session);
    }

    public void sendObject(Object obj) {
        for (Session session : sessionMap) {
            if (session.isOpen()) {
                synchronized (session) {
                    try {
                        session.getBasicRemote().sendObject(obj);
                    } catch (IOException | EncodeException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

    }

    @Override
    public void Event(Event event) {
        executorService.execute(() -> {
            ProgressTask result = null;
            switch (event.getType()) {
                case BreakPointStart:
                case MiddlewareContextCount:
                case FilterCount:
                case ServletCount:
                case Jersey1FrameworkCount:
                case Jersey2FrameworkCount:
                case SpringFrameworkCount:
                case Struts1FrameworkCount:
                case Struts2FrameworkCount:
                    result = progressService.registryTask((StartEvent) event);
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
                    result = progressService.updateTask((EndEvent) event);
                    break;
            }
            if (result == null) {
                return;
            }
            sendObject(result);
        });
    }

    @Override
    public void Error(Error error) {
        executorService.execute(() -> {
            ((ErrorEvent)error).getException().printStackTrace();
            ProgressTask progressTask = progressService.handlerErrorTask((ErrorEvent) error);
            sendObject(progressTask);
        });
    }
}
