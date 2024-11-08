package router.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import router.server.entity.FrameworkEntity;

import java.util.List;

@Mapper
public interface FrameworkMapper extends BaseMapper{
    List<String> selectTypeByProjectId(Integer projectId);
    List<FrameworkEntity> selectByProjectIdAndType(Integer projectId, String... types);
    int insertFramework(FrameworkEntity framework);
}
