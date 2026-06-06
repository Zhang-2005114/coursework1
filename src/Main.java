public class Main {
    public static void main(String[] args) {
        GameDataManager data = DataInitializer.createDefault();
        run(data);
    }

    private static void run(GameDataManager data) {
        System.out.println("=== Honor of Kings IMS ===");
        while (true) {
            printMenu();
            int choice = InputHelper.readInt("Select: ");
            if (choice == 0) {
                System.out.println("Goodbye.");
                break;
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1 Player Lookup  2 Team  3 Hero  4 Equipment  5 Match History");
        System.out.println("6 Leaderboard  7 Data Mgmt  8 Login  9 Save  10 Load  0 Exit");
    }
}

