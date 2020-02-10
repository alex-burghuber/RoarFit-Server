package data.entities;

import javax.persistence.*;

@Entity
public class ExerciseSpecification {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Exercise exercise;

    private String sets;
    private String reps;
    private String weight;

    private int group = 0;

    private String info;

    private boolean completed = false;

    public ExerciseSpecification() {
    }

    public ExerciseSpecification(Exercise exercise, String sets, String reps) {
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
    }

    public ExerciseSpecification(Exercise exercise, String sets, String reps, String weight) {
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise completedExercise) {
        this.exercise = completedExercise;
    }

    public String getSets() {
        return sets;
    }

    public void setSets(String sets) {
        this.sets = sets;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
