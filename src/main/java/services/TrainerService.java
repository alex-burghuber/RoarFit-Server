package services;

import data.dto.LoginDTO;
import repositories.TrainerRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@Path("/trainer")
public class TrainerService {

    private TrainerRepository repository = TrainerRepository.getInstance();

    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginDTO login) {
        return repository.login(login.getUsername(), login.getPassword());
    }

    @Path("/clients")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClients(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth) {
        return repository.getClients(getJwt(auth));
    }

    private String getJwt(String auth) {
        if (auth != null) {
            return auth.split("\\s")[1];
        }
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }
}
