package com.bsb.showcase.cf.dashboard.security;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.TestingAuthenticationToken;

import com.bsb.showcase.cf.dashboard.AbstractDashboardTest;
import com.bsb.showcase.cf.dashboard.user.EntityCleanupRule;
import com.bsb.showcase.cf.dashboard.user.User;
import com.bsb.showcase.cf.dashboard.user.UserRepository;

/**
 * @author Sebastien Gerard
 */
public class DashboardOauthAuthenticationProviderTest extends AbstractDashboardTest {

    @Rule
    public final EntityCleanupRule cleanupRule = new EntityCleanupRule();

    @Autowired
    private DashboardOauthAuthenticationProvider provider;

    @Autowired
    private UserRepository userRepository;

    @Test(expected = InternalAuthenticationServiceException.class)
    public void authenticationNotOauth() {
        provider.authenticate(new TestingAuthenticationToken("user", "pwd"));
    }

    @Test
    public void userAlreadyExist() {
        final User user = new User();
        user.setName("DashboardOauthAuthenticationProviderTest.userAlreadyExist");
        user.setFullName("John Smith");

        cleanupRule.saveEntity(userRepository, user);

        provider.authenticate(createAuthentication(user.getName(), user.getFullName()));

        assertTrue(userRepository.exists(user.getId()));
    }

    @Test
    public void userNotExist() {
        final String name = "DashboardOauthAuthenticationProviderTest.userAlreadyExist";
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

        auth.setDetails(new DashboardOAuth2AuthenticationDetails(new MockHttpServletRequest(), true, fullName));

        return auth;
    }
}
