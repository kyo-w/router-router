package kyodream.controller.wrapper;

public class Response {
    private Enum type;
    private Object msg;

    public Response(Enum type, Object msg) {
        this.type = type;
        this.msg = msg;
    }

    public Enum getType() {
        return type;
    }

    public void setType(Enum type) {
        this.type = type;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public static Response OK(Object msg){
        return new Response(ResponseType.OK, msg);
    }

    public static Response Fail(Object msg){
        return new Response(ResponseType.Fail, msg);
    }

    public static Response Warn(Object msg){
        return new Response(ResponseType.Warning, msg);
    }

    public static enum ResponseType {
        OK,
        Fail,
        Warning
    }
}
