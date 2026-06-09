
## 1. Project Goal
    1.1 System scope
        Design and implement an AI-assisted Honor of Kings information management system using Java OOP, providing query, statistics, ranking, and data management for heroes, equipment, teams, and match records.
    1.2 User roles
        Ordinary players view personal information, heroes, achievements, and leaderboards; administrators add, delete, and modify all data.
    1.3 Development approach
        Use console interaction for a complete, maintainable codebase; record the full AI-assisted development process in the "ai/" folder.

## 2. Requirement Analysis
    2.1 Player Lookup
        Find players by ID or name, display team, level, winning rate, heroes and equipment.
    2.2 Team Overview
        Check the team by ID or name, show the members, average level, total number of games, winning rate, and the strongest player in the team.
    2.3 Hero Details
        Check the hero name, display type, basic attributes, adaptive equipment, players and recommended equipment.
    2.4 Equipment Statistics
        Sort the equipment according to the usage rate, victory rate contribution and the number of heroes, with the description of the sorting formula.
    2.5 Match History
        Query the player or team recent n games, display the opponent, date, victory and defeat, hero selection, victory rate, hero selection rate.
    2.6 Leader board
        Display top x players by winning rate, level, number of games and custom score, and explain the same score processing rules.
    2.7 Data Management
        The administrator can add, delete and modify players, heroes, equipment, teams and game records; players can only view public data and edit basic personal information.
    2.8 Authentication
        Realize administrator/player dual role login and logout, and strictly separate permissions to ensure data security.

## 3. Java Concept Used
    3.1 Inheritance
        Player and Admin inherit the abstract parent class Person.
    3.2 Encapsulation
        All model class fields are private and accessed through getters and setters.
    3.3 Interface
        Implement Searchable, Persistable, and Authenticatable interfaces on the appropriate classes.
    3.4 Polymorphism
        Store Player/Admin objects with Person references and handle user logic in a unified manner.
    3.5 Collection
        Use ArrayList for match records and ordered lists, HashMap for ID lookup, and TreeMap for leaderboard sorting.
    3.6 Exception handling
        Capture input errors, missing IDs, duplicate data, and file read-write exceptions with friendly prompts.
    3.7 File I/O
        Read and write text/CSV data; support saving and loading system data on exit and startup.
    3.8 Enumeration
        HeroType (MAGE/Shooter/Tank/Jungler/Support), MatchResult (WIN/LOSE), EquipmentType (ATTACK/DEFENSE/SPELL), Role (ADMIN/PLAYER).

## 4. Class Design
### 4.1 model
    4.1.1 Person (abstract)
        Variables: "id", "name", "password", "role". Implements Authenticatable. Methods: "login()", "logout()", "getInfo()", "authenticate()", "isAdmin()", "isPlayer()".
    4.1.2 Player
        Extends Person; implements Searchable and Persistable. Variables: "level", "winRate", "teamId", "ownedHeroes", "matchCount", "equippedItemsByHeroId". Methods: "viewProfile()", "editBasicInfo()".
    4.1.3 Admin
        Extends Person. Methods: "manageAll()", "addData()", "deleteData()", "editData()".
    4.1.4 Hero
        Implements Searchable and Persistable. Variables: "id", "name", "type", "baseStats", "compatibleEquipments", "ownerPlayers". Methods: "getRecommendedEquipment()", "setStat()", "addCompatibleEquipment()".
    4.1.5 Equipment
        Implements Searchable and Persistable. Variables: "id", "name", "type", "usageCount", "winRateContribution", "compatibleHeroes". Methods: "getHeroUsageCount()", "addCompatibleHero()".
    4.1.6 Team
        Implements Searchable and Persistable. Variables: "id", "name", "playerList", "avgLevel", "totalMatches", "winRate", "topPlayer". Methods: "recalculateStats()", "addPlayer()", "removePlayer()".
    4.1.7 MatchRecord
        Implements Searchable and Persistable. Variables: "id", "date", "playerId", "teamId", "opponent", "result", "pickedHeroes". Methods: "isWin()", "getResultDisplay()".
### 4.2 service
    4.2.1 GameDataManager
        Central data store (§2.7). Variables: "players", "heroes", "equipments", "teams" ("HashMap<Integer, …>"), "matchRecords" ("ArrayList<MatchRecord>"), "admins". Methods: add/remove/update/getById for each entity; "initializeSampleData()"; "getAllPlayers()" and related bulk accessors. Remove operations cascade to linked data.
    4.2.2 AuthenticationService
        Handles login, logout, role verification, and permission control for Admin and Player (§2.8).
    4.2.3 SearchService
        Lookup players, teams, heroes, equipment, and matches by ID or name 
    4.2.4 RankingService
        Equipment statistics and player leaderboards by win rate, level, match count, and custom score  .
    4.2.5 FileStorageService
        Save and load system data to/from CSV or text files (§3.7).
    4.2.6 MatchHistoryService
        Retrieve recent match records for a player or team; show opponents, results, and hero picks .
