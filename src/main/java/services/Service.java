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
        return "ello";
    }

    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginDTO login) {
        return repository.login(login.getUsername(), login.getPassword());
    }

    @Path("/user")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth) {
        return repository.getUser(getJwt(auth));
    }

    @Path("/workoutplans")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWorkoutPlans(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth) {
        return repository.getWorkoutPlans(getJwt(auth));
    }

    @Path("/exercises/{workoutId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExercises(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth,
                                 @PathParam("workoutId") long workoutId) {
        return repository.getExercises(getJwt(auth), workoutId);
    }

    private String getJwt(String auth) {
        return auth.split("\\s")[1];
    }
}
