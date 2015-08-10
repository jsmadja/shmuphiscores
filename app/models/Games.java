package models;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class Games {

    @XmlElement(name = "game")
    public List<Game> games;

    public Games() {

    }

}
