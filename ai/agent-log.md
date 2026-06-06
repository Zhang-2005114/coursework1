# Agent Log

## 1. Architect Agent

    Main contribution:  
    Suggested the seven model classes (Person, Player, Admin, Hero, Equipment, Team, MatchRecord) with inheritance and associations; proposed five service classes mapped to plan.md features §2.1–2.8; advised use of interfaces, collections, and enums.

    Human decision:  
    Accepted the seven model classes and documented fields/methods in plan.md 4.1. 
    Accepted five service classes and added MatchHistoryService for 2.5. Drew UML (`docs/uml.png`) and updated 5 with class variables and methods.

    Related commits:  
    - 049f030 — model class design in plan.md  
    - 8a69cc8 — service class design in plan.md  


## 2. Implementation Agent

    Main contribution:  
    Helped implement enums, model classes, interfaces, CsvUtil, GameDataManager (CRUD/find), and
    DataInitializer for the plan.md §6 minimum sample dataset.

    Human decision:  
    Implemented code in `src/enums/`, `src/interfaces/`, `src/model/`, `src/service/GameDataManager.java`,
    and `src/util/` (CsvUtil, DataInitializer). Extended HeroType with JUNGLER and SUPPORT. Used
    `List<Integer>` for entity links. `deleteHero` removes the hero from every player's `ownedHeroes` and
    related references. DataInitializer loads 3 teams, 15 players, 15 heroes, 20 equipment, 10 matches,
    and 1 admin; calls `recalculateStats()` after load.

    Related commits:
    - 54af218 — Inplement enums
    - 77cb1c9 — Complete the remaining classes in the model and do not implement the interface temporarily
    - 444d6d5 — Add CsvUtil for interfaces
    - 1ef21fa — Add DataInitializer sample data

## 3. Testing / Reviewer Agent

