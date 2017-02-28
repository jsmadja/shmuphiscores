package models;

import actions.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import formatters.ScoreFormatter;

import javax.annotation.Nullable;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static java.math.RoundingMode.HALF_UP;

public class Ranking {

    public Difficulty difficulty;

    public Mode mode;

    public List<Score> scores;

    @Transient
    public String difficultyName;

    @Transient
    public String modeName;

    public boolean general;

    Ranking() {
    }

    public Ranking(Collection<Score> scores) {
        this.scores = new ArrayList<Score>();
        int rank = 1;
        for (Score score : scores) {
            if (score.isVip()) {
                if (score.rank == null) {
                    score.updateRank(rank);
                    score.update();
                }
                this.scores.add(score);
                rank++;
            }
        }
    }

    public Ranking(Collection<Score> scores, Difficulty difficulty) {
        this(scores);
        this.difficulty = difficulty;
        this.difficultyName = difficulty.name;
    }

    public Ranking(Collection<Score> scores, Mode mode) {
        this(scores);
        this.mode = mode;
        this.modeName = mode.name;
    }

    public Ranking(Collection<Score> scores, Difficulty difficulty, Mode mode) {
        this(scores);
        this.difficulty = difficulty;
        this.difficultyName = difficulty.name;

        this.mode = mode;
        this.modeName = mode.name;
    }

    public String joinedPlayerCountPerSplittedScore() {
        TreeSet<BigDecimal> scores = new TreeSet<BigDecimal>(scoresWithAverageScore());
        List<Integer> playerPerCategories = new ArrayList<Integer>();
        for (BigDecimal category : getSplittedScores()) {
            playerPerCategories.add(scores.tailSet(category).size());
        }
        return Joiner.on(",").join(playerPerCategories);
    }

    public String joinedSplittedScores() {
        return Joiner.on(",").join(getSplittedScores());
    }

    public int averageScoreIndex() {
        BigDecimal averageScore = averageScoreAsBigDecimal();
        return getSplittedScores().indexOf(averageScore);
    }

    public int playerScoreIndex() {
        Player player = User.current();
        if (player.isAuthenticated()) {
            for (Score score : scores) {
                if (score.isPlayedBy(player)) {
                    return getSplittedScores().indexOf(score.value);
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    private List<BigDecimal> scoresWithAverageScore() {
        List<BigDecimal> scores = newArrayList(transform(this.scores, new Function<Score, BigDecimal>() {
            @Nullable
            @Override
            public BigDecimal apply(@Nullable Score score) {
                return score.value;
            }
        }));
        scores.add(averageScoreAsBigDecimal());
        scores.add(geomAverageScoreAsBigDecimal());
        Collections.sort(scores);
        return scores;
    }

    private List<BigDecimal> getSplittedScores() {
        TreeSet<BigDecimal> scoresMaps = new TreeSet<BigDecimal>(scoresWithAverageScore());
        List<BigDecimal> scoreCategories = new ArrayList<BigDecimal>();
        BigDecimal min = scoresMaps.first();
        BigDecimal max = scoresMaps.last();
        if (min.equals(max)) {
            min = BigDecimal.ZERO;
        }
        BigDecimal step = (max.subtract(min)).divide(BigDecimal.valueOf(scoresMaps.size()), HALF_UP);
        scoreCategories.add(min);
        for (int i = 1; i < (scoresMaps.size() - 1); i++) {
            scoreCategories.add(scoreCategories.get(i - 1).add(step));
        }
        scoreCategories.add(max);
        TreeSet<BigDecimal> longs = new TreeSet<BigDecimal>(scoreCategories);
        longs.add(averageScoreAsBigDecimal());
        longs.add(geomAverageScoreAsBigDecimal());

        Player current = User.current();
        for (Score score : scores) {
            if (score.isPlayedBy(current)) {
                longs.add(score.value);
                break;
            }
        }

        return new ArrayList<BigDecimal>(longs);
    }

    public String averageScore() {
        return ScoreFormatter.format(averageScoreAsBigDecimal());
    }

    public String geomAverageScore() {
        return ScoreFormatter.format(geomAverageScoreAsBigDecimal());
    }

    public int geomAverageScoreIndex() {
        BigDecimal averageScore = geomAverageScoreAsBigDecimal();
        return getSplittedScores().indexOf(averageScore);
    }

    private BigDecimal averageScoreAsBigDecimal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (Score score : scores) {
            sum = sum.add(score.value);
        }
        if (sum.longValue() == 0L) {
            return BigDecimal.ZERO;
        }
        return sum.divide(BigDecimal.valueOf(scores.size()), HALF_UP);
    }

    private BigDecimal geomAverageScoreAsBigDecimal() {
        BigDecimal GM_log = BigDecimal.ZERO;
        for (Score score : scores) {
            if (score.value.equals(BigDecimal.ZERO)) {
                return BigDecimal.ZERO;
            }
		try {
            GM_log = GM_log.add(BigDecimal.valueOf(Math.log(score.value.doubleValue())));
}catch(Exception e){
// invalid score
}
        }
        BigDecimal divisor = BigDecimal.valueOf(scores.size());
        BigDecimal divide = GM_log.divide(divisor, HALF_UP);
        double a = divide.doubleValue();
        return BigDecimal.valueOf((long)Math.exp(a));
    }

    public String uniqueKey() {
        String difficultyId = "";
        if (difficulty != null) {
            difficultyId = difficulty.id.toString();
        }
        String modeId = "";
        if (mode != null) {
            modeId = mode.id.toString();
        }
        return difficultyId + "_" + modeId;
    }

    public JsonNode json() {
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        if (difficulty != null) {
            node.set("difficulty", difficulty.json());
        }
        if (mode != null) {
            node.set("mode", mode.json());
        }
        ArrayNode scores = new ArrayNode(JsonNodeFactory.instance);
        for (Score score : this.scores) {
            scores.add(score.json());
        }
        node.set("scores", scores);
        return node;
    }

    public boolean isNotEmpty() {
        return !this.scores.isEmpty();
    }
}
