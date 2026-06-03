# Agent Log

## 1. Architect Agent

    Main contribution:  
    Suggested the seven model classes (Person, Player, Admin, Hero, Equipment, Team, MatchRecord) with inheritance and associations; proposed five service classes mapped to plan.md features §2.1–2.8; advised use of interfaces, collections, and enums.

    Human decision:  
    Accepted the seven model classes and documented fields/methods in plan.md §4.1. Accepted five service classes and added **MatchHistoryService** for §2.5. Drew UML (`docs/uml.png`) and updated §5 with class variables and methods.

    Related commits:  
    - 049f030 — model class design in plan.md  
    - 8a69cc8 — service class design in plan.md  


## 2. Implementation Agent

    Main contribution: 
    Provided implementation guidance four enums (`src/enums/`), interfaces (`Searchable`, `Persistable`, `Authenticatable`), and full model code for Hero, Equipment, Team, MatchRecord per plan.md.

    Human decision: 
    Implemented classes manually in `src/model/` and `src/enums/`; extended "HeroType" with JUNGLER and SUPPORT; used `List<Integer>` for entity links; did not put Searchable/Persistable on model classes yet

    Related commits:
    - 54af218 — enums  

## 3. Testing / Reviewer Agent

