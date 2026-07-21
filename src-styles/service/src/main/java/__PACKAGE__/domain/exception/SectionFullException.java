package __PACKAGE__.domain.exception;

import __PACKAGE__.domain.model.ClassSectionId;

/**
 * Domain exception: an enrollment was attempted on a section that has
 * already reached its capacity.
 *
 * <p>Pure domain type — it carries the offending {@link ClassSectionId}
 * so the infrastructure layer (an exception mapper) can translate it
 * into the right HTTP response without the domain knowing about HTTP.
 */
public final class SectionFullException extends RuntimeException {

    private final transient ClassSectionId sectionId;

    public SectionFullException(final ClassSectionId sectionId) {
        super("Class section " + sectionId.value() + " is full");
        this.sectionId = sectionId;
    }

    public ClassSectionId sectionId() {
        return sectionId;
    }
}