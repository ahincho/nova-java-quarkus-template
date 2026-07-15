package __PACKAGE__.boot;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * Integration test that boots Quarkus on the test port and exercises
 * the product REST surface end-to-end via REST Assured.
 *
 * <p>The template's CI workflow runs {@code ./gradlew :boot:test} which
 * triggers this class through {@code @QuarkusTest}'s build-time
 * extension.
 */
@QuarkusTest
class ApplicationTest {

    @Test
    void productCrudRoundTrip() {
        final String createdId = given()
                .contentType("application/json")
                .body("{\"name\":\"Widget\",\"price\":\"9.99\"}")
                .when().post("/api/products")
                .then()
                .statusCode(200)
                .body("name", is("Widget"))
                .body("price", is("9.99"))
                .body("id", notNullValue())
                .extract().path("id");

        given()
                .when().get("/api/products/{id}", createdId)
                .then()
                .statusCode(200)
                .body("id", is(createdId))
                .body("name", is("Widget"))
                .body("price", is("9.99"));
    }
}