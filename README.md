# FocusBoard

A simple, dependency-free Kotlin CLI task tracker. Tasks are persisted to a JSON file (`tasks.json`).

## Requirements

- **Kotlin compiler** (`kotlinc`) installed on your system
- No external dependencies — uses only Kotlin's standard library

## Installation

```bash
# Clone and build
git clone <repository-url>
cd FocusBoard
./build.sh
```

## Usage

```bash
./focusboard <command> [arguments]
```

### Commands

| Command | Description | Example |
|---------|-------------|---------|
| `add <title>` | Add a new task | `./focusboard add "Buy groceries"` |
| `list` | List all tasks | `./focusboard list` |
| `done <id>` | Mark a task as done | `./focusboard done 1` |
| `delete <id>` | Delete a task by id | `./focusboard delete 1` |
| `help` | Show help message | `./focusboard help` |

### Example Session

```bash
$ ./focusboard add "Prepare Air demo"

$ ./focusboard add "Write tests"

$ ./focusboard list

$ ./focusboard done 1

$ ./focusboard delete 2

$ ./focusboard list
```

## Running Tests

```bash
./run_tests.sh
```

## Project Structure

```
FocusBoard/
├── src/
│   ├── main/
│   │   ├── model/
│   │   │   └── Task.kt          # Task data model
│   │   ├── storage/
│   │   │   └── TaskStore.kt     # JSON file persistence
│   │   ├── commands/
│   │   │   └── Commands.kt      # CLI command handlers
│   │   └── Main.kt              # Entry point & argument parsing
│   └── test/
│       ├── TaskTest.kt          # Task model tests
│       └── TaskStoreTest.kt     # Storage & persistence tests
├── build.sh                     # Build script
├── run_tests.sh                 # Test runner script
├── focusboard                   # CLI entry point script
└── README.md
```

## Data Storage

Tasks are stored in `tasks.json` in the current working directory:

```json
[
  {
    "id": "1",
    "title": "Prepare Air demo",
    "done": true,
    "createdAt": 1700000000000
  }
]
```

## License

MIT
