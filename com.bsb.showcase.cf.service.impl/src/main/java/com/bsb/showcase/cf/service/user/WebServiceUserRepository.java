package com.bsb.showcase.cf.service.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link CrudRepository} for {@link WebServiceUser}.
 *
 * @author Sebastien Gerard
 */
@Repository
public interface WebServiceUserRepository extends CrudRepository<WebServiceUser, Long> {

    /**
     * Finds the user by his name.
     *
     * @return the matching user, can be <tt>null</tt>
     */
    WebServiceUser findByName(String name);
}
