package __PACKAGE__.__DOMAIN__.exception;

import __PACKAGE__.__DOMAIN__.domain.model.ClassSectionId;

/**
 * Domain exception: a class section was requested by id but does not
 * exist.
 *
 * <p>Pure domain type — it carries the missing {@link ClassSectionId}
 * so the infrastructure exception mapper can translate it into the
 * right HTTP response (typically 404 Not Found) without the domain
 * knowing about HTTP.
 */
public final class SectionNotFoundException extends RuntimeException {

    private final transient ClassSectionId sectionId;

    public SectionNotFoundException(final ClassSectionId sectionId) {
        super("Class section " + sectionId.value() + " was not found");
        this.sectionId = sectionId;
    }

    public ClassSectionId sectionId() {
        return sectionId;
    }
}