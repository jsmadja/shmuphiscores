package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Difficulty extends BaseModel<Difficulty> {

    public String name;

    public String sortOrder;

    @ManyToOne
    public Game game;

    public static Finder<Long, Difficulty> finder = new Model.Finder(Long.class, Difficulty.class);

    public Difficulty(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
