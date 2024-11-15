package router.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import router.server.entity.FilterEntity;

import java.util.List;

@Mapper
public interface FilterMapper extends BaseMapper{
    int insertFilters(List<FilterEntity> filterEntities);
    List<FilterEntity> getFilterByMiddleId(Integer middleId, Integer page, Integer limit);
    Integer selectFilterCountByMiddleId(Integer middleId);

    void deleteByMiddlewareId(Integer middleId);
}
