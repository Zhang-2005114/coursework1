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


## Test 11 — GUI Player Lookup (panel 1)

    function tested
        PlayerLookupPanel; Search by name; JTextArea shows player details

    input
        Run: java -cp out gui.GuiMain
        Click "1 Player Lookup" → Search type "By Name" → Tom → Search

    expected output
        JTextArea shows === Player Details === for Tom (ID 1), Team1, owned heroes, equipped items

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


## Test 12 — GUI Team Overview (panel 2)

    function tested
        TeamOverviewPanel; Search by name Team1

    input
        Click "2 Team Overview" → "By Name" → Team1 → Search

    expected output
        Team1 overview: avg level 12.00, 5 members, top player Peter

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


## Test 13 — GUI Hero Details (panel 3)

    function tested
        HeroDetailsPanel; search hero Li Bai

    input
        Click "3 Hero Details" → Li Bai → Search

    expected output
        Hero Li Bai, Type MAGE, stats, compatible equipment, owner players

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


## Test 14 — GUI Equipment Statistics (panel 4)

    function tested
        EquipmentStatsPanel; rank by usage count

    input
        Click "4 Equipment Stats" → Sort "Usage count" → Rank

    expected output
        Ranked list descending by usage; top Void Lens (ID 20, usage 29)

    actual output
        --- Equipment ranking by Usage count ---
        1. Void Lens (ID: 20) | Usage: 29 | ...
        2. Sun Pendant (ID: 19) | Usage: 28 | ...
        (full list 20 entries, matches console Test 04)

    pass/fail result
        Pass

    bug found, if any
        None


## Test 15 — GUI Match History (panel 5)

    function tested
        MatchHistoryPanel; query by player Tom, n=3

    input
        Click "5 Match History" → By player → By Name → Tom → Recent n: 3 → Search

    expected output
        1 match for Tom; win rate 100%; hero pick rates shown

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


## Test 16 — GUI Leaderboard (panel 6)

    function tested
        LeaderboardPanel; top 3 by win rate

    input
        Click "6 Leaderboard" → "Win rate" → Top x: 3 → Show

    expected output
        John, Richard, Peter as top 3 (same as console Test 06)

    actual output
        --- Top players by Win rate ---
        1. John (ID: 6) | Level: 15 | Win rate: 70.00% | Matches: 17
        2. Richard (ID: 12) | Level: 13 | Win rate: 70.00% | Matches: 23
        3. Peter (ID: 5) | Level: 14 | Win rate: 65.00% | Matches: 16

    pass/fail result
        Pass

    bug found, if any
        None


## Test 17 — GUI Login / Logout (Session menu)

    function tested
        LoginDialog; Session → Login / Logout; status bar session display

    input
        Session → Login → admin / admin123 → OK
        Status bar shows Session: admin (Admin)
        Session → Logout

    expected output
        Login dialog accepts credentials; status bar updates; logout clears session

    actual output
        Login successful (dialog closes)
        Status bar: Session: admin (Admin)
        After Logout: Status bar: Session: not logged in

    pass/fail result
        Pass

    bug found, if any
        None


## Test 18 — GUI Save (File menu)

    function tested
        MainFrame File → Save; FileStorageService.saveAll via AppContext

    input
        File → Save (fresh sample data)

    expected output
        Info dialog: Data saved to data/save.dat

    actual output
        Dialog: Data saved to data/save.dat
        File data/save.dat created with [PLAYERS] … [ADMINS] sections

    pass/fail result
        Pass

    bug found, if any
        None


## Test 19 — GUI Data Management (panel 7, admin)

    function tested
        DataManagementPanel; admin edit player name via Players tab

    input
        Session → Login → admin / admin123
        Click "7 Data Management" → Players tab → select ID 1 → Edit
        Name: TomUpdated → (other fields blank) → OK
        Player Lookup → TomUpdated to verify

    expected output
        Player table updates; lookup shows TomUpdated (ID 1)

    actual output
        Edit dialog accepted; Players table shows TomUpdated
        Player Lookup panel:
        === Player Details ===
        ID: 1
        Name: TomUpdated
        Level: 10
        (other fields unchanged)

    pass/fail result
        Pass

    bug found, if any
        None


## Test 20 — GUI Load + Player Profile (File menu + panel 7)

    function tested
        File → Load (reloadData, session reset); ProfileEditPanel for player

    input
        File → Load (after Test 19 save)
        Status bar: not logged in
        Player Lookup → TomUpdated (equipped items intact)
        Session → Login → TomUpdated / player1
        7 Data Management → change name back or edit password (blank keep) → Save Profile

    expected output
        Load restores TomUpdated with equipment; load clears login session
        Player can open profile panel and save basic info

    actual output
        File → Load: Data loaded from data/save.dat
        Session: not logged in
        Player Lookup finds TomUpdated with equipped items (Storm Blade, Arcane Staff, Iron Helm)
        Login TomUpdated / player1: success (renamed account keeps password)
        Profile Edit panel shows current profile; Save Profile: Profile updated

    pass/fail result
        Pass

    bug found, if any
        None (load resets session, same as console Test 10)

