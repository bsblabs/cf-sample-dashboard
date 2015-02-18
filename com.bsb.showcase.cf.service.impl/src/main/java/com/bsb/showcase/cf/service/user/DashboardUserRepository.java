package com.bsb.showcase.cf.service.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link CrudRepository} for {@link DashboardUser}.
 *
 * @author Sebastien Gerard
 */
@Repository
public interface DashboardUserRepository extends CrudRepository<DashboardUser, Long> {

    /**
     * Finds the user by his name.
     *
     * @return the matching user, can be <tt>null</tt>
     */
    DashboardUser findByName(String name);
}
