# Backend Tests: Assessment & Improvement Plan

## Context

Three review questions about the backend test suite:

1. Are the backend tests good?
2. Is the coverage high enough?
3. Why isn't `@AutoConfigureMockMvc` used — would it simplify the MockMvc tests?

This file answers each question, then proposes the (small) set of improvements that
are actually worth making. Nothing has been changed in the codebase yet.

---

## 1. Are the tests good? — **Yes, mostly.**

The suite is **2,890 lines across 17 test files** and follows modern Spring Boot 4
conventions consistently. Strengths:

- **Right tool per layer**:
  - Controllers → `@WebMvcTest(X.class)` slice + `@MockitoBean` (the modern replacement for `@MockBean`).
  - Services → pure `@ExtendWith(MockitoExtension.class)` with `@Mock` / `@InjectMocks`. No Spring context, fast.
  - Repositories → `@SpringBootTest` against Flapdoodle embedded MongoDB.
- **Readable structure**: JUnit 5 `@Nested` + `@DisplayName` groups tests by endpoint or method; AssertJ (`assertThat`, `assertThatThrownBy`) is used throughout; method names follow `method_condition_expectedBehavior`.
- **Good security coverage in controller tests**: `@WithMockUser` for authenticated paths, `anonymous()` + XSRF cookie for public-endpoint checks, `csrf()` for mutating requests, and explicit "missing CSRF → 403" assertions for POST/DELETE.
- **Branch coverage in services**: `ClimbLogServiceTest` (378 lines) covers all flash-detection branches, all filter combinations, and every validation guard with both blank and null inputs. `UserTest` (355 lines) exhaustively exercises the immutable-record mutation helpers.

### Weaknesses (small, fixable)

- **Loose status assertions.** Several tests assert `status().is4xxClientError()` or `is5xxServerError()` when the exact status is known. Example: `ClimbLogControllerTest.createClimbLog_invalidRequest_returns400` could assert `isBadRequest()`. `deleteClimbLog_notFound_returns5xx` accepts any 5xx when the production behaviour is actually 500. Tightening these turns vague green tests into real regression guards.
- **`SecurityConfig` import is inconsistent.** `GymControllerTest`, `RouteControllerTest`, `RouteFeedbackControllerTest`, and `AuthControllerTest` all `@Import(SecurityConfig.class)`. `ClimbLogControllerTest` and `UserControllerTest` do not. The CSRF/401/403 assertions in those two only pass because Spring Boot's default security applies — but the production CORS/CSRF/role rules are not being exercised in those classes. Either import everywhere or document why two are intentionally different.
- **Repository tests use the heavy `@SpringBootTest`** instead of `@DataMongoTest`. `@DataMongoTest` would load only Mongo auto-configuration, run in seconds, and is the conventional choice. Current tests work, but are slower than needed.
- **`GlobalExceptionHandler` has no dedicated test.** It is picked up automatically by `@WebMvcTest` (Spring scans `@ControllerAdvice`), so it's exercised indirectly — but the mapping `RuntimeException → 500`, `IllegalArgumentException → 400`, `ResponseStatusException → status from ex` is load-bearing for the whole API contract and deserves its own test class.
- **A code smell surfaced by the tests:** `ClimbLogService.deleteClimbLog` throws a bare `RuntimeException` for "not found", which the handler maps to 500. That's why `deleteClimbLog_notFound_returns5xx` exists at all. The right shape is `ResponseStatusException(NOT_FOUND, ...)` → 404, and the test should assert `isNotFound()`. This is a production fix, not a test-only fix.

---

## 2. Is coverage high enough? — **Probably, but unmeasured.**

### Unmeasured

There is **no JaCoCo** (or any coverage plugin) in `backend/pom.xml`. The
`.claude/agents/java-backend-tester.md` document states a goal of **≥80% line
coverage on services and controllers**, but nothing in the build enforces it. You
can't answer this question with confidence until coverage is being reported.

### What's covered (production class → test class)

| Layer | Covered | Not covered |
|---|---|---|
| Controllers | ClimbLog, Gym, Route, RouteFeedback, User, Auth | `GlobalExceptionHandler` |
| Services | all 7 | — |
| Repositories | ClimbLog, RouteFeedback (both with custom query methods) | `GymRepository`, `RouteRepository.findByGymId`, `UserRepository.findByGithubId` |
| Models | `User` (heavily — 355 lines) | `Gym`, `Route`, `ClimbLog`, `RouteFeedback` (records, no logic — fine to skip) |
| Security | wired into controller tests | no direct `SecurityConfig` test |
| DTOs | indirect | direct — fine to skip (records) |

