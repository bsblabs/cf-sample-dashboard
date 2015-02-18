package com.bsb.showcase.cf.service.security;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.bsb.showcase.cf.service.AbstractCfServiceTest;

/**
 * @author Sebastien Gerard
 */
public class DashboardAuthenticationSuccessHandlerTest extends AbstractCfServiceTest {

    @Test
    public void onAuthenticationSuccess() {
        final String currentUri = "/dashboard";
        final HttpServletRequest request = createRequest(currentUri);

        final HttpServletResponse response = createResponse();

        doOnAuthenticationSuccess(request, response);

        assertRedirection("http://localhost" + currentUri, response);
    }

    private MockHttpServletRequest createRequest(String currentUri) {
        final MockHttpServletRequest request = new MockHttpServletRequest();

        request.setRequestURI(currentUri);

        return request;
    }

    private MockHttpServletResponse createResponse() {
        return new MockHttpServletResponse();
    }

    private AuthenticationSuccessHandler createHandler() {
        return new DashboardAuthenticationSuccessHandler();
    }

    private void doOnAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response) {
        try {
            createHandler().onAuthenticationSuccess(request, response,
                  new TestingAuthenticationToken("user", "cred",
                        Collections.<GrantedAuthority>singletonList(new SimpleGrantedAuthority("ROLE_USER"))));
        } catch (IOException | ServletException e) {
            throw new IllegalStateException(e);
        }
    }

    private void assertRedirection(String currentUri, HttpServletResponse response) {
        assertEquals(currentUri, response.getHeader("Location"));
    }
}