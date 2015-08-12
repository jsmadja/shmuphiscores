package models;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;

public class Versus {

    public Player player1;
    public Player player2;

    public List<Comparison> comparisons = new ArrayList<Comparison>();

    public Versus(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public void add(Comparison comparison) {
        this.comparisons.add(comparison);
    }

    public static class Comparison {
        public final Game game;
        public final Difficulty difficulty;
        public final Mode mode;
        public final Score score1;
        public final Score score2;

        public Comparison(Game game, Difficulty difficulty, Mode mode, Score score1, Score score2) {
            this.game = game;
            this.difficulty = difficulty;
            this.mode = mode;
            this.score1 = score1;
            this.score2 = score2;
        }

        public int diff() {
            return this.score1.rank() > this.score2.rank() ? -1 : 1;
        }

        public int scoreGap() {
            Score scoreA, scoreB;
            if (score1.isTimeScore()) {
                scoreA = score2;
                scoreB = score1;
                DateTime dateTimeA = new DateTime(scoreA.value.longValue());
                DateTime dateTimeB = new DateTime(scoreB.value.longValue());
                double a = dateTimeA.getMinuteOfDay() * 60000 + dateTimeA.getSecondOfMinute() * 1000 + dateTimeA.getMillisOfSecond();
                double b = dateTimeB.getMinuteOfDay() * 60000 + dateTimeB.getSecondOfMinute() * 1000 + dateTimeB.getMillisOfSecond();
                return (int)((a * 100D) / b);
            } else {
                scoreA = score1;
                scoreB = score2;
            }
            return scoreA.value.multiply(BigDecimal.valueOf(100)).divide(scoreB.value, HALF_UP).intValue();
        }
    }
}
