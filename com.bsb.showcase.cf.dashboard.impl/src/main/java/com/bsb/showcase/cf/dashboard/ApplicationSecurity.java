package com.bsb.showcase.cf.dashboard;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author Sebastien Gerard
 */
@Configuration
@ImportResource("classpath:/META-INF/dashboard/oauth-form-security-strategy.xml")
public class ApplicationSecurity {

}
