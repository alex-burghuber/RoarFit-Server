package repositories;

import data.dto.CreateClientDTO;
import data.dto.addworkoutplan.ExerciseDTO;
import data.dto.addworkoutplan.ExerciseTemplateDTO;
import data.dto.addworkoutplan.WorkoutPlanDTO;
import data.entities.*;
import helper.EntityManagerHelper;
import helper.JwtHelper;
import helper.TimeFormatHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public Response getWorkoutPlan(String jwt, long memberId) {
        Trainer trainer = getTrainerFromJwt(jwt);

        List<StudioMember> studioMembers = getClientsOfTrainer(memberId, trainer.getId());

        if (!studioMembers.isEmpty()) {
            StudioMember member = studioMembers.get(0);

            String queryGetWorkoutPlan = "SELECT w FROM WorkoutPlan w JOIN StudioMember m ON m.id = :memberId " +
                    "WHERE w MEMBER OF m.workoutPlans " +
                    "AND w.archived = false";
            List<WorkoutPlan> workoutPlans = em.createQuery(queryGetWorkoutPlan, WorkoutPlan.class)
                    .setParameter("memberId", member.getId())
                    .getResultList();

            if (!workoutPlans.isEmpty()) {
                WorkoutPlan plan = workoutPlans.get(0);

                JSONObject planJO = plan.toJson();

                JSONArray workoutsJA = new JSONArray();
                for (Workout workout : plan.getWorkouts()) {
                    JSONObject workoutJO = workout.toJson();

                    JSONArray specificationsJA = new JSONArray();
                    for (ExerciseSpecification specification : workout.getSpecifications()) {
                        specificationsJA.put(specification.toJson());
                    }
                    workoutJO.put("specifications", specificationsJA);
                    workoutsJA.put(workoutJO);
                }
                planJO.put("workouts", workoutsJA);

                return Response.ok(planJO.toString()).build();
            } else {
                return Response.noContent().build();
            }
        }

        return Response.status(Response.Status.FORBIDDEN).build();
    }

    public Response addWorkoutPlan(String jwt, long memberId, WorkoutPlanDTO workoutPlanDTO) {
        Trainer trainer = getTrainerFromJwt(jwt);

        List<StudioMember> studioMembers = getClientsOfTrainer(memberId, trainer.getId());

        if (!studioMembers.isEmpty()) {
            StudioMember member = studioMembers.get(0);

            String query = "SELECT COUNT(w) FROM WorkoutPlan w JOIN StudioMember m ON m.id = :memberId " +
                    "WHERE w MEMBER OF m.workoutPlans AND w.archived = false";
            long workoutPlanCount = em.createQuery(query, Long.class)
                    .setParameter("memberId", member.getId())
                    .getSingleResult();

            if (workoutPlanCount == 0) {
                // here be dragons
                try {
                    SimpleDateFormat formatter = TimeFormatHelper.getInstance().formatter;
                    WorkoutPlan workoutPlan = new WorkoutPlan(
                            workoutPlanDTO.getName(),
                            formatter.parse(workoutPlanDTO.getWarmup()),
                            formatter.parse(workoutPlanDTO.getCooldown()),
                            workoutPlanDTO.getInfo()
                    );

                    List<Workout> workouts = new ArrayList<>();
                    workoutPlanDTO.getWorkouts().forEach(workoutDTO -> {

                        Workout workout = new Workout(
                                workoutDTO.getDay(),
                                workoutDTO.getWeek(),
                                workoutDTO.getInfo()
                        );

                        List<ExerciseSpecification> specifications = new ArrayList<>();
                        workoutDTO.getSpecifications().forEach(exerciseSpecificationDTO -> {
                            ExerciseSpecification specification = new ExerciseSpecification(
                                    exerciseSpecificationDTO.getSets(),
                                    exerciseSpecificationDTO.getReps(),
                                    exerciseSpecificationDTO.getWeight(),
                                    exerciseSpecificationDTO.getInfo()
                            );

                            ExerciseDTO exerciseDTO = exerciseSpecificationDTO.getExercise();
                            Exercise exercise = new Exercise();

                            ExerciseTemplateDTO templateDTO = exerciseDTO.getTemplate();
                            ExerciseTemplate template = new ExerciseTemplate(
                                    templateDTO.getName(),
                                    templateDTO.getEquipment(),
                                    templateDTO.getDescription(),
                                    templateDTO.getVideoUrl(),
                                    templateDTO.getBodyParts()
                            );

                            exercise.setTemplate(template);
                            specification.setExercise(exercise);

                            specifications.add(specification);
                        });

                        workout.setSpecifications(specifications);
                        workouts.add(workout);
                    });

                    workoutPlan.setWorkouts(workouts);

                    em.getTransaction().begin();
                    member.getWorkoutPlans().add(workoutPlan);
                    em.getTransaction().commit();

                    return Response.ok().build();
                } catch (ParseException e) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
            } else {
                return Response.status(Response.Status.CONFLICT).build();
            }
        }

        return Response.status(Response.Status.FORBIDDEN).build();
    }

    public Response archiveWorkoutPlan(String jwt, long memberId, long workoutPlanId) {
        Trainer trainer = getTrainerFromJwt(jwt);

        List<StudioMember> studioMembers = getClientsOfTrainer(memberId, trainer.getId());

        if (!studioMembers.isEmpty()) {
            StudioMember member = studioMembers.get(0);

            String query = "SELECT w FROM WorkoutPlan w " +
                    "JOIN StudioMember m ON m.id = :memberId " +
                    "WHERE w.id = :workoutPlanId";
            List<WorkoutPlan> workoutPlans = em.createQuery(query, WorkoutPlan.class)
                    .setParameter("memberId", member.getId())
                    .setParameter("workoutPlanId", workoutPlanId)
                    .getResultList();

            if (!workoutPlans.isEmpty()) {
                WorkoutPlan workoutPlan = workoutPlans.get(0);

                em.getTransaction().begin();
                workoutPlan.setArchived(true);
                em.getTransaction().commit();

                return Response.ok().build();
            }
        }

        return Response.status(Response.Status.FORBIDDEN).build();
    }

    /*public Response updateWorkoutPlan(String jwt, long memberId, long workoutPlanId, WorkoutPlanDTO workoutPlanDTO) {
        Trainer trainer = getTrainerFromJwt(jwt);

        List<StudioMember> studioMembers = getClientsOfTrainer(memberId, trainer.getId());

        if (!studioMembers.isEmpty()) {
            StudioMember studioMember = studioMembers.get(0);

            String query = "SELECT w FROM WorkoutPlan w " +
                    "JOIN StudioMember m ON m.id = :memberId " +
                    "WHERE w.id = :workoutPlanId " +
                    "AND w MEMBER OF m.workoutPlans " +
                    "AND w.archived = false";
            List<WorkoutPlan> workoutPlans = em.createQuery(query, WorkoutPlan.class)
                    .setParameter("memberId", studioMember.getId())
                    .setParameter("workoutPlanId", workoutPlanId)
                    .getResultList();

            if (!workoutPlans.isEmpty()) {
                WorkoutPlan workoutPlan = workoutPlans.get(0);

                SimpleDateFormat formatter = TimeFormatHelper.getInstance().formatter;
                try {
                    em.getTransaction().begin();
                    workoutPlan.setName(workoutPlanDTO.getName());
                    workoutPlan.setInfo(workoutPlanDTO.getInfo());
                    workoutPlan.setWarmup(formatter.parse(workoutPlanDTO.getWarmup()));
                    workoutPlan.setCooldown(formatter.parse(workoutPlanDTO.getCooldown()));



                    em.getTransaction().commit();
                    return Response.ok().build();
                } catch (ParseException e) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
            }
        }

        return Response.status(Response.Status.FORBIDDEN).build();
    }*/

    private List<StudioMember> getClientsOfTrainer(long memberId, long trainerId) {
        String queryGetMember = "SELECT m FROM StudioMember m JOIN Trainer t ON t.id = :trainerId WHERE m.id = :memberId";
        return em.createQuery(queryGetMember, StudioMember.class)
                .setParameter("trainerId", trainerId)
                .setParameter("memberId", memberId)
                .getResultList();
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
