package com.bsb.showcase.cf.service.user;

import static com.bsb.showcase.cf.test.service.user.WebServiceUserTestHelper.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.bsb.showcase.cf.service.AbstractCfServiceTest;

/**
 * @author Sebastien Gerard
 */
@Transactional
public class WebServiceUserRepositoryTest extends AbstractCfServiceTest {

    @Autowired
    private WebServiceUserRepository repository;

    @Test
    public void findByName() {
        final WebServiceUser billApp = repository.save(billAppWebServiceUser());

        final WebServiceUser foundByName = repository.findByName(billApp.getName());

        assertWebServicePersistentUserEquals(billApp, foundByName);
    }

    @Test
    public void findByNameNoMatch() {
        final WebServiceUser billApp = repository.save(billAppWebServiceUser());

        final WebServiceUser foundByName = repository.findByName(billApp.getName() + "2");

        assertNull(foundByName);
    }
}
