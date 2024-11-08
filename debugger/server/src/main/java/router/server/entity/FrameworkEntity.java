package router.server.entity;

import lombok.Data;

@Data
public class FrameworkEntity {
    private Integer id;
    private Integer projectId;
    private String type;
    private String contextPath;
    private String version;
}
