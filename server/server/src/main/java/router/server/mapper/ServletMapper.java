package router.server.mapper;

import router.server.entity.ServletEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ServletMapper extends BaseMapper{
    int insertServlets(List<ServletEntity> servletEntityList);
    List<ServletEntity> getAllServlet();
    List<ServletEntity> getServletByMiddlewareId(Integer middleId, Integer page, Integer limit);
    Integer selectServletCountByMiddlewareId(Integer middleId);
    void mark(boolean flag, Integer id);

    void deleteByMiddlewareId(Integer middleId);
}
