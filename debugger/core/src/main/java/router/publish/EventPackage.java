package router.publish;

public class EventPackage {
    private Long id;
    private Object msg;

    public EventPackage(Long id, Object msg) {
        this.id = id;
        this.msg = msg;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
