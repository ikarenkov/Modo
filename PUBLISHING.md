# Publishing to Maven Central

This guide is for library maintainers who need to publish new versions of Modo to Maven Central.

## Prerequisites

### 1. Credentials Setup

Create `local.properties` file in the project root (gitignored):

```properties
sonatypeUsername=your-central-portal-username
sonatypePassword=your-central-portal-password
signing.keyId=your-gpg-key-id
signing.password=your-gpg-key-password
signing.secretKeyRingFile=path/to/secring.gpg
```

**Where to get credentials:**
- **Sonatype credentials**: Register at [Maven Central Portal](https://central.sonatype.com) and generate a user token
- **GPG signing key**: Generate using `gpg --gen-key` and export with `gpg --export-secret-keys`

### 2. Update Version

Update the version in `gradle/libs.versions.toml`:
```toml
[versions]
modo = "x.y.z"  # Update this
```

## Publishing Workflows

### Option 1: Manual Release (Recommended)

This workflow publishes to a staging repository and validates artifacts, but requires manual approval before releasing to Maven Central.

```bash
./gradlew clean modo-compose:bundleReleaseAar publishAllPublicationsToSonatypeRepository closeSonatypeStagingRepository
```

**What happens:**
1. ✅ Artifacts are built (AAR, sources, javadoc)
2. ✅ Artifacts are signed with GPG
3. ✅ Uploaded to staging repository
4. ✅ Repository is closed and validated
5. ⏸️ **Waits for manual approval**

**Next steps:**
1. Go to https://central.sonatype.com/publishing
2. Find your deployment (should show as "VALIDATED")
3. Review the artifacts
4. Click **"Publish"** to release to Maven Central
5. Or click **"Drop"** to discard if something is wrong

**Why this is recommended:**
- ✅ Review artifacts before public release
- ✅ Can drop/fix if errors are found
- ✅ Safer for production releases
- ⚠️ Remember: Once published, versions are **immutable**

### Option 2: Automatic Release

This workflow automatically publishes to Maven Central after validation, with no manual review step.

```bash
./gradlew clean modo-compose:bundleReleaseAar \
  publishAllPublicationsToSonatypeRepository \
  closeAndReleaseSonatypeStagingRepository
```

**What happens:**
1. ✅ Artifacts are built, signed, and uploaded
2. ✅ Repository is closed and validated
3. ✅ **Automatically released to Maven Central**
4. ⏳ Artifacts appear on Maven Central within 10-30 minutes

**Use with caution:**
- ⚠️ No manual review - artifacts become public immediately
- ⚠️ Better for hotfixes or when you're very confident
- ⚠️ Can't undo once released

## Important Notes

### Gradle Session State

⚠️ **All publishing tasks must run in a single command.** Running them separately will fail:

```bash
# ❌ This will fail
./gradlew publishAllPublicationsToSonatypeRepository
./gradlew closeSonatypeStagingRepository  # Error: No staging repository found

# ✅ This works
./gradlew publishAllPublicationsToSonatypeRepository closeSonatypeStagingRepository
```

**Why?** The staging repository ID is stored in Gradle's task state, which only exists during one session. See the note in the project explaining this behavior.

### Version Immutability

Once a version is published to Maven Central:
- ❌ **Cannot be changed**
- ❌ **Cannot be deleted**
- ❌ **Cannot be republished**

If you publish a broken version, you must release a new version with a fix.

### Timing

- **Validation**: Immediate (during close task)
- **Publication to Maven Central**: 10-30 minutes after release
- **Maven Central search**: May take up to 2 hours to index

## Troubleshooting

### "Component already exists" Error

You're trying to republish an existing version. Solution:
1. Bump the version in `gradle/libs.versions.toml`
2. Or drop the deployment from https://central.sonatype.com/publishing (if not yet published)

### "401 Unauthorized" Error

Your credentials are invalid or from the old OSSRH system. Solution:
1. Verify you're using credentials from https://central.sonatype.com (not oss.sonatype.org)
2. Regenerate user token if needed
3. Check `local.properties` has correct `sonatypeUsername` and `sonatypePassword`

### "No staging repository found" Error

You ran tasks in separate Gradle invocations. Solution:
- Run all tasks in a single command (see "Gradle Session State" above)

## Architecture Notes

This project uses:
- **`maven-publish` plugin**: Creates and signs artifacts (configured in `PublishingPlugin.kt`)
- **`gradle-nexus/publish-plugin`**: Manages Nexus staging workflow (configured in root `build.gradle.kts`)
- **OSSRH Staging API compatibility endpoint**: Bridges old Gradle plugins with new Central Portal

The migration from OSSRH to Central Portal is complete, using the compatibility endpoint to maintain existing workflow.
