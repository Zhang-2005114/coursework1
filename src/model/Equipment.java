import java.util.ArrayList;
import java.util.List;

public class Equipment {
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
    public String toString() {
        return "Equipment{id=" + id + ", name='" + name + "', type=" + type
                + ", usageCount=" + usageCount + ", winRateContribution=" + winRateContribution
                + ", compatibleHeroes=" + compatibleHeroes + "}";
    }
}
