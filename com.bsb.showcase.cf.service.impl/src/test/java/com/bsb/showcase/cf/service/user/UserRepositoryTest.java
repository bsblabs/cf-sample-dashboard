package com.bsb.showcase.cf.service.user;

import static com.bsb.showcase.cf.test.service.user.UserTestUtils.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.bsb.showcase.cf.service.AbstractCfServiceTest;

/**
 * @author Sebastien Gerard
 */
@Transactional
public class UserRepositoryTest extends AbstractCfServiceTest {

    @Autowired
    private UserRepository repository;

    @Test
    public void findByName() {
        final User john = repository.save(johnUser());

        final User foundByName = repository.findByName(john.getName());

        assertPersistentUserEquals(john, foundByName);
    }

    @Test
    public void findByNameNoMatch() {
        final User john = repository.save(johnUser());

        final User foundByName = repository.findByName(john.getName() + "2");

        assertNull(foundByName);
    }
}
