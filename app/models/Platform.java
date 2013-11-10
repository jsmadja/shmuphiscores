package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Platform extends BaseModel<Platform> {

    public String name;

    @ManyToOne
    public Game game;

    public Platform(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
