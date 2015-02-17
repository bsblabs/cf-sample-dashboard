package com.bsb.showcase.cf.service.user;

import java.util.ArrayList;
import java.util.List;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.springframework.data.repository.CrudRepository;

/**
 * {@link MethodRule} that automatically deletes all entities
 * persisted thanks to this rule.
 *
 * @author Sebastien Gerard
 */
public class EntityCleanupRule implements MethodRule {

    private final List<Statement> deleteStatements = new ArrayList<>();

    @Override
    public Statement apply(final Statement statement, FrameworkMethod frameworkMethod, Object o) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    statement.evaluate();
                } finally {
                    for (int i = deleteStatements.size() - 1; i >= 0; i--) {
                        deleteStatements.get(i).evaluate();
                    }
                }
            }
        };
    }

    /**
     * Persists the specified entity with the specified repository and automatically
     * deletes it when the test method is finished.
     */
    public <E> E saveEntity(final CrudRepository<E, ?> repository, final E entity) {
        repository.save(entity);

        deleteStatements.add(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                repository.delete(entity);
            }
        });

        return entity;
    }
}
