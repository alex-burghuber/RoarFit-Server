package data.entities;

import org.json.JSONObject;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class StudioMember extends User {

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<WorkoutPlan> workoutPlans = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Exercise> personalExercises = new ArrayList<>();

    public StudioMember() {
    }

    public StudioMember(String username, String password, String firstName, String lastName) {
        super(username, password, firstName, lastName);
    }

    @Override
    public JSONObject toJson() {
        return super.toJson();
    }

    @Override
    public long getId() {
        return super.getId();
    }

    @Override
    public void setId(long id) {
        super.setId(id);
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public void setUsername(String username) {
        super.setUsername(username);
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public void setPassword(String password) {
        super.setPassword(password);
    }

    @Override
    public String getFirstName() {
        return super.getFirstName();
    }

    @Override
    public void setFirstName(String firstName) {
        super.setFirstName(firstName);
    }

    @Override
    public String getLastName() {
        return super.getLastName();
    }

    @Override
    public void setLastName(String lastName) {
        super.setLastName(lastName);
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
