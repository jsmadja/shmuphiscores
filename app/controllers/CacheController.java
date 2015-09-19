package controllers;

import models.Game;
import models.Player;
import play.cache.Cache;

import java.util.HashMap;
import java.util.Map;

public class CacheController {

    public static Map<Player, byte[]> getSignatureCache() {
        Map<Player, byte[]> signatures = (Map<Player, byte[]>) Cache.get("signatureCache");
        if (signatures == null) {
            signatures = new HashMap<Player, byte[]>();
            Cache.set("signatureCache", signatures, 3600);
        }
        return signatures;
    }

    public static Map<Player, byte[]> getMedalsCache() {
        Map<Player, byte[]> medals = (Map<Player, byte[]>) Cache.get("medalsCache");
        if (medals == null) {
            medals = new HashMap<Player, byte[]>();
            Cache.set("medalsCache", medals, 3600);
        }
        return medals;
    }

    public static Map<Game, byte[]> getRankingCache() {
        Map<Game, byte[]> rankings = (Map<Game, byte[]>) Cache.get("rankingCache");
        if (rankings == null) {
            rankings = new HashMap<Game, byte[]>();
            Cache.set("rankingCache", rankings);
        }
        return rankings;
    }

    public static Map<Player, byte[]> getVersusCache() {
        Map<Player, byte[]> versus = (Map<Player, byte[]>) Cache.get("versusCache");
        if (versus == null) {
            versus = new HashMap<Player, byte[]>();
            Cache.set("versusCache", versus, 3600);
        }
        return versus;
    }
}
