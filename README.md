# Sea Battle

## Overview

**Sea Battle** is a local networked Battleship-style game written in Java.  
You can host or join a game, place your ships on a grid, and take turns firing at your opponent until one fleet is sunk.

The project is structured as a simple Java desktop app:

- `game/` – core game logic and state
- `ui/` – screens and UI components
- `network/` – LAN hosting/joining logic

Compiled `.class` files are written to the `out/` directory.

## Requirements

- **Java 11 or newer** (JDK, not just JRE)
- A **terminal** (macOS, Linux, or Windows with WSL / Git Bash / similar)

The scripts assume that `JAVA_HOME` is set to a JDK 11+ installation.  
If it is not set, the scripts will try a default macOS path; you can override this by exporting `JAVA_HOME` yourself.

```bash
export JAVA_HOME=/path/to/your/jdk
```

## Building the game

From the project directory, run:

```bash
./build.sh
```

This will:

- Compile all Java sources from `game/`, `ui/`, and `network/`
- Place the compiled classes under `out/`

If you see a message like `javac: command not found`, make sure Java is installed and `JAVA_HOME` points to your JDK.

## Running a single instance

After building, you can start the game with:

```bash
./run.sh
```

If the game has not been built yet, `run.sh` will prompt you to run `build.sh` first.

## Running two instances (for local testing)

To quickly test a host and a client on the same machine, use:

```bash
./run-two.sh
```

This will:

- Build the project if needed
- Start **two** game instances a couple of seconds apart so you can host on one and join from the other

## Basic gameplay

- **Host a game**: choose the host option in the menu to start a lobby and wait for a client to connect.
- **Join a game**: choose the join option and enter the host's address if required (or use LAN discovery if available).
- **Place ships**: use the preparation screen to arrange your fleet on your board.
- **Take turns**: click on cells of your opponent's grid to fire; hits and misses are shown in the UI.
- **Win condition**: sink all of your opponent’s ships before they sink yours.

## Troubleshooting

- **"Run build.sh first."** – You tried to run the game before compiling. Run `./build.sh` and then `./run.sh`.
- **JAVA_HOME issues** – Ensure `JAVA_HOME` points to a valid JDK 11+ installation.
- **Out directory checked into git** – The `out/` directory is build output and should not be committed. It is already ignored via `.gitignore`.

If you run into other issues, check the terminal output for stack traces or error messages; they usually indicate what went wrong (missing Java, wrong version, etc.).