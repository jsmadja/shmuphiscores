package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Entity
public class Player extends BaseModel<Player> {

    public String name;

    @OneToMany(mappedBy = "player")
    public List<Score> scores;

    public static Finder<Long, Player> finder = new Model.Finder(Long.class, Player.class);

    public Player(String name) {
        this.name = name;
    }

    public List<Score> getScores() {
        return scores;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Player findOrCreatePlayer(String name) {
        Player player = Player.finder.where()
                .eq("name", name)
                .findUnique();
        if (player == null) {
            player = new Player(name);
            player.save();
        }
        return player;
    }

    public List<Score> bestScores() {
        List<Score> bestScores = new ArrayList<Score>(scores);
        Collections.sort(bestScores);
        bestScores = Scores.keepBestScoresForEachGame(bestScores);
        Collections.sort(bestScores, new Comparator<Score>() {
            @Override
            public int compare(Score score, Score score2) {
                return score.game.title.compareTo(score2.game.title);
            }
        });
        return bestScores;
    }

}
