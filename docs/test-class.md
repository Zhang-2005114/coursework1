## Test 01 — Player Lookup (menu 1)

    function tested
        Player lookup by name; SearchService.findPlayerByName, displayPlayerDetails

    input
        1 → 2 → Tom

    expected output
        === Player Details ===
        ID: 1, Name: Tom, Level: 10, Win Rate: 45.00%, Match Count: 12
        Team: Team1 (ID: 1)
        Owned Heroes: Li Bai (ID: 1), Han Xin (ID: 2), Liu Bang (ID: 3)
        Equipped Items by Hero: Storm Blade on Li Bai, Arcane Staff on Han Xin, Iron Helm on Liu Bang

    actual output
        === Player Details ===
        ID: 1
        Name: Tom
        Level: 10
        Win Rate: 45.00%
        Match Count: 12
        Team: Team1 (ID: 1)

        Owned Heroes:
          - Li Bai (ID: 1)
          - Han Xin (ID: 2)
          - Liu Bang (ID: 3)

        Equipped Items by Hero:
          Li Bai (ID: 1):
            - Storm Blade (ID: 1)
          Han Xin (ID: 2):
            - Arcane Staff (ID: 3)
          Liu Bang (ID: 3):
            - Iron Helm (ID: 5)

    pass/fail result
        Pass

    bug found, if any
        None


## Test 02 — Team Overview (menu 2)

    function tested
        Team lookup by name; SearchService.findTeamByName, displayTeamOverview

    input
        2 → 2 → Team1

    expected output
        === Team Overview ===
        ID: 1, Name: Team1, Average Level: 12.00, Total Matches: 5, Win Rate: 60.00%
        Top Player: Peter (ID: 5)
        Team Members (5): Tom, Jack, Mike, David, Peter (IDs 1–5)

    actual output
        === Team Overview ===
        ID: 1
        Name: Team1
        Average Level: 12.00
        Total Matches: 5
        Win Rate: 60.00%
        Top Player: Peter (ID: 5)

        Team Members (5):
          - Tom (ID: 1, Level: 10)
          - Jack (ID: 2, Level: 11)
          - Mike (ID: 3, Level: 12)
          - David (ID: 4, Level: 13)
          - Peter (ID: 5, Level: 14)

    pass/fail result
        Pass

    bug found, if any
        None

## Test 03 — Hero Details (menu 3)

    function tested
        Hero lookup by name; SearchService.findHeroByName, displayHeroDetails

    input
        3 → Li Bai

    expected output
        === Hero Details ===
        ID: 1, Name: Li Bai, Type: MAGE
        Base Stats: attack 80, defense 50, hp 3000
        Compatible Equipments: Storm Blade (ID: 1), Guard Plate (ID: 2)
        Owner Players includes Tom (ID: 1)

    actual output
        === Hero Details ===
        ID: 1
        Name: Li Bai
        Type: MAGE

        Base Stats:
          - defense: 50
          - attack: 80
          - hp: 3000

        Compatible Equipments:
          - Storm Blade (ID: 1)
          - Guard Plate (ID: 2)

        Owner Players (3):
          - Tom (ID: 1)
          - John (ID: 6)
          - William (ID: 11)

    pass/fail result
        Pass

    bug found, if any
        None


## Test 04 — Equipment Statistics (menu 4)

    function tested
        Equipment ranking by usage count; RankingService.rankEquipmentByUsage

    input
        4 → 1

    expected output
        Sort rule: usageCount descending; ties -> lower equipment ID.
        Top of list: Void Lens (ID: 20), Usage: 29
        Bottom of list: Storm Blade (ID: 1), Usage: 10
        Full list contains 20 equipment entries

    actual output
        Sort rule: usageCount descending; ties -> lower equipment ID.

        --- Equipment ranking by Usage count ---
        1. Void Lens (ID: 20) | Usage: 29
        2. Sun Pendant (ID: 19) | Usage: 28
        3. Moon Orb (ID: 18) | Usage: 27
        ...
        19. Guard Plate (ID: 2) | Usage: 11
        20. Storm Blade (ID: 1) | Usage: 10
        (full list: 20 entries, descending by usage count)

    pass/fail result
        Pass

    bug found, if any
        None


## Test 05 — Match History (menu 5)

    function tested
        Recent matches for player; MatchHistoryService.getRecentMatchesForPlayer, displayMatchHistory

    input
        5 → 1 → 3 → 2 → Tom
        (query by player, n=3, search by name)

    expected output
        --- Player: Tom ---
        Note: only 1 match(es) on record (requested 3).
        Date: 2026-03-01, Opponent: Shadow Legion, Result: Win
        Heroes picked: Li Bai, Han Xin
        Win rate (this sample): 100.00% (1W / 0L / 1 total)
        Hero pick rate: Li Bai 50.00%, Han Xin 50.00%

    actual output
        --- Player: Tom ---
        Note: only 1 match(es) on record (requested 3).
        Recent 1 game(s):
          Date: 2026-03-01
          Opponent: Shadow Legion
          Result: Win
          Heroes picked: Li Bai, Han Xin

        Win rate (this sample): 100.00% (1W / 0L / 1 total)
        Hero pick rate:
          - Li Bai: 50.00%
          - Han Xin: 50.00%

    pass/fail result
        Pass

    bug found, if any
        None


