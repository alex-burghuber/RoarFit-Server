package repositories;

import data.entities.*;
import helper.EntityManagerHelper;
import helper.JwtHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
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
                for (Exercise exercise : workout.getExercises()) {
                    exercisesJA.put(exercise.toJson());
                }

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
        User user2 = new User(8383, "danielpfeffer", "pfefferIsyes", "Mr", "Pepper");
        em.getTransaction().begin();
        em.persist(user1);
        em.persist(user2);
        em.getTransaction().commit();

        List<ExerciseTemplate> templates
                = em.createQuery("select t from ExerciseTemplate t", ExerciseTemplate.class).getResultList();

        /* create workout plan for user1 */

        // warmup/cooldown
        Date warmup1, cooldown1;
        try {
            warmup1 = formatter.parse("10:00");
            cooldown1 = formatter.parse("20:00");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        WorkoutPlan startingStrength = new WorkoutPlan("Starting Strength", warmup1, cooldown1);

        // create workouts
        Workout workout1 = new Workout(1);
        Exercise exercise1 = new Exercise(templates.get(1), 3, 5, 45);
        Exercise exercise2 = new Exercise(templates.get(2), 3, 6, 32.5);
        workout1.getExercises().add(exercise1);
        workout1.getExercises().add(exercise2);

        Workout workout2 = new Workout(2);
        Exercise exercise3 = new Exercise(templates.get(4), 5, 10);
        Exercise exercise4 = new Exercise(templates.get(5), 2, 4);
        workout2.getExercises().add(exercise3);
        workout2.getExercises().add(exercise4);

        startingStrength.getWorkouts().add(workout1);
        startingStrength.getWorkouts().add(workout2);

        // create workout plan for user2
        WorkoutPlan workoutPlan2 = new WorkoutPlan("Starting Strength", warmup, cooldown);

        // create workouts
        Workout workout3 = new Workout(1);
        Exercise exercise5 = new Exercise(templates.get(1), 3, 5);
        Exercise exercise6 = new Exercise(templates.get(2), 3, 6);
        workout1.getExercises().add(exercise5);
        workout1.getExercises().add(exercise6);

        Workout workout4 = new Workout(2);
        Exercise exercise7 = new Exercise(templates.get(4), 5, 10);
        Exercise exercise8 = new Exercise(templates.get(5), 2, 4);
        workout2.getExercises().add(exercise7);
        workout2.getExercises().add(exercise8);

        workoutPlan2.getWorkouts().add(workout3);
        workoutPlan2.getWorkouts().add(workout4);

        em.getTransaction().begin();
        user1.getWorkoutPlans().add(startingStrength);
        user2.getWorkoutPlans().add(workoutPlan2);
        em.getTransaction().commit();
    }
}
