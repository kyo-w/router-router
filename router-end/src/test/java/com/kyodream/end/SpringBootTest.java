package com.kyodream.end;

import com.kyodream.end.bean.A;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@org.springframework.boot.test.context.SpringBootTest
@Slf4j
public class SpringBootTest {
    @Autowired
    private ApplicationContext applicationContext;
    @Test
    public void test(){
        log.info("test");
    }
}

