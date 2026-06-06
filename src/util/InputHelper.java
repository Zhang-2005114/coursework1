import java.util.Scanner;

public final class InputHelper {
    private static final Scanner SCANNER = new Scanner(System.in);

    private InputHelper() {
    }

    public static String readString(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            String line = readString(prompt);
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter an integer.");
            }
        }
    }
}
