package com.bsb.showcase.cf.dashboard.security;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bsb.showcase.cf.dashboard.AbstractDashboardTest;
import com.bsb.showcase.cf.dashboard.user.EntityCleanupRule;
import com.bsb.showcase.cf.dashboard.user.TechUser;
import com.bsb.showcase.cf.dashboard.user.TechUserRepository;

/**
 * @author Sebastien Gerard
 */
public class TechUserDetailsServiceTest extends AbstractDashboardTest {

    @Autowired
    private TechUserDetailsService techUserDetailsService;

    @Rule
    public final EntityCleanupRule cleanupRule = new EntityCleanupRule();

    @Autowired
    private TechUserRepository techUserRepository;

    @Test
    public void loadUserByUsername() {
        final TechUser entity = new TechUser();
        entity.setName("TechUserDetailsServiceTest.loadUserByUsername");
        entity.setPassword("password");

        cleanupRule.saveEntity(techUserRepository, entity);

        final UserDetails userDetails = techUserDetailsService.loadUserByUsername(entity.getName());
        assertEquals(entity.getName(), userDetails.getUsername());
        assertEquals(entity.getPassword(), userDetails.getPassword());
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsernameNotFound() {
        techUserDetailsService.loadUserByUsername("TechUserDetailsServiceTest.loadUserByUsernameNotFound");
    }
}
