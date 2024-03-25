package com.miniproject.discoveryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
//@ComponentScan(basePackages = {"com.miniproject.discoveryserver.config"})
public class DiscoveryServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(DiscoveryServerApplication.class, args);
    }

}

