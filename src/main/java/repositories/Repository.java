package repositories;

import entities.User;
import entities.WorkoutPlan;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class Repository {

    private EntityManager em = Persistence
            .createEntityManagerFactory("RoarFitPU")
            .createEntityManager();

    public String init() {
        User user = new User();
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();

        Calendar warmup = Calendar.getInstance();
        warmup.clear();
        warmup.set(Calendar.MINUTE, 5);
        warmup.set(Calendar.SECOND, 25);

        Calendar cooldown = Calendar.getInstance();
        cooldown.clear();
        cooldown.set(Calendar.MINUTE, 2);
        cooldown.set(Calendar.SECOND, 30);

        WorkoutPlan workoutPlan = new WorkoutPlan("Starting Strength", warmup, cooldown);
        em.getTransaction().begin();
        user.getWorkoutPlans().add(workoutPlan);
        em.getTransaction().commit();

        return "Initialised";
    }

}
