package com.nugget;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @ClassName ServerApp
 * @Description:
 * @Author 米粒儿
 * @Date 2019/9/16
 * @Version V1.0
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class ServerApp {
    public static void main(String[] args) {
        SpringApplication.run(ServerApp.class,args);
    }
}
