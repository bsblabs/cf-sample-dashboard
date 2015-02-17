package com.bsb.showcase.cf.service.security;

import static com.bsb.showcase.cf.test.service.TestableRestTemplate.*;
import static org.junit.Assert.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletRequest;

import com.bsb.showcase.cf.service.AbstractCfServiceTest;
import com.bsb.showcase.cf.test.service.TestableRestTemplate;

/**
 * @author Sebastien Gerard
 */
public class DashboardOAuthAuthenticationDetailsSourceTest extends AbstractCfServiceTest {

    public static final String API_URL = "http://api.domain.com/v2/service_instances/[SUID]/permissions";
    public static final String USER_INFO_URL = "http://uaa.domain.com/userinfo";

    @Value("${dashboard.suid.file}")
    private String serviceInstanceIdFile;

    @Test
    public void isManagingApp() {
        final DashboardOAuthAuthenticationDetailsSource source = createSource(Boolean.TRUE, "", "");

        final DashboardOAuth2AuthenticationDetails details = source.buildDetails(new MockHttpServletRequest());
        assertTrue(details.isManagingApp());
    }

    @Test
    public void isNotManagingApp() {
        final DashboardOAuthAuthenticationDetailsSource source = createSource(Boolean.FALSE, "", "");

        final DashboardOAuth2AuthenticationDetails details = source.buildDetails(new MockHttpServletRequest());
        assertFalse(details.isManagingApp());
    }

    @Test
    public void fullName() {
        final DashboardOAuthAuthenticationDetailsSource source = createSource(true, "John", "Smith");

        final DashboardOAuth2AuthenticationDetails details = source.buildDetails(new MockHttpServletRequest());
        assertEquals("John Smith", details.getUserFullName());
    }

    @Test
    public void getUserFullNameWithName() {
        assertEquals("John Smith", createSource().getUserFullName(Collections.singletonMap("name", "John Smith")));
    }

    @Test
    public void getUserFullNameWithFormattedName() {
        assertEquals("John Smith",
              createSource().getUserFullName(Collections.singletonMap("formattedName", "John Smith")));
    }

    @Test
    public void getUserFullNameWithFullName() {
        assertEquals("John Smith", createSource().getUserFullName(Collections.singletonMap("fullName", "John Smith")));
    }

    @Test
    public void getUserFullNameGivenNamePlusLastName() {
        assertEquals("John Smith", createSource().getUserFullName(
              map(
                    entry("givenName", "John"),
                    entry("lastName", "Smith")
              )
        ));
    }

    @Test
    public void getUserFullNameGivenNamePlusFamilyName() {
        assertEquals("John Smith", createSource().getUserFullName(
              map(
                    entry("givenName", "John"),
                    entry("familyName", "Smith")
              )
        ));
    }

    @Test
    public void getUserFullNameFirstNamePlusFamilyName() {
        assertEquals("John Smith", createSource().getUserFullName(
              map(
                    entry("firstName", "John"),
                    entry("familyName", "Smith")
              )
        ));
    }

    @Test
    public void getUserFullNameFirstNamePlusLastName() {
        assertEquals("John Smith", createSource().getUserFullName(
              map(
                    entry("firstName", "John"),
                    entry("lastName", "Smith")
              )
        ));
    }

    @Test
    public void getUserFullNameEmptyMap() {
        assertNull(createSource().getUserFullName(map()));
    }

    private DashboardOAuthAuthenticationDetailsSource createSource() {
        return createSource(true, "John", "Smith");
    }

    private DashboardOAuthAuthenticationDetailsSource createSource(Boolean managing, String firstName, String lastName) {
        final TestableRestTemplate restTemplate = testableRestTemplate()
              .addResult("http://api.domain.com/v2/service_instances/1234567890/permissions",
                    map(entry(DashboardOAuthAuthenticationDetailsSource.MANAGED_KEY, managing.toString())))
              .addResult(USER_INFO_URL, map(entry("firstName", firstName), entry("lastName", lastName)));

        return createSource(restTemplate);
    }

    private DashboardOAuthAuthenticationDetailsSource createSource(TestableRestTemplate restTemplate) {
        return new DashboardOAuthAuthenticationDetailsSource(restTemplate, serviceInstanceIdFile, USER_INFO_URL, API_URL);
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
