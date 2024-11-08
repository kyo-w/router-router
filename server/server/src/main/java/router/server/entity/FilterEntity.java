package router.server.entity;

import lombok.Data;

@Data
public class FilterEntity {
    private Integer id;
    private Integer priority;
    private Integer middleId;
    private String classname;
    private String url;
    private boolean mark;
}
