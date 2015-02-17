package com.bsb.showcase.cf.test.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author Sebastien Gerard
 */
public class TestableAuthenticationManager implements AuthenticationManager {

    public static final String ROLE_USER = "ROLE_USER";

    public static TestableAuthenticationManager testableAuthenticationManager() {
        return new TestableAuthenticationManager();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final TestingAuthenticationToken token
              = new TestingAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), ROLE_USER);

        token.setDetails(authentication.getDetails());

        return token;
    }
}
