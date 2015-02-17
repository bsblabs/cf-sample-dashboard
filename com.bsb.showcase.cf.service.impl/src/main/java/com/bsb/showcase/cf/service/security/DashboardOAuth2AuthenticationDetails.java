package com.bsb.showcase.cf.service.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

/**
 * Extension of {@link OAuth2AuthenticationDetails} providing extra details about the current
 * user and his grant to manage the current service instance.
 *
 * @author Sebastien Gerard
 */
@SuppressWarnings("serial")
public class DashboardOAuth2AuthenticationDetails extends OAuth2AuthenticationDetails {

    private final boolean managingApp;
    private final String userFullName;

    /**
     * Records the access token value and remote address and will also set the session Id if a session already exists
     * (it won't create one).
     *
     * @param request that the authentication request was received from
     * @param managingApp flag indicating whether the current user is allowed to manage
     * @param userFullName the full user name (first name + last name)
     */
    public DashboardOAuth2AuthenticationDetails(HttpServletRequest request, boolean managingApp, String userFullName) {
        super(request);

        this.managingApp = managingApp;
        this.userFullName = userFullName;
    }

    /**
     * Returns the flag indicating whether the current user is allowed to manage
     * the current service instance.
     */
    public boolean isManagingApp() {
        return managingApp;
    }

    /**
     * Returns the current full user name (first name + last name).
     */
    public String getUserFullName() {
        return userFullName;
    }
}
