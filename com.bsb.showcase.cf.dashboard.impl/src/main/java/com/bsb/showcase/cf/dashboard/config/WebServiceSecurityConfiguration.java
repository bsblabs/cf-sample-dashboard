package com.bsb.showcase.cf.dashboard.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.bsb.showcase.cf.dashboard.user.WebServiceUserInitializer;
import com.bsb.showcase.cf.dashboard.user.WebServiceUserRepository;

/**
 * {@link Configuration} related to the web-service security.
 *
 * @author Sebastien Gerard
 */
@Configuration
public class WebServiceSecurityConfiguration {

    @Bean(name = "webServiceEntryPointMatcher")
    public RequestMatcher webServiceEntryPointMatcher() {
        return new AntPathRequestMatcher("/services/**");
    }

    @Bean
    @Autowired
    public WebServiceUserInitializer webServiceUserInitializer(WebServiceUserRepository webServiceUserRepository,
                                                               @Qualifier("passwordEncoder") PasswordEncoder passwordEncoder) {
        return new WebServiceUserInitializer(webServiceUserRepository, passwordEncoder);
    }

    @Bean(name = "passwordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
