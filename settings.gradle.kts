rootProject.name = "__ARTIFACT__"

// All modules are always included so Gradle can configure the build
// before the `rename` task runs. For the Layered style the `:product`
// module ships empty (stub) and the rename task populates it only
// when -Pstyle=hexagonal or -Pstyle=clean is set.
include("shared", "product", "boot")