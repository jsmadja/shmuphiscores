package models;

import actions.User;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import formatters.ScoreFormatter;

import javax.annotation.Nullable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;

@XmlAccessorType(XmlAccessType.FIELD)
public class Ranking {

    @XmlTransient
    public Difficulty difficulty;

    @XmlTransient
    public Mode mode;

    @XmlElement(name = "score")
    public List<Score> scores;

    @Transient
    @XmlAttribute(name = "platform")
    public String platformName;

    @Transient
    @XmlAttribute(name = "difficulty")
    public String difficultyName;

    @Transient
    @XmlAttribute(name = "mode")
    public String modeName;

    Ranking() {
    }

    public Ranking(Collection<Score> scores) {
        this.scores = new ArrayList<Score>(scores);
        int rank = 1;
        for (Score score : this.scores) {
            if (score.rank() == null) {
                score.updateRank(rank);
                score.update();
            }
            rank++;
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
        TreeSet<Long> scores = new TreeSet<Long>(scoresWithAverageScore());
        List<Integer> playerPerCategories = new ArrayList<Integer>();
        for (Long category : getSplittedScores()) {
            playerPerCategories.add(scores.tailSet(category).size());
        }
        return Joiner.on(",").join(playerPerCategories);
    }

    public String joinedSplittedScores() {
        return Joiner.on(",").join(getSplittedScores());
    }

    public int averageScoreIndex() {
        Long averageScore = averageScoreAsLong();
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

    private List<Long> scoresWithAverageScore() {
        List<Long> scores = newArrayList(transform(this.scores, new Function<Score, Long>() {
            @Nullable
            @Override
            public Long apply(@Nullable Score score) {
                return score.value;
            }
        }));
        scores.add(averageScoreAsLong());
        Collections.sort(scores);
        return scores;
    }

    private List<Long> getSplittedScores() {
        TreeSet<Long> scoresMaps = new TreeSet<Long>(scoresWithAverageScore());
        List<Long> scoreCategories = new ArrayList<Long>();
        Long min = scoresMaps.first();
        Long max = scoresMaps.last();
        if (min.equals(max)) {
            min = 0L;
        }
        long step = (max - min) / scoresMaps.size();
        scoreCategories.add(min);
        for (int i = 1; i < (scoresMaps.size() - 1); i++) {
            scoreCategories.add(scoreCategories.get(i - 1).longValue() + step);
        }
        scoreCategories.add(max);
        TreeSet<Long> longs = new TreeSet<Long>(scoreCategories);
        longs.add(averageScoreAsLong());

        Player current = User.current();
        for (Score score : scores) {
            if (score.isPlayedBy(current)) {
                longs.add(score.value);
                break;
            }
        }

        return new ArrayList<Long>(longs);
    }

    public String averageScore() {
        return ScoreFormatter.format(averageScoreAsLong());
    }

    private Long averageScoreAsLong() {
        BigDecimal sum = new BigDecimal(0);
        for (Score score : scores) {
            sum = sum.add(new BigDecimal(score.value));
        }
        if (sum.longValue() == 0L) {
            return 0L;
        }
        Long average = sum.divideToIntegralValue(new BigDecimal(scores.size())).longValue();
        return average;
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

}
