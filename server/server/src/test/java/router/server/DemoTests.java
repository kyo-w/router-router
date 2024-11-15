package router.server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import router.server.entity.ProjectEntity;
import router.server.mapper.ProjectMapper;
import java.util.Date;

@SpringBootTest
public class DemoTests {
    @Autowired
    ProjectMapper projectMapper;
    @Test
    public void createProjectTest(){
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(2);
        projectEntity.setCreateTime(new Date());
        projectEntity.setHostname("127.0.0.1");
        projectEntity.setPort("5005");
        projectEntity.setAlias("testst");
        projectMapper.saveProject(projectEntity);
        System.out.println(projectEntity);
    }
}
