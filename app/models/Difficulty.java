package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

@Entity
public class Difficulty extends BaseModel<Difficulty> {

    @XmlAttribute
    public String name;

    @XmlTransient
    public String sortOrder;

    @ManyToOne
    @XmlTransient
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
