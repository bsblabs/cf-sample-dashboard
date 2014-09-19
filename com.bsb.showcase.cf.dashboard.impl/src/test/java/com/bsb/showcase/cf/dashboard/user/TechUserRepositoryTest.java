package com.bsb.showcase.cf.dashboard.user;

import static org.junit.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Sebastien Gerard
 */
public class TechUserRepositoryTest extends BaseUserRepositoryTest<TechUser> {

    public static final String DEFAULT_PASSWORD = "password";

    @Autowired
    private TechUserRepository repository;

    @Override
    protected TechUser createUser(String name) {
        final TechUser user = new TechUser();

        user.setName(name);
        user.setPassword(DEFAULT_PASSWORD);

        return user;
    }

    @Override
    protected TechUserRepository getRepository() {
        return repository;
    }

    @Override
    protected void assertUser(TechUser expected, TechUser actual) {
        super.assertUser(expected, actual);

        assertEquals(expected.getPassword(), actual.getPassword());
    }
}
