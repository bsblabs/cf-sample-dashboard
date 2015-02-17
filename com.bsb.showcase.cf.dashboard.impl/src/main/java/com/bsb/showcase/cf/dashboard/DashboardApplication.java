package com.bsb.showcase.cf.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Sebastien Gerard
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class DashboardApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(DashboardApplication.class, args);
    }
}