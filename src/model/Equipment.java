import java.util.ArrayList;
import java.util.List;

public class Equipment implements Searchable, Persistable {
    private int id;
    private String name;
    private EquipmentType type;
    private int usageCount;
    private double winRateContribution;
    private List<Integer> compatibleHeroes;

    public Equipment() {
        this.compatibleHeroes = new ArrayList<>();
    }

    public Equipment(int id, String name, EquipmentType type, int usageCount,
                     double winRateContribution) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.usageCount = usageCount;
        this.winRateContribution = winRateContribution;
        this.compatibleHeroes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EquipmentType getType() {
        return type;
    }

    public void setType(EquipmentType type) {
        this.type = type;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public double getWinRateContribution() {
        return winRateContribution;
    }

    public void setWinRateContribution(double winRateContribution) {
        this.winRateContribution = winRateContribution;
    }

    public List<Integer> getCompatibleHeroes() {
        return compatibleHeroes;
    }

    public void setCompatibleHeroes(List<Integer> compatibleHeroes) {
        this.compatibleHeroes =
                compatibleHeroes != null ? new ArrayList<>(compatibleHeroes) : new ArrayList<>();
    }

    public void addCompatibleHero(int heroId) {
        if (!compatibleHeroes.contains(heroId)) {
            compatibleHeroes.add(heroId);
        }
    }

    public int getHeroUsageCount() {
        return compatibleHeroes.size();
    }

    @Override
    public boolean matches(String keyword) {
        return CsvUtil.matchesIdOrName(id, name, keyword);
    }

    @Override
    public String toCsvLine() {
        return String.join("|",
                String.valueOf(id),
                name != null ? name : "",
                type != null ? type.name() : "",
                String.valueOf(usageCount),
                String.valueOf(winRateContribution),
                CsvUtil.joinInts(compatibleHeroes, ";"));
    }

    public static Equipment fromCsvLine(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] parts = line.split("\\|", -1);
        if (parts.length < 5) {
            return null;
        }
        Equipment equipment = new Equipment(
                Integer.parseInt(parts[0].trim()),
                parts[1].trim(),
                EquipmentType.fromString(parts[2].trim()),
                Integer.parseInt(parts[3].trim()),
                Double.parseDouble(parts[4].trim()));
        if (parts.length > 5 && !parts[5].isBlank()) {
            equipment.setCompatibleHeroes(CsvUtil.parseIntList(parts[5], ";"));
        }
        return equipment;
    }

    @Override
    public String toString() {
        return "Equipment{id=" + id + ", name='" + name + "', type=" + type
                + ", usageCount=" + usageCount + ", winRateContribution=" + winRateContribution
                + ", compatibleHeroes=" + compatibleHeroes + "}";
    }
}
