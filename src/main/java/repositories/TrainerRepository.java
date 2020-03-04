package repositories;

import data.entities.Trainer;
import helper.EntityManagerHelper;
import helper.JwtHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

public class TrainerRepository {

    private EntityManager em;
    private JwtHelper jwtHelper;

    private static TrainerRepository repository;

    private TrainerRepository() {
        em = EntityManagerHelper.getInstance();
        jwtHelper = new JwtHelper();
    }

    public static TrainerRepository getInstance() {
        if (repository == null) {
            repository = new TrainerRepository();
        }
        return repository;
    }

    public Response login(String username, String password) {
        // check if a trainer with this username exists
        List<Trainer> results = em.createQuery("SELECT t FROM Trainer t WHERE t.username = :username", Trainer.class)
                .setParameter("username", username)
                .getResultList();

        if (results.size() != 0) {
            Trainer trainer = results.get(0);

            // check if the password is correct
            if (trainer.getPassword().equals(password)) {

                String jwt = jwtHelper.create(trainer.getId());
                JSONObject json = new JSONObject()
                        .put("token", jwt);
                return Response.ok(json.toString()).build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    public Response getClients(String jwt) {
        Trainer trainer = getTrainerFromJwt(jwt);

        JSONArray clientJA = new JSONArray();
        trainer.getClients().forEach(client -> clientJA.put(client.toJson()));

        return Response.ok(clientJA.toString()).build();
    }

    private Trainer getTrainerFromJwt(String jwt) {
        long id = jwtHelper.getUserId(jwt);
        Trainer trainer = em.find(Trainer.class, id);
        if (trainer == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        return trainer;
    }
}
