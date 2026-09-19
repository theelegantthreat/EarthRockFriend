# Contributing to Earth Rock Friend

Thanks for your interest in improving Earth Rock Friend.

## Ways to contribute

- Report bugs and issues
- Suggest enhancements or UX improvements
- Improve documentation
- Add tests and fix regressions
- Help with localization or accessibility work

## Development workflow

1. Fork the repository and create a branch for your work.
2. Keep changes focused and easy to review.
3. Test your changes locally before opening a pull request.
4. Open a pull request with a clear title and summary of the problem it solves.

## Local setup

- Use Android Studio or the Android SDK to open the project.
- Copy `.env.example` to `.env` and set the required values, including `GEMINI_API_KEY`.
- Run the project with the standard Gradle workflow for Android.

## Code expectations

- Follow the existing Kotlin style and project conventions.
- Keep code readable, well-organized, and commented where necessary.
- Prefer small, focused pull requests.
- Avoid introducing unrelated formatting changes.

## Testing

Please run the relevant checks before submission:

```bash
./gradlew test
```

If you are changing UI behavior, consider adding or updating automated tests when practical.

## Reporting issues

When filing an issue, include:

- A clear description of the problem
- Steps to reproduce it
- Expected behavior
- Actual behavior
- Relevant logs, screenshots, or device details

## Community expectations

Please review the project's [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) before participating in discussions or submitting changes.

## Licensing

By contributing to this project, you agree that your contributions will be licensed under the project's GNU General Public License v3.0 or later.
