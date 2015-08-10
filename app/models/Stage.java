package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Stage extends BaseModel<Stage> {

    public String name;

    public Long sortOrder;

    @ManyToOne
    public Game game;

    public Stage(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
