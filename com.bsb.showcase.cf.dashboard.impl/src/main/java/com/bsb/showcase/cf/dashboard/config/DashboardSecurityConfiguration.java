package com.bsb.showcase.cf.dashboard.config;

import static com.bsb.showcase.cf.dashboard.config.FilterWrapper.*;
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

import com.bsb.showcase.cf.dashboard.security.DashboardAuthenticationSuccessHandler;
import com.bsb.showcase.cf.dashboard.security.DashboardOAuth2AuthenticationDetails;
import com.bsb.showcase.cf.dashboard.security.DashboardOAuth2ClientAuthenticationProcessingFilter;
import com.bsb.showcase.cf.dashboard.security.DashboardOAuthAuthenticationDetailsSource;
import com.bsb.showcase.cf.dashboard.security.DashboardOauthAuthenticationProvider;
import com.bsb.showcase.cf.dashboard.security.UaaLogoutRedirectStrategy;
import com.bsb.showcase.cf.dashboard.user.UserRepository;

/**
 * {@link Configuration} related to the dashboard security.
 *
 * @author Sebastien Gerard
 */
@Configuration
public class DashboardSecurityConfiguration {

    public static final String CURRENT_URI = "currentUri";

    public static String isManagingApp() {
        return "(authentication.details != null) " +
              "and (authentication.details instanceof T(" + DashboardOAuth2AuthenticationDetails.class.getName() + ")) "
              + "and authentication.details.managingApp";
    }

    @Value("${oauth.client.id}")
    private String clientId;

    @Value("${oauth.client.secret}")
    private String clientSecret;

    @Value("${oauth.info.uri}")
    private String oauthInfoUrl;

    @Value("${api.url}")
    private String apiUrl;

    @Value("${oauth.token.check.uri}")
    private String checkTokenUri;

    @Value("${oauth.authorization.uri}")
    private String authorizationUri;

    @Value("${oauth.token.access.uri}")
    private String accessUri;

    @Value("${oauth.logout.url}")
    private String logoutUrl;

    @Value("${dashboard.suid.file}")
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

    @Bean(name = "oAuth2ClientContextFilter")
    public FilterWrapper oAuth2ClientContextFilter() {
        // If it was a Filter bean it would be automatically added out of the Spring security filter chain
        return wrap(new OAuth2ClientContextFilter());
    }

    @Bean(name = "socialClientFilter")
    @Autowired
    public FilterWrapper socialClientFilter() {
        // If it was a Filter bean it would be automatically added out of the Spring security filter chain
        final DashboardOAuth2ClientAuthenticationProcessingFilter filter
              = new DashboardOAuth2ClientAuthenticationProcessingFilter();

        filter.setRestTemplate(oauthRestOperations());
        filter.setTokenServices(resourceServerTokenServices());
        filter.setAuthenticationManager(authenticationManager);
        filter.setRequiresAuthenticationRequestMatcher(dashboardEntryPointMatcher());
        filter.setDetailsSource(dashboardAuthenticationDetailsSource());
        filter.setAuthenticationSuccessHandler(new DashboardAuthenticationSuccessHandler());

        return wrap(filter);
    }

    @Bean(name = "protectedResourceDetails")
    @Scope(value = WebApplicationContext.SCOPE_SESSION)
    @Autowired
    public AuthorizationCodeResourceDetails protectedResourceDetails() {
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

    @Bean(name = "oauthClientContext")
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Autowired
    public OAuth2ClientContext oauthClientContext() {
        return new DefaultOAuth2ClientContext(accessTokenRequest());
    }

    @Bean(name = "accessTokenRequest")
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Autowired
    public AccessTokenRequest accessTokenRequest() {
        final DefaultAccessTokenRequest request = new DefaultAccessTokenRequest(httpServletRequest.getParameterMap());

        request.setCurrentUri(httpServletRequest.getAttribute(CURRENT_URI).toString());

        return request;
    }

    @Bean(name = "oauthRestOperations")
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Autowired
    public OAuth2RestTemplate oauthRestOperations() {
        return new OAuth2RestTemplate(protectedResourceDetails(), oauthClientContext());
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
        return new DashboardOAuthAuthenticationDetailsSource(oauthRestOperations(), suidFile, oauthInfoUrl, apiUrl);
    }

    @Bean(name = "oAuthAuthenticationProvider")
    @Autowired
    public DashboardOauthAuthenticationProvider oAuthAuthenticationProvider(UserRepository userRepository) {
        return new DashboardOauthAuthenticationProvider(userRepository);
    }

    @Bean(name = "dashboardLogoutSuccessHandler")
    public LogoutSuccessHandler dashboardLogoutSuccessHandler() {
        final SimpleUrlLogoutSuccessHandler logoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();

        logoutSuccessHandler.setRedirectStrategy(new UaaLogoutRedirectStrategy(logoutUrl));

        return logoutSuccessHandler;
    }

    @Bean(name = "dashboardLogoutUrlMatcher")
    public RequestMatcher dashboardLogoutUrlMatcher() {
        return new AntPathRequestMatcher("/dashboard/logout");
    }
}
