package __PACKAGE__.__DOMAIN__.port.out;

import __PACKAGE__.__DOMAIN__.domain.model.ClassSection;
import __PACKAGE__.__DOMAIN__.domain.model.ClassSectionId;
import java.util.Optional;

/**
 * Port: persistence contract the domain declares for
 * {@link ClassSection} aggregates.
 *
 * <p>This interface lives in the domain because it expresses a domain
 * need, not a technical detail. The concrete adapter (Panache, JDBC,
 * in-memory, …) lives in {@code infrastructure/persistence} and
 * implements it. That inversion is the core of Hexagonal: the domain
 * depends on nothing outward; infrastructure depends inward on this
 * port.
 */
public interface ClassSectionRepository {

    /**
     * @param id the section identity.
     * @return the aggregate if present.
     */
    Optional<ClassSection> findById(ClassSectionId id);

    /**
     * Persists the current state of the aggregate (insert or update).
     *
     * @param section the aggregate to save.
     */
    void save(ClassSection section);
}