
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
    5.1 Abstract class person: subclasses player, admin (inheritance relationship) 
    5.2 Player aggregate Hero: one player has multiple Heroes 
    5.3 Team aggregation player: a team contains multiple players 
    5.4 Hero associated equipment: heroes can equip multiple equipment 
    5.5 Matchrecord associated player/team, Hero: record player/team, select hero and opponent 
    5.6 Interfaces: searchable (player, team, hero, equipment, matchrecord Implementation), persistent (all model class implementation), authenticatable (person Implementation) 

## 6. Data Design
    Initial data set
    Team: 3 teams with at least 5 players in each team.
    Players: 10, each with at least 3 heroes.
    Hero: 15, each with at least 2 pieces of equipment.
    Equipment: 20 pieces, including attack, defense and magic.
    Competition records: 10 entries, including players, teams, opponents, dates, winners and losers, and heroes.
    data storage: Hard coded initialization data at the initial stage of development; In the later stage, the text/CSV file is used for persistence, and the program starts loading and exits saving to ensure that the data is not lost.
## 7. AI Usage Plan
    Use vscode (DeepSeek-coder-V2 16B) in the whole process to play three types of roles with clear division of labor:
    Architect agent: responsible for overall project design, class structure, UML suggestions, module division, interface design, Java concept application scheme
    Implementation agent: responsible for providing code fragments and implementation ideas
    Testing/reviewer: responsible for designing test cases, reviewing code logic, finding bugs, optimizing exception handling, checking OOP specifications, and evaluating code quality

## 8. Prompt Strategy
    Design class prompt: clear requirements, such as "design java OOP class structure based on IMS requirements of King glory, including inheritance, interface, set and enumeration, explain class responsibilities and relationships, and do not write complete code".
    Implementation class prompt: precise limit, such as "only implement the player query method, handle ID/name search, null value, no player exceptions, and attach notes."
    Debug/review prompt: focus on specific problems, such as "for a problem, locate the cause, explain the problem, give the minimum repair scheme, and do not modify irrelevant code."

## 9. Development Timeline
    Stage 1:Read requirements, create repository, write first plan.md.  
    Stage 2:AskArchitect Agentfordesignfeedback; revise class structure manually.  
    Stage 3:Implement model classes and initial data.  
    Stage 4:Implement menu system and search features.  
    Stage 5:Implement authentication and admin/player permissions.  
    Stage 6:Implement persistence and ranking functions.  
    Stage 7:Use Testing/Reviewer Agent to find bugs; fix and record decisions.  
    Stage 8:Finish documentation, reflection, Git export, and final testing.  

## 10. Testing Plan
    Test scope
    Cover all core functions: identity authentication, player query, team overview, hero details, equipment statistics, game records, leaderboards, data management, file persistence

## 11. Risk Analysis
    
## 12. Final Reflection Placeholder