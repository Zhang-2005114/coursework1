package gui;

import service.*;
import util.DataInitializer;

import java.io.IOException;

public class AppContext {
    private GameDataManager dataManager;
    private AuthenticationService authService;
    private SearchService searchService;
    private RankingService rankingService;
    private MatchHistoryService matchHistoryService;
    private final FileStorageService fileStorageService = new FileStorageService();

    public AppContext(GameDataManager dataManager) {
        this.dataManager = dataManager;
        rebindServices();
    }

    public static AppContext createFromStartup() {
        FileStorageService storage = new FileStorageService();
        GameDataManager manager;
        if (storage.saveFileExists()) {
            try {
                manager = storage.loadAll();
            } catch (IOException e) {
                manager = DataInitializer.createDefault();
            }
        } else {
            manager = DataInitializer.createDefault();
        }
        return new AppContext(manager);
    }

    public void rebindServices() {
        authService = new AuthenticationService(dataManager);
        searchService = new SearchService(dataManager);
        rankingService = new RankingService(dataManager);
        matchHistoryService = new MatchHistoryService(dataManager);
    }

    public void reloadData() throws IOException {
        dataManager = fileStorageService.loadAll();
        rebindServices();
    }

    public GameDataManager getDataManager() {
        return dataManager;
    }

    public AuthenticationService getAuthService() {
        return authService;
    }

    public SearchService getSearchService() {
        return searchService;
    }

    public RankingService getRankingService() {
        return rankingService;
    }

    public MatchHistoryService getMatchHistoryService() {
        return matchHistoryService;
    }

    public FileStorageService getFileStorageService() {
        return fileStorageService;
    }

    public void saveAll() throws IOException {
        fileStorageService.saveAll(dataManager);
    }
}
