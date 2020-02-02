package services;

import repositories.Repository;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

@Provider
@Path("/roarfit")
public class Service {

    private Repository repository = new Repository();

    @Path("/hello")
    @GET
    public String greetings() {
        return "hello";
    }
}
