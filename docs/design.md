
## 1. Architecture Overview
    1.1 Pattern
        Three-layer console application: presentation (Main), business logic (service), data (model + util).
        GameDataManager is the single in-memory repository; services receive it via constructor injection.
        Planned Swing GUI (§12) adds a parallel presentation layer (gui/) without changing service/model code.
    1.2 Package layout (src/)
        Main.java — menu loop and handlers (primary entry)
        gui/ — planned Swing layer: AppContext, GuiMain, MainFrame, panel/, dialog/, util/ (see §12)
        model/ — Person, Player, Admin, Hero, Equipment, Team, MatchRecord
        service/ — GameDataManager, AuthenticationService, SearchService, RankingService,
                   MatchHistoryService, FileStorageService
        interfaces/ — Authenticatable, Searchable, Persistable
        enums/ — HeroType, EquipmentType, MatchResult, Role
        util/ — CsvUtil, DataInitializer, InputHelper
    1.3 Startup flow
        If data/save.dat exists → FileStorageService.loadAll()
        Else → DataInitializer.createDefault()
        Then rebindServices() wires all services to the active GameDataManager.

## 2. OOP Design
    2.1 Inheritance
        Person (abstract) ← Player, Admin. Shared id, name, password, role; authenticate() compares password.
    2.2 Interfaces
        Authenticatable — Person: login(), logout(), authenticate()
        Searchable — Player, Hero, Equipment, Team, MatchRecord: getId(), getName(), matches(keyword)
        Persistable — Player, Hero, Equipment, Team, MatchRecord: toCsvLine(); static fromCsvLine() on each class
    2.3 Polymorphism
        AuthenticationService holds Person currentUser; isAdmin()/isPlayer() delegate to Person role checks.
        Admin CRUD and player profile edit branch on authService.isAdmin() / isPlayer().
    2.4 Encapsulation
        All entity fields private; collections exposed as mutable lists/maps via getters (coursework simplification).
    2.5 Collections
        HashMap<Integer, Entity> for players, heroes, equipment, teams, admins (O(1) lookup by ID)
        ArrayList<MatchRecord> for ordered match history
        TreeMap in RankingService for score-grouped descending sort with tie-break by ID

## 3. Model Layer
    3.1 Person / Player / Admin
        Person: id, name, password, role; isAdmin(), isPlayer(), getInfo()
        Player: level, winRate, teamId, ownedHeroes (List<Integer>), matchCount,
                equippedItemsByHeroId (Map<heroId, List<equipId>>); viewProfile(), editBasicInfo()
        Admin: manageAll(), addData(), deleteData(), editData() (placeholder menus; CRUD via Main + GameDataManager)
    3.2 Hero
        type (HeroType), baseStats (Map<String,Integer>), compatibleEquipments, ownerPlayers
        getRecommendedEquipment() returns compatible equipment IDs
    3.3 Equipment
        type (EquipmentType), usageCount, winRateContribution, compatibleHeroes
        getHeroUsageCount() returns compatibleHeroes.size()
    3.4 Team
        playerList (member IDs), avgLevel, totalMatches, winRate, topPlayer (derived)
        recalculateStats(allPlayers, allMatches) recomputes aggregates from linked data
    3.5 MatchRecord
        date (LocalDate), playerId, teamId, opponent, result (MatchResult), pickedHeroes
        isWin(), getResultDisplay()

## 4. Service Layer
    4.1 GameDataManager
        Central store; add/update/remove/getById for each entity; getAll* bulk accessors.
        Remove cascades: removeHero clears player owned/equipped refs, equipment compatibility, match picks;
        removePlayer clears team roster, hero ownerPlayers, match records; similar for equipment/team.
    4.2 AuthenticationService
        login(int id, password) | login(String name, password); logout(); getCurrentUser()
        isAdmin(), isPlayer(), requireAdmin() for permission gate
        findPersonById checks admins before players (note: ID 1 collision with default data)
    4.3 SearchService
        findPlayerById/Name, findTeamById/Name, findHeroByName
        displayPlayerDetails, displayTeamOverview, displayHeroDetails (joins related entities from GameDataManager)
    4.4 RankingService
        rankEquipmentByUsage / WinRateContribution / HeroCount / CustomScore(formula)
        getTopPlayersByWinRate / Level / MatchCount / CustomScore(x)
        explainEquipmentSorting(), explainTieBreaking(); default formulas documented in code
    4.5 MatchHistoryService
        getRecentMatchesForPlayer/Team(id, n) — filter, sort by date descending, limit n
        displayMatchHistory, calculateWinLossRecord, calculateHeroPickRate
    4.6 FileStorageService
        saveAll(data) / loadAll() → data/save.dat
        Per-entity collectXxxLines / loadXxx; load order: equipment → heroes → players → teams → admins → matches
        refreshTeamStats after load; Admin lines use id|name|password|Admin

