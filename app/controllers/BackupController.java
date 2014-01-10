package controllers;

import models.Game;
import play.mvc.Controller;
import play.mvc.Result;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;

public class BackupController extends Controller {

    public static Result index() throws IOException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Game.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        StringWriter wr = new StringWriter();
        marshaller.marshal(Game.finder.byId(2L), wr);
        response().setContentType("application/xml");
        return ok(wr.toString());
    }

}
