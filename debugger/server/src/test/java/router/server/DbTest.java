package router.server;

import router.server.entity.FilterEntity;
import router.server.entity.MiddlewareEntity;
import router.server.entity.ProjectEntity;
import router.server.mapper.FilterMapper;
import router.server.mapper.MiddlewareMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import router.server.mapper.ProjectMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@SpringBootTest
public class DbTest {
    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    FilterMapper filterMapper;
//    @Test
    public void testProject() {
        projectMapper.initTable();
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setAlias("test");
        projectEntity.setPort("323");
        projectEntity.setHostname("tes");
        projectEntity.setCreateTime(new Date());
        projectMapper.saveProject(projectEntity);
        List<ProjectEntity> allProjectList = projectMapper.getAllProjectList();
        Date createTime = allProjectList.get(0).getCreateTime();
        System.out.println(createTime);
    }

//    @Test
    public void testFilter() {
        filterMapper.initTable();
        ArrayList<FilterEntity> filterEntities = new ArrayList<>();
        FilterEntity filterEntity = new FilterEntity();
        filterEntity.setClassname("test");
        filterEntity.setUrl(".test");
        filterEntity.setMiddleId(1);
        filterEntities.add(filterEntity);
        filterMapper.insertFilters(filterEntities);
    }
}
