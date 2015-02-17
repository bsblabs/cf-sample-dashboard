package com.bsb.showcase.cf.service.security;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.bsb.showcase.cf.service.AbstractCfServiceTest;

/**
 * @author Sebastien Gerard
 */
public class DashboardAuthenticationDetailsTest extends AbstractCfServiceTest {

    @Test
    public void managingApp() {
        assertTrue(new DashboardAuthenticationDetails(new MockHttpServletRequest(), true, "name").isManagingService());
    }

    @Test
    public void fullName() {
        final String name = "name";

        assertEquals(name,
              new DashboardAuthenticationDetails(new MockHttpServletRequest(), false, name).getUserFullName());
    }
}
