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
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
                .getResource("templates/exercises.json");

        if (url != null) {
            try (InputStream inputStream = url.openStream()) {
                JSONArray jsonArray = new JSONArray(new JSONTokener(inputStream));

                em.getTransaction().begin();
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject json = jsonArray.getJSONObject(i);

                    ExerciseTemplate template = new ExerciseTemplate(
                            json.getString("name"),
                            json.getEnum(BodyPart.class, "bodyPart")
                    );
                    if (!json.isNull("equipment")) {
                        template.setEquipment(json.getString("equipment"));
                    }

                    em.persist(template);
                }
                em.getTransaction().commit();
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

    private void createPlaceholders() {
        // create user
        User user1 = new User(8387, "Alex123", "123ALEXtest", "Alex", "Burg");
        User user2 = new User(8383, "danielpfeffer", "pfefferIsyes", "Mr", "Pepper");
        em.getTransaction().begin();
        em.persist(user1);
        em.persist(user2);
        em.getTransaction().commit();

        List<ExerciseTemplate> templates
                = em.createQuery("select t from ExerciseTemplate t", ExerciseTemplate.class).getResultList();

        // create workoutPlan
        Calendar warmup = Calendar.getInstance();
        warmup.clear();
        warmup.set(Calendar.MINUTE, 10);
        warmup.set(Calendar.SECOND, 0);

        Calendar cooldown = Calendar.getInstance();
        cooldown.clear();
        cooldown.set(Calendar.MINUTE, 17);
        cooldown.set(Calendar.SECOND, 50);

        WorkoutPlan workoutPlan1 = new WorkoutPlan("Starting Strength", warmup, cooldown);

        // create workouts
        Workout workout1 = new Workout(1);
        UserExercise userExercise1 = new UserExercise(templates.get(1), 3, 5);
        UserExercise userExercise2 = new UserExercise(templates.get(2), 3, 6);
        workout1.getUserExercises().add(userExercise1);
        workout1.getUserExercises().add(userExercise2);

        Workout workout2 = new Workout(2);
        UserExercise userExercise3 = new UserExercise(templates.get(4), 5, 10);
        UserExercise userExercise4 = new UserExercise(templates.get(5), 2, 4);
        workout2.getUserExercises().add(userExercise3);
        workout2.getUserExercises().add(userExercise4);

        workoutPlan1.getWorkouts().add(workout1);
        workoutPlan1.getWorkouts().add(workout2);

        WorkoutPlan workoutPlan2 = new WorkoutPlan("Starting Strength", warmup, cooldown);

        // create workouts
        Workout workout3 = new Workout(1);
        UserExercise userExercise5 = new UserExercise(templates.get(1), 3, 5);
        UserExercise userExercise6 = new UserExercise(templates.get(2), 3, 6);
        workout1.getUserExercises().add(userExercise5);
        workout1.getUserExercises().add(userExercise6);

        Workout workout4 = new Workout(2);
        UserExercise userExercise7 = new UserExercise(templates.get(4), 5, 10);
        UserExercise userExercise8 = new UserExercise(templates.get(5), 2, 4);
        workout2.getUserExercises().add(userExercise7);
        workout2.getUserExercises().add(userExercise8);

        workoutPlan2.getWorkouts().add(workout3);
        workoutPlan2.getWorkouts().add(workout4);

        em.getTransaction().begin();
        user1.getWorkoutPlans().add(workoutPlan1);
        user2.getWorkoutPlans().add(workoutPlan2);
        em.getTransaction().commit();
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

        JSONArray workoutPlansJson = new JSONArray();
        for (WorkoutPlan plan : user.getWorkoutPlans()) {
            JSONObject planJson = new JSONObject()
                    .put("id", plan.getId())
                    .put("name", plan.getName())
                    .put("warmup", formatter.format(plan.getWarmup().getTime()))
                    .put("cooldown", formatter.format(plan.getCooldown().getTime()));

            JSONArray workoutsJson = new JSONArray();
            for (Workout workout : plan.getWorkouts()) {
                JSONObject workoutJson = new JSONObject()
                        .put("id", workout.getId())
                        .put("day", workout.getDay());
                workoutsJson.put(workoutJson);
            }
            planJson.put("workouts", workoutsJson);

            workoutPlansJson.put(planJson);
        }
        return Response.ok(workoutPlansJson.toString()).build();
    }

    private User getUserFromJwt(String jwt) {
        long id = jwtHelper.getUserId(jwt);
        User user = em.find(User.class, id);
        if (user == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        return user;
    }
}
