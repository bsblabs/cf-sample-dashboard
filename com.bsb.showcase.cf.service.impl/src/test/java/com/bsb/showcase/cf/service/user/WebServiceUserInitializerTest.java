package com.bsb.showcase.cf.service.user;

import static com.bsb.showcase.cf.test.service.user.WebServiceUserTestHelper.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.bsb.showcase.cf.service.AbstractCfServiceTest;
import com.bsb.showcase.cf.test.service.user.WebServiceUserTestHelper;

/**
 * @author Sebastien Gerard
 */
@Transactional
public class WebServiceUserInitializerTest extends AbstractCfServiceTest {

    @Autowired
    private WebServiceUserRepository repository;

    private WebServiceUserTestHelper testHelper;

    @Test
    public void initializeUserSpringBean() {
        assertNotNull(repository.findByName("admin"));
    }

    @Test
    public void initializeUserAlreadyPresent() {
        final WebServiceUser billApp = repository.save(billAppWebServiceUser());
        final WebServiceUserInitializer initializer = createInitializer(createEncoder(), billApp);

        initializer.initializeUser();

        testHelper.assertExists(billApp);
    }

    @Test
    public void initializeUserNotPresent() {
        final WebServiceUser billApp = billAppWebServiceUser();
        final WebServiceUser billAppEncoded = billAppWebServiceUser();
        billAppEncoded.setPassword("billAppPasswordEncoded");

        final PasswordEncoder encoder = createEncoder(billApp.getPassword(), billAppEncoded.getPassword());
        final WebServiceUserInitializer initializer = createInitializer(encoder, billApp);

        initializer.initializeUser();

        testHelper.assertExists(billAppEncoded);
    }

    private PasswordEncoder createEncoder() {
        return mock(PasswordEncoder.class);
    }

    private PasswordEncoder createEncoder(String password, String encoded){
        final PasswordEncoder passwordEncoder = createEncoder();

        when(passwordEncoder.encode(password))
              .thenReturn(encoded);

        return passwordEncoder;
    }

    private WebServiceUserInitializer createInitializer(PasswordEncoder encoder, WebServiceUser user) {
        return new WebServiceUserInitializer(repository, encoder, user.getName(), user.getPassword());
    }

    @PostConstruct
    void initializeHelper(){
        testHelper = new WebServiceUserTestHelper(repository);
    }
}