## 5. Utility Layer
    5.1 CsvUtil
        joinInts / parseIntList (Pattern.quote for delimiter literals)
        encodeStats / decodeStats (key=value; pairs)
        encodeEquipped / decodeEquipped (heroId:equipId.equipId, groups comma-separated)
        matchesIdOrName for Searchable.matches()
    5.2 DataInitializer
        createDefault() → GameDataManager with 3 teams, 15 players, 15 heroes, 20 equipment, 10 matches, 1 admin
        Links hero↔equipment compatibility, player owned heroes, team rosters; refreshTeamStats at end
    5.3 InputHelper
        readString, readInt with retry on NumberFormatException

## 6. Entity Relationships
    6.1 Link style
        Cross-entity references use Integer IDs in List<Integer> or Map<Integer, List<Integer>> (not object graphs).
    6.2 Key links
        Player.teamId → Team.id
        Player.ownedHeroes → Hero.id list
        Player.equippedItemsByHeroId → Hero.id → Equipment.id list
        Hero.compatibleEquipments ↔ Equipment.compatibleHeroes
        Hero.ownerPlayers ← Player.id
        Team.playerList ← Player.id
        MatchRecord.playerId, teamId, pickedHeroes

## 7. Main Menu Mapping
    7.1 Public (no login required)
        1 Player Lookup → SearchService
        2 Team Overview → SearchService
        3 Hero Details → SearchService
        4 Equipment Statistics → RankingService
        5 Match History → MatchHistoryService + SearchService
        6 Leaderboard → RankingService
        9 Save → FileStorageService.saveAll
        10 Load → FileStorageService.loadAll + rebindServices
        0 Exit → saveAll then quit
    7.2 Authenticated
        8 Login/Logout → AuthenticationService
        7 Data Management → Admin: add/delete/edit via GameDataManager; Player: editBasicInfo only
    7.3 Session display
        printMenu shows current user name and role, or "not logged in"

## 8. Authentication & Permissions
    8.1 Default accounts
        Admin: admin / admin123 (id=1)
        Player example: Tom / player1 (id=1 — use username login to avoid admin collision)
    8.2 Permission rules
        requireAdmin() gates menu 7 CRUD submenu
        Player on menu 7 may only edit own name/password
        Public menus 1–6 readable without login 

## 9. Persistence Design
    9.1 File
        data/save.dat (gitignored); UTF-8 text
    9.2 Section format
        # comment line
        [PLAYERS] / [HEROES] / [EQUIPMENT] / [TEAMS] / [MATCHES] / [ADMINS]
        One CSV line per record (pipe-separated fields; nested lists use ; , : . per CsvUtil)
    9.3 Player line example
        id|name|password|role|level|winRate|teamId|matchCount|ownedHeroIds|equippedEncoded
        e.g. 1|Tom|player1|Player|10|0.45|1|12|1;2;3|1:1,2:3,3:5
    9.4 Failure handling
        IOException on load → console message; startup falls back to DataInitializer sample data

## 10. UML
    10.1 Class diagram
        See docs/UML.png
    10.2 Summary
        Person hierarchy; model classes implement Searchable/Persistable where listed in plan.md §5;
        services depend on GameDataManager; Main depends on all services

## 11. Design Decisions
    11.1 Why ID lists instead of references
        Simplifies CSV persistence and delete cascades; GameDataManager resolves IDs at display time.
    11.2 Why services over fat models
        Keeps models as data + light behaviour; query/sort/auth/persistence rules live in dedicated services.
    11.3 Why single save.dat
        Coursework scope: one-file backup/restore without external DB; section headers aid manual debugging.
    11.4 Known trade-offs
        Admin/Player share id=1 in sample data; load (menu 10) resets login session; team stat refresh after
        delete is called from Main, not inside GameDataManager.

