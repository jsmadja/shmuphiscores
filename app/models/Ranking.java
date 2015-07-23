package models;

import actions.User;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import formatters.ScoreFormatter;

import javax.annotation.Nullable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static java.math.RoundingMode.HALF_UP;

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

    public boolean general;

    Ranking() {
    }

    public Ranking(Collection<Score> scores) {
        this.scores = new ArrayList<Score>();
        int rank = 1;
        for (Score score : scores) {
            if (score.isVip()) {
                if (score.rank() == null) {
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
