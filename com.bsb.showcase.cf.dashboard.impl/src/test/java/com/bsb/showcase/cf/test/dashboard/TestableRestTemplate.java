package com.bsb.showcase.cf.test.dashboard;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import junit.framework.AssertionFailedError;

/**
 * @author Sebastien Gerard
 */
public class TestableRestTemplate extends RestTemplate implements OAuth2RestOperations {

    private final Map<String, Map<String, String>> results = new LinkedHashMap<>();

    public static TestableRestTemplate testableRestTemplate() {
        return new TestableRestTemplate();
    }

    public TestableRestTemplate() {
        super(new SimpleClientHttpRequestFactory());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getForObject(String url, Class<T> responseType, Object... urlVariables) throws RestClientException {
        if (!Map.class.isAssignableFrom(responseType)) {
            throw new IllegalArgumentException("Only maps are supported, but was [" + responseType + "]");
        }

        if (!results.containsKey(url)) {
            throw new AssertionFailedError("The URL [" + url + "] is not expected");
        }

        return (T) results.get(url);
    }

    @Override
    public OAuth2AccessToken getAccessToken() throws UserRedirectRequiredException {
        return new DefaultOAuth2AccessToken("TOKEN");
    }

    @Override
    public OAuth2ClientContext getOAuth2ClientContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OAuth2ProtectedResourceDetails getResource() {
        throw new UnsupportedOperationException();
    }

    public TestableRestTemplate addResult(String url, Map<String, String> result) {
        results.put(url, result);
        return this;
    }
}
