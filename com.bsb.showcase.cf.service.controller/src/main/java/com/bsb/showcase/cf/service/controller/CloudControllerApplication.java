package com.bsb.showcase.cf.service.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Stub implementation of a Cloud Foundry Controller.
 *
 * @author Sebastien Gerard
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class CloudControllerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudControllerApplication.class, args);
    }

}
