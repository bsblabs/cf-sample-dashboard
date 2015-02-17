package com.bsb.showcase.cf.service.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.RedirectStrategy;

/**
 * {@link RedirectStrategy} redirecting to the UAA logout page.
 * <p/>
 * When the UAA has performed the logout on its side, the page is
 * redirected again to the specified URL.
 *
 * @author Sebastien Gerard
 */
public class DashboardLogoutRedirectStrategy implements RedirectStrategy {

    private final String uaaLogoutUrl;

    public DashboardLogoutRedirectStrategy(String uaaLogoutUrl) {
        this.uaaLogoutUrl = uaaLogoutUrl;
    }

    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        final String redirectUrl = uaaLogoutUrl + "?redirect=" + url;

        response.sendRedirect(response.encodeRedirectURL(redirectUrl));
    }
}
