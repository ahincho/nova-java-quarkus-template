# nova-java-quarkus-template

> Gradle template for microservice instances built on the **Nova Platform**
> meta-framework with **Quarkus 3.33.2.1 LTS**.

This is the **Gradle counterpart** of
[`nova-java-quarkus-archetype`](https://github.com/ahincho/nova-java-quarkus-archetype)
(Maven). Maven has no native equivalent of `maven-archetype` for
Gradle, so the Nova Platform ships its Gradle template as a
**GitHub repository template** + a `gradle rename` bootstrap task.

## What you get

A multi-module Gradle project wired with:

- **Java 25** toolchain (`languageVersion.set(JavaLanguageVersion.of(25))`).
- **Quarkus 3.33.2.1 LTS** platform BOM (`quarkus-bom`).
- **Nova Platform notifications adapter**
  (`nova-notifications-quarkus-extension:1.1.6`) pre-installed.
- Three modules:
  - `shared/` — pure Java library (domain primitives, exceptions).
  - `product/` — example bounded context (hexagonal: domain → application).
  - `boot/` — Quarkus entry point (`@QuarkusMain`, REST resources,
    exception mapper, in-memory repository).
- `Dockerfile.jvm` for the standard multi-stage JVM image build
  (`eclipse-temurin:25-{jdk,jre}-alpine`, non-root UID 1001, tini).
- CI workflow that simulates the rename flow end-to-end.

## How to instantiate

### 1. Click **Use this template** on GitHub

```
https://github.com/ahincho/nova-java-quarkus-template/generate
```

Choose the owner / repository name (e.g. `acme/ms-course`). The new
repo starts with placeholder identifiers in every file:

- `__GROUP__` → your Maven `groupId` (e.g. `com.acme`).
- `__ARTIFACT__` → your Maven `artifactId` (e.g. `ms-course`).
- `__PACKAGE__` → your Java base package (e.g. `com.acme.ms`).

### 2. Clone and run the rename task

```bash
git clone https://github.com/acme/ms-course.git
cd ms-course
./gradlew rename \
  -PgroupId=com.acme \
  -PartifactId=ms-course \
  -Ppackage=com.acme.ms
```

The `rename` task rewrites every tracked text file in place,
replacing `__GROUP__` / `__ARTIFACT__` / `__PACKAGE__` /
`__PACKAGE_PATH__`, and updates `gradle.properties` with the new
`group`, `artifactId`, and `version` values.

> Note: the rename does **not** move source files between folders. The
> module names (`shared/`, `product/`, `boot/`) are fixed; only the
> Java `package` declarations inside them change.

### 3. Commit the renamed files and build

```bash
git add -A
git commit -m "rename: ms-course"
./gradlew build           # compile + test
./gradlew :boot:quarkusDev # dev mode with live reload
./gradlew :boot:quarkusBuild # produces boot/build/quarkus-app/
```

## Compared to the Maven archetype

| Aspect | Maven archetype | Gradle template (this repo) |
|---|---|---|
| Distribution channel | Maven Central / GitHub Packages (`maven-archetype` packaging) | GitHub repository template (one-click clone) |
| Instantiation command | `mvn archetype:generate -DarchetypeGroupId=...` | Click **Use this template** + `./gradlew rename` |
| Build tool | Maven (XML) | Gradle Kotlin DSL |
| Multi-module split | `shared/`, `product/`, `boot/` | `shared/`, `product/`, `boot/` |
| Quarkus version | 3.33.2.1 LTS | 3.33.2.1 LTS |
| Nova extension | `1.1.6` (via parent POM) | `1.1.6` (via `gitHubPackages` repository in root) |
| CI workflow | Reusable workflow from `nova-devops` | Self-contained `.github/workflows/ci.yml` |
| Java toolchain | Java 25 via `maven-compiler-plugin` | Java 25 via Gradle toolchain chain |

Both paths produce a runnable microservice with the **same package
structure** (`{group}.shared`, `{group}.product`, `{group}.boot`) and
**the same notifications wiring**.

## Repository layout

```
nova-java-quarkus-template/
├── .github/workflows/ci.yml          # Self-tests the rename + build flow
├── build.gradle.kts                  # Root: Quarkus BOM + rename task
├── settings.gradle.kts               # Multi-module wiring
├── gradle.properties                 # group / artifactId / version (placeholders)
├── gradle/wrapper/                   # Gradle wrapper (run ./gradlew)
├── README.md
├── shared/                           # Pure Java domain primitives
│   ├── build.gradle.kts
│   └── src/main/java/__PACKAGE__/shared/
│       └── domain/
│           ├── AggregateId.java
│           ├── ConflictException.java
│           ├── DomainException.java
│           ├── ErrorCode.java
│           ├── NotFoundException.java
│           └── ValidationException.java
├── product/                          # Example bounded context (hexagonal)
│   ├── build.gradle.kts
│   └── src/main/java/__PACKAGE__/product/
│       ├── application/CreateProductUseCase.java
│       └── domain/
│           ├── Product.java
│           ├── ProductId.java
│           ├── ProductName.java
│           ├── ProductPrice.java
│           └── ProductRepository.java
└── boot/                             # Quarkus entry point
    ├── build.gradle.kts
    ├── Dockerfile.jvm                # Multi-stage JVM image
    └── src/
        ├── main/java/__PACKAGE__/boot/
        │   ├── Application.java
        │   ├── infrastructure/InMemoryProductRepository.java
        │   └── resource/
        │       ├── DomainExceptionMapper.java
        │       └── ProductResource.java
        ├── main/resources/application.properties
        └── test/java/__PACKAGE__/boot/ApplicationTest.java
```

## Required secrets for consumers

For a consumer of the renamed template to resolve the
`nova-notifications-quarkus-extension` from GitHub Packages, configure
either:

- `NOVA_RELEASE_PAT` (PAT with `packages:read` scope on
  `nova-java-notifications-quarkus-extension`), **or**
- `GITHUB_TOKEN` (works only if the consumer repo lives under
  `ahincho`; PAT is required for cross-org consumption).

Set this as a repository secret on the renamed instance's
**Settings → Secrets and variables → Actions**.

## License

UNLICENSED — each instantiated project is owned by its creator.
The template itself is part of the Nova Platform and follows the
same disclosure rules documented in
[`AI-ATTRIBUTION.md`](https://github.com/ahincho/nova-devops/blob/main/AI-ATTRIBUTION.md).