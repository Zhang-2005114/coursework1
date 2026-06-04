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
    Helped implement enums, model classes (Hero, Equipment, Team, MatchRecord), and utility class-CsvUtil for CSV encoding/decoding and shared search matching.

    Human decision:  
    Implemented code in `src/enums/`, `src/model/`, and `src/util/CsvUtil.java`. Extended HeroType with JUNGLER and SUPPORT. 
    Used `List<Integer>` for links between entities. Applied Searchable and Persistable on Player, Hero, Equipment, Team, MatchRecord; Person implements Authenticatable. 
    Used CsvUtil in `toCsvLine()`, `fromCsvLine()`, and `matches()` to avoid duplicate logic.

    Related commits:
    - 54af218 — enums 
    - 77cb1c9 — Hero, Equipment, Team, MatchRecord  
    - 444d6d5 — Add CsvUtil for interfaces

## 3. Testing / Reviewer Agent

