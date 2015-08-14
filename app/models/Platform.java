package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class Platform extends BaseModel<Platform> {

    public String name;

    @ManyToOne
    public Game game;

    public static Model.Finder<Long, Platform> finder = new Model.Finder(Long.class, Platform.class);

    public Platform(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
