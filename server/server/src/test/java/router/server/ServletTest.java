package router.server;

import router.server.mapper.ServletMapper;
import router.server.entity.ServletEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
public class ServletTest {
    @Autowired
    ServletMapper servletContext;
//    @Test
    public void TestServlet() {
        servletContext.initTable();
        List<ServletEntity> allServletEntity = servletContext.getAllServlet();
        System.out.println(allServletEntity);
    }
}
