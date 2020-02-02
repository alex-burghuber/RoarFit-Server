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

    @OneToMany(cascade = CascadeType.ALL)
    private List<UserExercise> userExercises;

    public Workout() {
        userExercises = new ArrayList<>();
    }

    public Workout(int day) {
        this();
        this.day = day;
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

    public List<UserExercise> getUserExercises() {
        return userExercises;
    }

    public void setUserExercises(List<UserExercise> userExercises) {
        this.userExercises = userExercises;
    }
}
