package model;

import enums.HeroType;
import interfaces.Persistable;
import interfaces.Searchable;
import util.CsvUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hero implements Searchable, Persistable {
    private int id;
    private String name;
    private HeroType type;
    private Map<String, Integer> baseStats;
    private List<Integer> compatibleEquipments;
    private List<Integer> ownerPlayers;

    public Hero() {
        this.baseStats = new HashMap<>();
        this.compatibleEquipments = new ArrayList<>();
        this.ownerPlayers = new ArrayList<>();
    }

    public Hero(int id, String name, HeroType type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.baseStats = new HashMap<>();
        this.compatibleEquipments = new ArrayList<>();
        this.ownerPlayers = new ArrayList<>();
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

    public HeroType getType() {
        return type;
    }

    public void setType(HeroType type) {
        this.type = type;
    }

    public Map<String, Integer> getBaseStats() {
        return baseStats;
    }

    public void setBaseStats(Map<String, Integer> baseStats) {
        this.baseStats = baseStats != null ? new HashMap<>(baseStats) : new HashMap<>();
    }

    public void setStat(String key, int value) {
        baseStats.put(key, value);
    }

    public Integer getStat(String key) {
        return baseStats.get(key);
    }

    public List<Integer> getCompatibleEquipments() {
        return compatibleEquipments;
    }

    public void setCompatibleEquipments(List<Integer> compatibleEquipments) {
        this.compatibleEquipments =
                compatibleEquipments != null ? new ArrayList<>(compatibleEquipments) : new ArrayList<>();
    }

    public void addCompatibleEquipment(int equipmentId) {
        if (!compatibleEquipments.contains(equipmentId)) {
            compatibleEquipments.add(equipmentId);
        }
    }

    public List<Integer> getOwnerPlayers() {
        return ownerPlayers;
    }

    public void setOwnerPlayers(List<Integer> ownerPlayers) {
        this.ownerPlayers = ownerPlayers != null ? new ArrayList<>(ownerPlayers) : new ArrayList<>();
    }

    public void addOwnerPlayer(int playerId) {
        if (!ownerPlayers.contains(playerId)) {
            ownerPlayers.add(playerId);
        }
    }

    public List<Integer> getRecommendedEquipment() {
        return new ArrayList<>(compatibleEquipments);
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
                CsvUtil.encodeStats(baseStats),
                CsvUtil.joinInts(compatibleEquipments, ";"),
                CsvUtil.joinInts(ownerPlayers, ";"));
    }

    public static Hero fromCsvLine(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] parts = line.split("\\|", -1);
        if (parts.length < 3) {
            return null;
        }
        Hero hero = new Hero(
                Integer.parseInt(parts[0].trim()),
                parts[1].trim(),
                HeroType.fromString(parts[2].trim()));
        if (parts.length > 3 && !parts[3].isBlank()) {
            hero.setBaseStats(CsvUtil.decodeStats(parts[3]));
        }
        if (parts.length > 4 && !parts[4].isBlank()) {
            hero.setCompatibleEquipments(CsvUtil.parseIntList(parts[4], ";"));
        }
        if (parts.length > 5 && !parts[5].isBlank()) {
            hero.setOwnerPlayers(CsvUtil.parseIntList(parts[5], ";"));
        }
        return hero;
    }

    @Override
    public String toString() {
        return "Hero{id=" + id + ", name='" + name + "', type=" + type
                + ", baseStats=" + baseStats + ", compatibleEquipments=" + compatibleEquipments
                + ", ownerPlayers=" + ownerPlayers + "}";
    }
}
