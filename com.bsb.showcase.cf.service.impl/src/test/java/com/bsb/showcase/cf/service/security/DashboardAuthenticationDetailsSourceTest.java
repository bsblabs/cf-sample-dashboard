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
public class DashboardAuthenticationDetailsSourceTest extends AbstractCfServiceTest {

    public static final String API_URL = "http://api.domain.com/v2/service_instances/[SUID]/permissions";
    public static final String USER_INFO_URL = "http://uaa.domain.com/userinfo";

    @Value("${cf.service.suid.file}")
    private String serviceInstanceIdFile;

    @Test
    public void isManagingApp() {
        final DashboardAuthenticationDetailsSource source = createSource(Boolean.TRUE, "", "");

        final DashboardAuthenticationDetails details = source.buildDetails(new MockHttpServletRequest());
        assertTrue(details.isManagingService());
    }

    @Test
    public void isNotManagingApp() {
        final DashboardAuthenticationDetailsSource source = createSource(Boolean.FALSE, "", "");

        final DashboardAuthenticationDetails details = source.buildDetails(new MockHttpServletRequest());
        assertFalse(details.isManagingService());
    }

    @Test
    public void fullName() {
        final DashboardAuthenticationDetailsSource source = createSource(true, "John", "Smith");

        final DashboardAuthenticationDetails details = source.buildDetails(new MockHttpServletRequest());
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

    private DashboardAuthenticationDetailsSource createSource() {
        return createSource(true, "John", "Smith");
    }

    private DashboardAuthenticationDetailsSource createSource(Boolean managing, String firstName, String lastName) {
        final TestableRestTemplate restTemplate = testableRestTemplate()
              .addResult("http://api.domain.com/v2/service_instances/1234567890/permissions",
                    map(entry(DashboardAuthenticationDetailsSource.MANAGED_KEY, managing.toString())))
              .addResult(USER_INFO_URL, map(entry("firstName", firstName), entry("lastName", lastName)));

        return createSource(restTemplate);
    }

    private DashboardAuthenticationDetailsSource createSource(TestableRestTemplate restTemplate) {
        return new DashboardAuthenticationDetailsSource(restTemplate, serviceInstanceIdFile, USER_INFO_URL, API_URL);
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
