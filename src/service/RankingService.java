import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

public class RankingService {
    private static final String DEFAULT_EQUIPMENT_FORMULA =
            "usage*0.5+winRate*100+heroCount*2";
    private static final String DEFAULT_PLAYER_FORMULA =
            "level*0.4+winRate*100+matchCount*0.1";

    private final GameDataManager dataManager;

    public RankingService(GameDataManager dataManager) {
        this.dataManager = dataManager;
    }

    public List<Equipment> rankEquipmentByUsage() {
        return rankEquipmentByKey(Equipment::getUsageCount);
    }

    public List<Equipment> rankEquipmentByWinRateContribution() {
        return rankEquipmentByDoubleKey(Equipment::getWinRateContribution);
    }

    public List<Equipment> rankEquipmentByHeroCount() {
        return rankEquipmentByKey(Equipment::getHeroUsageCount);
    }

    public List<Equipment> rankEquipmentByCustomScore(String formula) {
        String effectiveFormula =
                formula == null || formula.isBlank() ? DEFAULT_EQUIPMENT_FORMULA : formula.trim();
        TreeMap<Double, List<Equipment>> grouped = new TreeMap<>(Collections.reverseOrder());
        for (Equipment equipment : dataManager.getAllEquipments()) {
            double score = evaluateEquipmentScore(equipment, effectiveFormula);
            grouped.computeIfAbsent(score, key -> new ArrayList<>()).add(equipment);
        }
        return flattenEquipmentGroups(grouped);
    }

    public List<Player> getTopPlayersByWinRate(int x) {
        return getTopPlayers(x, Comparator
                .comparingDouble(Player::getWinRate).reversed()
                .thenComparing(Comparator.comparingInt(Player::getLevel).reversed())
                .thenComparingInt(Player::getId));
    }

    public List<Player> getTopPlayersByLevel(int x) {
        return getTopPlayers(x, Comparator
                .comparingInt(Player::getLevel).reversed()
                .thenComparing(Comparator.comparingDouble(Player::getWinRate).reversed())
                .thenComparingInt(Player::getId));
    }

    public List<Player> getTopPlayersByMatchCount(int x) {
        return getTopPlayers(x, Comparator
                .comparingInt(Player::getMatchCount).reversed()
                .thenComparing(Comparator.comparingDouble(Player::getWinRate).reversed())
                .thenComparingInt(Player::getId));
    }

    public List<Player> getTopPlayersByCustomScore(int x) {
        TreeMap<Double, List<Player>> grouped = new TreeMap<>(Collections.reverseOrder());
        for (Player player : dataManager.getAllPlayers()) {
            double score = evaluatePlayerScore(player, DEFAULT_PLAYER_FORMULA);
            grouped.computeIfAbsent(score, key -> new ArrayList<>()).add(player);
        }
        List<Player> ranked = flattenPlayerGroups(grouped);
        return ranked.subList(0, Math.min(x, ranked.size()));
    }

    public String getDefaultEquipmentFormula() {
        return DEFAULT_EQUIPMENT_FORMULA;
    }

    public String getDefaultPlayerFormula() {
        return DEFAULT_PLAYER_FORMULA;
    }

    public double calculatePlayerScore(Player player) {
        return evaluatePlayerScore(player, DEFAULT_PLAYER_FORMULA);
    }

    public double calculateEquipmentScore(Equipment equipment, String formula) {
        String effectiveFormula =
                formula == null || formula.isBlank() ? DEFAULT_EQUIPMENT_FORMULA : formula.trim();
        return evaluateEquipmentScore(equipment, effectiveFormula);
    }

    public void explainEquipmentSorting() {
        System.out.println("=== Equipment Sorting Formulas ===");
        System.out.println("1. Usage count: rank by usageCount (descending).");
        System.out.println("2. Win rate contribution: rank by winRateContribution (descending).");
        System.out.println("3. Hero count: rank by compatible hero count (descending).");
        System.out.println("4. Custom score: " + DEFAULT_EQUIPMENT_FORMULA);
        System.out.println("   usage = usageCount, winRate = winRateContribution, heroCount = compatible heroes.");
        System.out.println("Tie-break (all sorts): lower equipment ID ranks higher.");
        System.out.println();
    }

