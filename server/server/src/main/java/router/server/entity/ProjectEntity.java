package router.server.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectEntity {
    private Integer id;
    private String alias;
    private String hostname;
    private String port;
    private Date createTime;
    private boolean complete;
}
