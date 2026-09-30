# Contributing

Thanks for helping improve `installed_apps`.

1. Fork the repo and branch from `develop`.
2. Make your change. Keep new options optional so releases stay non-breaking.
3. Run the checks:

   ```bash
   flutter pub get
   flutter analyze
   flutter test
   cd example && flutter build apk --debug
   ```

4. Add or update tests, the README and the CHANGELOG when behavior changes.
5. Open a pull request to `develop` and describe what and why.

Found a bug or have an idea? [Open an issue](https://github.com/sharmadhiraj/installed_apps/issues/new/choose).
