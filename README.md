# Earth Rock Friend

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

Copyright (C) 2026 theelegantthreat

Earth Rock Friend is an Android companion for field gemology and mineralogy. It helps users explore rocks and gems with offline database lookup, multi-parameter physical search, dual-mode camera identification, forensic-style details, safety guidance, and an AI-powered GemConsult assistant.

## Features

- Offline local database for rocks, minerals, and gemstones
- Multi-parameter search based on physical characteristics
- Camera-based identification flow with dual-mode capture support
- Educational lore and contextual information for each specimen
- Safety and handling notes for collecting and identification work
- Gemini-powered conversational assistance via GemConsult

## Project layout

- `app/` — Android application sources and Gradle configuration
- `gradle/` — Gradle wrapper files
- `.env.example` — example environment variables for AI API configuration
- `build.gradle.kts` / `settings.gradle.kts` — project-level Gradle setup

## Getting started

### Prerequisites

- Android Studio or the Android SDK
- JDK 11+
- A Gemini API key for the AI features

### Setup

1. Clone the repository.
2. Copy `.env.example` to `.env`.
3. Add your `GEMINI_API_KEY` value to `.env`.
4. Open the project in Android Studio and sync Gradle.
5. Build or run the app.

### Build commands

```bash
./gradlew assembleDebug
./gradlew test
```

## Contributing

Contributions are welcome. Please read [CONTRIBUTING.md](CONTRIBUTING.md) for the workflow, coding expectations, and pull request process.

## Code of conduct

Please review [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) before participating in the project.

## License

This project is licensed under the GNU General Public License v3.0 or later.

See the [LICENSE](LICENSE) file for the full text.
