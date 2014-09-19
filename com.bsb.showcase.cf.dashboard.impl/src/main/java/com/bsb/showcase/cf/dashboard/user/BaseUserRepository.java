package com.bsb.showcase.cf.dashboard.user;

import org.springframework.data.repository.CrudRepository;

/**
 * Base {@link CrudRepository} for {@link BaseUser}.
 *
 * @author Sebastien Gerard
 */
public interface BaseUserRepository<U extends BaseUser> extends CrudRepository<U, Long> {

    /**
     * Finds the user by his name.
     *
     * @return the matching user, can be <tt>null</tt>
     */
    U findByName(String name);
}