The "agent doc" rule **"all custom repository query methods must have integration
tests"** is currently violated by `RouteRepository.findByGymId` and
`UserRepository.findByGithubId`. `GymRepository` has no custom queries, so it's
fine to skip.

### Recommendation

Wire up JaCoCo so coverage is a number, not a feeling — then close the two real
gaps (`GlobalExceptionHandler`, the two untested custom queries). After that, the
suite is in good shape.

---

## 3. Why isn't `@AutoConfigureMockMvc` used? Would it simplify the tests? — **No, it would not. The current pattern is better.**

`@AutoConfigureMockMvc` is the right tool when paired with `@SpringBootTest`,
because `@SpringBootTest` does not auto-create a `MockMvc` bean on its own.

The controller tests here use **`@WebMvcTest(SomeController.class)`** — a Spring
Boot **slice** annotation that:

- already includes `@AutoConfigureMockMvc` internally (adding it again is a no-op),
- loads **only** the web layer (no MongoDB, no full app config) → tests start in seconds, not tens of seconds,
- registers `@ControllerAdvice` automatically,
- forces collaborators (`Service` beans) to be supplied as `@MockitoBean`, which keeps controller tests focused on controller logic.

`@SpringBootTest` + `@AutoConfigureMockMvc` would be **strictly worse here**:
slower startup, you'd lose the "must mock the service" forcing function, and you'd
gain nothing in expressiveness. The MockMvc API itself is identical in both modes.

The `.claude/agents/java-backend-tester.md` doc suggests `@SpringBootTest +
@AutoConfigureMockMvc`. The current code disagrees with the doc, and the current
code is right. Worth updating the doc, not the tests.

(Note: `@WebMvcTest` is web-MVC-only. If you ever needed full-stack controller
tests that actually hit Mongo — e.g. to verify a Spring-Data query through an
endpoint — *those* would correctly use `@SpringBootTest + @AutoConfigureMockMvc`.
None of the existing tests need that today.)

---

## Proposed improvements (minimal, optional)

A short, prioritized list. Each item is independent — implement only what you want.

### High value
1. **Add JaCoCo to `backend/pom.xml`.** Configure the `jacoco-maven-plugin` with `prepare-agent` + `report` goals so `mvn test` produces a coverage report under `target/site/jacoco`. Optionally fail the build below 80%, as the agent doc suggests.
   - File: `backend/pom.xml`
2. **Add a `GlobalExceptionHandlerTest`.** Either a unit test that calls the handler methods directly, or a tiny `@WebMvcTest` against a throwaway controller that throws each exception type. Asserts the three mappings (`ResponseStatusException` → passthrough, `IllegalArgumentException` → 400, `RuntimeException` → 500).
   - New file: `backend/src/test/java/de/clsg/boulder_companion/controller/GlobalExceptionHandlerTest.java`
3. **Add `@DataMongoTest` repository tests for the two custom queries.**
   - `RouteRepositoryTest.findByGymId_returnsOnlyMatchingGym`
   - `UserRepositoryTest.findByGithubId_returnsMatchingUser` + a not-found case
   - New files under `backend/src/test/java/de/clsg/boulder_companion/repository/`

### Medium value
4. **Tighten loose status assertions.** Replace `is4xxClientError()` / `is5xxServerError()` with the exact status (`isBadRequest`, `isNotFound`, `isInternalServerError`). Touches `ClimbLogControllerTest` and any sibling using the same pattern.
5. **Make `SecurityConfig` import consistent.** Add `@Import(SecurityConfig.class)` to `ClimbLogControllerTest` and `UserControllerTest`, or remove it from the others and document the choice. Pick one rule.

### Low value (production fix, surfaced by tests)
6. **Replace `throw new RuntimeException("Climb log not found")` in `ClimbLogService.deleteClimbLog` with `ResponseStatusException(NOT_FOUND, ...)`.** Then change the test to assert `isNotFound()`. Same shape probably applies to other "not found" paths in services.

### Optional / cosmetic
7. **Convert repository tests from `@SpringBootTest` to `@DataMongoTest`** for a noticeable speed-up. Pure refactor — no behaviour change.

---

## Verification

After each change:

```bash
# from repo root
docker compose run --rm backend mvn test
```

For coverage (once JaCoCo is wired in):

```bash
docker compose run --rm backend mvn test
# then open backend/target/site/jacoco/index.html
```

Acceptance:
- All existing tests still pass.
- New tests pass.
- JaCoCo report shows ≥80% line coverage on `controller/` and `service/` packages, and 100% on `GlobalExceptionHandler` and the new repository test methods.
