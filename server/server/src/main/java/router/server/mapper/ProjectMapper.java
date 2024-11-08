package router.server.mapper;

import router.server.entity.ProjectEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectMapper extends BaseMapper {
    List<ProjectEntity> getAllProjectList();
    ProjectEntity getProjectByName(String alias);
    ProjectEntity getProjectById(Integer id);
    void saveProject(ProjectEntity project);
}
