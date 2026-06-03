import java.util.ArrayList;
import java.util.List;

public class Team {
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
    public String toString() {
        return "Team{id=" + id + ", name='" + name + "', playerList=" + playerList
                + ", avgLevel=" + avgLevel + ", totalMatches=" + totalMatches
                + ", winRate=" + winRate + ", topPlayer=" + topPlayer + "}";
    }
}
