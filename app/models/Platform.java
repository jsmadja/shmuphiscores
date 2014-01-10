package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

@Entity
public class Platform extends BaseModel<Platform> {

    @XmlAttribute
    public String name;

    @ManyToOne
    @XmlTransient
    public Game game;

    public static Finder<Long, Platform> finder = new Model.Finder(Long.class, Platform.class);

    public Platform(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