### 4.3 util
    4.3.1 CsvUtil
        Static CSV helpers for Persistable models and Searchable lookup: "joinInts"/"parseIntList", "encodeStats"/"decodeStats", "encodeEquipped"/"decodeEquipped", "matchesIdOrName".
    4.3.2 DataInitializer
        Builds the §6 initial dataset via "createSampleData()" and "loadSampleData(manager)"; links hero owners, equipment compatibility, and team stats. Called by "GameDataManager.initializeSampleData()".
    4.3.3 InputHelper
        Console input for the menu: "readString(prompt)", "readInt(prompt)" with retry on invalid integers.

## 5. UML Draft
    5.1 Diagram
        See "docs/uml.png" (source: "docs/uml.mmd").
    5.2 Person (abstract)
        Variables: "id", "name", "password", "role". Methods: "login()", "logout()", "getInfo()", "authenticate()".
    5.3 Player
        Variables: "level", "winRate", "teamId", "ownedHeroes", "matchCount". Methods: "viewProfile()", "editBasicInfo()".
    5.4 Admin
        Variables: (none beyond Person). Methods: "manageAll()", "addData()", "deleteData()", "editData()".
    5.5 Hero
        Variables: "id", "name", "type", "baseStats", "compatibleEquipments", "ownerPlayers". Methods: "getRecommendedEquipment()".
    5.6 Equipment
        Variables: "id", "name", "type", "usageCount", "winRateContribution", "compatibleHeroes". Methods: "getHeroUsageCount()".
    5.7 Team
        Variables: "id", "name", "playerList", "avgLevel", "totalMatches", "winRate", "topPlayer". Methods: "recalculateStats()".
    5.8 MatchRecord
        Variables: "id", "date", "playerId", "teamId", "opponent", "result", "pickedHeroes". Methods: "isWin()", "getResultDisplay()".
    5.9 Authenticatable (interface)
        Used by Person. Methods: "login()", "logout()", "authenticate()".
    5.10 Searchable (interface)
        Methods: "getId()", "getName()", "matches(keyword)".
    5.11 Persistable (interface)
        Methods: "toCsvLine()"; model classes also provide "fromCsvLine()" for loading.

## 6. Data Design
    6.1 Teams
        3 teams, each with 5 players.
    6.2 Players
        15 players, each owning at least 3 heroes.
    6.3 Heroes
        15 heroes, each linked to at least 2 equipment items.
    6.4 Equipment
        20 items covering attack, defense, and spell types.
    6.5 Match records
        10 entries with player, team, opponent, date, result (win/lose), and heroes picked.
    6.6 Admin account
        One default admin ("admin" / "admin123") for testing login.
    6.7 Data storage
        Sample data via DataInitializer on first run; FileStorageService persists to data/save.dat on menu 9,
        menu 0 exit, and auto-load on startup when the file exists. Section-based text format with CSV lines
        per entity ([PLAYERS], [HEROES], [EQUIPMENT], [TEAMS], [MATCHES], [ADMINS]).

## 7. AI Usage Plan
    7.1 Tool
        VS Code with DeepSeek-Coder-V2 16B throughout the project.
    7.2 Architect Agent
        Overall design, class structure, UML, module division, interface design, and Java concept mapping.
    7.3 Implementation Agent
        Code snippets and implementation ideas for scoped tasks only.
    7.4 Testing/Reviewer Agent
        Test cases, code review, bug finding, exception handling, OOP checks, and quality feedback.
    7.5 Documentation
        Record all prompts and decisions in "ai/prompt.md", "ai/agent-log.md", and "ai/reflection.md".

## 8. Prompt Strategy
    8.1 Design-class prompt
        State requirements clearly; ask for class responsibilities and relationships only, not full code.
    8.2 Implementation prompt
        Limit scope precisely; specify one class or method, null/missing handling, and brief comments.
    8.3 Debug/review prompt
        Target one issue; explain cause and give a minimal fix without changing unrelated code.

