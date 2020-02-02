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
import java.util.List;

public class Repository {

    private EntityManager em = Persistence
            .createEntityManagerFactory("RoarFitPU")
            .createEntityManager();

    public Repository() {
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

    private void createPlaceholders() {
        // create user
        User user = new User("Max", "Mustermann");
        em.getTransaction().begin();
        em.persist(user);
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

        WorkoutPlan workoutPlan = new WorkoutPlan("Starting Strength", warmup, cooldown);

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

        workoutPlan.getWorkouts().add(workout1);
        workoutPlan.getWorkouts().add(workout2);

        em.getTransaction().begin();
        user.getWorkoutPlans().add(workoutPlan);
        em.getTransaction().commit();
    }
}
