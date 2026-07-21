package __PACKAGE__.infrastructure.web.in.request;

/**
 * Request body for {@code POST /sections/{sectionId}/enrollments}.
 *
 * <p>Inbound HTTP contract, decoupled from both the domain and the
 * application command. The controller maps it into an
 * {@code EnrollStudentCommand}.
 */
public record EnrollStudentRequest(String studentId) {
}