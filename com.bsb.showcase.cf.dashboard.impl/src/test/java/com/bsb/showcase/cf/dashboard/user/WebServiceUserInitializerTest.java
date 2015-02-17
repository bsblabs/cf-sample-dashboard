package com.bsb.showcase.cf.dashboard.user;

import static com.bsb.showcase.cf.test.dashboard.StubPasswordEncoder.*;
import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bsb.showcase.cf.dashboard.AbstractDashboardTest;
import com.bsb.showcase.cf.test.dashboard.StubPasswordEncoder;

/**
 * @author Sebastien Gerard
 */
public class WebServiceUserInitializerTest extends AbstractDashboardTest {

    @Rule
    public final EntityCleanupRule cleanupRule = new EntityCleanupRule();

    @Autowired
    private WebServiceUserRepository webServiceUserRepository;

    @Test
    public void alreadyPresent() {
        final String name = "name";

        cleanupRule.saveEntity(webServiceUserRepository, createUser(name));

        createInitializer(name).initializeUser();
    }

    @Test
    public void notPresent() {
        final String name = UUID.randomUUID().toString().substring(8);

        assertNull(webServiceUserRepository.findByName(name));

        createInitializer(name).initializeUser();

        final WebServiceUser webServiceUser = webServiceUserRepository.findByName(name);
        try {
            assertNotNull(webServiceUser);
            assertEquals(StubPasswordEncoder.ENCODED_PWD, webServiceUser.getPassword());
        } finally {
            webServiceUserRepository.delete(webServiceUser);
        }
    }

    private WebServiceUser createUser(String name) {
        final WebServiceUser user = new WebServiceUser();

        user.setName(name);
        user.setPassword(name);

        return user;
    }

    private WebServiceUserInitializer createInitializer(String name) {
        return new WebServiceUserInitializer(webServiceUserRepository, stubPasswordEncoder(), name, name);
    }
}
