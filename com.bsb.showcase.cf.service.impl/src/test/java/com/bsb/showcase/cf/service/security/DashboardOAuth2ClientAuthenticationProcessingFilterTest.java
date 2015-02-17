package com.bsb.showcase.cf.service.security;

import static com.bsb.showcase.cf.test.service.TestableAuthenticationManager.*;
import static com.bsb.showcase.cf.test.service.TestableResourceServerTokenServices.*;
import static com.bsb.showcase.cf.test.service.TestableRestTemplate.*;
import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import com.bsb.showcase.cf.service.AbstractCfServiceTest;
import com.bsb.showcase.cf.test.service.TestableAuthenticationManager;

/**
 * @author Sebastien Gerard
 */
public class DashboardOAuth2ClientAuthenticationProcessingFilterTest extends AbstractCfServiceTest {

    @Test
    public void notRequireAuthenticationAuthentication() {
        try {
            SecurityContextHolder.createEmptyContext();
            SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("user", "cred", "ROLE_USER"));

            assertFalse(launchRequireAuthentication());
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    public void requiresAuthenticationNotAuthenticated() {
        assertTrue(launchRequireAuthentication());
    }

    @Test
    public void authenticateWithNoDetailSource() throws IOException, ServletException {
        final DashboardOAuth2ClientAuthenticationProcessingFilter filter = createFilter();

        final Authentication authentication
              = filter.attemptAuthentication(new MockHttpServletRequest(), new MockHttpServletResponse());

        assertEquals(1, authentication.getAuthorities().size());
        assertEquals(TestableAuthenticationManager.ROLE_USER,
              authentication.getAuthorities().iterator().next().getAuthority());
        assertNull(authentication.getDetails());
    }

    @Test
    public void authenticateWithDetailSource() throws IOException, ServletException {
        final DashboardOAuth2ClientAuthenticationProcessingFilter filter = createFilter();

        final String details = "details";
        filter.setDetailsSource(new AuthenticationDetailsSource<HttpServletRequest, Object>() {
            @Override
            public Object buildDetails(HttpServletRequest context) {
                return details;
            }
        });

        final Authentication authentication
              = filter.attemptAuthentication(new MockHttpServletRequest(), new MockHttpServletResponse());

        assertEquals(1, authentication.getAuthorities().size());
        assertEquals(TestableAuthenticationManager.ROLE_USER,
              authentication.getAuthorities().iterator().next().getAuthority());
        assertEquals(details, authentication.getDetails());
    }

    private DashboardOAuth2ClientAuthenticationProcessingFilter createFilter() {
        final DashboardOAuth2ClientAuthenticationProcessingFilter filter
              = new DashboardOAuth2ClientAuthenticationProcessingFilter();

        filter.setAuthenticationManager(testableAuthenticationManager());
        filter.setRestTemplate(testableRestTemplate());
        filter.setTokenServices(withAuthentication(new OAuth2Authentication(null,
              new TestingAuthenticationToken("name", "cred"))));

        return filter;
    }

    private boolean launchRequireAuthentication() {
        return new DashboardOAuth2ClientAuthenticationProcessingFilter()
              .requiresAuthentication(new MockHttpServletRequest("GET", "/"), new MockHttpServletResponse());
    }
}
