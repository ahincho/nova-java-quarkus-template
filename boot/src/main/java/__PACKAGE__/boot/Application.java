package __PACKAGE__.boot;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * Entry point for the Quarkus runtime. The class is a pure
 * bootstrapper: it owns no instance state and the constructor is private.
 *
 * <p>Annotated with {@link QuarkusMain} so {@code ./gradlew :boot:quarkusRun}
 * (or the generated {@code __ARTIFACT__-runner.jar}) finds it as the
 * main class.
 */
@QuarkusMain
public final class Application {

    private Application() {
    }

    /**
     * Boots Quarkus and blocks until the runtime stops.
     *
     * @param args command-line arguments forwarded to {@link Quarkus#run}.
     */
    public static void main(final String[] args) {
        Quarkus.run(args);
    }
}