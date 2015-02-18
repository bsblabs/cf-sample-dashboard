package com.bsb.showcase.cf.service.security;

import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.bsb.showcase.cf.service.AbstractCfServiceTest;

/**
 * @author Sebastien Gerard
 */
public class DashboardAuthenticationDetailsSourceTest extends AbstractCfServiceTest {

    public static final String API_URL = "http://api.domain.com/v2/service_instances/[SUID]/permissions";
    public static final String USER_INFO_URL = "http://uaa.domain.com/userinfo";
    public static final String API_URL_WITH_DEFAULT_SUID = "http://api.domain.com/v2/service_instances/1234567890/permissions";

    @Value("${cf.service.suid.file}")
    private String serviceInstanceIdFile;

    @Test
    public void isManagingAppAllowed() {
        final DashboardAuthenticationDetailsSource source = createSource(Boolean.TRUE);

        final DashboardAuthenticationDetails details = source.buildDetails(new MockHttpServletRequest());

        assertTrue(details.isManagingService());
    }

    @Test
    public void isManagingAppNotAllowed() {
        final DashboardAuthenticationDetailsSource source = createSource(Boolean.FALSE);

        final DashboardAuthenticationDetails details = source.buildDetails(new MockHttpServletRequest());

        assertFalse(details.isManagingService());
    }

    @Test
    public void isManagingAppFailToConnectAPI() {
        final RestTemplate restTemplate = createRestTemplate();
        when(restTemplate.getForObject(API_URL_WITH_DEFAULT_SUID, Map.class))
              .thenThrow(new RestClientException("Planned exception"));

        final DashboardAuthenticationDetailsSource source = createSource(restTemplate);

        final DashboardAuthenticationDetails details = source.buildDetails(new MockHttpServletRequest());

        assertFalse(details.isManagingService());
    }

    @Test(expected = InternalAuthenticationServiceException.class)
    public void isManagingFailToLoadSuidFile() {
        final DashboardAuthenticationDetailsSource source = createSource(createRestTemplate(), "/does-not-exist.txt");

        source.buildDetails(new MockHttpServletRequest());
    }

    @Test
    public void fullName() {
        final DashboardAuthenticationDetailsSource source = createSource(createRestTemplate("John", "Smith"));

        final DashboardAuthenticationDetails details = source.buildDetails(new MockHttpServletRequest());

        assertEquals("John Smith", details.getUserFullName());
    }

    @Test
    public void fullNameFailToConnectUserInfo() {
        final RestTemplate restTemplate = createRestTemplate();
        when(restTemplate.getForObject(USER_INFO_URL, Map.class))
              .thenThrow(new RestClientException("Planned exception"));

        final DashboardAuthenticationDetailsSource source = createSource(restTemplate);

        final DashboardAuthenticationDetails details = source.buildDetails(new MockHttpServletRequest());

        assertEquals("", details.getUserFullName());
    }

    @Test
    public void getUserFullNameWithName() {
        assertEquals("John Smith", DashboardAuthenticationDetailsSource.getUserFullName(
              map(
                    entry("name", "John Smith")
              )
        ));
    }

    @Test
    public void getUserFullNameWithFormattedName() {
        assertEquals("John Smith", DashboardAuthenticationDetailsSource.getUserFullName(
              map(
                    entry("formattedName", "John Smith")
              )
        ));
    }

    @Test
    public void getUserFullNameWithFullName() {
        assertEquals("John Smith", DashboardAuthenticationDetailsSource.getUserFullName(
              map(
                    entry("fullName", "John Smith")
              )
        ));
    }

    @Test
    public void getUserFullNameGivenNamePlusLastName() {
        assertEquals("John Smith", DashboardAuthenticationDetailsSource.getUserFullName(
              map(
                    entry("givenName", "John"),
                    entry("lastName", "Smith")
              )
        ));
    }

    @Test
    public void getUserFullNameGivenNamePlusFamilyName() {
        assertEquals("John Smith", DashboardAuthenticationDetailsSource.getUserFullName(
              map(
                    entry("givenName", "John"),
                    entry("familyName", "Smith")
              )
        ));
    }

    @Test
    public void getUserFullNameFirstNamePlusFamilyName() {
        assertEquals("John Smith", DashboardAuthenticationDetailsSource.getUserFullName(
              map(
                    entry("firstName", "John"),
                    entry("familyName", "Smith")
              )
        ));
    }

    @Test
    public void getUserFullNameFirstNamePlusLastName() {
        assertEquals("John Smith", DashboardAuthenticationDetailsSource.getUserFullName(
              map(
                    entry("firstName", "John"),
                    entry("lastName", "Smith")
              )
        ));
    }

    @Test
    public void getUserFullNameEmptyMap() {
        assertNull(DashboardAuthenticationDetailsSource.getUserFullName(map()));
    }

    private RestTemplate createRestTemplate(String firstName, String lastName) {
        final RestTemplate restTemplate = createRestTemplate();

        fillWithContactInfo(restTemplate, map(entry("firstName", firstName), entry("lastName", lastName)));

        return restTemplate;
    }

    private RestTemplate createRestTemplate() {
        final RestTemplate restTemplate = mock(RestTemplate.class);

        fillWithContactInfo(restTemplate, emptyMap());

        fillWithManagingFlag(restTemplate, Boolean.FALSE);

        return restTemplate;
    }

    private DashboardAuthenticationDetailsSource createSource(Boolean managing) {
        final RestTemplate restTemplate = createRestTemplate();

        fillWithManagingFlag(restTemplate, managing);

        return createSource(restTemplate);
    }

    private DashboardAuthenticationDetailsSource createSource(RestTemplate restTemplate) {
        return createSource(restTemplate, serviceInstanceIdFile);
    }

    private DashboardAuthenticationDetailsSource createSource(RestTemplate restTemplate, String file) {
        return new DashboardAuthenticationDetailsSource(restTemplate, file, USER_INFO_URL, API_URL);
    }

    private void fillWithManagingFlag(RestTemplate restTemplate, Boolean managing) {
        when(restTemplate.getForObject(API_URL_WITH_DEFAULT_SUID, Map.class))
              .thenReturn(singletonMap(DashboardAuthenticationDetailsSource.MANAGED_KEY, managing.toString()));
    }

    private void fillWithContactInfo(RestTemplate restTemplate, Map<?, ?> contactInfo) {
        when(restTemplate.getForObject(USER_INFO_URL, Map.class))
              .thenReturn(contactInfo);
    }

    @SafeVarargs
    private final Map<String, String> map(Map.Entry<String, String>... entries) {
        final Map<String, String> map = new HashMap<>();

        for (Entry<String, String> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }

    private SimpleEntry<String, String> entry(String key, String value) {
        return new SimpleEntry<>(key, value);
    }
}
