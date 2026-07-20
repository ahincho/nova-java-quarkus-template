package __PACKAGE__.application.service;

import __PACKAGE__.application.command.EnrollStudentCommand;
import __PACKAGE__.application.port.in.EnrollStudentUseCase;
import __PACKAGE__.domain.exception.SectionNotFoundException;
import __PACKAGE__.domain.model.ClassSection;
import __PACKAGE__.domain.model.ClassSectionId;
import __PACKAGE__.domain.model.StudentId;
import __PACKAGE__.domain.port.ClassSectionRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Application service implementing the {@link EnrollStudentUseCase}
 * inbound port.
 *
 * <p>Holds the orchestration for a single use case — it loads the
 * aggregate through the outbound repository port, lets the aggregate
 * enforce its own invariants ({@code section.enroll}) and persists the
 * result. It contains no business rules of its own; those live in
 * {@link ClassSection}.
 *
 * <p>{@code @ApplicationScoped} so Quarkus manages it as a CDI bean and
 * injects the repository port. The inbound adapter depends on the
 * {@link EnrollStudentUseCase} interface, never on this class.
 */
@ApplicationScoped
public class EnrollStudentService implements EnrollStudentUseCase {

    private final ClassSectionRepository repository;

    public EnrollStudentService(final ClassSectionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(final EnrollStudentCommand command) {
        final ClassSectionId sectionId = ClassSectionId.of(command.sectionId());
        final StudentId studentId = StudentId.of(command.studentId());

        final ClassSection section = repository.findById(sectionId)
                .orElseThrow(() -> new SectionNotFoundException(sectionId));

        section.enroll(studentId);

        repository.save(section);
    }
}