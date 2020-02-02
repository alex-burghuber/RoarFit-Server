package data.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue
    private long id;

    private String firstName;
    private String lastName;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<WorkoutPlan> workoutPlans;

    public User() {
        workoutPlans = new ArrayList<>();
    }

    public User(String firstName, String lastName) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<WorkoutPlan> getWorkoutPlans() {
        return workoutPlans;
    }

    public void setWorkoutPlans(List<WorkoutPlan> workoutPlans) {
        this.workoutPlans = workoutPlans;
    }
}
