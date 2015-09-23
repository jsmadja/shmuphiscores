package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Mode extends BaseModel<Mode> {

    public String name;

    public String sortOrder;

    @ManyToOne
    public Game game;

    public String scoreType;

    public Mode(String name) {
        this.name = name;
    }

    public Mode(String name, String scoreType) {
        this(name);
        this.scoreType = scoreType;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isTimerScore() {
        return "timer".equals(scoreType);
    }

    public JsonNode json() {
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.set("id", new LongNode(id));
        node.set("name", new TextNode(name));
        return node;
    }
}