## Test 06 — Leaderboard (menu 6)

    function tested
        Top players by win rate; RankingService.getTopPlayersByWinRate

    input
        6 → 1 → 3
        (top by win rate, show top 3)

    expected output
        --- Top players by Win rate ---
        1. John (ID: 6) | Level: 15 | Win rate: 70.00% | Matches: 17
        2. Richard (ID: 12) | Level: 13 | Win rate: 70.00% | Matches: 23
        3. Peter (ID: 5) | Level: 14 | Win rate: 65.00% | Matches: 16

    actual output
        --- Top players by Win rate ---
        1. John (ID: 6) | Level: 15 | Win rate: 70.00% | Matches: 17
        2. Richard (ID: 12) | Level: 13 | Win rate: 70.00% | Matches: 23
        3. Peter (ID: 5) | Level: 14 | Win rate: 65.00% | Matches: 16

    pass/fail result
        Pass

    bug found, if any
        None


## Test 07 — Data Management (menu 7)

    function tested
        Admin edit player; GameDataManager.updatePlayer via handleDataManagement;
        AuthenticationService.requireAdmin()

    input
        8 → 2 → admin → admin123
        7 → 3 → 1 → 1 → TomUpdated → (blank × 5, keep other fields)
        0

    expected output
        Login successful. Welcome, admin (Admin).
        --- Admin Data Management --- with 1 Add  2 Delete  3 Edit
        Player updated.
        Player lookup shows Name: TomUpdated (ID: 1)

    actual output
        Login successful. Welcome, admin (Admin).
        Session: admin (Admin)

        --- Admin Data Management ---
        1 Add  2 Delete  3 Edit  0 Back
        Select entity: Player
        Name [Tom]: TomUpdated
        (password/level/win rate/team/match count left blank)
        Player updated.

        (Verify via menu 1 → 2 → TomUpdated)
        === Player Details ===
        ID: 1
        Name: TomUpdated
        Level: 10
        Win Rate: 45.00%
        Match Count: 12
        Team: Team1 (ID: 1)

    pass/fail result
        Pass

    bug found, if any
        None


## Test 08 — Login / Logout (menu 8)

    function tested
        Login by username and logout; AuthenticationService.login, logout, session display

    input
        8 → 2 → admin → admin123
        8 → 1
        (logout)

    expected output
        Login successful. Welcome, admin (Admin).
        Main menu shows Session: admin (Admin)
        User: admin has logged out.
        Main menu shows Session: not logged in

    actual output
        Login successful. Welcome, admin (Admin).

        Session: admin (Admin)

        Logged in as: User information [ID: 1, name: admin, role: Admin]
        1 Logout  0 Back
        User: admin has logged out.

        Session: not logged in

    pass/fail result
        Pass

    bug found, if any
        None

## Test 09 — Save (menu 9)

    function tested
        Persist all data to file; FileStorageService.saveAll

    input
        9
        (fresh sample data, no existing save.dat)

    expected output
        Data saved to data/save.dat
        File data/save.dat created with section headers [PLAYERS], [HEROES], etc.

    actual output
        Data saved to data/save.dat

        (File created: data/save.dat, ~3 KB, contains [PLAYERS] section with 15 player lines)

    pass/fail result
        Pass

    bug found, if any
        None


## Test 10 — Load (menu 10)

    function tested
        Reload data from file; FileStorageService.loadAll, rebindServices

    input
        (After Test 07 save: player Tom renamed to TomUpdated)
        10
        1 → 2 → TomUpdated

    expected output
        Data loaded from data/save.dat
        Player lookup finds TomUpdated with saved attributes

    actual output
        Data loaded from data/save.dat

        Session: not logged in
        (load resets login session)

        === Player Details ===
        ID: 1
        Name: TomUpdated
        Level: 10
        Win Rate: 45.00%
        Match Count: 12
        Team: Team1 (ID: 1)

        Equipped Items by Hero:
          Li Bai (ID: 1): Storm Blade (ID: 1)
          Han Xin (ID: 2): Arcane Staff (ID: 3)
          Liu Bang (ID: 3): Iron Helm (ID: 5)

    pass/fail result
        Pass

    bug found, if any
        None (fixed CsvUtil.parseIntList: delimiter "." now uses Pattern.quote)

