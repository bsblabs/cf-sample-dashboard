package com.bsb.showcase.cf.dashboard.user;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Services creating an initial technical user if not present.
 *
 * @author Sebastien Gerard
 */
public class TechUserInitializer implements InitializingBean {

    private final TechUserRepository techUserRepository;
    private PasswordEncoder passwordEncoder;
    private final String user;
    private final String password;

    @Autowired
    public TechUserInitializer(TechUserRepository techUserRepository) {
        this(techUserRepository, "admin", "admin");
    }

    TechUserInitializer(TechUserRepository techUserRepository, String user, String password) {
        this.techUserRepository = techUserRepository;
        this.user = user;
        this.password = password;
    }

    @Override
    public void afterPropertiesSet() {
        if (techUserRepository.findByName(user) == null) {
            final TechUser techUser = new TechUser();

            techUser.setName(user);
            techUser.setPassword(passwordEncoder.encode(password));

            techUserRepository.save(techUser);
        }
    }

    @Required
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
