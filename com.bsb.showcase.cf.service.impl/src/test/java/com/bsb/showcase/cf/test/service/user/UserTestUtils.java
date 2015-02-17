package com.bsb.showcase.cf.test.service.user;

import static org.junit.Assert.assertEquals;

import com.bsb.showcase.cf.service.user.User;

/**
 * @author Sebastien Gerard
 */
public final class UserTestUtils {

    private UserTestUtils() {
    }

    public static User johnUser() {
        return new User(0L, "john", "John Smith");
    }

    public static void assertPersistentUserEquals(User expected, User actual) {
        assertUserEquals(expected, actual);
        assertEquals(expected.getId(), actual.getId());
    }

    public static void assertUserEquals(User expected, User actual) {
        assertEquals(expected != null, actual != null);

        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getFullName(), actual.getFullName());
    }
}
