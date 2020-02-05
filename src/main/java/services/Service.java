package services;

import data.dto.LoginDTO;
import repositories.Repository;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@Path("/roarfit")
public class Service {

    private Repository repository = Repository.getInstance();

    @Path("/hello")
    @GET
    public String greetings() {
        return "hello";
    }

    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginDTO login) {
        return repository.login(login.getUsername(), login.getPassword());
    }

    @Path("/customers/customer/{customerNumber}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomer(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth,
                                @PathParam("customerNumber") long customerNumber) {
        return repository.getCustomer(getJwt(auth), customerNumber);
    }

    private String getJwt(String auth) {
        return auth.split("\\s")[1];
    }
}
