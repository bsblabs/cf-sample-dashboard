package com.bsb.showcase.cf.dashboard.security;

import java.util.Collection;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

/**
 * {@link AccessDecisionVoter} voting accordingly to
 * {@link DashboardOAuth2AuthenticationDetails#isManagingApp()}.
 * <p/>
 * If the user is not yet authenticated, this voter {@link #ACCESS_ABSTAIN abstains}.
 * When the user is authenticated, if the user is allowed to manage the application,
 * he is {@link #ACCESS_GRANTED granted}, otherwise he is {@link #ACCESS_DENIED denied}.
 *
 * @author Sebastien Gerard
 */
public class CloudInstanceAccessDecisionVoter implements AccessDecisionVoter<Object> {

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return false;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        if (authentication.isAuthenticated()
              && authentication.getDetails() instanceof DashboardOAuth2AuthenticationDetails) {
            final DashboardOAuth2AuthenticationDetails details
                  = (DashboardOAuth2AuthenticationDetails) authentication.getDetails();

            return details.isManagingApp() ? ACCESS_GRANTED : ACCESS_DENIED;
        } else {
            return ACCESS_ABSTAIN;
        }
    }
}

