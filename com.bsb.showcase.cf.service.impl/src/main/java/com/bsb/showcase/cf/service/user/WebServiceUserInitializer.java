package com.bsb.showcase.cf.service.user;

import javax.annotation.PostConstruct;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Services creating an initial web service-user if not present.
 *
 * @author Sebastien Gerard
 */
public class WebServiceUserInitializer {

    private final WebServiceUserRepository webServiceUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final String user;
    private final String password;

    public WebServiceUserInitializer(WebServiceUserRepository webServiceUserRepository, PasswordEncoder passwordEncoder) {
        this(webServiceUserRepository, passwordEncoder, "admin", "admin");
    }

    WebServiceUserInitializer(WebServiceUserRepository webServiceUserRepository, PasswordEncoder passwordEncoder,
                              String user, String password) {
        this.webServiceUserRepository = webServiceUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.user = user;
        this.password = password;
    }

    @PostConstruct
    public void initializeUser() {
        if (webServiceUserRepository.findByName(user) == null) {
            final WebServiceUser webServiceUser = new WebServiceUser();

            webServiceUser.setName(user);
            webServiceUser.setPassword(passwordEncoder.encode(password));

            webServiceUserRepository.save(webServiceUser);
        }
    }
}
