package models;

import com.google.common.base.Predicate;
import formatters.ScoreFormatter;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Collections2.filter;

@XmlAccessorType(XmlAccessType.FIELD)
public class Ranking {

    @XmlTransient
    public Difficulty difficulty;

    public Mode mode;

    @XmlElementWrapper
    @XmlElement(name="score")
    public List<Score> scores;

    Ranking() {
    }

    public Ranking(Collection<Score> scores) {
        this.scores = new ArrayList<Score>(scores);
    }

    public Ranking(Collection<Score> scores, Difficulty difficulty) {
        this(scores);
        this.difficulty = difficulty;
    }

    public Ranking(Collection<Score> scores, Mode mode) {
        this(scores);
        this.mode = mode;
    }

    public Ranking(Collection<Score> scores, Difficulty difficulty, Mode mode) {
        this(scores);
        this.difficulty = difficulty;
        this.mode = mode;
    }

    public Collection<Score> getScores() {
        return scores;
    }

    @XmlTransient
    public Difficulty getDifficulty() {
        return difficulty;
    }

    @XmlTransient
    public Mode getMode() {
        return mode;
    }

    public Collection<Score> findScoresMatching(final Difficulty difficulty, final Mode mode) {
        if (scores == null) {
            return new ArrayList<Score>();
        }
        return filter(scores, new Predicate<Score>() {
            @Override
            public boolean apply(@Nullable Score score) {
                return (difficulty == null || score.concerns(difficulty)) && (mode == null || score.concerns(mode));
            }
        });
    }

    public String averageScore() {
        return ScoreFormatter.format(averageScoreAsLong());
    }

    public Long averageScoreAsLong() {
        BigDecimal sum = new BigDecimal(0);
        for (Score score : scores) {
            sum = sum.add(new BigDecimal(score.value));
        }
        if(sum.longValue() == 0L) {
            return 0L;
        }
        Long average = sum.divideToIntegralValue(new BigDecimal(scores.size())).longValue();
        return average;
    }

}
