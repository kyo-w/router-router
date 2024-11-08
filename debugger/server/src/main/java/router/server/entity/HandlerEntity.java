package router.server.entity;

import lombok.Data;

import java.util.List;

/**
 * 与Servlet不同，Handler由各种web框架所产生(Spring/Strutx/JerseyX...)
 */
@Data
public class HandlerEntity {
    private Integer id;
    private Integer frameworkId;
    private String classname;
    private List<String> urls;
    private boolean mark;
}
