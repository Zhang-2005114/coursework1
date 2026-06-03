
## 1.Project Goal:
    The goal of this project is to design and implement an AI assisted IMS, which uses java object-oriented programming technology to provide players and administrators with query, statistics, ranking and data management functions of heroes, equipment, teams and game records.  
    The system is designed for two types of users: ordinary players (viewing personal information, heroes, achievements, and leaderboards) and administrators (adding, deleting, and modifying all data).  
    The system uses console interaction to ensure complete functions, clear code structure, scalability and maintainability. The whole process of AI aided development is recorded to reflect the responsible AI use and programming ability.  

## 2. Requirement Analysis:
    2.1 Player Lookup: Find players by ID or name, display team, level, winning rate, heroes and equipment.   
    2.2 Team Overview: Check the team by ID or name, show the members, average level, total number of games, winning rate, and the strongest player in the team.  
    2.3 Hero Details: Check the hero's name, display type, basic attributes, adaptive equipment, players and recommended equipment.  
    2.4 Equipment Statistics: Sort the equipment according to the usage rate, victory rate contribution and the number of heroes, with the description of the sorting formula.  
    2.5 Match History:Query the player or team's recent n games, display the opponent, date, victory and defeat, hero selection, victory rate, hero selection rate.  
    2.6 Leader board: Display top x players by winning rate, level, number of games and custom score, and explain the same score processing rules.  
    2.7 Data Management: The administrator can add, delete and modify players, heroes, equipment, teams and game records; Players can only view public data and edit basic personal information.  
    2.8 Authentication: Realize administrator/player dual role login and logout, and strictly separate permissions to ensure data security.  

## 3. Java Concept Used
    3.1 Inheritance: player and admin inherit the abstract parent class person.  
    3.2 Encapsulation: all model class fields are set to private and can be accessed and modified through getter or setter.  
    3.3 Interface: implement searchable, persistent, and authenticated interfaces.  
    3.4 Polymorphism: store player/admin objects with the person parent class reference, and handle user logic in a unified manner.  
    3.5 Collection: use ArrayList to save player/hero/game records, HashMap to quickly search by ID, and treemap to sort the leaderboards.  
    3.6 Exception handling: capture input errors, ID does not exist, data duplication, file read-write exceptions, and friendly prompts.  
    3.7 File i/o: text/CSV read/write data, support system data saving and loading.  
    3.8 Enumeration: herotype (MAGE/Shooter/tank), matchresult (win/lose), equipmenttype (attack/defense/spell).  

## 4. Class Design
### 4.1 model
    4.1.1 Person (abstract class): attribute ID, name, password, role; Methods login(), logout(), getinfo(). 
    4.1.2 Player (subclass): inherit person; Attribute level, winrate, teamid, ownedheroes; Methods viewprofile(), editbasicinfo(). 
    4.1.3 Admin (subclass): inherit person; Methods adddata(), deletedata(), editdata(), managerall(). 
    4.1.4 Hero: attribute ID, name, type, basestats, compatibleequipments, ownerplayers. 
    4.1.5 Equipment: attribute ID, name, type, usagecount, winratecontribution, compatibleheroes. 
    4.1.6 Team: attribute ID, name, playerlist, avgglevel, totalmatches, winrate, topplayer. 
    4.1.7 Matchrecord: attribute ID, date, playerid/teamid, opponent, result, pickedheroes. 
### 4.2 service
    4.2.1 GameDataManager: Central data manager storing all players, heroes, equipment, teams, and match records; provides unified CRUD operations.
    4.2.2 AuthenticationService: Handles user login, logout, role verification, and permission control for Admin and Player.
    4.2.3 SearchService: Implements lookup functions for players, teams, heroes, equipment, and matches by ID or name.
    4.2.4 RankingService: Calculates equipment statistics and generates player leaderboards by win rate, level, match count, and custom score.
    4.2.5 FileStorageService: Supports data persistence by saving and loading system data to/from CSV or text files.
    4.2.6 MatchHistoryService: Retrieves recent match records for players or teams and displays match results and hero picks.
