import java.util.List;

public class Main {
    private static SearchService searchService;
    private static RankingService rankingService;
    private static MatchHistoryService matchHistoryService;

    public static void main(String[] args) {
        GameDataManager data = DataInitializer.createDefault();
        searchService = new SearchService(data);
        rankingService = new RankingService(data);
        matchHistoryService = new MatchHistoryService(data);
        run(data);
    }

    private static void run(GameDataManager data) {
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
                case 0:
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
        System.out.println("6 Leaderboard  7 Data Management  8 Login  9 Save  10 Load  0 Exit");
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
}
