# AI-Assisted Honor of Kings Information Management System

A console-based Java OOP coursework project for managing Honor of Kings–style game data: players, heroes, equipment, teams, and match records. The system supports public queries, rankings, role-based login, admin CRUD, and file persistence.

## 1. Project Overview

This project implements an information management system (IMS) with:

- Players  — level, win rate, team, owned heroes, equipped items
- Heroes  — type, stats, compatible equipment, owner players
- Equipment — usage statistics and hero compatibility
- Teams — roster, average level, match count, win rate, top player
- Match records — date, opponent, result, heroes picked

Development was assisted by AI (Architect, Implementation, Testing agents). Design notes live in `docs/plan.md` and `docs/design.md`; prompts and logs are in `ai/`.

## 2. How to Run

 Requirements: JDK 8+ (tested with standard `javac` / `java`)

```powershell
cd coursework1

# Compile all sources
$files = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName }
javac -d out -sourcepath src $files

# Run
java -cp out Main
```

On startup, if `data/save.dat` exists the program loads it; otherwise sample data from `DataInitializer` is used.

To start fresh with sample data, delete the save file first:

```powershell
Remove-Item -Force data/save.dat -ErrorAction SilentlyContinue
```

### Main menu

| Option | Function |
|--------|----------|
| 1 | Player lookup (by ID or name) |
| 2 | Team overview |
| 3 | Hero details |
| 4 | Equipment statistics (sort + formulas) |
| 5 | Match history (player or team, recent n games) |
| 6 | Leaderboard (top x players) |
| 7 | Data management (login required) |
| 8 | Login / logout |
| 9 | Save to `data/save.dat` |
| 10 | Load from `data/save.dat` |
| 0 | Save and exit |

## 3. Default Login Accounts

| Role | Username | Password | Notes |
|------|----------|----------|-------|
| Admin | `admin` | `admin123` | Full CRUD on menu 7 |
| Player | `Tom` | `player1` | Edit own profile on menu 7 |
| Player | `Jack` | `player2` | … up to `player15` for other sample players |

 Note: Admin and player Tom both have ID `1`. Use 'username login' (menu 8 → 2) for players; ID login prefers Admin when IDs collide.

## 4. Implemented Features

| Feature | Implementation |
|---------|----------------|
| Player lookup | `SearchService` — menu 1 |
| Team overview | `SearchService` — menu 2 |
| Hero details | `SearchService` — menu 3 |
| Equipment statistics | `RankingService` — menu 4 |
| Match history | `MatchHistoryService` — menu 5 |
| Leaderboard | `RankingService` — menu 6 |
| Data management | `GameDataManager` + `AuthenticationService` — menu 7 |
| Login / logout | `AuthenticationService` — menu 8 |
| Save / load | `FileStorageService` — menu 9 / 10 / exit |

Sample dataset: 3 teams (5 players each), 15 heroes, 20 equipment items, 10 match records, 1 admin.

## 5. Java Concepts Used

| Concept            | Where |
|--------------------|--------|
| Inheritance        | `Player`, `Admin` extend abstract `Person` |
| Encapsulation      | Private fields with getters/setters on all models |
| Interfaces         | `Authenticatable`, `Searchable`, `Persistable` |
| Polymorphism       | `Person currentUser` in `AuthenticationService` |
| Collections        | `HashMap` (ID lookup), `ArrayList` (matches), `TreeMap` (rankings) |
| Enums              | `HeroType`, `EquipmentType`, `MatchResult`, `Role` |
| Exception handling | Invalid input retry, IO errors on save/load, friendly console messages |
| File I/O           | `FileStorageService` → `data/save.dat` (section-based CSV text) |

## 6. AI Usage Summary

| Agent | Role |
|-------|------|
| Architect | Class structure, services, UML (`docs/UML.png`) |
| Implementation | Enums, models, services, `Main` menu wiring |
| Testing / Reviewer | `docs/test-class.md`, `GameDataManager` / CSV review, bug fixes |

Recorded in:

- `ai/prompt.md` — prompts and decisions  
- `ai/agent-log.md` — agent contributions and commits  
- `ai/reflection.md` — personal reflection (to complete)

Tooling: VS Code with DeepSeek-Coder-V2 16B.

## 7. Testing Summary

Manual console tests are documented in **`docs/test-class.md`** (Test 01–10, all Pass):

- Menus 1–6: query and statistics  
- Menus 7–8: data management and authentication  
- Menus 9–10: save/load round-trip (including equipped items after CsvUtil fix)

Run tests: compile, start `Main`, follow each test’s input sequence, compare with expected output.

## 8. Known Limitations

- Console UI only (no GUI or network API)
- Admin and sample player share ID `1` — use username login for players
- Menu 10 load resets the current login session (`rebindServices`)
- Deleting a player or team removes related match records (hard delete)
- Team stats after delete are refreshed in `Main`, not inside `GameDataManager`
- `README` / `ai/reflection.md` may still need personal sections filled before submission

## Project Structure

```
coursework1/
├── src/           Java source (Main, model, service, util, enums, interfaces)
├── docs/          plan.md, design.md, test-class.md, UML.png
├── ai/            prompt.md, agent-log.md, reflection.md
├── data/          save.dat (generated, gitignored)
└── out/           compiled classes (gitignored)
```

## Related Documents

- [plan.md](docs/plan.md) — requirements, timeline, testing plan, risks  
- [design.md](docs/design.md) — architecture, classes, persistence format  
- [test-class.md](docs/test-class.md) — manual test cases  
