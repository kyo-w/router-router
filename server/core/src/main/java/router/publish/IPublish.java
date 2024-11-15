package router.publish;

public interface IPublish {
    void Event(Event event);

    //    导致分析无法进行的错误
    void Error(Error event);
}
