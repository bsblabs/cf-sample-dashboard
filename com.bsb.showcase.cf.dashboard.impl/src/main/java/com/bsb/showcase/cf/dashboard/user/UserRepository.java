package com.bsb.showcase.cf.dashboard.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link CrudRepository} for {@link User}.
 *
 * @author Sebastien Gerard
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Finds the user by his name.
     *
     * @return the matching user, can be <tt>null</tt>
     */
    User findByName(String name);
}
