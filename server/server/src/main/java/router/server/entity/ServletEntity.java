package router.server.entity;

import lombok.Data;

import java.util.List;


/**
 * 每一个Servlet绑定一个Middleware对象
 */
@Data
public class ServletEntity {
    private Integer id;
    private Integer middleId;
    private String classname;
    private List<String> urls;
    private boolean mark;
}
