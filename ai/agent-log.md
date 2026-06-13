# Agent Log

## 1. Architect Agent

    1.1 Main contribution:  
    Suggested the seven model classes (Person, Player, Admin, Hero, Equipment, Team, MatchRecord) with inheritance and associations; proposed five service classes mapped to plan.md features §2.1–2.8; advised use of interfaces, collections, and enums. Proposed Swing GUI architecture: AppContext + MainFrame shell, feature panels for menu 1–8, admin CRUD tabs, File/Session menus; services unchanged.

    1.2 Human decision:  
    1.2.1 Accepted the seven model classes and documented fields/methods in plan.md 4.1. 
    1.2.2 Accepted five service classes and added MatchHistoryService for 2.5. Drew UML (`docs/uml.png`) and updated 5 with class variables and methods.
    1.2.3 GUI architecture (Prompt 14): four layers — AppContext bootstrap, MainFrame shell, feature
        panels mapped to menu 1–8, admin CRUD tabs behind requireAdmin(); reuse all services unchanged;
        package gui/ with panel/, dialog/, util/; GuiMain as alternate entry; CardLayout + status bar
        for session; File menu for save/load/exit.

    1.3 Related commits:  
    - 049f030 — model class design in plan.md  
    - 8a69cc8 — service class design in plan.md  
    - 8cb47af — GUI architecture design


## 2. Implementation Agent

    2.1 Main contribution:  
    Helped implement enums, model classes, interfaces, CsvUtil, GameDataManager (CRUD/find),
    DataInitializer for minimum sample dataset, SearchService,
    RankingService, MatchHistoryService, AuthenticationService, Main menu 7–8, and FileStorageService

    2.2 Human decision:  
    2.2.1 Implemented code in `src/enums/`, `src/interfaces/`, `src/model/`, `src/service/GameDataManager.java`,
        and `src/util/` (CsvUtil, DataInitializer). Extended HeroType with JUNGLER and SUPPORT. 
    2.2.2 Used `List<Integer>` for entity links. `deleteHero` removes the hero from every player's `ownedHeroes` and
        related references. DataInitializer loads 3 teams, 15 players, 15 heroes, 20 equipment, 10 matches,
        and 1 admin; calls `recalculateStats()` after load.
    2.2.3 SearchService: eight lookup/display methods; Main menu 1–3 wired for player, team, hero.
    2.2.4 RankingService: equipment rank by usage, win-rate contribution, hero count, and custom formula;
        player top-X by win rate, level, match count, and custom score; TreeMap grouping with ID/level tie-break;
        explainTieBreaking() documents default formulas and same-score rules; Main menu 4 wired.
    2.2.5 MatchHistoryService: getRecentMatchesForPlayer/Team, displayMatchHistory, calculateWinLossRecord,
        calculateHeroPickRate; WinLossRecord inner class.
    2.2.6 Main menu 7–8: handleDataManagement — admin add/delete/edit for all entities via GameDataManager,
        player editBasicInfo only; handleLogin — login by ID/username and logout; session status in printMenu;
        refreshAllTeamStats() after data changes.
    2.2.7 FileStorageService: saveAll/loadAll to data/save.dat; section headers per entity type; reuses
        model toCsvLine/fromCsvLine (Admin via id|name|password|Admin); collectXxxLines and loadXxx per entity;
        load order equipment → heroes → players → teams → admins → matches; refreshTeamStats after load;
        saveFileExists() and configurable savePath constructor.

    2.3 Related commits:
    - 54af218 — Inplement enums
    - 77cb1c9 — Complete the remaining classes in the model and do not implement the interface temporarily
    - 444d6d5 — Add CsvUtil for interfaces
    - 1ef21fa — Add DataInitializer sample data
    - 1bb5f30 — Implement SearchService.java
    - f7362c3 — Implement RankingService.java
    - e83728b — Implement MatchHistoryService.java
    - e4b6393 — Main menu 7 Data Management and 8 Login/Logout
    - 3611f96 — Implement FileStorageService.java


## 3. Testing / Reviewer Agent

    3.1 Main contribution:
    Manual test documentation in docs/test-class.md for menu 7-10; review of GameDataManager delete cascade.

    3.2 Human decision:
    3.2.1 Test 07–10 (pending): data management (admin edit player), login/logout, FileStorageService
        save/load — ran Main, filled actual output, all Pass; load resets session after rebindServices.
   
    3.3 Related commits:
    - e86ef38 — Manual test cases for menu functions 7–10 in test-class.md
