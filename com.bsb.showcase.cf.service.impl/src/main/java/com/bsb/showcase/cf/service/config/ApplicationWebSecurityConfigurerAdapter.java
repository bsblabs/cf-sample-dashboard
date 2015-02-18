package com.bsb.showcase.cf.service.config;

import static com.bsb.showcase.cf.service.config.DashboardSecurityConfiguration.*;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.context.request.RequestContextListener;

/**
 * {@link WebSecurityConfigurerAdapter} securing the web application.
 *
 * @author Sebastien Gerard
 */
@Configuration
@EnableWebMvcSecurity
public class ApplicationWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    public static final String ROLE_DASHBOARD = "DASHBOARD";

    public static final String ROLE_WEB_SERVICE = "WEB_SERVICE";

    @Autowired
    private DataSource dataSource;

    @Autowired
    @Qualifier("passwordEncoder")
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("dashboardAuthenticationProvider")
    private AuthenticationProvider dashboardAuthenticationProvider;

    @Bean(name = "authenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    @ConditionalOnMissingBean(RequestContextListener.class)
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
              .antMatcher("/webjars/**")
              .antMatcher("/css/**")
              .antMatcher("/js/**")
              .antMatcher("/")
              .authorizeRequests()
              .anyRequest()
              .permitAll()
              .and().csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
              .jdbcAuthentication()
              .dataSource(dataSource)
              .usersByUsernameQuery("select name,password,true from WEB_SERVICE_USER where name=?")
              .authoritiesByUsernameQuery("select name, '" + ROLE_WEB_SERVICE + "' from WEB_SERVICE_USER where name=?")
              .passwordEncoder(passwordEncoder)
              .rolePrefix("ROLE_");

        auth.authenticationProvider(dashboardAuthenticationProvider);
    }

    /**
     * {@link WebSecurityConfigurerAdapter} securing web-services.
     */
    @Configuration
    @Order(1)
    public static class WebServiceSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        @Qualifier("webServiceEntryPointMatcher")
        private RequestMatcher webServiceEntryPointMatcher;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                  .requestMatcher(webServiceEntryPointMatcher)
                  .authorizeRequests()
                  .anyRequest().hasRole(ROLE_WEB_SERVICE)
                  .and()
                  .httpBasic()
                  .realmName("Sample Cloud Foundry Service");
        }
    }

    /**
     * {@link WebSecurityConfigurerAdapter} securing the dashboard.
     */
    @Configuration
    @Order(2)
    public static class DashboardWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        @Qualifier("dashboardEntryPointMatcher")
        private RequestMatcher dashboardEntryPointMatcher;

        @Autowired
        @Qualifier("dashboardClientContextFilter")
        private FilterWrapper dashboardClientContextFilter;

        @Autowired
        @Qualifier("dashboardSocialClientFilter")
        private FilterWrapper dashboardSocialClientFilter;

        @Autowired
        @Qualifier("dashboardLogoutSuccessHandler")
        private LogoutSuccessHandler dashboardLogoutSuccessHandler;

        @Autowired
        @Qualifier("dashboardLogoutUrlMatcher")
        private RequestMatcher dashboardLogoutUrlMatcher;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                  .requestMatcher(dashboardEntryPointMatcher)
                  .authorizeRequests()
                  .anyRequest().access(isManagingApp())
                  .and()

                  .addFilterBefore(dashboardClientContextFilter.unwrap(), AbstractPreAuthenticatedProcessingFilter.class)
                  .addFilterBefore(dashboardSocialClientFilter.unwrap(), AbstractPreAuthenticatedProcessingFilter.class)

                  .logout()
                  .logoutSuccessHandler(dashboardLogoutSuccessHandler)
                  .logoutRequestMatcher(dashboardLogoutUrlMatcher);
        }
    }
}
