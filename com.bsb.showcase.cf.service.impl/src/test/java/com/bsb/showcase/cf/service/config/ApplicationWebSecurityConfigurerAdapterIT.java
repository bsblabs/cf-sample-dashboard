package com.bsb.showcase.cf.service.config;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.springframework.security.core.authority.AuthorityUtils.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashSet;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.bsb.showcase.cf.service.AbstractCfServiceTest;
import com.bsb.showcase.cf.service.security.DashboardAuthenticationDetails;

/**
 * @author Sebastien Gerard
 */
public class ApplicationWebSecurityConfigurerAdapterIT extends AbstractCfServiceTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    @Qualifier("springSecurityFilterChain")
    private Filter springSecurityFilterChain;

    private MockMvc mvc;
    private MockHttpSession mockSession;

    @Test
    public void accessWebService() throws Exception {
        mvc
              .perform(
                    request(HttpMethod.GET, "/services/v1/ping")
                          .with(httpBasic("admin", "admin"))
              )
              .andExpect(status().is(HttpStatus.OK.value()))
              .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    public void accessWebServiceNoCred() throws Exception {
        mvc
              .perform(
                    request(HttpMethod.GET, "/services/v1/ping")
              )
              .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    public void accessWebServiceWrongCred() throws Exception {
        mvc
              .perform(
                    request(HttpMethod.GET, "/services/v1/ping")
                          .with(httpBasic("admin", "admin2"))
              )
              .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    public void accessCss() throws Exception {
        mvc
              .perform(
                    request(HttpMethod.GET, "/css/base.css")
              )
              .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void accessDashboardNoCred() throws Exception {
        mvc
              .perform(
                    request(HttpMethod.GET, "/dashboard/")
                          .session(mockSession)
              )
              .andExpect(status().is(HttpStatus.FOUND.value())); // redirection
    }

    @Test
    public void accessDashboardAuthentication() throws Exception {
        mvc
              .perform(
                    request(HttpMethod.GET, "/dashboard/")
                          .session(mockSession)
                          .with(authentication(johnSmithAuthentication()))
              )
              .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void accessDashboardWebServiceCred() throws Exception {
        mvc

              .perform(
                    request(HttpMethod.GET, "/dashboard/")
                          .with(httpBasic("admin", "admin2"))
                          .session(mockSession)
              )

              .andExpect(status().is(HttpStatus.FOUND.value())); // redirection
    }

    private OAuth2Authentication johnSmithAuthentication() {
        final OAuth2Authentication oauthAuthentication = new OAuth2Authentication(
              new OAuth2Request(
                    singletonMap("client_id", "myOauthITClient"),
                    "myOauthITClient",
                    null,
                    true,
                    new HashSet<>(asList("cloud_controller_service_permissions.read", "openid")),
                    new HashSet<>(asList("myOauthITClient", "cloud_controller_service_permissions", "openid")),
                    null,
                    null,
                    null
              ),
              new UsernamePasswordAuthenticationToken("marissa", null,
                    createAuthorityList("ROLE_" + ApplicationWebSecurityConfigurerAdapter.ROLE_DASHBOARD)));

        oauthAuthentication.setDetails(new DashboardAuthenticationDetails(new MockHttpServletRequest(), true, "John Smith"));

        return oauthAuthentication;
    }

    @PostConstruct
    private void initialize() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
              .addFilter(springSecurityFilterChain)
              .build();

        mockSession = new MockHttpSession(webApplicationContext.getServletContext(), UUID.randomUUID().toString());
    }
}