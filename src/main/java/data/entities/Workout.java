package data.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Workout {

    @Id
    @GeneratedValue
    private long id;

    private int day;
    private int week;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Exercise> exercises;

    public Workout() {
        exercises = new ArrayList<>();
    }

    public Workout(int day) {
        this();
        this.day = day;
    }

    public Workout(int day, int week) {
        this();
        this.day = day;
        this.week = week;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }
}
