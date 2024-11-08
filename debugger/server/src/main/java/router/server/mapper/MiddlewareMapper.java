package router.server.mapper;

import router.server.entity.MiddlewareEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MiddlewareMapper extends BaseMapper {
    List<String> selectTypeByProjectId(Integer projectId, String... types);

    List<MiddlewareEntity> selectByProjectIdAndType(Integer projectId, String... types);

    int insert(MiddlewareEntity middlewareEntity);
}
