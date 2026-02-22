# BlockFighter

An experimental combat and AI mod for Minecraft built with Fabric.

## Overview
BlockFighter is a combat automation mod that lets you create and customize an in-game fighting bot for PvP battles. Built with Fabric, it's designed to be extensible—tweak combat styles, add new techniques, and experiment with AI behaviors.

## Features
- **Combat AI** — Customizable fighting logic
- **Combat Styles** — Sword, axe, combo, mace, and crystal fighting
- **Rendering Tools** — ESP, PvP overlays, hit indicators
- **Extensible** — Add your own fighting methods or optimizations

## Getting Started

### Prerequisites
- Java 17+
- Gradle
- Minecraft with Fabric loader

### Build
```bash
git clone https://github.com/Lumitani-pixel/blockfighter.git
cd blockfighter
./gradlew build
```

### Run
Place the built JAR from `build/libs/` into your Fabric mods folder and launch Minecraft.

## Commands

All commands use `$` as a prefix.

| Command | Description |
|---------|-------------|
| `$help` | List all available commands |
| `$toggle` | Toggle the fight bot on/off |
| `$togglemodule <module>` | Toggle a specific module by name |
| `$say <message>` | Send a message to the server |

Use `$help` in-game to see available modules for `$togglemodule`.

## Contributing

Contributions are welcome! Before submitting:

1. **Test your code** — Ensure it compiles and runs without errors
2. **Understand your code** — Be prepared to explain your changes
3. **Keep commits clean** — Don't remove important files or break the build

Open a pull request with a clear description of your changes.
