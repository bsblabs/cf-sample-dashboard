package com.bsb.showcase.cf.dashboard.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bsb.showcase.cf.dashboard.user.TechUser;
import com.bsb.showcase.cf.dashboard.user.TechUserRepository;

/**
 * {@link UserDetailsService} based on {@link TechUserRepository}.
 *
 * @author Sebastien Gerard
 */
public class TechUserDetailsService implements UserDetailsService {

    /**
     * Name of the role associated to technical users.
     */
    public static final String ROLE_TECH = "ROLE_TECH";

    private final TechUserRepository techUserRepository;

    @Autowired
    public TechUserDetailsService(TechUserRepository techUserRepository) {
        this.techUserRepository = techUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final TechUser byName = techUserRepository.findByName(username);
        if (byName == null) {
            throw new UsernameNotFoundException(username);
        }

        return new org.springframework.security.core.userdetails.User(username, byName.getPassword(),
              Collections.singleton(new SimpleGrantedAuthority(ROLE_TECH)));
    }
}
