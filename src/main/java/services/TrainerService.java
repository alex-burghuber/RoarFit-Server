package services;

import data.dto.CreateClientDTO;
import data.dto.LoginDTO;
import data.dto.addworkoutplan.WorkoutPlanDTO;
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

    @Path("/clients")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createClient(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, CreateClientDTO createClientDTO) {
        return repository.createClient(getJwt(auth), createClientDTO);
    }

    @Path("/clients/{memberId}")
    @PUT
    public Response addClient(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("memberId") long memberId) {
        return repository.addClient(getJwt(auth), memberId);
    }

    @Path("/clients/{memberId}")
    @DELETE
    public Response removeClient(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("memberId") long memberId) {
        return repository.removeClient(getJwt(auth), memberId);
    }

    @Path("/workoutplan/{memberId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWorkoutPlan(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("memberId") long memberId) {
        return repository.getWorkoutPlan(getJwt(auth), memberId);
    }

    @Path("/workoutplan/{memberId}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addWorkoutPlan(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth,
                                   @PathParam("memberId") long memberId, WorkoutPlanDTO workoutPlanDTO) {
        return repository.addWorkoutPlan(getJwt(auth), memberId, workoutPlanDTO);
    }

    @Path("/workoutplan/{memberId}/{workoutPlanId}")
    @DELETE
    public Response archiveWorkoutPlan(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth,
                                       @PathParam("memberId") long memberId, @PathParam("workoutPlanId") long workoutPlanId) {
        return repository.archiveWorkoutPlan(getJwt(auth), memberId, workoutPlanId);
    }

    private String getJwt(String auth) {
        if (auth != null) {
            String[] split = auth.split("\\s");
            if (split.length > 1) {
                return split[1];
            }
        }
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }
}
