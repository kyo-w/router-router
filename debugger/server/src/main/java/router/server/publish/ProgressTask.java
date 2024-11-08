package router.server.publish;

import lombok.Data;

/**
 * 分析进程中的某个阶段性任务
 */
@Data
public class ProgressTask {
//    远程集合对象的ID
    private Long Id;
//    阶段性任务的名称
    private String taskName;
//    阶段性任务的总量
    private Integer count;
//    阶段性任务的完成总量
    private Integer current;

    public void updateCount(){
        current++;
    }
}
