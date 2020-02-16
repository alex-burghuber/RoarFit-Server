package repositories;

import data.entities.*;
import data.enums.BodyPart;
import helper.EntityManagerHelper;
import helper.JwtHelper;
import helper.TimeFormatHelper;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Repository {

    private EntityManager em;
    private JwtHelper jwtHelper;

    private static Repository repository;

    private Repository() {
        em = EntityManagerHelper.getInstance();
        jwtHelper = new JwtHelper();
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
        return Response.ok(user.toJson().toString()).build();
    }

    public Response getWorkoutPlans(String jwt) {
        User user = getUserFromJwt(jwt);

        JSONArray plansJA = new JSONArray();
        for (WorkoutPlan plan : user.getWorkoutPlans()) {
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
            plansJA.put(planJO);
        }
        return Response.ok(plansJA.toString()).build();
    }

    public Response getEquipment(String jwt) {
        // for jwt validation
        getUserFromJwt(jwt);

        String query = "SELECT DISTINCT t.equipment FROM ExerciseTemplate t where t.equipment != null";
        List<String> equipment = em.createQuery(query, String.class)
                .getResultList();

        return Response.ok(equipment).build();
    }

    public Response getExerciseTemplates(String jwt, String equipment) {
        // for jwt validation
        getUserFromJwt(jwt);

        String query = "SELECT t FROM ExerciseTemplate t where LOWER(t.equipment) = LOWER(:equipment)";
        List<ExerciseTemplate> templates = em.createQuery(query, ExerciseTemplate.class)
                .setParameter("equipment", equipment)
                .getResultList();

        JSONArray templatesJA = new JSONArray();
        for (ExerciseTemplate template : templates) {
            templatesJA.put(template.toJson());
        }
        return Response.ok(templatesJA.toString()).build();
    }

    private User getUserFromJwt(String jwt) {
        long id = jwtHelper.getUserId(jwt);
        User user = em.find(User.class, id);
        if (user == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        return user;
    }

    public void fillDatabase() {

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

                    ExerciseTemplate template = new ExerciseTemplate(json.getString("name"));
                    template.getBodyParts().add(json.getEnum(BodyPart.class, "bodyPart"));

                    if (!json.isNull("equipment")) {
                        template.setEquipment(json.getString("equipment"));
                    }

                    if (!json.isNull("desc")) {
                        template.setDescription(json.getString("desc"));
                    }

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

        // create user
        User user1 = new User(8387, "Alex123", "123ALEXtest", "Alex", "Burg");
        User user2 = new User(8383, "Pfeff", "PfeffPwd", "Mr", "Pepper");
        em.getTransaction().begin();
        em.persist(user1);
        em.persist(user2);
        em.getTransaction().commit();

        List<ExerciseTemplate> templates
                = em.createQuery("select t from ExerciseTemplate t", ExerciseTemplate.class).getResultList();

        WorkoutPlan workoutPlan1 = createWorkoutPlan();
        WorkoutPlan workoutPlan2 = createWorkoutPlan();

        em.getTransaction().begin();
        user1.getWorkoutPlans().add(workoutPlan1);
        user2.getWorkoutPlans().add(workoutPlan2);
        em.getTransaction().commit();
    }

    private WorkoutPlan createWorkoutPlan() {
        // warmup/cooldown
        Date warmup1, cooldown1;
        try {
            SimpleDateFormat formatter = TimeFormatHelper.getInstance().formatter;
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

        ExerciseTemplate template2 = new ExerciseTemplate("Bankdrücken (20)", BodyPart.CHEST);
        Exercise exercise2 = new Exercise(template2);
        ExerciseSpecification specification2 = new ExerciseSpecification(exercise2, "3", "5", "32,5");

        ExerciseTemplate template3 = new ExerciseTemplate("KH - Rudern im Stütz + Superman", BodyPart.BACK);
        Exercise exercise3 = new Exercise(template3);
        ExerciseSpecification specification3 = new ExerciseSpecification(exercise3, "3", "10/S. 15-20", "2x5");

        workout1.getSpecifications().addAll(Arrays.asList(specification1, specification2, specification3));
        workoutPlan1.getWorkouts().add(workout1);

        Workout workout2 = new Workout(2);

        ExerciseTemplate template4 = new ExerciseTemplate("Military Press", BodyPart.SHOULDER);
        Exercise exercise4 = new Exercise(template4);
        ExerciseSpecification specification4 = new ExerciseSpecification(exercise4, "3", "5", "22,5");

        ExerciseTemplate template5 = new ExerciseTemplate("Planksaw", BodyPart.BELLY);
        Exercise exercise5 = new Exercise(template5);
        ExerciseSpecification specification5 = new ExerciseSpecification(exercise5, "3", "10-15");

        workout2.getSpecifications().addAll(Arrays.asList(specification4, specification5));
        workoutPlan1.getWorkouts().add(workout2);

        return workoutPlan1;
    }
}
