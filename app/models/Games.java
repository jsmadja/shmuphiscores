package models;

import javax.xml.bind.annotation.*;
import java.util.Collection;

@XmlRootElement(name = "games")
@XmlAccessorType(XmlAccessType.FIELD)
public class Games {

    @XmlElement(name= "game")
    public Collection<Game> games;

    public Games() {

    }

}
