package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Stage extends BaseModel<Stage> {

    public String name;

    public Long sortOrder;

    @ManyToOne
    public Game game;

    public Stage(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public JsonNode json() {
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.set("id", new LongNode(id));
        node.set("name", new TextNode(name));
        return node;
    }

}
