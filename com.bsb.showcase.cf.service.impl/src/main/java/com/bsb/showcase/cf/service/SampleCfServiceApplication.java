package com.bsb.showcase.cf.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Sample Cloud Foundry service application.
 *
 * @author Sebastien Gerard
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class SampleCfServiceApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SampleCfServiceApplication.class, args);
    }
}