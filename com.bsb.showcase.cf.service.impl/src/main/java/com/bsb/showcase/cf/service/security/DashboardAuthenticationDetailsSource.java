package com.bsb.showcase.cf.service.security;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * {@link AuthenticationDetailsSource} providing extra details about the current
 * user and his grant to manage the current service instance.
 *
 * @author Sebastien Gerard
 */
public class DashboardAuthenticationDetailsSource
      implements AuthenticationDetailsSource<HttpServletRequest, OAuth2AuthenticationDetails> {

    /**
     * Token to use in {@link #getCheckUrl()} to specify the service instance id.
     */
    public static final String TOKEN_SUID = "[SUID]";

    /**
     * Key used in the JSON map returned by the call to {@link #getCheckUrl()} and associated
     * to the service instance id.
     */
    public static final String MANAGED_KEY = "manage";

    private static final Logger logger = LoggerFactory.getLogger(DashboardAuthenticationDetailsSource.class);

    private final RestTemplate restTemplate;
    private final String serviceInstanceIdFile;
    private final String userInfoUrl;
    private final String apiUrl;

    /**
     * @param restTemplate the template to use to contact Cloud components
     * @param serviceInstanceIdFile the file containing the service instance id
     * @param userInfoUrl the URL used to get the current OAuth user details
     * @param apiUrl the URL used to get the service instance permission
     */
    public DashboardAuthenticationDetailsSource(RestTemplate restTemplate, String serviceInstanceIdFile,
                                                String userInfoUrl, String apiUrl) {
        this.restTemplate = restTemplate;
        this.serviceInstanceIdFile = serviceInstanceIdFile;
        this.userInfoUrl = userInfoUrl;
        this.apiUrl = apiUrl;
    }

    @Override
    public DashboardAuthenticationDetails buildDetails(HttpServletRequest request) {
        return new DashboardAuthenticationDetails(request, isManagingApp(), getUserFullName());
    }

    /**
     * Returns the full name (first + last name) contains in the specified map.
     */
    protected String getUserFullName(Map<String, String> map) {
        if (map.containsKey("name")) {
            return map.get("name");
        }
        if (map.containsKey("formattedName")) {
            return map.get("formattedName");
        }
        if (map.containsKey("fullName")) {
            return map.get("fullName");
        }
        String firstName = null;
        if (map.containsKey("firstName")) {
            firstName = map.get("firstName");
        }
        if (map.containsKey("givenName")) {
            firstName = map.get("givenName");
        }
        String lastName = null;
        if (map.containsKey("lastName")) {
            lastName = map.get("lastName");
        }
        if (map.containsKey("familyName")) {
            lastName = map.get("familyName");
        }
        if (firstName != null) {
            if (lastName != null) {
                return firstName + " " + lastName;
            }
        }
        return null;
    }

    /**
     * Checks whether the user is allowed to manage the current service instance.
     */
    private boolean isManagingApp() {
        final String url = getCheckUrl();
        try {
            final Map<?, ?> result = restTemplate.getForObject(url, Map.class);

            return Boolean.TRUE.toString().equals(result.get(MANAGED_KEY).toString().toLowerCase());
        } catch (RestClientException e) {
            logger.error("Error while retrieving authorization from [" + url + "].", e);
            return false;
        }
    }

    /**
     * Returns the full name of the current user (first + last name).
     */
    @SuppressWarnings("unchecked")
    private String getUserFullName() {
        try {
            return getUserFullName(restTemplate.getForObject(userInfoUrl, Map.class));
        } catch (RestClientException e) {
            logger.error("Error while user full name from [" + userInfoUrl + "].", e);
            return "";
        }
    }

    /**
     * Returns the URL used to check whether the current user is allowed
     * to access the current service instance.
     */
    private String getCheckUrl() {
        return apiUrl.replace(TOKEN_SUID, getServiceInstance());
    }

    /**
     * Returns the current service instance id.
     */
    private String getServiceInstance() {
        final File config = new File(serviceInstanceIdFile);
        try {
            return new String(FileCopyUtils.copyToByteArray(config));
        } catch (IOException e) {
            throw new InternalAuthenticationServiceException("Error while retrieving the service instance", e);
        }
    }
}
