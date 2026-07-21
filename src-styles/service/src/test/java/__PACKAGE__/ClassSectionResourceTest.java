package __PACKAGE__;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

/**
 * Boots Quarkus and exercises the enrollment endpoint end-to-end.
 * Enrolling into a non-existent section returns 404 (mapped from
 * SectionNotFoundException).
 */
@QuarkusTest
class ClassSectionResourceTest {

    @Test
    void enrollingIntoUnknownSectionReturns404() {
        given()
                .contentType("application/json")
                .body("{\"studentId\":\"11111111-1111-1111-1111-111111111111\"}")
                .when()
                .post("/sections/{sectionId}/enrollments",
                        "22222222-2222-2222-2222-222222222222")
                .then()
                .statusCode(404);
    }
}