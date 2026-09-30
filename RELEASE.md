# Release process

Replace `x.y.z` with the new version. Use the `sharmadhiraj` GitHub account (`gh auth switch -u sharmadhiraj`).

1. **Get `develop` ready.** All changes are merged, `flutter analyze` and `flutter test` pass.

   ```bash
   git checkout develop && git pull
   ```

2. **Create the release branch.**

   ```bash
   git checkout -b release-x.y.z
   ```

3. **Update version info.** Set `version: x.y.z` in `pubspec.yaml` and add a `## x.y.z` section at the top of `CHANGELOG.md`. Check that the README matches the API.

4. **Squash the release branch commits into one.** Only the commits that are not in `develop`.

   ```bash
   git reset --soft origin/develop
   git commit -m "Release x.y.z"
   git push -u origin release-x.y.z --force-with-lease
   ```

5. **Open a PR to `master`.** It starts a quick "Verify Plugin Build" run.

   ```bash
   gh pr create --base master --head release-x.y.z --title "Release x.y.z"
   ```

6. **Run the full workflow and wait for it to pass.**

   ```bash
   gh workflow run plugin_build_verification.yml --ref release-x.y.z -f run_mode=full -f platform=ubuntu
   gh run list --workflow plugin_build_verification.yml --limit 1
   ```

7. **Test the example app on a device.**

   ```bash
   cd example && flutter run
   ```

8. **Publish.** Dry run first, then publish from the clean release branch.

   ```bash
   flutter pub publish --dry-run
   flutter pub publish
   ```

9. **Merge the PR into `master`** with a merge commit (no squash, no rebase).

   ```bash
   gh pr merge --merge
   ```

10. **Delete the remote release branch.** Keep the local one.

    ```bash
    git push origin --delete release-x.y.z
    ```

11. **Pull `master` locally.**

    ```bash
    git checkout master && git pull
    ```

12. **Merge `master` into `develop` and push.**

    ```bash
    git checkout develop && git merge master && git push
    ```

13. **Comment on the issues this release touched.** Leave every other issue alone (no comment, no close).

    - **Fixed by this release:** comment with the version, then close. Issues named with `Closes #N`
      in the PR close automatically on merge, so just leave the comment.

      ```bash
      gh issue comment N --body "Thanks for reporting this. It is fixed in x.y.z, now on pub.dev. Run \`flutter pub upgrade installed_apps\` to get it. If you still see the problem, let us know and we will reopen this."
      gh issue close N
      ```

      For a feature request, say what was added and how to use it instead.

    - **Touched or possibly fixed (not confirmed):** comment only, do not close. Say what changed and ask
      the reporter to check. Do not use `Closes #N` for these in the PR.

      ```bash
      gh issue comment N --body "Thanks for reporting this. x.y.z, now on pub.dev, changes this area and may fix it. Could you upgrade with \`flutter pub upgrade installed_apps\` and tell us if it still happens?"
      ```
