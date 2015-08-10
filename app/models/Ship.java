package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Ship extends BaseModel<Ship> {

    public String name;

    public String sortOrder;

    @ManyToOne
    public Game game;

    public Ship(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
