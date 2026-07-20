package __PACKAGE__.infrastructure.persistence;

import __PACKAGE__.domain.model.ClassSection;
import __PACKAGE__.domain.model.ClassSectionId;
import __PACKAGE__.domain.port.ClassSectionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory adapter for the {@link ClassSectionRepository} outbound
 * port.
 *
 * <p>Placeholder persistence for the skeleton: it keeps aggregates in a
 * concurrent map so the service compiles and runs end-to-end without a
 * database. Swap this class for a JPA/Panache (or other) adapter when
 * the platform's persistence choice is settled — the domain and the
 * use case do not change, only this implementation of the port.
 */
@ApplicationScoped
public class InMemoryClassSectionRepository implements ClassSectionRepository {

    private final ConcurrentMap<ClassSectionId, ClassSection> store =
            new ConcurrentHashMap<>();

    @Override
    public Optional<ClassSection> findById(final ClassSectionId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void save(final ClassSection section) {
        store.put(section.id(), section);
    }
}