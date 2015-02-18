package com.bsb.showcase.cf.test.service.user;

import static org.junit.Assert.*;

import com.bsb.showcase.cf.service.user.DashboardUser;
import com.bsb.showcase.cf.service.user.DashboardUserRepository;

/**
 * @author Sebastien Gerard
 */
public final class UserTestHelper {

    private final DashboardUserRepository repository;

    public UserTestHelper(DashboardUserRepository repository) {
        this.repository = repository;
    }

    public static DashboardUser johnUser() {
        return new DashboardUser(0L, "john", "John Smith");
    }

    public static void assertPersistentUserEquals(DashboardUser expected, DashboardUser actual) {
        assertUserEquals(expected, actual);
        assertEquals(expected.getId(), actual.getId());
    }

    public static void assertUserEquals(DashboardUser expected, DashboardUser actual) {
        assertEquals(expected != null, actual != null);

        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getFullName(), actual.getFullName());
    }

    public DashboardUser assertExists(DashboardUser user){
        final DashboardUser found = repository.findByName(user.getName());

        assertUserEquals(user, found);

        return found;
    }
}
