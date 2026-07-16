package __PACKAGE__;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * Application entry point for the Quarkus microservice generated from
 * the {@code nova-java-quarkus-template} (Layered variant).
 *
 * <p>For Layered services the {@code Application} class is intentionally
 * thin: it only delegates to {@link Quarkus#run(Class, String[])} and
 * lets Quarkus handle the actual boot. All wiring happens through CDI
 * annotations on classes inside the {@code controller..},
 * {@code service..}, {@code repository..}, {@code entity..} and
 * {@code dto..} packages.
 */
@QuarkusMain
public final class Application {

    /**
     * Program entry point. Delegates to {@link Quarkus#run(Class, String[])}.
     *
     * @param args command-line arguments forwarded to Quarkus.
     */
    public static void main(final String[] args) {
        Quarkus.run(Application.class, args);
    }
}