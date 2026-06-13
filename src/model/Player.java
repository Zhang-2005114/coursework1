package model;

import interfaces.Persistable;
import interfaces.Searchable;
import util.CsvUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player extends Person implements Searchable, Persistable {
    private int level;
    private double winRate;
    private int teamId;
    private List<Integer> ownedHeroes;
    private int matchCount;
    private Map<Integer, List<Integer>> equippedItemsByHeroId;

    public Player() {
        super();
        this.ownedHeroes = new ArrayList<>();
        this.equippedItemsByHeroId = new HashMap<>();
        setRole("Player");
    }

    public Player(int id, String name, String password, int level, double winRate, int teamId,
                  int matchCount) {
        super(id, name, password, "Player");
        this.level = level;
        this.winRate = winRate;
        this.teamId = teamId;
        this.matchCount = matchCount;
        this.ownedHeroes = new ArrayList<>();
        this.equippedItemsByHeroId = new HashMap<>();
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public List<Integer> getOwnedHeroes() {
        return ownedHeroes;
    }

    public void setOwnedHeroes(List<Integer> ownedHeroes) {
        this.ownedHeroes = ownedHeroes != null ? ownedHeroes : new ArrayList<>();
    }

    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }

    public Map<Integer, List<Integer>> getEquippedItemsByHeroId() {
        return equippedItemsByHeroId;
    }

    public void setEquippedItemsByHeroId(Map<Integer, List<Integer>> equippedItemsByHeroId) {
        this.equippedItemsByHeroId =
                equippedItemsByHeroId != null ? equippedItemsByHeroId : new HashMap<>();
    }

    public void viewProfile() {
        System.out.println(getInfo());
        System.out.println("Level: " + level);
        System.out.println("Win rate: " + winRate);
        System.out.println("Team ID: " + teamId);
        System.out.println("Match count: " + matchCount);
        System.out.println("Owned hero IDs: " + ownedHeroes);
        System.out.println("Equipped items by hero: " + equippedItemsByHeroId);
    }

    public void editBasicInfo() {
        System.out.println("Editable fields: name, password. Use editBasicInfo(newName, newPassword).");
    }

    public void editBasicInfo(String newName, String newPassword) {
        if (newName != null && !newName.isBlank()) {
            setName(newName);
        }
        if (newPassword != null && !newPassword.isBlank()) {
            setPassword(newPassword);
        }
    }

    public List<Integer> getEquippedItemsForHero(Hero hero) {
        if (hero == null) {
            return Collections.emptyList();
        }
        List<Integer> items = equippedItemsByHeroId.get(hero.getId());
        if (items == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(items);
    }

    public void setEquippedItemsForHero(int heroId, List<Integer> equipmentIds) {
        if (equipmentIds == null) {
            equippedItemsByHeroId.remove(heroId);
        } else {
            equippedItemsByHeroId.put(heroId, new ArrayList<>(equipmentIds));
        }
    }

    public void addOwnedHero(int heroId) {
        if (!ownedHeroes.contains(heroId)) {
            ownedHeroes.add(heroId);
        }
    }

    @Override
    public boolean matches(String keyword) {
        return CsvUtil.matchesIdOrName(getId(), getName(), keyword);
    }

    @Override
    public String toCsvLine() {
        return String.join("|",
                String.valueOf(getId()),
                getName(),
                getPassword(),
                getRole(),
                String.valueOf(level),
                String.valueOf(winRate),
                String.valueOf(teamId),
                String.valueOf(matchCount),
                CsvUtil.joinInts(ownedHeroes, ";"),
                CsvUtil.encodeEquipped(equippedItemsByHeroId));
    }

    public static Player fromCsvLine(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] parts = line.split("\\|", -1);
        if (parts.length < 8) {
            return null;
        }
        Player player = new Player(
                Integer.parseInt(parts[0].trim()),
                parts[1].trim(),
                parts[2].trim(),
                Integer.parseInt(parts[4].trim()),
                Double.parseDouble(parts[5].trim()),
                Integer.parseInt(parts[6].trim()),
                Integer.parseInt(parts[7].trim()));
        player.setRole(parts[3].trim());
        if (parts.length > 8 && !parts[8].isBlank()) {
            player.setOwnedHeroes(CsvUtil.parseIntList(parts[8], ";"));
        }
        if (parts.length > 9 && !parts[9].isBlank()) {
            player.setEquippedItemsByHeroId(CsvUtil.decodeEquipped(parts[9]));
        }
        return player;
    }
}