## 12. GUI Architecture (Planned — Swing)
    12.1 Design goal
        Add a Swing presentation layer without modifying model/ or service/ packages. Reuse GameDataManager
        and all six services via constructor injection; console Main.java remains the primary deliverable until
        GUI panels are implemented. GuiMain is an alternate entry point with identical startup logic.
    12.2 Layering
        Four layers: bootstrap (AppContext, GuiMain) → shell (MainFrame) → feature panels (gui/panel/) →
        dialogs/util (gui/dialog/, gui/util/). Services stay free of System.out; panels render JTable/JTextArea
        from service query results instead of SearchService.display* console output.
    12.3 Package layout (src/gui/)
        gui/AppContext — holds GameDataManager + AuthenticationService, SearchService, RankingService,
            MatchHistoryService, FileStorageService; reloadData() after loadAll() rebinds services (same as
            Main.rebindServices())
        gui/GuiMain — if data/save.dat exists loadAll(), else DataInitializer; build AppContext; open MainFrame on EDT
        gui/MainFrame — JFrame shell (see §12.4)
        gui/panel/ — feature panels (see §12.5)
        gui/dialog/ — LoginDialog, EntityEditDialog for admin add/edit forms
        gui/util/ — TableModels for ranking/match lists; MessageHelper for errors and confirm dialogs
    12.4 MainFrame shell
        Menu bar:
            File — Save (FileStorageService.saveAll), Load (loadAll + context.reloadData + refresh panels), Exit (save then quit)
            Session — Login (opens LoginDialog), Logout (AuthenticationService.logout)
        Status bar — current user name and role, or "not logged in"
        Navigation — sidebar buttons or tab strip for features 1–6 (always enabled)
        Content — CardLayout area swaps the active feature panel
        Session listener — on login/logout, enable/disable Data Management (menu 7) and admin tabs; update status bar
    12.5 Feature panels (console menu mapping)
        PlayerLookupPanel (1) — ID/name search → SearchService.find* + displayPlayerDetails data in table/area
        TeamOverviewPanel (2) — SearchService displayTeamOverview
        HeroDetailsPanel (3) — SearchService displayHeroDetails
        EquipmentStatsPanel (4) — RankingService rankEquipment* with formula explanation text area
        MatchHistoryPanel (5) — MatchHistoryService + SearchService for player/team recent n matches
        LeaderboardPanel (6) — RankingService getTopPlayers* with tie-break explanation
        DataManagementPanel (7) — JTabbedPane: Player/Hero/Equipment/Team/Match CRUD; visible only when isAdmin();
            requireAdmin() before destructive actions; uses GameDataManager add/update/remove
        ProfileEditPanel (7, player) — editBasicInfo (name/password only) when isPlayer() and not admin
        LoginDialog (8) — username or ID + password → AuthenticationService.login; modal, not a permanent panel
    12.6 Permission rules
        Panels 1–6 — public, no login required (same as console)
        Menu 7 / DataManagementPanel — admin tabs enabled only when AuthenticationService.isAdmin()
        Player role — ProfileEditPanel only on menu 7; no CRUD on other entities
        Save/Load/Exit — File menu, available without login; Load clears login session (same as console menu 10)
    12.7 Console-to-GUI mapping
        Main handler* methods → panel ActionListener / button handlers
        SearchService.display* → panel builds DefaultTableModel or formatted JTextArea from returned/joined data
        InputHelper.read* → JOptionPane, JTextField, JComboBox in dialogs
        printMenu session line → MainFrame status bar
    12.8 Load and refresh
        After FileStorageService.loadAll(): AppContext.reloadData() re-instantiates services against the new
        GameDataManager; MainFrame clears auth session, disables admin UI, and calls refresh() on visible panels
        so tables reflect loaded data (including equipped items via fixed CsvUtil.parseIntList).
    12.9 Implementation notes
        SwingUtilities.invokeLater for GuiMain; keep each panel focused on one menu feature; EntityEditDialog
        reused across admin tabs; console Main retained for coursework demo and manual test-class.md runs.
