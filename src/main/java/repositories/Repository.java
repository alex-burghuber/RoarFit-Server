package repositories;

import data.entities.*;
import data.enums.BodyPart;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;

public class Repository {

    private EntityManager em = Persistence
            .createEntityManagerFactory("RoarFitPU")
            .createEntityManager();

    public String init() {
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
                            json.getString("equipment"),
                            json.getEnum(BodyPart.class, "bodyPart")
                    );
                    em.persist(template);
                }
                em.getTransaction().commit();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Exercise templates file not found");
        }

        // create user
        User user = new User();
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();

        // create workoutPlan
        Calendar warmup = Calendar.getInstance();
        warmup.clear();
        warmup.set(Calendar.MINUTE, 10);
        warmup.set(Calendar.SECOND, 0);

        Calendar cooldown = Calendar.getInstance();
        cooldown.clear();
        cooldown.set(Calendar.MINUTE, 17);
        cooldown.set(Calendar.SECOND, 50);

        WorkoutPlan workoutPlan = new WorkoutPlan("Starting Strength", warmup, cooldown);

        // create workouts
        Workout workout1 = new Workout(1);

        UserExercise userExercise = new UserExercise();

        Workout workout2 = new Workout(2);
        Workout workout3 = new Workout(3);


        em.getTransaction().begin();
        user.getWorkoutPlans().add(workoutPlan);
        em.getTransaction().commit();


        return "Initialised";
    }
}
