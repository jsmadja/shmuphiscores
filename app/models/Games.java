package models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "games")
@XmlAccessorType(XmlAccessType.FIELD)
public class Games {

    @XmlElement(name = "game")
    public List<Game> games;

    public Games() {

    }

}
