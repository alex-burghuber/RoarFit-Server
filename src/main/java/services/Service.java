package services;

import data.dto.LoginDTO;
import data.dto.TemplateDTO;
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
        return "Hello there!";
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

    @Path("/equipment")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEquipment(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth) {
        return repository.getEquipment(getJwt(auth));
    }

    @Path("/templates")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExerciseTemplates(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, TemplateDTO template) {
        return repository.getExerciseTemplates(getJwt(auth), template.getEquipment());
    }

    private String getJwt(String auth) {
        if (auth != null) {
            return auth.split("\\s")[1];
        }
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }
}
