package com.bsb.showcase.cf.service.security;

import static com.bsb.showcase.cf.test.service.user.UserTestHelper.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.transaction.annotation.Transactional;

import com.bsb.showcase.cf.service.AbstractCfServiceTest;
import com.bsb.showcase.cf.service.user.DashboardUser;
import com.bsb.showcase.cf.service.user.DashboardUserRepository;
import com.bsb.showcase.cf.test.service.user.UserTestHelper;

/**
 * @author Sebastien Gerard
 */
@Transactional
public class DashboardAuthenticationProviderTest extends AbstractCfServiceTest {

    @Autowired
    private DashboardAuthenticationProvider provider;

    @Autowired
    private DashboardUserRepository userRepository;

    private UserTestHelper testHelper;

    @Test
    public void supportsOAuth2AuthenticationSupported() {
        assertTrue(createProvider().supports(OAuth2Authentication.class));
    }

    @Test
    public void supportsTestingAuthenticationTokenNotSupported() {
        assertFalse(createProvider().supports(TestingAuthenticationToken.class));
    }

    @Test(expected = InternalAuthenticationServiceException.class)
    public void authenticationNotOauth() {
        provider.authenticate(new TestingAuthenticationToken("user", "pwd"));
    }

    @Test
    public void authenticateUserAlreadyExist() {
        final DashboardUser john = userRepository.save(johnUser());

        createProvider().authenticate(createAuthentication(john));

        testHelper.assertExists(john);
    }

    @Test
    public void authenticateUserNotExist() {
        final DashboardUser john = johnUser();

        createProvider().authenticate(createAuthentication(john));

        testHelper.assertExists(john);
    }

    @Test(expected = InternalAuthenticationServiceException.class)
    public void authenticateErrorSave(){
        final DashboardUserRepository mock = mock(DashboardUserRepository.class);
        when(mock.save(Matchers.<DashboardUser>any()))
              .thenThrow(new RuntimeException("Planned exception"));

        createProvider(mock).authenticate(createAuthentication(johnUser()));
    }

    private AuthenticationProvider createProvider() {
        return createProvider(userRepository);
    }

    private AuthenticationProvider createProvider(DashboardUserRepository repository) {
        return new DashboardAuthenticationProvider(repository);
    }

    private TestingAuthenticationToken createAuthentication(DashboardUser user) {
        final TestingAuthenticationToken auth = new TestingAuthenticationToken(user.getName(), null);

        auth.setDetails(new DashboardAuthenticationDetails(new MockHttpServletRequest(), true, user.getFullName()));

        return auth;
    }

    @PostConstruct
    private void initializeHelper(){
        this.testHelper = new UserTestHelper(this.userRepository);
    }
}
