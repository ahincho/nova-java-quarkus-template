package __PACKAGE__;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * Application entry point for the Nova Platform microservice generated
 * from the {@code service} archetype (Hexagonal + DDD).
 *
 * <p>Thin bootstrap: it only hands control to Quarkus. All wiring is
 * done via CDI on the classes under {@code domain}, {@code application},
 * {@code infrastructure} and the cross-cutting packages
 * ({@code config}, {@code events}, {@code security}, {@code shared}).
 */
@QuarkusMain
public final class Application {

    private Application() {
        // entry point only, not instantiable
    }

    /**
     * Program entry point.
     *
     * @param args command-line arguments forwarded to Quarkus.
     */
    public static void main(final String[] args) {
        Quarkus.run(args);
    }
}