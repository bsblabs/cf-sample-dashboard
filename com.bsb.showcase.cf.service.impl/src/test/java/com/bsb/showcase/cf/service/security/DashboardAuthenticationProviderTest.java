package com.bsb.showcase.cf.service.security;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.TestingAuthenticationToken;

import com.bsb.showcase.cf.service.AbstractCfServiceTest;
import com.bsb.showcase.cf.service.user.EntityCleanupRule;
import com.bsb.showcase.cf.service.user.User;
import com.bsb.showcase.cf.service.user.UserRepository;

/**
 * @author Sebastien Gerard
 */
public class DashboardAuthenticationProviderTest extends AbstractCfServiceTest {

    @Rule
    public final EntityCleanupRule cleanupRule = new EntityCleanupRule();

    @Autowired
    private DashboardAuthenticationProvider provider;

    @Autowired
    private UserRepository userRepository;

    @Test(expected = InternalAuthenticationServiceException.class)
    public void authenticationNotOauth() {
        provider.authenticate(new TestingAuthenticationToken("user", "pwd"));
    }

    @Test
    public void userAlreadyExist() {
        final User user = new User();
        user.setName("DashboardAuthenticationProviderTest.userAlreadyExist");
        user.setFullName("John Smith");

        cleanupRule.saveEntity(userRepository, user);

        provider.authenticate(createAuthentication(user.getName(), user.getFullName()));

        assertTrue(userRepository.exists(user.getId()));
    }

    @Test
    public void userNotExist() {
        final String name = "DashboardAuthenticationProviderTest.userAlreadyExist";
        final String fullName = "John Smith";

        provider.authenticate(createAuthentication(name, fullName));

        final User user = userRepository.findByName(name);
        try {
            assertEquals(name, user.getName());
            assertEquals(fullName, user.getFullName());
        } finally {
            userRepository.delete(user);
        }
    }

    private TestingAuthenticationToken createAuthentication(String name, String fullName) {
        final TestingAuthenticationToken auth = new TestingAuthenticationToken(name, null);

        auth.setDetails(new DashboardAuthenticationDetails(new MockHttpServletRequest(), true, fullName));

        return auth;
    }
}
