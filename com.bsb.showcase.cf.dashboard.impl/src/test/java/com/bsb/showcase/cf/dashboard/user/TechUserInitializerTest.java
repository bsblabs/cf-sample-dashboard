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
public class TechUserInitializerTest extends AbstractDashboardTest {

    @Rule
    public final EntityCleanupRule cleanupRule = new EntityCleanupRule();

    @Autowired
    private TechUserRepository techUserRepository;

    @Test
    public void alreadyPresent() {
        final String name = "name";

        cleanupRule.saveEntity(techUserRepository, createUser(name));

        createInitializer(name).afterPropertiesSet();
    }

    @Test
    public void notPresent() {
        final String name = UUID.randomUUID().toString().substring(8);

        assertNull(techUserRepository.findByName(name));

        createInitializer(name).afterPropertiesSet();

        final TechUser techUser = techUserRepository.findByName(name);
        try {
            assertNotNull(techUser);
            assertEquals(StubPasswordEncoder.ENCODED_PWD, techUser.getPassword());
        } finally {
            techUserRepository.delete(techUser);
        }
    }

    private TechUser createUser(String name) {
        final TechUser user = new TechUser();

        user.setName(name);
        user.setPassword(name);

        return user;
    }

    private TechUserInitializer createInitializer(String name) {
        final TechUserInitializer initializer = new TechUserInitializer(techUserRepository, name, name);

        initializer.setPasswordEncoder(stubPasswordEncoder());

        return initializer;
    }
}
