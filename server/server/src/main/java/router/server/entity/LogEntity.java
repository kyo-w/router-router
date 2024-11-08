package router.server.entity;

import lombok.Data;

@Data
public class LogEntity {
    private Integer Id;
    private Integer projectId;
    private String type;
    private String message;
}
