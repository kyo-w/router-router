package router.publish;

public interface IPublish {
    void Event(EventType eventType, EventPackage eventPackage);
    //    导致分析无法进行的错误
    void Error(Exception msg);
}
