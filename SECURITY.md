# Security policy: Parchment

Parchment is an Android UI library with no network, storage, or permission
surface of its own. Its security posture is the integrity of the code and
the build: what ships is what is in `main`, built by CI, with dependencies
that are scanned on every change.

---

## Reporting a vulnerability

**Do not file a public issue for a suspected vulnerability.**

Use one of:

1. **GitHub private vulnerability reporting**: open the repo, click
   *Security → Report a vulnerability*. This is preferred because it
   creates a tracked advisory and a private discussion thread.
2. **Email**: `security@tinkernorth.com` (PGP key on request).
   Include the version (commit SHA or release tag), reproduction steps,
   and impact.

### Response SLA

| Severity | Triage acknowledgement | Initial assessment | Fix target |
|---|---|---|---|
| Critical (CVSS >= 9.0) | 1 business day | 3 business days | 14 days, coordinated disclosure |
| High (CVSS 7.0-8.9)    | 2 business days | 5 business days | 30 days |
| Medium / Low           | 5 business days | 10 business days | next minor release |

If we miss the SLA, you may publish 90 days after the original report
date regardless. We'd rather know than not know.

### Scope

In scope:

- The `library` module and its consumer ProGuard rules.
- The build and CI configuration in this repository (a compromised build
  is a compromised artifact).

Out of scope:

- The `sample` app. It is a demo, ships nowhere, and loads images from a
  third-party host over HTTPS purely for illustration.
- Bugs that need a malicious adapter in the host app. The library trusts
  its adapter the way `ListView` does.

---

## Supported versions

| Version | Supported |
|---|---|
| `main` (2.0.0 in development) | Yes |
| `1.6.x` (2014, Maven Central `mobi.parchment:parchment`) | No. Unmaintained; upgrade to 2.0 when it ships |

---

## How CI prevents vulnerable code from shipping

**On every PR** (blocking):

- Action-pin lint: every `uses:` line must reference a 40-char SHA.
- Allowlist expiry: `.security/allowlist.yaml` entries must be unexpired.
- Dependency review: GitHub advisory DB (PR-only).
- OSV-Scanner: Gradle manifest deps, exact Maven coordinates.
- Gitleaks: secret scanning over the full history.
- CodeQL: `java-kotlin`, security-extended + security-and-quality packs.
- Gradle wrapper validation (checksums of `gradle-wrapper.jar` and the
  pinned distribution).

Dependabot opens weekly PRs for Gradle dependencies and GitHub Actions.

---

## Known gaps

- **No signed releases yet.** 2.0 has not been published. When it is,
  releases will follow the TinkerNorth pattern: GPG-signed Maven artifacts,
  `SHA256SUMS`, and provenance from a tag-triggered workflow. Until then
  the only artifact is the AAR CI attaches to each run, built from a public
  commit.
- **Branch protection on `main`.** The org plan does not expose
  required-status-check enforcement here; direct pushes are blocked by
  convention only. The CI workflows are the de-facto gate.
- **`gradle/verification-metadata.xml` is not committed.** Dependency
  checksums are verified by Gradle only when a contributor generates the
  file locally (see `CONTRIBUTING.md`).
