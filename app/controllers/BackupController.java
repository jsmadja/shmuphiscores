package controllers;

import models.Game;
import models.Games;
import play.mvc.Controller;
import play.mvc.Result;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class BackupController extends Controller {

    public static Result index() throws IOException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Games.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        StringWriter wr = new StringWriter();
        List<Game> all = Game.findAll();
        Games games = new Games();
        games.games = all;
        marshaller.marshal(games, wr);
        response().setContentType("application/xml");
        return ok(wr.toString());
    }

}