    public void explainTieBreaking() {
        System.out.println("=== Tie-Breaking Rules ===");
        System.out.println("Equipment: same score -> lower equipment ID ranks higher.");
        System.out.println("Players (win rate / match count): higher level, then higher win rate,");
        System.out.println("  then lower player ID.");
        System.out.println("Players (level): higher win rate, then lower player ID.");
        System.out.println("Players (custom score): higher level, then higher win rate,");
        System.out.println("  then lower player ID.");
        System.out.println();
        System.out.println("Default equipment formula: " + DEFAULT_EQUIPMENT_FORMULA);
        System.out.println("  (usage = usageCount, winRate = winRateContribution, heroCount = compatible heroes)");
        System.out.println("Default player formula: " + DEFAULT_PLAYER_FORMULA);
        System.out.println("  (level, winRate, matchCount)");
    }

    private List<Equipment> rankEquipmentByKey(ToIntFunction<Equipment> keyFn) {
        TreeMap<Integer, List<Equipment>> grouped = new TreeMap<>(Collections.reverseOrder());
        for (Equipment equipment : dataManager.getAllEquipments()) {
            grouped.computeIfAbsent(keyFn.applyAsInt(equipment), key -> new ArrayList<>()).add(equipment);
        }
        return flattenEquipmentGroups(grouped);
    }

    private List<Equipment> rankEquipmentByDoubleKey(ToDoubleFunction<Equipment> keyFn) {
        TreeMap<Double, List<Equipment>> grouped = new TreeMap<>(Collections.reverseOrder());
        for (Equipment equipment : dataManager.getAllEquipments()) {
            grouped.computeIfAbsent(keyFn.applyAsDouble(equipment), key -> new ArrayList<>())
                    .add(equipment);
        }
        return flattenEquipmentGroups(grouped);
    }

    private List<Equipment> flattenEquipmentGroups(TreeMap<?, List<Equipment>> grouped) {
        List<Equipment> result = new ArrayList<>();
        for (List<Equipment> group : grouped.values()) {
            group.sort(Comparator.comparingInt(Equipment::getId));
            result.addAll(group);
        }
        return result;
    }

    private List<Player> flattenPlayerGroups(TreeMap<Double, List<Player>> grouped) {
        List<Player> result = new ArrayList<>();
        for (List<Player> group : grouped.values()) {
            group.sort(Comparator
                    .comparingInt(Player::getLevel).reversed()
                    .thenComparing(Comparator.comparingDouble(Player::getWinRate).reversed())
                    .thenComparingInt(Player::getId));
            result.addAll(group);
        }
        return result;
    }

    private List<Player> getTopPlayers(int x, Comparator<Player> comparator) {
        List<Player> players = new ArrayList<>(dataManager.getAllPlayers());
        players.sort(comparator);
        return players.subList(0, Math.min(x, players.size()));
    }

    private double evaluateEquipmentScore(Equipment equipment, String formula) {
        String expression = formula
                .replaceAll("(?i)usage", String.valueOf(equipment.getUsageCount()))
                .replaceAll("(?i)winRate", String.valueOf(equipment.getWinRateContribution()))
                .replaceAll("(?i)heroCount", String.valueOf(equipment.getHeroUsageCount()));
        return evaluateSimpleExpression(expression);
    }

    private double evaluatePlayerScore(Player player, String formula) {
        String expression = formula
                .replaceAll("(?i)level", String.valueOf(player.getLevel()))
                .replaceAll("(?i)winRate", String.valueOf(player.getWinRate()))
                .replaceAll("(?i)matchCount", String.valueOf(player.getMatchCount()));
        return evaluateSimpleExpression(expression);
    }

    private double evaluateSimpleExpression(String expression) {
        String normalized = expression.replaceAll("\\s+", "");
        if (normalized.isEmpty()) {
            return 0;
        }
        double sum = 0;
        int index = 0;
        while (index < normalized.length()) {
            int nextPlus = normalized.indexOf('+', index);
            if (nextPlus < 0) {
                nextPlus = normalized.length();
            }
            String term = normalized.substring(index, nextPlus);
            sum += evaluateTerm(term);
            index = nextPlus + 1;
        }
        return sum;
    }

    private double evaluateTerm(String term) {
        if (term.isEmpty()) {
            return 0;
        }
        double product = 1;
        int index = 0;
        while (index < term.length()) {
            int nextMul = term.indexOf('*', index);
            if (nextMul < 0) {
                nextMul = term.length();
            }
            product *= Double.parseDouble(term.substring(index, nextMul));
            index = nextMul + 1;
        }
        return product;
    }
}
