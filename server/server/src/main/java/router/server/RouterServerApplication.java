package router.server;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import router.server.service.ConnectService;

import javax.annotation.PreDestroy;

@Slf4j
@SpringBootApplication
public class RouterServerApplication implements CommandLineRunner {
    @Value("${server.port}")
    private String port;
    @Autowired
    ConnectService service;

    public static void main(String[] args) {
        SpringApplication.run(RouterServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(this.port);
    }


    @PreDestroy
    public void onDestroy() throws Exception {
        service.closeConnect();
    }
}
