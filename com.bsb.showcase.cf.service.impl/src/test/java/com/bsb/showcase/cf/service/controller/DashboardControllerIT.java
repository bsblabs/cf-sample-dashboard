package com.bsb.showcase.cf.service.controller;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.bsb.showcase.cf.service.AbstractCfServiceTest;
import com.bsb.showcase.cf.service.security.DashboardAuthenticationDetails;

/**
 * @author Sebastien Gerard
 */
public class DashboardControllerIT extends AbstractCfServiceTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;

    @Test
    public void home() throws Exception {
        final Authentication originalAuthentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            final String userFullName = "John Smith";

            final TestingAuthenticationToken authentication = new TestingAuthenticationToken("principal", "cred");
            authentication.setDetails(new DashboardAuthenticationDetails(new MockHttpServletRequest(), true, userFullName));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            final MvcResult mvcResult = mvc
                  .perform(
                        request(HttpMethod.GET, "/dashboard/")
                  )
                  .andExpect(status().is(HttpStatus.OK.value()))
                  .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                  .andReturn();

            assertEquals(userFullName, mvcResult.getModelAndView().getModelMap().get(DashboardController.USER_FULL_NAME));
            assertEquals(DashboardController.HOME_VIEW, mvcResult.getModelAndView().getViewName());
        } finally {
            SecurityContextHolder.getContext().setAuthentication(originalAuthentication);
        }
    }

    @PostConstruct
    private void initialize() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
              .build();
    }
}