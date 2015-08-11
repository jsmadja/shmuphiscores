package models;

import java.util.ArrayList;
import java.util.List;

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
            return this.score1.rank() > this.score2.rank() ? -1 :  1;
        }

        public int gap() {
            return -this.score1.rank() + this.score2.rank();
        }
    }
}
