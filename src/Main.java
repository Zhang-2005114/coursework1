public class Main {
    private static SearchService searchService;

    public static void main(String[] args) {
        GameDataManager data = DataInitializer.createDefault();
        searchService = new SearchService(data);
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
        System.out.println("1 Player Lookup  2 Team  3 Hero  4 Equipment  5 Match History");
        System.out.println("6 Leaderboard  7 Data Mgmt  8 Login  9 Save  10 Load  0 Exit");
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
}