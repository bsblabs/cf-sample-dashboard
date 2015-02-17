package com.bsb.showcase.cf.service.user;

/**
 * Base test class for {@link BaseUserRepository}.
 *
 * @author Sebastien Gerard
 */
public abstract class BaseUserRepositoryTest/*<U extends BaseUser> extends AbstractDashboardTest*/ {

/*    public static final String DEFAULT_NAME = "UserRepositoryTestUserName";

    @Rule
    public final EntityCleanupRule cleanupRule = new EntityCleanupRule();

    @Test
    public void findByName() {
        final U expected = createSaveUser(DEFAULT_NAME);
        createSaveUser("noise");

        assertUser(expected, getRepository().findByName(DEFAULT_NAME));
    }

    @Test
    public void findByNameNoMatch() {
        assertNull(getRepository().findByName("notExist"));
    }

    protected abstract BaseUserRepository<U> getRepository();

    protected abstract U createUser(String name);

    protected U createSaveUser(String name) {
        return cleanupRule.saveEntity(getRepository(), createUser(name));
    }

    protected void assertUser(U expected, U actual) {
        assertNotNull("The actual user is null", actual);
        assertNotNull("The expected user is null", expected);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }*/
}
