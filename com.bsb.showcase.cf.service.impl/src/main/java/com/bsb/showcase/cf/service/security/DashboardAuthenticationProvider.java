package com.bsb.showcase.cf.service.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import com.bsb.showcase.cf.service.user.DashboardUser;
import com.bsb.showcase.cf.service.user.DashboardUserRepository;

/**
 * {@link AuthenticationProvider} used to make the link between an OAuth user
 * and an internal User.
 *
 * @author Sebastien Gerard
 */
public class DashboardAuthenticationProvider implements AuthenticationProvider {

    private final DashboardUserRepository userRepository;

    public DashboardAuthenticationProvider(DashboardUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String name = authentication.getName();
        final Object details = authentication.getDetails();

        if (!(details instanceof DashboardAuthenticationDetails)) {
            throw new InternalAuthenticationServiceException("The authentication details [" + details
                  + "] are not an instance of " + DashboardAuthenticationDetails.class.getSimpleName());
        }

        try {
            final DashboardUser byName = userRepository.findByName(name);
            if (byName == null) {
                final DashboardAuthenticationDetails oauthDetails = (DashboardAuthenticationDetails) details;

                final DashboardUser user = new DashboardUser();
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

