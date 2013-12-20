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

    public static Player guest = new Player(0L, "guest");
    public String name;

    public Long shmupUserId;

    @OneToMany(mappedBy = "player")
    public List<Score> scores = new ArrayList<Score>();

    public static Finder<Long, Player> finder = new Model.Finder(Long.class, Player.class);

    public Player(Long id, String name) {
        this.id = id;
        this.name = name;
    }

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


    public static Player findByShmupUserId(Long shmupUserId) {
        return Player.finder.where()
                .eq("shmupUserId", shmupUserId)
                .findUnique();
    }

    public List<Score> bestScores() {
        List<Score> bestScores = new ArrayList<Score>(scores);
        bestScores = Scores.keepBestScoresForEachGame(bestScores);
        Collections.sort(bestScores, new Comparator<Score>() {
            @Override
            public int compare(Score score, Score score2) {
                return score.game.title.compareTo(score2.game.title);
            }
        });
        return bestScores;
    }

    public boolean isAuthenticated() {
        return !this.equals(guest);
    }

    public int oneCreditCount() {
        int oneCreditCount = 0;
        for (Score score : bestScores()) {
            if (score.stage != null && score.stage.name.toLowerCase().contains("all")) {
                oneCreditCount++;
            }
        }
        return oneCreditCount;
    }

    public int firstRankCount() {
        int firstRankCount = 0;
        for (Score score : bestScores()) {
            if (score.rank() == 1) {
                firstRankCount++;
            }
        }
        return firstRankCount;
    }

    public int top3Count() {
        int top3Count = 0;
        for (Score score : bestScores()) {
            if (score.rank() <= 3) {
                top3Count++;
            }
        }
        return top3Count;
    }

    public int top10Count() {
        int top10Count = 0;
        for (Score score : bestScores()) {
            if (score.rank() <= 10) {
                top10Count++;
            }
        }
        return top10Count;
    }

}