## 9. Development Timeline
    9.1 Stage 1 — Done
        Read requirements, create the repository, write the first "plan.md".
    9.2 Stage 2 — Done
        Use the Architect Agent for design feedback; revise the class structure manually.
    9.3 Stage 3 — Done
        Implement model classes, enums, interfaces, util classes, and initial sample data.
    9.4 Stage 4 — Done
        Implement the menu system and search features (Main menu 1–3, SearchService).
    9.5 Stage 5 — Done
        Implement authentication and Admin/Player permissions (AuthenticationService, Main menu 7–8).
    9.6 Stage 6 — Done
        Implement persistence and ranking features (RankingService, MatchHistoryService, FileStorageService,
        Main menu 4–6, 9–10).
    9.7 Stage 7 — Done
        Testing/Reviewer Agent: manual tests in docs/test-class.md (Test 01–10); fixed CsvUtil.parseIntList
        delimiter bug found during save/load review (commit c26ea89).
    9.8 Stage 8 — In progress
        Complete README, ai/reflection.md, final Git export, and demo preparation.

## 10. Testing Plan
    10.1 Test scope
        Cover all core functions in §2: authentication, player lookup, team overview, hero details,
        equipment statistics, match history, leaderboards, data management, and file persistence.
    10.2 Test method
        Manual console testing (no JUnit); record input, expected output, actual output, and pass/fail in
        docs/test-class.md. Run: compile all src/*.java, java -cp out Main, follow each Test input sequence.
    10.3 Test cases (docs/test-class.md)
        Test 01 — menu 1, player lookup by name (Tom)
        Test 02 — menu 2, team overview (Team1)
        Test 03 — menu 3, hero details (Li Bai)
        Test 04 — menu 4, equipment ranking by usage
        Test 05 — menu 5, match history for player (Tom, n=3)
        Test 06 — menu 6, top-3 players by win rate
        Test 07 — menu 7, admin edit player (admin/admin123)
        Test 08 — menu 8, login and logout
        Test 09 — menu 9, save to data/save.dat
        Test 10 — menu 10, load and verify persisted data (including equipped items)
    10.4 Result summary
        All Test 01–10 marked Pass after CsvUtil fix. Known non-blocking behaviour: menu 10 load resets
        login session via rebindServices().

## 11. Risk Analysis
    11.1 ID collision (Admin vs Player)
        Admin and Player 1 share ID 1; login by ID always matches Admin first. Mitigation: use username
        login for players; consider separate admin ID in future data design.
    11.2 Cascade delete side effects
        removePlayer/removeTeam delete related MatchRecords entirely; team stats are refreshed in Main but
        not inside GameDataManager. Mitigation: document behaviour; optional refactor to soft-delete records
        and central refreshTeamStats in GameDataManager.
    11.3 File persistence format
        Pipe-delimited CSV in a single save.dat file; manual edits can corrupt load. Mitigation: always use
        menu 9/0 to save; load failure falls back to sample data on startup with a console message.
    11.4 CSV parsing edge cases
        Special regex characters in delimiters broke equipped-item load (fixed: Pattern.quote in parseIntList).
        Risk remains if new delimiters are added without literal splitting.
    11.5 AI-generated code quality
        AI may over-scope changes or miss integration (e.g. Main wiring). Mitigation: scoped prompts,
        manual compile/run after each stage, Testing Agent review before submission.
    11.6 Scope creep
        Console-only IMS without GUI or network is sufficient for coursework; avoid adding unrequested features
        close to deadline.

## 12. Final Reflection
    12.1 Project outcome
        Delivered a working console IMS covering §2.1–2.8: query, statistics, rankings, CRUD with role
        permissions, login/logout, and file persistence. Code lives under src/; AI process documented in ai/.
    12.2 What went well
        Layered design (model / service / util) kept Main readable; DataInitializer gives repeatable demo
        data; manual test-class.md gives traceable evidence for each menu function.
    12.3 What was challenging
        Wiring many services into Main; ensuring save/load round-trip preserves linked data; reviewing delete
        cascades and CSV encoding/decoding consistency.
    12.4 Bugs found and fixed
        CsvUtil.parseIntList treated "." as regex, causing empty equipped items after load (fixed in c26ea89).
        GameDataManager.removeHero cascade was verified correct during review.
    12.5 Remaining limitations
        No removeAdmin; README and ai/reflection.md still to fill; Admin/Player ID 1 collision; load clears
        session; only 10 sample match records.
    12.6 Personal reflection
        Complete the questionnaire in ai/reflection.md (tools used, useful prompts, self vs AI contribution,
        Java concepts learned). Export Git history and prepare demo script from docs/test-class.md.
