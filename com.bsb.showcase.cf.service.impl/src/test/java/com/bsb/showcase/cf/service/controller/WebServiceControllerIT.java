package com.bsb.showcase.cf.service.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.bsb.showcase.cf.service.AbstractCfServiceTest;

/**
 * @author Sebastien Gerard
 */
public class WebServiceControllerIT extends AbstractCfServiceTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;

    @Test
    public void ping() throws Exception {
        mvc
              .perform(
                    request(HttpMethod.GET, "/services/v1/ping")
              )
              .andExpect(status().is(HttpStatus.OK.value()))
              .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
              .andExpect(content().string("{" + "\"message\":\"Hello World!\"}"));
    }

    @PostConstruct
    private void initialize() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
              .build();
    }
}