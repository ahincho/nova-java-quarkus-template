# Nova Platform Quarkus Template (Gradle)

GitHub repository template (Gradle Kotlin DSL) for bootstrapping a
Quarkus-based microservice that follows the Nova Platform conventions:
notifications adapter, kebab-case configuration, layered package layout
chosen via a single CLI flag, and — when the chosen style is `layered`
or `clean` — automatic enforcement via the
[`nova-architecture-rules`](https://github.com/ahincho/nova-java-architecture-rules)
ArchUnit library.

## Architectural styles supported

| Flag | Style | Module layout | Skeleton |
|---|---|---|---|
| `-Pstyle=layered` *(default)* | Traditional N-tier | single `boot/` module, packages `controller..`, `service..`, `repository..`, `entity..`, `dto..` | [`src-styles/layered/`](src-styles/layered) |
| `-Pstyle=clean` | Clean Architecture (Uncle Bob) | `domain` + `application` + `infrastructure` + `api` modules (phase 2) | [`src-styles/clean/`](src-styles/clean) |
| `-Pstyle=hexagonal` | Ports & Adapters | `shared` + `product` + `boot` modules (current default before this template split) | [`src-styles/hexagonal/`](src-styles/hexagonal) |

`shared/` is style-agnostic and lives in
[`src-styles/shared-resources/`](src-styles/shared-resources).

## Use this template

1. Click **Use this template → Create a new repository** on
   [github.com/ahincho/nova-java-quarkus-template](https://github.com/ahincho/nova-java-quarkus-template).
2. Clone your new repository.
3. Run the rename task with your coordinates:

   ```bash
   ./gradlew rename \
       -PgroupId=com.acme \
       -PartifactId=my-service \
       -Ppackage=com.acme.my \
       -Pstyle=layered
   ```

4. Commit the rewritten files:

   ```bash
   git add -A
   git commit -m "rename: my-service (layered)"
   ```

5. Build and run:

   ```bash
   ./gradlew build
   ./gradlew :boot:quarkusDev
   ```

## CI

The CI matrix tests **every supported style** in parallel — see
[`.github/workflows/ci.yml`](.github/workflows/ci.yml). Each matrix job:

1. Clones the template at HEAD.
2. Runs `rename` with example coordinates (`com.example.<style>`).
3. Runs `./gradlew check` (compile + unit + integration tests).
4. Runs `./gradlew :boot:quarkusBuild` (verifies the Quarkus uber-jar
   packages cleanly).
5. Reports the style as a GitHub step summary.

## Architecture enforcement (Layered / Clean)

For `-Pstyle=layered` and `-Pstyle=clean`, the generated
`ArchitectureTest` extends the abstract
[`LayeredArchitectureTest`](https://github.com/ahincho/nova-java-architecture-rules/blob/main/src/main/java/pe/edu/nova/java/archunit/LayeredArchitectureTest.java)
shipped by `nova-architecture-rules:1.0.0`. Every `@ArchTest` rule
defined there is automatically enforced on your compile-time classpath:

- `controller..` cannot depend on `repository..`
- `service..` cannot depend on `controller..`
- `repository..` cannot depend on `service..` or `controller..`
- `entity..` does not depend on any other layer
- `dto..` does not depend on `entity..`
- No service method declares `throws Exception`
- No service field is mutable

Run `mvn test` (or `./gradlew check`) and every violation is reported
as a JUnit failure pointing at the offending class.

## Notifications

The `boot/` module depends on
`pe.edu.nova.java.starters:nova-notifications-quarkus-extension:1.1.6`,
which auto-wires the `NotificationFacade` bean. Configuration lives in
`boot/src/main/resources/application.properties` under the kebab-case
`nova.notifications.*` prefix:

```properties
nova.notifications.enabled=true
nova.notifications.email.enabled=true
nova.notifications.email.provider=sendgrid
nova.notifications.email.from-address=no-reply@example.test
```

See the [extension README](https://github.com/ahincho/nova-java-notifications-quarkus-extension)
for the full channel list (email, SMS, push, Slack).

## License

Apache 2.0.