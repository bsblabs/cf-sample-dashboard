package com.bsb.showcase.cf.test.dashboard;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

/**
 * @author Sebastien Gerard
 */
public class TestableResourceServerTokenServices implements ResourceServerTokenServices {

    private OAuth2Authentication auth2Authentication;

    public static TestableResourceServerTokenServices noAuthentication() {
        return new TestableResourceServerTokenServices(null);
    }

    public static TestableResourceServerTokenServices withAuthentication(OAuth2Authentication auth2Authentication) {
        return new TestableResourceServerTokenServices(auth2Authentication);
    }

    public TestableResourceServerTokenServices(OAuth2Authentication auth2Authentication) {
        this.auth2Authentication = auth2Authentication;
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException {
        if (auth2Authentication == null) {
            throw new InvalidTokenException("No authentication");
        }

        return auth2Authentication;
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException();
    }
}
