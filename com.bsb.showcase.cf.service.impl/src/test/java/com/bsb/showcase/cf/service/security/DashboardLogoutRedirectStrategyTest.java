package com.bsb.showcase.cf.service.security;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.bsb.showcase.cf.service.AbstractCfServiceTest;

/**
 * @author Sebastien Gerard
 */
public class DashboardLogoutRedirectStrategyTest extends AbstractCfServiceTest {

    @Test
    public void sendRedirect() throws IOException {
        final DashboardLogoutRedirectStrategy strategy = new DashboardLogoutRedirectStrategy("http://uaa.domain.com");

        final MockHttpServletResponse response = new MockHttpServletResponse();
        strategy.sendRedirect(new MockHttpServletRequest(), response, "http://myApp.domain.com");

        assertEquals("http://uaa.domain.com?redirect=http://myApp.domain.com", response.getRedirectedUrl());
    }
}
