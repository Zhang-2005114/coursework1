import java.util.List;

public class Main {
    private static SearchService searchService;
    private static RankingService rankingService;

    public static void main(String[] args) {
        GameDataManager data = DataInitializer.createDefault();
        searchService = new SearchService(data);
        rankingService = new RankingService(data);
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

    private static void handleHeroDetails() {
        System.out.println("\n=== Hero Details ===");
        String name = InputHelper.readString("Enter hero name: ");
        Hero hero = searchService.findHeroByName(name);
        searchService.displayHeroDetails(hero);
    }

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
}
