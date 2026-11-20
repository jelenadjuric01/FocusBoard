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
| `done <id>` | Mark a task as done | `./focusboard done abc1234` |
| `search <query>` | Search tasks by text | `./focusboard search "groceries"` |
| `delete <id>` | Delete a task by id | `./focusboard delete abc1234` |
| `help` | Show help message | `./focusboard help` |

### Example Session

```bash
$ ./focusboard add "Prepare Air demo"
Added task: "Prepare Air demo" (id: a1b2c3d4)

$ ./focusboard add "Write tests"
Added task: "Write tests" (id: e5f6g7h8)

$ ./focusboard list
  1. [○] [a1b2c3d4] Prepare Air demo
  2. [○] [e5f6g7h8] Write tests

Total: 2 task(s)

$ ./focusboard done a1b2c3d4
Marked done: "Prepare Air demo"

$ ./focusboard list
  1. [✓] [a1b2c3d4] Prepare Air demo
  2. [○] [e5f6g7h8] Write tests

Total: 2 task(s)

$ ./focusboard search "demo"
Search results for "demo":
  [✓] [a1b2c3d4] Prepare Air demo

Found: 1 task(s)

$ ./focusboard delete e5f6g7h8
Deleted task with id 'e5f6g7h8'.
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
    "id": "a1b2c3d4",
    "title": "Prepare Air demo",
    "done": true,
    "createdAt": 1700000000000
  }
]
```

## License

MIT
