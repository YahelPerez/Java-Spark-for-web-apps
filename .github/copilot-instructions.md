## Copilot / AI agent instructions — Java Spark for Web Apps

Short and actionable guidance for editing this small Java Spark app.

- Repository purpose: a small demo REST + server-side-rendered app for a collectibles store. Main entry: `src/main/java/com/rafael/collectibles/Main.java`.
- Build: Maven (Java 17). The project produces a shaded JAR with main class `com.rafael.collectibles.Main` via the maven-shade-plugin in `pom.xml`.

Quick start (Windows cmd.exe):

```
mvn package
java -jar target\Java-Spark-for-web-apps-1.0-SNAPSHOT-shaded.jar
```

Key locations to reference when changing behavior:
- `pom.xml` — dependencies and Java/Jetty pinning.
- `src/main/java/com/rafael/collectibles/Main.java` — single-file app: routes, models (as static inner classes), in-memory datasets, templates, and exception handling.
- `src/main/resources/public` — static assets (served at `/` because `staticFiles.location("/public")`).
- `src/main/resources/templates` — Mustache templates (`list.mustache`, `detail.mustache`, `error.mustache`).
- `DECISIONS.md` — project decisions and constraints.

Architecture / data flow highlights (read these files together):
- The app uses SparkJava for routing and Mustache for server-side views. JSON API endpoints return objects serialized via Gson (see `gson::toJson` usages).
- In-memory "databases": `private static Map<String, User> users` and `private static Map<String, Item> items` inside `Main.java`. There is no external DB or persistence; unit of state is the JVM process.
- API routes under `/users` are JSON-only and use after-filter `res.type("application/json")`. View routes (templates) live at `/items` and `/items/:id` and render Mustache templates.
- Error handling: `exception(Exception.class, ...)` renders `error.mustache` and sets HTTP 500. Throwing an exception in a view route will surface through that handler.

Conventions / patterns to follow when editing code:
- Add web routes using the same style: group under `path("/users", () -> { ... })` or standalone `get("/items", ...)`. Keep JSON routes returning objects and use `gson::toJson` as the transformer.
- Models are simple POJOs defined as static inner classes inside `Main.java`. New small models may follow this pattern; larger models should be extracted to `com.rafael.collectibles` package files.
- Templates must expose getters (e.g., `getId()`, `getName()`) because Mustache reads JavaBean-style getters.
- Status codes: follow existing usage (e.g., 201 for created, 404 for not found, 409 for conflict). There is one bug to watch for: a `put` sets `res.status(4404)` (typo) — prefer `res.status(404)`.

Integration & dependencies to be aware of:
- SparkJava (routing), Gson (JSON serialization), Mustache (templates), Logback/SLF4J for logging. Jetty components are version-managed in `pom.xml` to address known vulnerabilities — don't change `jetty.version` without reviewing `DECISIONS.md`.

Editing notes and examples:
- To serve new static JS/CSS, place files under `src/main/resources/public` and reference them from templates at `/static` paths.
- To add a JSON endpoint that returns a POJO:

  - Use `get("/path", (req, res) -> obj, gson::toJson);`
  - Ensure `after("/*", (req,res) -> res.type("application/json"));` is applied if grouped under `path`.

Debugging & local development:
- App listens on port 8080 (`port(8080)`). If port conflicts happen, change that in `Main.java` (or set via env and adapt code).
- Logs: code uses `System.out.println` and `exception.printStackTrace()` for simple logging. Logback is present; if you add structured logging, add a `logback.xml` to `resources`.
- Run from IDE by launching `com.rafael.collectibles.Main` with Java 17.

Tests & CI:
- There are no test files in the repository. If you add tests, use Maven surefire and ensure Java 17 compatibility in `pom.xml`.

Small gotchas discovered while analyzing the codebase:
- Models are inner classes; large refactors should move them to separate files to avoid a monolithic `Main.java`.
- `put` route sets `res.status(4404)` by accident — treat as bug and change to `404`.
- The application is stateful in-memory: restarting the JVM resets data (useful for local testing but not production-ready).

When in doubt, consult these files first: `pom.xml`, `src/main/java/com/rafael/collectibles/Main.java`, `src/main/resources/templates/*`, and `DECISIONS.md`.

If any section above is unclear or missing data you expect, tell me what you want clarified and I will iterate on this file.
