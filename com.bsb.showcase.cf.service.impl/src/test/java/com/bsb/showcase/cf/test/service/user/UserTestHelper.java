package com.bsb.showcase.cf.test.service.user;

import static org.junit.Assert.*;

import com.bsb.showcase.cf.service.user.User;
import com.bsb.showcase.cf.service.user.UserRepository;

/**
 * @author Sebastien Gerard
 */
public final class UserTestHelper {

    private final UserRepository repository;

    public UserTestHelper(UserRepository repository) {
        this.repository = repository;
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

    public User assertExists(User user){
        final User found = repository.findByName(user.getName());

        assertUserEquals(user, found);

        return found;
    }
}
