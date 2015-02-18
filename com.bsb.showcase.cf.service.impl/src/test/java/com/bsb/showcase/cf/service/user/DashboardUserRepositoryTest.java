package com.bsb.showcase.cf.service.user;

import static com.bsb.showcase.cf.test.service.user.UserTestHelper.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.bsb.showcase.cf.service.AbstractCfServiceTest;

/**
 * @author Sebastien Gerard
 */
@Transactional
public class DashboardUserRepositoryTest extends AbstractCfServiceTest {

    @Autowired
    private DashboardUserRepository repository;

    @Test
    public void findByName() {
        final DashboardUser john = repository.save(johnUser());

        final DashboardUser foundByName = repository.findByName(john.getName());

        assertPersistentUserEquals(john, foundByName);
    }

    @Test
    public void findByNameNoMatch() {
        final DashboardUser john = repository.save(johnUser());

        final DashboardUser foundByName = repository.findByName(john.getName() + "2");

        assertNull(foundByName);
    }
}
