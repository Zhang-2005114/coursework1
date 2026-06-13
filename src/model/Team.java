package model;

import interfaces.Persistable;
import interfaces.Searchable;
import util.CsvUtil;

import java.util.ArrayList;
import java.util.List;

public class Team implements Searchable, Persistable {
    private int id;
    private String name;
    private List<Integer> playerList;
    private double avgLevel;
    private int totalMatches;
    private double winRate;
    private int topPlayer;

    public Team() {
        this.playerList = new ArrayList<>();
        this.topPlayer = -1;
    }

    public Team(int id, String name) {
        this.id = id;
        this.name = name;
        this.playerList = new ArrayList<>();
        this.topPlayer = -1;
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

    public List<Integer> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<Integer> playerList) {
        this.playerList = playerList != null ? new ArrayList<>(playerList) : new ArrayList<>();
    }

    public void addPlayer(int playerId) {
        if (!playerList.contains(playerId)) {
            playerList.add(playerId);
        }
    }

    public void removePlayer(int playerId) {
        playerList.remove(Integer.valueOf(playerId));
    }

    public double getAvgLevel() {
        return avgLevel;
    }

    public void setAvgLevel(double avgLevel) {
        this.avgLevel = avgLevel;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(int totalMatches) {
        this.totalMatches = totalMatches;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public int getTopPlayer() {
        return topPlayer;
    }

    public void setTopPlayer(int topPlayer) {
        this.topPlayer = topPlayer;
    }

    public void recalculateStats(List<Player> allPlayers, List<MatchRecord> allMatches) {
        if (allPlayers == null) {
            allPlayers = List.of();
        }
        if (allMatches == null) {
            allMatches = List.of();
        }

        List<Player> members = new ArrayList<>();
        for (Player player : allPlayers) {
            if (playerList.contains(player.getId())) {
                members.add(player);
            }
        }

        if (members.isEmpty()) {
            avgLevel = 0;
            topPlayer = -1;
        } else {
            double levelSum = 0;
            Player strongest = members.get(0);
            for (Player member : members) {
                levelSum += member.getLevel();
                if (member.getLevel() > strongest.getLevel()) {
                    strongest = member;
                }
            }
            avgLevel = levelSum / members.size();
            topPlayer = strongest.getId();
        }

        int wins = 0;
        int matches = 0;
        for (MatchRecord record : allMatches) {
            if (record.getTeamId() == id) {
                matches++;
                if (record.isWin()) {
                    wins++;
                }
            }
        }
        totalMatches = matches;
        winRate = matches == 0 ? 0 : (double) wins / matches;
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
                CsvUtil.joinInts(playerList, ";"),
                String.valueOf(avgLevel),
                String.valueOf(totalMatches),
                String.valueOf(winRate),
                String.valueOf(topPlayer));
    }

    public static Team fromCsvLine(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] parts = line.split("\\|", -1);
        if (parts.length < 2) {
            return null;
        }
        Team team = new Team(Integer.parseInt(parts[0].trim()), parts[1].trim());
        if (parts.length > 2 && !parts[2].isBlank()) {
            team.setPlayerList(CsvUtil.parseIntList(parts[2], ";"));
        }
        if (parts.length > 3 && !parts[3].isBlank()) {
            team.setAvgLevel(Double.parseDouble(parts[3].trim()));
        }
        if (parts.length > 4 && !parts[4].isBlank()) {
            team.setTotalMatches(Integer.parseInt(parts[4].trim()));
        }
        if (parts.length > 5 && !parts[5].isBlank()) {
            team.setWinRate(Double.parseDouble(parts[5].trim()));
        }
        if (parts.length > 6 && !parts[6].isBlank()) {
            team.setTopPlayer(Integer.parseInt(parts[6].trim()));
        }
        return team;
    }

    @Override
    public String toString() {
        return "Team{id=" + id + ", name='" + name + "', playerList=" + playerList
                + ", avgLevel=" + avgLevel + ", totalMatches=" + totalMatches
                + ", winRate=" + winRate + ", topPlayer=" + topPlayer + "}";
    }
}
