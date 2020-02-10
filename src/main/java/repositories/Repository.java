package repositories;

import data.entities.*;
import data.enums.BodyPart;
import helper.EntityManagerHelper;
import helper.JwtHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Repository {

    private EntityManager em;
    private JwtHelper jwtHelper;
    private SimpleDateFormat formatter;

    private static Repository repository;

    private Repository() {
        em = EntityManagerHelper.getInstance();
        jwtHelper = new JwtHelper();
        formatter = new SimpleDateFormat("mm:ss");

        // load templates
        URL url = Thread.currentThread()
                .getContextClassLoader()
                .getResource("templates/templates.json");

        if (url != null) {
            try (InputStream inputStream = url.openStream()) {
                JSONArray jsonArray = new JSONArray(new JSONTokener(inputStream));
                System.out.println(jsonArray.toString());
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject json = jsonArray.getJSONObject(i);

                    ExerciseTemplate template = new ExerciseTemplate(
                            json.getString("name"),
                            json.getEnum(BodyPart.class, "bodyPart")
                    );

                    if (!json.isNull("equipment")) {
                        template.setEquipment(json.getString("equipment"));
                    }

                    /*if (!json.isNull("desc")) {
                        template.setDesc(json.getString("desc"));
                    }*/

                    em.getTransaction().begin();
                    em.persist(template);
                    em.getTransaction().commit();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Exercise templates file not found");
        }


        createPlaceholders();
    }

    public static Repository getInstance() {
        if (repository == null) {
            repository = new Repository();
        }
        return repository;
    }

    public Response login(String username, String password) {
        // check if an user with this username exists
        List<User> results = em.createQuery("select u from User u where u.username = :username", User.class)
                .setParameter("username", username)
                .getResultList();
        if (results.size() != 0) {
            User user = results.get(0);
            // check if the password is correct
            if (user.getPassword().equals(password)) {

                String jwt = jwtHelper.create(user.getId());
                JSONObject json = new JSONObject()
                        .put("token", jwt);
                return Response.ok(json.toString()).build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    public Response getUser(String jwt) {
        User user = getUserFromJwt(jwt);

        JSONObject json = new JSONObject()
                .put("id", user.getId())
                .put("firstName", user.getFirstName())
                .put("lastName", user.getLastName());

        return Response.ok(json.toString()).build();
    }

    public Response getWorkoutPlans(String jwt) {
        User user = getUserFromJwt(jwt);

        // todo capitalize using WordUtils.capitalizeFully()

        JSONArray plansJA = new JSONArray();
        for (WorkoutPlan plan : user.getWorkoutPlans()) {
            JSONObject planJO = new JSONObject()
                    .put("id", plan.getId())
                    .put("name", plan.getName())
                    .put("warmup", formatter.format(plan.getWarmup().getTime()))
                    .put("cooldown", formatter.format(plan.getCooldown().getTime()));

            JSONArray workoutsJA = new JSONArray();
            for (Workout workout : plan.getWorkouts()) {
                JSONObject workoutJO = new JSONObject()
                        .put("id", workout.getId())
                        .put("day", workout.getDay());

                JSONArray exercisesJA = new JSONArray();
                /*for (Exercise exercise : workout.getExercises()) {
                    exercisesJA.put(exercise.toJson());
                }*/

                workoutJO.put("exercises", exercisesJA);
                workoutsJA.put(workoutJO);
            }
            planJO.put("workouts", workoutsJA);
            plansJA.put(planJO);
        }
        return Response.ok(plansJA.toString()).build();
    }

    private User getUserFromJwt(String jwt) {
        long id = jwtHelper.getUserId(jwt);
        User user = em.find(User.class, id);
        if (user == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        return user;
    }

    private void createPlaceholders() {
        // create user
        User user1 = new User(8387, "Alex123", "123ALEXtest", "Alex", "Burg");
        em.getTransaction().begin();
        em.persist(user1);
        em.getTransaction().commit();

        List<ExerciseTemplate> templates
                = em.createQuery("select t from ExerciseTemplate t", ExerciseTemplate.class).getResultList();

        /* create workout plan (starting strength) for user1 */

        // warmup/cooldown
        Date warmup1, cooldown1;
        try {
            warmup1 = formatter.parse("10:00");
            cooldown1 = formatter.parse("20:00");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        WorkoutPlan workoutPlan1 = new WorkoutPlan("Starting Strength", warmup1, cooldown1);

        // create workouts
        Workout workout1 = new Workout(1);

        ExerciseTemplate template1 = new ExerciseTemplate("Kniebeuge", BodyPart.LEGS);
        Exercise exercise1 = new Exercise(template1);
        ExerciseSpecification specification1 = new ExerciseSpecification(exercise1, "3", "5", "45");

        workout1.getExercises().add(specification1);
        workoutPlan1.getWorkouts().add(workout1);

        em.getTransaction().begin();
        em.persist(workoutPlan1);
        em.getTransaction().commit();
    }
}
