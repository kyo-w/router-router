package router.server.entity;

import lombok.Data;

@Data
public class MiddlewareEntity {
    private Integer id;
    private Integer projectId;
    private String type;
    private String virtualPath;
    private String physicalPath;
    private String version;
}