### 4.3 util


## 5. UML Draft
    Diagram: `docs/UML.png`. 

    5.1 Person(abstract)  
    Variables: `id`, `name`, `password`, `role`  
    Methods: `login()`, `logout()`, `getInfo()`, `authenticate()`

    5.2 Player 
    Variables: `level`, `winRate`, `teamId`, `ownedHeroes`, `matchCount`  
    Methods: `viewProfile()`, `editBasicInfo()`

    5.3 Admin  
    Variables: (none beyond `Person`)  
    Methods: `manageAll()`, `addData()`, `deleteData()`, `editData()`

    5.4 Hero   
    Variables: `id`, `name`, `type`, `baseStats`, `compatibleEquipments`, `ownerPlayers`  
    Methods: `getRecommendedEquipment()`

    5.5 Equipment   
    Variables: `id`, `name`, `type`, `usageCount`, `winRateContribution`, `compatibleHeroes`  
    Methods: `getHeroUsageCount()`

    5.6 Team  
    Variables: `id`, `name`, `playerList`, `avgLevel`, `totalMatches`, `winRate`, `topPlayer`  
    Methods: `recalculateStats()`

    5.7 MatchRecord  
    Variables: `id`, `date`, `playerId`, `teamId`, `opponent`, `result`, `pickedHeroes`  
    Methods: `isWin()`, `getResultDisplay()`

    5.8 Authenticatable (interface, used by `Person`): `login()`, `logout()`, `authenticate()`

## 6. Data Design
    Initial data set  
    Teams: 3 teams, each with at least 5 players.  
    Players: 10 players, each owning at least 3 heroes.  
    Heroes: 15 heroes, each able to equip at least 2 items.  
    Equipment: 20 items, including attack, defense, and spell types.  
    Match records: 10 entries, covering players, teams, opponents, dates, results (win/lose), and heroes picked.  
    Data storage: use hard-coded sample data during early development; later use text or CSV files via `FileStorageService` to load on startup and save on exit so data is not lost.

## 7. AI Usage Plan
    Use VS Code (DeepSeek-Coder-V2 16B) throughout the project with three agent roles:  
    Architect Agent: overall design, class structure, UML, module division, interface design, and how Java concepts are applied.  
    Implementation Agent: code snippets and implementation ideas for selected tasks only.  
    Testing/Reviewer Agent: test cases, code review, bug finding, exception handling, OOP checks, and quality feedback.  
    Record all prompts and decisions in the `ai/` folder (`prompts.md`, `agent-log.md`, `reflection.md`).

## 8. Prompt Strategy
    Design-class prompt: state requirements clearly, e.g. “Design the Java OOP structure for the Honor of Kings IMS (inheritance, interfaces, collections). Explain class responsibilities and relationships only; do not write full code.”  
    Implementation prompt: limit scope precisely, e.g. “Implement only the player lookup method; support search by ID/name; handle null and missing player; add brief comments.”  
    Debug/review prompt: target one issue, e.g. “This method fails when the hero is missing. Explain the cause and give a minimal fix without changing unrelated code.”

## 9. Development Timeline
    Stage 1: Read requirements, create the repository, write the first `plan.md`.  
    Stage 2: Use the Architect Agent for design feedback; revise the class structure manually.  
    Stage 3: Implement model classes and initial sample data.  
    Stage 4: Implement the menu system and search features.  
    Stage 5: Implement authentication and Admin/Player permissions.  
    Stage 6: Implement persistence and ranking features.  
    Stage 7: Use the Testing/Reviewer Agent to find bugs; fix issues and record decisions.  
    Stage 8: Complete documentation, reflection, Git export, and final testing.

## 10. Testing Plan
    Test scope: cover all core functions in §2 — authentication, player lookup, team overview, hero details, equipment statistics, match history, leaderboards, data management, and file persistence.  
    Document at least 10 test cases in `docs/test-cases.md` (test ID, function, input, expected output, actual output, pass/fail, bugs found).

## 11. Risk Analysis
    
## 12. Final Reflection Placeholder