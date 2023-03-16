package com.kyodream.end;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLOutput;

@SpringBootApplication
public class RouterEndApplication implements CommandLineRunner {

    @Value("${server.address}")
    private String address;

    @Value("${server.port}")
    private String port;

    public static void main(String[] args) {
        SpringApplication.run(RouterEndApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("address: " + address);
        System.out.println("port: " + port);
    }
}
