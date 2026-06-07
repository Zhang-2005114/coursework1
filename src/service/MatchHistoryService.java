import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class MatchHistoryService {
    private final GameDataManager dataManager;

    public MatchHistoryService(GameDataManager dataManager) {
        this.dataManager = dataManager;
    }

    public List<MatchRecord> getRecentMatchesForPlayer(int playerId, int n) {
        return getRecentMatches(record -> record.getPlayerId() == playerId, n);
    }

    public List<MatchRecord> getRecentMatchesForTeam(int teamId, int n) {
        return getRecentMatches(record -> record.getTeamId() == teamId, n);
    }

    public void displayMatchHistory(List<MatchRecord> matches) {
        if (matches == null || matches.isEmpty()) {
            System.out.println("No match records found.");
            System.out.println();
            return;
        }

        System.out.println("Recent " + matches.size() + " game(s):");
        for (MatchRecord record : matches) {
            System.out.println("  Date: " + record.getDate());
            System.out.println("  Opponent: " + record.getOpponent());
            System.out.println("  Result: " + record.getResultDisplay());
            System.out.print("  Heroes picked: ");
            printHeroNames(record.getPickedHeroes());
            System.out.println();
        }

        WinLossRecord winLoss = calculateWinLossRecord(matches);
        System.out.printf("Win rate (this sample): %.2f%% (%dW / %dL / %d total)%n",
                winLoss.getWinRate(), winLoss.getWins(), winLoss.getLosses(), winLoss.getTotal());

        Map<Integer, Double> pickRates = calculateHeroPickRate(matches);
        if (pickRates.isEmpty()) {
            System.out.println("Hero pick rate: N/A");
        } else {
            System.out.println("Hero pick rate:");
            for (Map.Entry<Integer, Double> entry : pickRates.entrySet()) {
                Hero hero = dataManager.getHeroById(entry.getKey());
                String name = hero != null ? hero.getName() : "Hero#" + entry.getKey();
                System.out.printf("  - %s: %.2f%%%n", name, entry.getValue());
            }
        }
        System.out.println();
    }

    public WinLossRecord calculateWinLossRecord(List<MatchRecord> matches) {
        if (matches == null || matches.isEmpty()) {
            return new WinLossRecord(0, 0);
        }
        int wins = 0;
        for (MatchRecord record : matches) {
            if (record.isWin()) {
                wins++;
            }
        }
        return new WinLossRecord(wins, matches.size() - wins);
    }

    public Map<Integer, Double> calculateHeroPickRate(List<MatchRecord> matches) {
        Map<Integer, Integer> pickCounts = new HashMap<>();
        int totalPicks = 0;
        if (matches != null) {
            for (MatchRecord record : matches) {
                for (Integer heroId : record.getPickedHeroes()) {
                    pickCounts.merge(heroId, 1, Integer::sum);
                    totalPicks++;
                }
            }
        }
        if (totalPicks == 0) {
            return Map.of();
        }

        List<Map.Entry<Integer, Integer>> sorted = new ArrayList<>(pickCounts.entrySet());
        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        Map<Integer, Double> rates = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : sorted) {
            double rate = (double) entry.getValue() / totalPicks * 100;
            rates.put(entry.getKey(), rate);
        }
        return rates;
    }

    private List<MatchRecord> getRecentMatches(Predicate<MatchRecord> filter, int n) {
        List<MatchRecord> filtered = new ArrayList<>();
        for (MatchRecord record : dataManager.getAllMatchRecords()) {
            if (filter.test(record)) {
                filtered.add(record);
            }
        }
        filtered.sort(Comparator
                .comparing(MatchRecord::getDate, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(Comparator.comparingInt(MatchRecord::getId).reversed()));
        int limit = Math.max(0, n);
        return filtered.subList(0, Math.min(limit, filtered.size()));
    }

    private void printHeroNames(List<Integer> heroIds) {
        if (heroIds == null || heroIds.isEmpty()) {
            System.out.println("None");
            return;
        }
        List<String> names = new ArrayList<>();
        for (Integer heroId : heroIds) {
            Hero hero = dataManager.getHeroById(heroId);
            names.add(hero != null ? hero.getName() : "Hero#" + heroId);
        }
        System.out.println(String.join(", ", names));
    }

    public static class WinLossRecord {
        private final int wins;
        private final int losses;

        public WinLossRecord(int wins, int losses) {
            this.wins = wins;
            this.losses = losses;
        }

        public int getWins() {
            return wins;
        }

        public int getLosses() {
            return losses;
        }

        public int getTotal() {
            return wins + losses;
        }

        public double getWinRate() {
            if (getTotal() == 0) {
                return 0;
            }
            return (double) wins / getTotal() * 100;
        }
    }
}
