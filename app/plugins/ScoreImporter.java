package plugins;

import models.*;
import play.Logger;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.defaultString;

public class ScoreImporter {

    public static void importScores(Game game, List<String[]> lines) {
        for (String[] string : lines) {
            Logger.info(Arrays.toString(string));
            Player player = player(string[2]);
            Stage stage = stage(game, string[4]);
            Mode mode = mode(game, string[0]);
            Difficulty difficulty = difficulty(game, string[1]);
            String comment = comment(string);
            Platform platform = platform(game, string[5]);
            Long score = score(string[3]);
            String replay = null;
            if (string.length > 7) {
                replay = replay(string[7]);
            }
            Score newScore = new Score(game, player, stage, mode, difficulty, comment, platform, score, null, replay);
            newScore.save();
        }
    }

    private static Platform platform(Game game, String str) {
        String platformName = defaultString(str).trim();
        for (Platform platform : game.platforms) {
            if (platform.name.equals(platformName)) {
                return platform;
            }
        }
        return null;
    }

    private static Difficulty difficulty(Game game, String str) {
        String difficultyName = defaultString(str).trim();
        List<Difficulty> difficulties = game.difficulties;
        for (Difficulty difficulty : difficulties) {
            if (difficulty.name.equals(difficultyName)) {
                return difficulty;
            }
        }
        return null;
    }

    private static String comment(String[] string) {
        return defaultString(string[6]).trim();
    }

    private static String replay(String string) {
        if (string == null) {
            return null;
        }
        return string.trim();
    }

    private static Mode mode(Game game, String str) {
        String modeName = defaultString(str).trim();
        List<Mode> modes = game.modes;
        for (Mode mode : modes) {
            if (mode.name.equals(modeName)) {
                return mode;
            }
        }
        return null;
    }

    private static Long score(String str) {
        String score = defaultString(str).trim();
        score = score.replaceAll("\\.", "");
        score = score.replaceAll(",", "");
        score = score.replaceAll(" ", "");
        return Long.valueOf(score);
    }

    private static Stage stage(Game game, String str) {
        String stageName = defaultString(str).trim();
        List<Stage> stages = game.stages;
        for (Stage stage : stages) {
            if (stage.name.equals(stageName)) {
                return stage;
            }
        }
        return null;
    }

    private static Player player(String str) {
        String player = defaultString(str).replaceAll("[0-9]*\\.", "").trim();
        return Player.findOrCreatePlayer(player);
    }

}
