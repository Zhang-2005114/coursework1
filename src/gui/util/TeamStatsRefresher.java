package gui.util;

import gui.AppContext;
import model.Team;
import service.GameDataManager;

public final class TeamStatsRefresher {
    private TeamStatsRefresher() {
    }

    public static void refreshAll(AppContext context) {
        GameDataManager data = context.getDataManager();
        for (Team team : data.getAllTeams()) {
            team.recalculateStats(data.getAllPlayers(), data.getAllMatchRecords());
        }
    }
}
