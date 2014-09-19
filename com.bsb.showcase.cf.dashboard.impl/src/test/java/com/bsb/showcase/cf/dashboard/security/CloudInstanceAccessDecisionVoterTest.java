package com.bsb.showcase.cf.dashboard.security;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.bsb.showcase.cf.dashboard.AbstractDashboardTest;

/**
 * @author Sebastien Gerard
 */
public class CloudInstanceAccessDecisionVoterTest extends AbstractDashboardTest {

    @Test
    public void voteNotAuthenticated() {
        assertEquals(AccessDecisionVoter.ACCESS_ABSTAIN, vote(createNotAuthenticated()));
    }

    @Test
    public void voteAuthenticatedNotOauthDetails() {
        assertEquals(AccessDecisionVoter.ACCESS_ABSTAIN, vote(createAuthenticatedNoDetails()));
    }

    @Test
    public void voteAuthenticatedOauthDetailsNotGranted() {
        final TestingAuthenticationToken details = createAuthenticatedNoDetails();
        details.setDetails(createDetails(false));

        assertEquals(AccessDecisionVoter.ACCESS_DENIED, vote(details));
    }

    @Test
    public void voteAuthenticatedOauthDetailsGranted() {
        final TestingAuthenticationToken details = createAuthenticatedNoDetails();
        details.setDetails(createDetails(true));

        assertEquals(AccessDecisionVoter.ACCESS_GRANTED, vote(details));
    }

    private int vote(Authentication authentication) {
        return new CloudInstanceAccessDecisionVoter().vote(authentication, null,
              Collections.<ConfigAttribute>emptyList());
    }

    private TestingAuthenticationToken createNotAuthenticated() {
        return new TestingAuthenticationToken("principal", "credentials");
    }

    private TestingAuthenticationToken createAuthenticatedNoDetails() {
        return new TestingAuthenticationToken("principal", "credentials", "ROLE_USER");
    }

    private DashboardOAuth2AuthenticationDetails createDetails(boolean manageApp) {
        return new DashboardOAuth2AuthenticationDetails(new MockHttpServletRequest(), manageApp, "full name");
    }
}
