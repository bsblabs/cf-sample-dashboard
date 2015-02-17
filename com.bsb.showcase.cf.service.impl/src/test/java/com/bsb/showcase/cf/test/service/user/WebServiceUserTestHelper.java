package com.bsb.showcase.cf.test.service.user;

import static org.junit.Assert.*;

import com.bsb.showcase.cf.service.user.WebServiceUser;
import com.bsb.showcase.cf.service.user.WebServiceUserRepository;

/**
 * @author Sebastien Gerard
 */
public class WebServiceUserTestHelper {

    private final WebServiceUserRepository repository;

    public WebServiceUserTestHelper(WebServiceUserRepository repository) {
        this.repository = repository;
    }

    public static WebServiceUser billAppWebServiceUser() {
        return new WebServiceUser(0L, "billApp", "billAppPassword");
    }

    public static void assertWebServicePersistentUserEquals(WebServiceUser expected, WebServiceUser actual) {
        assertWebServiceUserEquals(expected, actual);

        assertEquals(expected.getId(), actual.getId());
    }

    public static void assertWebServiceUserEquals(WebServiceUser expected, WebServiceUser actual) {
        assertEquals(expected != null, actual != null);

        assertEquals(expected.getName(), actual.getName());
    }

    public WebServiceUser assertExists(WebServiceUser webServiceUser){
        final WebServiceUser found = repository.findByName(webServiceUser.getName());

        assertWebServiceUserEquals(webServiceUser, found);

        return found;
    }
}
