package com.bsb.showcase.cf.dashboard.security;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.bsb.showcase.cf.dashboard.AbstractDashboardTest;

/**
 * @author Sebastien Gerard
 */
public class DashboardOAuth2AuthenticationDetailsTest extends AbstractDashboardTest {

    @Test
    public void managingApp() {
        assertTrue(new DashboardOAuth2AuthenticationDetails(new MockHttpServletRequest(), true, "name").isManagingApp());
    }

    @Test
    public void fullName() {
        final String name = "name";

        assertEquals(name,
              new DashboardOAuth2AuthenticationDetails(new MockHttpServletRequest(), false, name).getUserFullName());
    }
}
