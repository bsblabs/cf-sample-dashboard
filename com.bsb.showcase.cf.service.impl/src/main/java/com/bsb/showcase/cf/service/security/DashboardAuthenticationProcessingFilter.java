package com.bsb.showcase.cf.service.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * Extension of {@link OAuth2ClientAuthenticationProcessingFilter} that uses the
 * {@link org.springframework.security.authentication.AuthenticationManager}.
 * This implementation also starts authentication if there is no authentication and
 * if the current request requires authentication.
 *
 * @author Sebastien Gerard
 */
public class DashboardAuthenticationProcessingFilter extends OAuth2ClientAuthenticationProcessingFilter {

    private AuthenticationDetailsSource<HttpServletRequest, ?> detailsSource;

    public DashboardAuthenticationProcessingFilter() {
        super("/");
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return (SecurityContextHolder.getContext().getAuthentication() == null)
              && super.requiresAuthentication(request, response);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
          throws AuthenticationException, IOException, ServletException {
        final Authentication authentication = super.attemptAuthentication(request, response);

        if (detailsSource != null) {
            ((OAuth2Authentication) authentication).setDetails(detailsSource.buildDetails(request));
        }

        return getAuthenticationManager().authenticate(authentication);
    }

    /**
     * Sets the optional source providing {@link Authentication#getDetails() authentication details}.
     */
    public void setDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> detailsSource) {
        this.detailsSource = detailsSource;
    }
}
