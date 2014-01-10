package controllers;

import models.*;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;

import static com.google.common.xml.XmlEscapers.xmlContentEscaper;

public class BackupController extends Controller {

    public static Result index() throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("<shmup>");
        builder.append("<games>");
        for (Game game : Game.findAll()) {
            builder.append("<game>");
            insertGameDetail(builder, game);
            insertDifficulties(builder, game);
            insertPlatforms(builder, game);
            insertModes(builder, game);
            insertRankings(builder, game);
            builder.append("</game>");
        }
        builder.append("</games>");
        builder.append("</shmup>");
        response().setContentType("application/xml");
        return ok(builder.toString());
    }

    private static void insertRankings(StringBuilder builder, Game game) {
        builder.append("<rankings>");
        for (Ranking ranking : game.getRankings()) {
            scores(builder, ranking);
        }
        builder.append("</rankings>");
    }

    private static void scores(StringBuilder builder, Ranking ranking) {
        builder.append("<ranking>");
        if (!ranking.getScores().isEmpty()) {
            if (ranking.mode != null) {
                insert(builder, "mode", ranking.mode.name);
            }
            if (ranking.difficulty != null) {
                insert(builder, "difficulty", ranking.difficulty.name);
            }
            insertScores(builder, ranking);
        }
        builder.append("</ranking>");

    }

    private static void insertScores(StringBuilder builder, Ranking ranking) {
        builder.append("<scores>");
        for (Score score : ranking.getScores()) {
            insertScore(builder, score);
        }
        builder.append("</scores>");
    }

    private static void insertScore(StringBuilder builder, Score score) {
        builder.append("<score>");
        insert(builder, "rank", score.formattedRank());
        insert(builder, "player", score.player.name);
        insert(builder, "photo", score.photo);
        insert(builder, "replay", score.replay);
        insert(builder, "value", score.value.toString());
        insert(builder, "formatted-value", score.formattedValue());
        if (score.stage != null) {
            insert(builder, "stage", score.stage.name);
        }
        insert(builder, "comment", score.comment);
        builder.append("</score>");
    }

    private static void insertGameDetail(StringBuilder builder, Game game) {
        insert(builder, "title", game.title);
        insert(builder, "cover", game.cover);
        insert(builder, "thread", game.thread);
    }

    private static void insertModes(StringBuilder builder, Game game) {
        if (game.modes != null) {
            builder.append("<modes>");
            for (Mode mode : game.modes) {
                builder.append("<mode>");
                insert(builder, "name", mode.name);
                insert(builder, "order", mode.sortOrder);
                builder.append("</mode>");
            }
            builder.append("</modes>");
        }
    }

    private static void insertPlatforms(StringBuilder builder, Game game) {
        if (game.platforms != null) {
            builder.append("<platforms>");
            for (Platform platform : game.platforms) {
                builder.append("<platform>");
                insert(builder, "name", platform.name);
                builder.append("</platform>");
            }
            builder.append("</platforms>");
        }
    }

    private static void insertDifficulties(StringBuilder builder, Game game) {
        if (game.difficulties != null) {
            builder.append("<difficulties>");
            for (Difficulty difficulty : game.difficulties) {
                builder.append("<difficulty>");
                insert(builder, "name", difficulty.name);
                insert(builder, "order", difficulty.sortOrder);
                builder.append("</difficulty>");
            }
            builder.append("</difficulties>");
        }
    }

    private static void insert(StringBuilder builder, String node, String value) {
        if (value == null) {
            return;
        }
        builder.append("<").append(node).append(">").append(xmlContentEscaper().escape(value)).append("</").append(node).append(">");
    }

}
