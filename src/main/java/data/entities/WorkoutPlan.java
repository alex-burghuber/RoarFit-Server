package data.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
public class WorkoutPlan {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    @Temporal(value = TemporalType.TIME)
    private Calendar warmup;

    @Temporal(value = TemporalType.TIME)
    private Calendar cooldown;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Workout> workouts;

    public WorkoutPlan() {
        workouts = new ArrayList<>();
    }

    public WorkoutPlan(String name, Calendar warmup, Calendar cooldown) {
        this();
        this.name = name;
        this.warmup = warmup;
        this.cooldown = cooldown;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getWarmup() {
        return warmup;
    }

    public void setWarmup(Calendar warmup) {
        this.warmup = warmup;
    }

    public Calendar getCooldown() {
        return cooldown;
    }

    public void setCooldown(Calendar cooldown) {
        this.cooldown = cooldown;
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
    }
}
