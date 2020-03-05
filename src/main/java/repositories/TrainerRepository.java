package repositories;

import data.dto.CreateClientDTO;
import data.entities.StudioMember;
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

    public Response createClient(String jwt, CreateClientDTO createClientDTO) {
        Trainer trainer = getTrainerFromJwt(jwt);

        String username = createClientDTO.getUsername();
        String password = createClientDTO.getPassword();
        String firstName = createClientDTO.getFirstName();
        String lastName = createClientDTO.getLastName();

        if (username != null && username.length() >= 3 && username.length() <= 20
                && password != null && password.length() >= 6 && password.length() <= 25
                && firstName != null && firstName.length() >= 3 && firstName.length() <= 20
                && lastName != null && lastName.length() >= 3 && lastName.length() <= 20) {

            long amountOfUsers = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                    .setParameter("username", username)
                    .getSingleResult();

            if (amountOfUsers == 0L) {
                StudioMember member = new StudioMember(username, password, firstName, lastName);

                em.getTransaction().begin();
                trainer.getClients().add(member);
                em.getTransaction().commit();

                return Response.ok().build();
            } else {
                return Response.status(Response.Status.CONFLICT).build();
            }
        }

        return Response.status(Response.Status.FORBIDDEN).build();
    }

    public Response addClient(String jwt, long memberId) {
        Trainer trainer = getTrainerFromJwt(jwt);

        String query = "SELECT m FROM StudioMember m JOIN Trainer t " +
                "WHERE m.id = :memberId " +
                "AND m NOT MEMBER OF t.clients";
        List<StudioMember> studioMembers = em.createQuery(query, StudioMember.class)
                .setParameter("memberId", memberId)
                .getResultList();

        if (!studioMembers.isEmpty()) {
            StudioMember member = studioMembers.get(0);

            em.getTransaction().begin();
            trainer.getClients().add(member);
            em.getTransaction().commit();

            return Response.ok().build();
        }

        return Response.status(Response.Status.FORBIDDEN).build();
    }

    public Response removeClient(String jwt, long memberId) {
        Trainer trainer = getTrainerFromJwt(jwt);

        StudioMember member = em.find(StudioMember.class, memberId);
        if (member != null) {
            em.getTransaction().begin();
            boolean wasPartOf = trainer.getClients().remove(member);
            em.getTransaction().commit();
            if (wasPartOf) {
                return Response.ok().build();
            }
        }

        return Response.status(Response.Status.FORBIDDEN).build();
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
