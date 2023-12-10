package com.kyodream.debugger;

import com.kyodream.debugger.core.DebugManger;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PreDestroy;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

@SpringBootApplication
public class DebuggerApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DebuggerApplication.class);
    @Autowired
    DebugManger debugManger;

    @Value("${server.port}")
    private String port;

    public static void main(String[] args) {
        SpringApplication.run(DebuggerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(this.port);
    }


    @PreDestroy
    public void onDestroy() throws Exception {
        System.out.println("执行销毁操作...");
        debugManger.closeConnect();
    }
}
