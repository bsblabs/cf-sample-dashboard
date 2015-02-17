package com.bsb.showcase.cf.service.config;

import static com.bsb.showcase.cf.service.config.FilterWrapper.*;
import static java.util.Arrays.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.context.WebApplicationContext;

import com.bsb.showcase.cf.service.security.DashboardAuthenticationDetails;
import com.bsb.showcase.cf.service.security.DashboardAuthenticationDetailsSource;
import com.bsb.showcase.cf.service.security.DashboardAuthenticationProcessingFilter;
import com.bsb.showcase.cf.service.security.DashboardAuthenticationProvider;
import com.bsb.showcase.cf.service.security.DashboardAuthenticationSuccessHandler;
import com.bsb.showcase.cf.service.security.DashboardLogoutRedirectStrategy;
import com.bsb.showcase.cf.service.user.UserRepository;

/**
 * {@link Configuration} related to the dashboard security.
 *
 * @author Sebastien Gerard
 */
@Configuration
public class DashboardSecurityConfiguration {

    public static final String CURRENT_URI = "currentUri";

    /**
     * Returns the SPeL expression checking that the current user is authorized
     * to manage this service.
     */
    public static String isManagingApp() {
        return "(authentication.details != null) " +
              "and (authentication.details instanceof T(" + DashboardAuthenticationDetails.class.getName() + ")) "
              + "and authentication.details.managingService " +
              "and hasRole('ROLE_" + ApplicationWebSecurityConfigurerAdapter.ROLE_USER + "')";
    }

    @Value("${cf.uaa.oauth.client.id}")
    private String clientId;

    @Value("${cf.uaa.oauth.client.secret}")
    private String clientSecret;

    @Value("${cf.uaa.oauth.info.uri}")
    private String oauthInfoUrl;

    @Value("${cf.api.url}")
    private String apiUrl;

    @Value("${cf.uaa.oauth.token.check.uri}")
    private String checkTokenUri;

    @Value("${cf.uaa.oauth.authorization.uri}")
    private String authorizationUri;

    @Value("${cf.uaa.oauth.token.access.uri}")
    private String accessUri;

    @Value("${cf.uaa.oauth.logout.url}")
    private String logoutUrl;

    @Value("${cf.service.suid.file}")
    private String suidFile;

    @Autowired
    @Qualifier("authenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Bean(name = "dashboardEntryPointMatcher")
    public RequestMatcher dashboardEntryPointMatcher() {
        return new AntPathRequestMatcher("/dashboard/**");
    }

    @Bean(name = "dashboardClientContextFilter")
    public FilterWrapper dashboardClientContextFilter() {
        // If it was a Filter bean it would be automatically added out of the Spring security filter chain.
        return wrap(new OAuth2ClientContextFilter());
    }

    @Bean(name = "dashboardSocialClientFilter")
    @Autowired
    public FilterWrapper dashboardSocialClientFilter() {
        // If it was a Filter bean it would be automatically added out of the Spring security filter chain.
        final DashboardAuthenticationProcessingFilter filter
              = new DashboardAuthenticationProcessingFilter();

        filter.setRestTemplate(dashboardRestOperations());
        filter.setTokenServices(resourceServerTokenServices());
        filter.setAuthenticationManager(authenticationManager);
        filter.setRequiresAuthenticationRequestMatcher(dashboardEntryPointMatcher());
        filter.setDetailsSource(dashboardAuthenticationDetailsSource());
        filter.setAuthenticationSuccessHandler(new DashboardAuthenticationSuccessHandler());

        return wrap(filter);
    }

    @Bean(name = "dashboardProtectedResourceDetails")
    @Scope(value = WebApplicationContext.SCOPE_SESSION)
    @Autowired
    public AuthorizationCodeResourceDetails dashboardProtectedResourceDetails() {
        final AuthorizationCodeResourceDetails resourceDetails = new AuthorizationCodeResourceDetails() {
            @Override
            public boolean isClientOnly() {
                return true;
            }
        };

        resourceDetails.setClientId(clientId);
        resourceDetails.setClientSecret(clientSecret);
        resourceDetails.setUserAuthorizationUri(authorizationUri);
        resourceDetails.setAccessTokenUri(accessUri);
        resourceDetails.setUseCurrentUri(true);
        resourceDetails.setScope(asList("openid", "cloud_controller_service_permissions.read"));

        return resourceDetails;
    }

    @Bean(name = "dashboardClientContext")
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Autowired
    public OAuth2ClientContext dashboardClientContext() {
        return new DefaultOAuth2ClientContext(dashboardAccessTokenRequest());
    }

    @Bean(name = "dashboardAccessTokenRequest")
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Autowired
    public AccessTokenRequest dashboardAccessTokenRequest() {
        final DefaultAccessTokenRequest request = new DefaultAccessTokenRequest(httpServletRequest.getParameterMap());

        request.setCurrentUri(httpServletRequest.getAttribute(CURRENT_URI).toString());

        return request;
    }

    @Bean(name = "dashboardRestOperations")
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Autowired
    public OAuth2RestTemplate dashboardRestOperations() {
        return new OAuth2RestTemplate(dashboardProtectedResourceDetails(), dashboardClientContext());
    }

    @Bean(name = "accessTokenConverter")
    public AccessTokenConverter accessTokenConverter() {
        final DefaultAccessTokenConverter defaultAccessTokenConverter = new DefaultAccessTokenConverter();

        final DefaultUserAuthenticationConverter userTokenConverter = new DefaultUserAuthenticationConverter();
        userTokenConverter.setDefaultAuthorities(new String[]{"ROLE_" + ApplicationWebSecurityConfigurerAdapter.ROLE_USER});

        defaultAccessTokenConverter.setUserTokenConverter(userTokenConverter);

        return defaultAccessTokenConverter;
    }

    @Bean(name = "resourceServerTokenServices")
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Autowired
    public ResourceServerTokenServices resourceServerTokenServices() {
        final RemoteTokenServices remoteTokenServices = new RemoteTokenServices();

        remoteTokenServices.setClientId(clientId);
        remoteTokenServices.setClientSecret(clientSecret);
        remoteTokenServices.setCheckTokenEndpointUrl(checkTokenUri);
        remoteTokenServices.setAccessTokenConverter(accessTokenConverter());

        return remoteTokenServices;
    }

    @Bean(name = "dashboardAuthenticationDetailsSource")
    @Autowired
    public AuthenticationDetailsSource<HttpServletRequest, ?> dashboardAuthenticationDetailsSource() {
        return new DashboardAuthenticationDetailsSource(dashboardRestOperations(), suidFile, oauthInfoUrl, apiUrl);
    }

    @Bean(name = "dashboardAuthenticationProvider")
    @Autowired
    public DashboardAuthenticationProvider dashboardAuthenticationProvider(UserRepository userRepository) {
        return new DashboardAuthenticationProvider(userRepository);
    }

    @Bean(name = "dashboardLogoutSuccessHandler")
    public LogoutSuccessHandler dashboardLogoutSuccessHandler() {
        final SimpleUrlLogoutSuccessHandler logoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();

        logoutSuccessHandler.setRedirectStrategy(new DashboardLogoutRedirectStrategy(logoutUrl));

        return logoutSuccessHandler;
    }

    @Bean(name = "dashboardLogoutUrlMatcher")
    public RequestMatcher dashboardLogoutUrlMatcher() {
        return new AntPathRequestMatcher("/dashboard/logout");
    }
}
