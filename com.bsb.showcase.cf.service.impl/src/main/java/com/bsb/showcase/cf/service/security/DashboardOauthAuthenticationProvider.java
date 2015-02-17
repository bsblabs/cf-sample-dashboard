package com.bsb.showcase.cf.service.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import com.bsb.showcase.cf.service.user.User;
import com.bsb.showcase.cf.service.user.UserRepository;

/**
 * {@link AuthenticationProvider} used to make the link between an OAuth user
 * and an internal User.
 *
 * @author Sebastien Gerard
 */
public class DashboardOauthAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;

    public DashboardOauthAuthenticationProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String name = authentication.getName();
        final Object details = authentication.getDetails();

        if (!(details instanceof DashboardOAuth2AuthenticationDetails)) {
            throw new InternalAuthenticationServiceException("The authentication details [" + details
                  + "] are not an instance of " + DashboardOAuth2AuthenticationDetails.class.getSimpleName());
        }

        try {
            final User byName = userRepository.findByName(name);
            if (byName == null) {
                final DashboardOAuth2AuthenticationDetails oauthDetails = (DashboardOAuth2AuthenticationDetails) details;

                final User user = new User();
                user.setName(name);
                user.setFullName(oauthDetails.getUserFullName());

                userRepository.save(user);
            }
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException("Error while creating a user based on [" + name + "]", e);
        }

        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2Authentication.class.isAssignableFrom(authentication);
    }
}

