package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

@Entity
public class Stage extends BaseModel<Stage> {

    @XmlAttribute
    public String name;

    @XmlTransient
    public Long sortOrder;

    @ManyToOne
    @XmlTransient
    public Game game;

    public Stage(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
