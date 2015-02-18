package com.bsb.showcase.cf.service.security;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import com.bsb.showcase.cf.service.AbstractCfServiceTest;

/**
 * @author Sebastien Gerard
 */
public class DashboardAuthenticationProcessingFilterTest extends AbstractCfServiceTest {

    @Test
    public void requiresAuthenticationNotRequireAlreadyAuthenticated() {
        try {
            SecurityContextHolder.createEmptyContext();
            SecurityContextHolder.getContext().setAuthentication(createAuthentication());

            assertFalse(doRequireAuthentication());
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    public void requiresAuthenticationNotAuthenticated() {
        assertTrue(doRequireAuthentication());
    }

    @Test
    public void attemptAuthenticationNoDetailSource() throws IOException, ServletException {
        final OAuth2Authentication oAuth2Authentication = createAuthentication();
        final Authentication resultAuthentication = createResultAuthentication(oAuth2Authentication);

        final DashboardAuthenticationProcessingFilter filter = createFilter(oAuth2Authentication, resultAuthentication);

        final Authentication actualResultAuthentication = filter.attemptAuthentication(createRequest(), createResponse());

        assertSame(resultAuthentication, actualResultAuthentication);
        assertNull(oAuth2Authentication.getDetails());
    }

    @Test
    public void attemptAuthenticationWithDetailSource() throws IOException, ServletException {
        final OAuth2Authentication oAuth2Authentication = createAuthentication();
        final Object details = "details";
        final Authentication resultAuthentication = createResultAuthentication(oAuth2Authentication);

        final HttpServletRequest request = createRequest();
        final AuthenticationDetailsSource<HttpServletRequest, ?> detailsSource = createDetailsSource(request, details);

        final DashboardAuthenticationProcessingFilter filter =
              createFilter(oAuth2Authentication, resultAuthentication, detailsSource);

        final Authentication actualResultAuthentication = filter.attemptAuthentication(request, createResponse());

        assertSame(resultAuthentication, actualResultAuthentication);
        assertEquals(details, oAuth2Authentication.getDetails());
    }

    private OAuth2Authentication createAuthentication() {
        return new OAuth2Authentication(null, new TestingAuthenticationToken("name", "cred"));
    }

    private DashboardAuthenticationProcessingFilter createFilter(OAuth2Authentication oAuth2Authentication,
                                                                 Authentication resultAuthentication,
                                                                 AuthenticationDetailsSource<HttpServletRequest, ?> source) {
        final String token = "TOKEN";
        final DashboardAuthenticationProcessingFilter filter = new DashboardAuthenticationProcessingFilter();

        filter.setAuthenticationManager(createAuthenticationManagerForUserAuth(oAuth2Authentication, resultAuthentication));
        filter.setRestTemplate(createRestTemplate(token));
        filter.setTokenServices(createResourceTokenServices(oAuth2Authentication, token));
        filter.setDetailsSource(source);

        return filter;
    }

    private DashboardAuthenticationProcessingFilter createFilter(OAuth2Authentication oAuth2Authentication,
                                                                 Authentication resultAuthentication) {
        return createFilter(oAuth2Authentication, resultAuthentication, null);
    }

    private AuthenticationManager createAuthenticationManagerForUserAuth(OAuth2Authentication oAuth2Authentication,
                                                                         Authentication resultAuthentication) {
        final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);

        when(authenticationManager.authenticate(oAuth2Authentication))
              .thenReturn(resultAuthentication);

        return authenticationManager;
    }

    private Authentication createResultAuthentication(OAuth2Authentication oAuth2Authentication) {
        return new TestingAuthenticationToken(
              oAuth2Authentication.getUserAuthentication().getPrincipal(),
              oAuth2Authentication.getCredentials(),
              new ArrayList<>(oAuth2Authentication.getAuthorities())
        );
    }

    private OAuth2RestOperations createRestTemplate(String token) {
        final OAuth2RestOperations auth2RestOperations = mock(OAuth2RestOperations.class);

        when(auth2RestOperations.getAccessToken())
              .thenReturn(new DefaultOAuth2AccessToken(token));

        return auth2RestOperations;
    }

    private ResourceServerTokenServices createResourceTokenServices(OAuth2Authentication authentication, String token) {
        final ResourceServerTokenServices resourceServerTokenServices = mock(ResourceServerTokenServices.class);

        when(resourceServerTokenServices.loadAuthentication(token))
              .thenReturn(authentication);

        return resourceServerTokenServices;
    }

    @SuppressWarnings("unchecked")
    private AuthenticationDetailsSource<HttpServletRequest, ?> createDetailsSource(HttpServletRequest request,
                                                                                   Object details) {
        final AuthenticationDetailsSource<HttpServletRequest, ?> source = mock(AuthenticationDetailsSource.class);

        when(source.buildDetails(request))
              .thenReturn(details);

        return source;
    }

    private HttpServletRequest createRequest() {
        return new MockHttpServletRequest();
    }

    private MockHttpServletResponse createResponse() {
        return new MockHttpServletResponse();
    }

    private boolean doRequireAuthentication() {
        return new DashboardAuthenticationProcessingFilter()
              .requiresAuthentication(new MockHttpServletRequest("GET", "/"), createResponse());
    }
}
