package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Difficulty extends BaseModel<Difficulty> {

    public String name;

    @ManyToOne
    public Game game;

    public Difficulty(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
