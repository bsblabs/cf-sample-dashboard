package com.bsb.showcase.cf.service.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Sebastien Gerard
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CloudControllerApplication.class)
@WebAppConfiguration
public class CloudControllerIT {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;

    @Test
    public void managed() throws Exception {
        mvc
              .perform(
                    request(HttpMethod.GET, "/v2/service_instances/87728870-b29c-11e4-ab27-0800200c9a66/permissions")
              )
              .andExpect(status().is(HttpStatus.OK.value()))
              .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
              .andExpect(content().string("{\"manage\":true}"));
    }

    @PostConstruct
    private void initialize() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
              .build();
    }
}