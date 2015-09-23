package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Event extends BaseModel<Event> implements Comparable<Event> {

    public String name;
    public String description;

    @JoinColumn(name="game_id")
    @OneToOne(cascade = CascadeType.ALL)
    public Game game;

    @Override
    public int compareTo(Event event) {
        return name.compareTo(event.name);
    }
}
