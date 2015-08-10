package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Mode extends BaseModel<Mode> {

    public String name;

    public String sortOrder;

    @ManyToOne
    public Game game;

    public Mode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
