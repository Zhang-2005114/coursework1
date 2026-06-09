import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class Main {
    private static GameDataManager dataManager;
    private static AuthenticationService authService;
    private static SearchService searchService;
    private static RankingService rankingService;
    private static MatchHistoryService matchHistoryService;
    private static final FileStorageService fileStorageService = new FileStorageService();

    public static void main(String[] args) {
        dataManager = initializeDataManager();
        rebindServices();
        run();
    }

    private static GameDataManager initializeDataManager() {
        if (fileStorageService.saveFileExists()) {
            try {
                GameDataManager loaded = fileStorageService.loadAll();
                System.out.println("Loaded data from " + fileStorageService.getSavePath());
                return loaded;
            } catch (IOException e) {
                System.out.println("Failed to load save file, using sample data: " + e.getMessage());
            }
        }
        return DataInitializer.createDefault();
    }

    private static void rebindServices() {
        authService = new AuthenticationService(dataManager);
        searchService = new SearchService(dataManager);
        rankingService = new RankingService(dataManager);
        matchHistoryService = new MatchHistoryService(dataManager);
    }

    private static void run() {
        System.out.println("=== Honor of Kings IMS ===");
        while (true) {
            printMenu();
            int choice = InputHelper.readInt("Select: ");
            switch (choice) {
                case 1:
                    handlePlayerLookup();
                    break;
                case 2:
                    handleTeamOverview();
                    break;
                case 3:
                    handleHeroDetails();
                    break;
                case 4:
                    handleEquipmentStatistics();
                    break;
                case 5:
                    handleMatchHistory();
                    break;
                case 6:
                    handleLeaderboard();
                    break;
                case 7:
                    handleDataManagement();
                    break;
                case 8:
                    handleLogin();
                    break;
                case 9:
                    handleSave();
                    break;
                case 10:
                    handleLoad();
                    break;
                case 0:
                    handleSaveOnExit();
                    System.out.println("Goodbye.");
                    return;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1 Player Lookup  2 Team Overview  3 Hero Details 4 Equipment Statistics 5 Match History");
        System.out.println("6 Leaderboard  7 Data Management  8 Login/Logout  9 Save  10 Load  0 Exit");
        if (authService.getCurrentUser() != null) {
            System.out.println("Session: " + authService.getCurrentUser().getName()
                    + " (" + authService.getCurrentUser().getRole() + ")");
        } else {
            System.out.println("Session: not logged in");
        }
    }

    //--1 Player Lookup--
    private static void handlePlayerLookup() {
        System.out.println("\n=== Player Lookup ===");
        System.out.println("1 Search by ID");
        System.out.println("2 Search by Name");
        int subChoice = InputHelper.readInt("Select search method: ");

        Player player = null;
        switch (subChoice) {
            case 1:
                int id = InputHelper.readInt("Enter player ID: ");
                player = searchService.findPlayerById(id);
                break;
            case 2:
                String name = InputHelper.readString("Enter player name: ");
                player = searchService.findPlayerByName(name);
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        searchService.displayPlayerDetails(player);
    }

    //-- 2 Team Overview --
    private static void handleTeamOverview() {
        System.out.println("\n=== Team Overview ===");
        System.out.println("1 Search by ID");
        System.out.println("2 Search by Name");
        int subChoice = InputHelper.readInt("Select search method: ");

        Team team = null;
        switch (subChoice) {
            case 1:
                int id = InputHelper.readInt("Enter team ID: ");
                team = searchService.findTeamById(id);
                break;
            case 2:
                String name = InputHelper.readString("Enter team name: ");
                team = searchService.findTeamByName(name);
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        searchService.displayTeamOverview(team);
    }

    //--3 Hero Details--
    private static void handleHeroDetails() {
        System.out.println("\n=== Hero Details ===");
        String name = InputHelper.readString("Enter hero name: ");
        Hero hero = searchService.findHeroByName(name);
        searchService.displayHeroDetails(hero);
    }

    //--4 Equipment Statistics--
    private static void handleEquipmentStatistics() {
        System.out.println("\n=== Equipment Statistics ===");
        System.out.println("1 Sort by usage count");
        System.out.println("2 Sort by win rate contribution");
        System.out.println("3 Sort by hero count");
        System.out.println("4 Sort by custom score");
        System.out.println("5 View sorting formulas");
        int subChoice = InputHelper.readInt("Select sort method: ");

        if (subChoice == 5) {
            rankingService.explainEquipmentSorting();
            return;
        }

        List<Equipment> ranked;
        String sortLabel;
        String formula = null;
        switch (subChoice) {
            case 1:
                ranked = rankingService.rankEquipmentByUsage();
                sortLabel = "Usage count";
                System.out.println("Sort rule: usageCount descending; ties -> lower equipment ID.");
                break;
            case 2:
                ranked = rankingService.rankEquipmentByWinRateContribution();
                sortLabel = "Win rate contribution";
                System.out.println("Sort rule: winRateContribution descending; ties -> lower equipment ID.");
                break;
            case 3:
                ranked = rankingService.rankEquipmentByHeroCount();
                sortLabel = "Compatible hero count";
                System.out.println("Sort rule: compatible hero count descending; ties -> lower equipment ID.");
                break;
            case 4:
                formula = InputHelper.readString(
                        "Enter formula (blank = default " + rankingService.getDefaultEquipmentFormula() + "): ");
                ranked = rankingService.rankEquipmentByCustomScore(formula);
                sortLabel = "Custom score";
                String effectiveFormula = formula == null || formula.isBlank()
                        ? rankingService.getDefaultEquipmentFormula()
                        : formula.trim();
                System.out.println("Sort rule: score = " + effectiveFormula);
                System.out.println("  (usage=usageCount, winRate=winRateContribution, heroCount=compatible heroes)");
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        printEquipmentRanking(ranked, sortLabel, formula);
    }

    private static void printEquipmentRanking(List<Equipment> ranked, String sortLabel, String formula) {
        System.out.println("\n--- Equipment ranking by " + sortLabel + " ---");
        if (ranked.isEmpty()) {
            System.out.println("No equipment found.");
            return;
        }
        boolean showScore = formula != null;
        int rank = 1;
        for (Equipment equipment : ranked) {
            System.out.printf("%d. %s (ID: %d)%n", rank++, equipment.getName(), equipment.getId());
            if (showScore) {
                double score = rankingService.calculateEquipmentScore(equipment, formula);
                System.out.printf("   Score: %.2f | Type: %s | Usage: %d | Win rate contrib: %.2f | Heroes: %d%n",
                        score,
                        equipment.getType(),
                        equipment.getUsageCount(),
                        equipment.getWinRateContribution(),
                        equipment.getHeroUsageCount());
            } else {
                System.out.printf("   Type: %s | Usage: %d | Win rate contrib: %.2f | Heroes: %d%n",
                        equipment.getType(),
                        equipment.getUsageCount(),
                        equipment.getWinRateContribution(),
                        equipment.getHeroUsageCount());
            }
        }
        System.out.println();
    }

    //--5 Match History--
    private static void handleMatchHistory() {
        System.out.println("\n=== Match History ===");
        System.out.println("1 Query by player");
        System.out.println("2 Query by team");
        int subChoice = InputHelper.readInt("Select query type: ");
        int n = InputHelper.readInt("How many recent games (n): ");

        switch (subChoice) {
            case 1:
                System.out.println("1 Search player by ID");
                System.out.println("2 Search player by name");
                int playerChoice = InputHelper.readInt("Select: ");
                Player player = null;
                if (playerChoice == 1) {
                    player = searchService.findPlayerById(InputHelper.readInt("Enter player ID: "));
                } else if (playerChoice == 2) {
                    player = searchService.findPlayerByName(InputHelper.readString("Enter player name: "));
                } else {
                    System.out.println("Invalid choice.");
                    return;
                }
                if (player == null) {
                    System.out.println("Player not found.");
                    return;
                }
                List<MatchRecord> playerMatches =
                        matchHistoryService.getRecentMatchesForPlayer(player.getId(), n);
                printMatchHistorySubject("Player: " + player.getName(), n, playerMatches.size());
                matchHistoryService.displayMatchHistory(playerMatches);
                break;
            case 2:
                System.out.println("1 Search team by ID");
                System.out.println("2 Search team by name");
                int teamChoice = InputHelper.readInt("Select: ");
                Team team = null;
                if (teamChoice == 1) {
                    team = searchService.findTeamById(InputHelper.readInt("Enter team ID: "));
                } else if (teamChoice == 2) {
                    team = searchService.findTeamByName(InputHelper.readString("Enter team name: "));
                } else {
                    System.out.println("Invalid choice.");
                    return;
                }
                if (team == null) {
                    System.out.println("Team not found.");
                    return;
                }
                List<MatchRecord> teamMatches =
                        matchHistoryService.getRecentMatchesForTeam(team.getId(), n);
                printMatchHistorySubject("Team: " + team.getName(), n, teamMatches.size());
                matchHistoryService.displayMatchHistory(teamMatches);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void printMatchHistorySubject(String subject, int requested, int found) {
        System.out.println();
        System.out.println("--- " + subject + " ---");
        if (found < requested) {
            System.out.println("Note: only " + found + " match(es) on record (requested " + requested + ").");
        }
    }

    //-- 6 Leaderboard--
    private static void handleLeaderboard() {
        System.out.println("\n=== Leaderboard ===");
        System.out.println("1 Top by win rate");
        System.out.println("2 Top by level");
        System.out.println("3 Top by match count");
        System.out.println("4 Top by custom score");
        System.out.println("5 Explain tie-breaking rules");
        int subChoice = InputHelper.readInt("Select: ");

        if (subChoice == 5) {
            rankingService.explainTieBreaking();
            return;
        }

        int x = InputHelper.readInt("Show top how many players (x): ");
        List<Player> top;
        String label;
        boolean showCustomScore = false;
        switch (subChoice) {
            case 1:
                top = rankingService.getTopPlayersByWinRate(x);
                label = "Win rate";
                break;
            case 2:
                top = rankingService.getTopPlayersByLevel(x);
                label = "Level";
                break;
            case 3:
                top = rankingService.getTopPlayersByMatchCount(x);
                label = "Match count";
                break;
            case 4:
                top = rankingService.getTopPlayersByCustomScore(x);
                label = "Custom score";
                showCustomScore = true;
                rankingService.explainTieBreaking();
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        printPlayerLeaderboard(top, label, showCustomScore);
    }


    private static void printPlayerLeaderboard(List<Player> players, String label, boolean showCustomScore) {
        System.out.println("\n--- Top players by " + label + " ---");
        if (players.isEmpty()) {
            System.out.println("No players found.");
            return;
        }
        int rank = 1;
        for (Player player : players) {
            if (showCustomScore) {
                System.out.printf("%d. %s (ID: %d) | Score: %.2f | Level: %d | Win rate: %.2f%% | Matches: %d%n",
                        rank++,
                        player.getName(),
                        player.getId(),
                        rankingService.calculatePlayerScore(player),
                        player.getLevel(),
                        player.getWinRate() * 100,
                        player.getMatchCount());
            } else {
                System.out.printf("%d. %s (ID: %d) | Level: %d | Win rate: %.2f%% | Matches: %d%n",
                        rank++,
                        player.getName(),
                        player.getId(),
                        player.getLevel(),
                        player.getWinRate() * 100,
                        player.getMatchCount());
            }
        }
        System.out.println();
    }

    //-- 7 Log in/out --
    private static void handleLogin() {
        System.out.println("\n=== Login / Logout ===");
        if (authService.getCurrentUser() != null) {
            System.out.println("Logged in as: " + authService.getCurrentUser().getInfo());
            System.out.println("1 Logout  0 Back");
            int choice = InputHelper.readInt("Select: ");
            if (choice == 1) {
                authService.logout();
            } else if (choice != 0) {
                System.out.println("Invalid choice.");
            }
            return;
        }

        System.out.println("1 Login by ID");
        System.out.println("2 Login by username");
        System.out.println("0 Back");
        int choice = InputHelper.readInt("Select: ");
        switch (choice) {
            case 1:
                int id = InputHelper.readInt("User ID: ");
                String passwordById = InputHelper.readString("Password: ");
                authService.login(id, passwordById);
                break;
            case 2:
                String name = InputHelper.readString("Username: ");
                String passwordByName = InputHelper.readString("Password: ");
                authService.login(name, passwordByName);
                break;
            case 0:
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    //-- 9 Save --
    private static void handleSave() {
        try {
            fileStorageService.saveAll(dataManager);
            System.out.println("Data saved to " + fileStorageService.getSavePath());
        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    /// -- 10 Load --
    private static void handleLoad() {
        try {
            dataManager = fileStorageService.loadAll();
            rebindServices();
            System.out.println("Data loaded from " + fileStorageService.getSavePath());
        } catch (IOException e) {
            System.out.println("Load failed: " + e.getMessage());
        }
    }

    private static void handleSaveOnExit() {
        try {
            fileStorageService.saveAll(dataManager);
            System.out.println("Data saved to " + fileStorageService.getSavePath());
        } catch (IOException e) {
            System.out.println("Save on exit failed: " + e.getMessage());
        }
    }

    //-- Data Management --
    private static void handleDataManagement() {
        System.out.println("\n=== Data Management ===");
        if (authService.getCurrentUser() == null) {
            System.out.println("Please log in first (menu option 8).");
            return;
        }
        if (authService.isAdmin()) {
            handleAdminDataManagement();
        } else if (authService.isPlayer()) {
            handlePlayerBasicInfoEdit();
        }
    }

    private static void handleAdminDataManagement() {
        while (true) {
            System.out.println("\n--- Admin Data Management ---");
            System.out.println("1 Add  2 Delete  3 Edit  0 Back");
            int choice = InputHelper.readInt("Select: ");
            switch (choice) {
                case 1:
                    handleAdminAdd();
                    break;
                case 2:
                    handleAdminDelete();
                    break;
                case 3:
                    handleAdminEdit();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void handlePlayerBasicInfoEdit() {
        Player player = (Player) authService.getCurrentUser();
        System.out.println("Current profile:");
        player.viewProfile();
        String newName = InputHelper.readString("New name (blank to keep): ");
        String newPassword = InputHelper.readString("New password (blank to keep): ");
        player.editBasicInfo(
                newName.isBlank() ? null : newName,
                newPassword.isBlank() ? null : newPassword);
        if (dataManager.updatePlayer(player)) {
            System.out.println("Profile updated.");
        } else {
            System.out.println("Failed to update profile.");
        }
    }

    private static int readEntityType() {
        System.out.println("1 Player  2 Hero  3 Equipment  4 Team  5 MatchRecord  0 Cancel");
        return InputHelper.readInt("Select entity: ");
    }

    private static void handleAdminAdd() {
        if (!authService.requireAdmin()) {
            return;
        }
        int type = readEntityType();
        switch (type) {
            case 1:
                addPlayer();
                break;
            case 2:
                addHero();
                break;
            case 3:
                addEquipment();
                break;
            case 4:
                addTeam();
                break;
            case 5:
                addMatchRecord();
                break;
            case 0:
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void handleAdminDelete() {
        if (!authService.requireAdmin()) {
            return;
        }
        int type = readEntityType();
        if (type == 0) {
            return;
        }
        int id = InputHelper.readInt("Enter ID to delete: ");
        boolean removed = false;
        switch (type) {
            case 1:
                removed = dataManager.removePlayer(id);
                break;
            case 2:
                removed = dataManager.removeHero(id);
                break;
            case 3:
                removed = dataManager.removeEquipment(id);
                break;
            case 4:
                removed = dataManager.removeTeam(id);
                break;
            case 5:
                removed = dataManager.removeMatchRecord(id);
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        if (removed) {
            refreshAllTeamStats();
            System.out.println("Deleted successfully.");
        } else {
            System.out.println("Delete failed: ID not found.");
        }
    }

    private static void handleAdminEdit() {
        if (!authService.requireAdmin()) {
            return;
        }
        int type = readEntityType();
        switch (type) {
            case 1:
                editPlayer();
                break;
            case 2:
                editHero();
                break;
            case 3:
                editEquipment();
                break;
            case 4:
                editTeam();
                break;
            case 5:
                editMatchRecord();
                break;
            case 0:
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void addPlayer() {
        int id = InputHelper.readInt("Player ID: ");
        if (dataManager.getPlayerById(id) != null) {
            System.out.println("Player ID already exists.");
            return;
        }
        String name = InputHelper.readString("Name: ");
        String password = InputHelper.readString("Password: ");
        int level = InputHelper.readInt("Level: ");
        double winRate = readDouble("Win rate (0-1): ");
        int teamId = InputHelper.readInt("Team ID: ");
        int matchCount = InputHelper.readInt("Match count: ");
        Player player = new Player(id, name, password, level, winRate, teamId, matchCount);
        dataManager.addPlayer(player);
        Team team = dataManager.getTeamById(teamId);
        if (team != null) {
            team.addPlayer(id);
        }
        refreshAllTeamStats();
        System.out.println("Player added.");
    }

    private static void addHero() {
        int id = InputHelper.readInt("Hero ID: ");
        if (dataManager.getHeroById(id) != null) {
            System.out.println("Hero ID already exists.");
            return;
        }
        String name = InputHelper.readString("Name: ");
        HeroType type = readHeroType();
        if (type == null) {
            System.out.println("Invalid hero type.");
            return;
        }
        Hero hero = new Hero(id, name, type);
        hero.setStat("attack", InputHelper.readInt("Attack: "));
        hero.setStat("defense", InputHelper.readInt("Defense: "));
        hero.setStat("hp", InputHelper.readInt("HP: "));
        dataManager.addHero(hero);
        System.out.println("Hero added.");
    }

    private static void addEquipment() {
        int id = InputHelper.readInt("Equipment ID: ");
        if (dataManager.getEquipmentById(id) != null) {
            System.out.println("Equipment ID already exists.");
            return;
        }
        String name = InputHelper.readString("Name: ");
        EquipmentType type = readEquipmentType();
        if (type == null) {
            System.out.println("Invalid equipment type.");
            return;
        }
        int usage = InputHelper.readInt("Usage count: ");
        double winRate = readDouble("Win rate contribution: ");
        dataManager.addEquipment(new Equipment(id, name, type, usage, winRate));
        System.out.println("Equipment added.");
    }

    private static void addTeam() {
        int id = InputHelper.readInt("Team ID: ");
        if (dataManager.getTeamById(id) != null) {
            System.out.println("Team ID already exists.");
            return;
        }
        String name = InputHelper.readString("Name: ");
        dataManager.addTeam(new Team(id, name));
        System.out.println("Team added.");
    }

    private static void addMatchRecord() {
        int id = InputHelper.readInt("Match ID: ");
        if (dataManager.getMatchRecordById(id) != null) {
            System.out.println("Match ID already exists.");
            return;
        }
        LocalDate date = readDate("Date (yyyy-MM-dd): ");
        if (date == null) {
            return;
        }
        int playerId = InputHelper.readInt("Player ID: ");
        int teamId = InputHelper.readInt("Team ID: ");
        String opponent = InputHelper.readString("Opponent: ");
        MatchResult result = readMatchResult();
        if (result == null) {
            System.out.println("Invalid result.");
            return;
        }
        List<Integer> heroes = CsvUtil.parseIntList(
                InputHelper.readString("Hero IDs (semicolon-separated, e.g. 1;2): "), ";");
        dataManager.addMatchRecord(new MatchRecord(id, date, playerId, teamId, opponent, result, heroes));
        refreshAllTeamStats();
        System.out.println("Match record added.");
    }

    private static void editPlayer() {
        int id = InputHelper.readInt("Player ID: ");
        Player player = dataManager.getPlayerById(id);
        if (player == null) {
            System.out.println("Player not found.");
            return;
        }
        System.out.println("Leave blank to keep current value.");
        String name = InputHelper.readString("Name [" + player.getName() + "]: ");
        if (!name.isBlank()) {
            player.setName(name);
        }
        String password = InputHelper.readString("Password [hidden]: ");
        if (!password.isBlank()) {
            player.setPassword(password);
        }
        String levelStr = InputHelper.readString("Level [" + player.getLevel() + "]: ");
        if (!levelStr.isBlank()) {
            player.setLevel(Integer.parseInt(levelStr));
        }
        String winRateStr = InputHelper.readString("Win rate [" + player.getWinRate() + "]: ");
        if (!winRateStr.isBlank()) {
            player.setWinRate(Double.parseDouble(winRateStr));
        }
        String teamStr = InputHelper.readString("Team ID [" + player.getTeamId() + "]: ");
        if (!teamStr.isBlank()) {
            player.setTeamId(Integer.parseInt(teamStr));
        }
        String matchStr = InputHelper.readString("Match count [" + player.getMatchCount() + "]: ");
        if (!matchStr.isBlank()) {
            player.setMatchCount(Integer.parseInt(matchStr));
        }
        dataManager.updatePlayer(player);
        refreshAllTeamStats();
        System.out.println("Player updated.");
    }

    private static void editHero() {
        int id = InputHelper.readInt("Hero ID: ");
        Hero hero = dataManager.getHeroById(id);
        if (hero == null) {
            System.out.println("Hero not found.");
            return;
        }
        String name = InputHelper.readString("Name [" + hero.getName() + "]: ");
        if (!name.isBlank()) {
            hero.setName(name);
        }
        String typeStr = InputHelper.readString("Type [" + hero.getType() + "]: ");
        if (!typeStr.isBlank()) {
            HeroType type = HeroType.fromString(typeStr);
            if (type == null) {
                System.out.println("Invalid type, keeping current.");
            } else {
                hero.setType(type);
            }
        }
        dataManager.updateHero(hero);
        System.out.println("Hero updated.");
    }

    private static void editEquipment() {
        int id = InputHelper.readInt("Equipment ID: ");
        Equipment equipment = dataManager.getEquipmentById(id);
        if (equipment == null) {
            System.out.println("Equipment not found.");
            return;
        }
        String name = InputHelper.readString("Name [" + equipment.getName() + "]: ");
        if (!name.isBlank()) {
            equipment.setName(name);
        }
        String usageStr = InputHelper.readString("Usage [" + equipment.getUsageCount() + "]: ");
        if (!usageStr.isBlank()) {
            equipment.setUsageCount(Integer.parseInt(usageStr));
        }
        dataManager.updateEquipment(equipment);
        System.out.println("Equipment updated.");
    }

    private static void editTeam() {
        int id = InputHelper.readInt("Team ID: ");
        Team team = dataManager.getTeamById(id);
        if (team == null) {
            System.out.println("Team not found.");
            return;
        }
        String name = InputHelper.readString("Name [" + team.getName() + "]: ");
        if (!name.isBlank()) {
            team.setName(name);
        }
        dataManager.updateTeam(team);
        System.out.println("Team updated.");
    }

    private static void editMatchRecord() {
        int id = InputHelper.readInt("Match ID: ");
        MatchRecord record = dataManager.getMatchRecordById(id);
        if (record == null) {
            System.out.println("Match record not found.");
            return;
        }
        String opponent = InputHelper.readString("Opponent [" + record.getOpponent() + "]: ");
        if (!opponent.isBlank()) {
            record.setOpponent(opponent);
        }
        String resultStr = InputHelper.readString("Result (WIN/LOSE) [" + record.getResult() + "]: ");
        if (!resultStr.isBlank()) {
            MatchResult result = MatchResult.fromString(resultStr);
            if (result != null) {
                record.setResult(result);
            }
        }
        dataManager.updateMatchRecord(record);
        refreshAllTeamStats();
        System.out.println("Match record updated.");
    }

    private static void refreshAllTeamStats() {
        for (Team team : dataManager.getAllTeams()) {
            team.recalculateStats(dataManager.getAllPlayers(), dataManager.getAllMatchRecords());
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            String line = InputHelper.readString(prompt);
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    private static LocalDate readDate(String prompt) {
        while (true) {
            String line = InputHelper.readString(prompt);
            try {
                return LocalDate.parse(line);
            } catch (Exception e) {
                System.out.println("Please enter date as yyyy-MM-dd.");
            }
        }
    }

    private static HeroType readHeroType() {
        System.out.println("Types: MAGE, SHOOTER, TANK, JUNGLER, SUPPORT");
        return HeroType.fromString(InputHelper.readString("Hero type: "));
    }

    private static EquipmentType readEquipmentType() {
        System.out.println("Types: ATTACK, DEFENSE, SPELL");
        return EquipmentType.fromString(InputHelper.readString("Equipment type: "));
    }

    private static MatchResult readMatchResult() {
        System.out.println("Results: WIN, LOSE");
        return MatchResult.fromString(InputHelper.readString("Result: "));
    }
}
