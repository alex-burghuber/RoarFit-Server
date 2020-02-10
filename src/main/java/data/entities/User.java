package data.entities;

import org.json.JSONObject;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {

    @Id
    private long id;

    private String username;
    private String password;

    private String firstName;
    private String lastName;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<WorkoutPlan> workoutPlans;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Exercise> personalExercises;

    public User() {
        workoutPlans = new ArrayList<>();
        personalExercises = new ArrayList<>();
    }

    public User(int id, String username, String password, String firstName, String lastName) {
        this();
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public JSONObject toJson() {
        return new JSONObject()
                .put("username", username)
                .put("firstName", firstName)
                .put("lastName", lastName);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public List<Exercise> getPersonalExercises() {
        return personalExercises;
    }

    public void setPersonalExercises(List<Exercise> personalExercises) {
        this.personalExercises = personalExercises;
    }
}
