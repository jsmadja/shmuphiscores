package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

@Entity
public class Mode extends BaseModel<Mode> {

    @XmlAttribute
    public String name;

    @XmlTransient
    public String sortOrder;

    @ManyToOne
    @XmlTransient
    public Game game;

    public Mode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
