package router.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import router.server.entity.HandlerEntity;

import java.util.List;

@Mapper
public interface HandlerMapper extends BaseMapper {
    void insertHandler(List<HandlerEntity> handlerList);
    List<HandlerEntity> selectAllByFrameworkId(Integer frameworkId, Integer page, Integer limit);
    int selectCountByFrameworkId(Integer frameworkId);
}
