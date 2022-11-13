1. `git checkout main`
2. `git pull origin main`
3. Change the version in `gradle.properties` to a non-snapshot version.
4. Update the `README.md` with the new version.
5. `./gradlew versionCurrentDocs`
6. `git commit -am "Prepare for release X.Y.Z"`
7. `./publish.sh`.
8. Close and release on [Sonatype](https://oss.sonatype.org/#stagingRepositories).
9. `git push origin main`
10. Release on GitHub:
     1. Create a new release [here](https://github.com/ansman/deager/releases/new).
     2. Use the automatic changelog. Update if needed.
     3. Ensure you pick the "Prepare for release X.Y.Z" as the target commit.
11. `git pull origin main --tags`
12. Update the `gradle.properties` to the next SNAPSHOT version.
13. `git commit -am "Prepare next development version"`
14. `git push origin main`