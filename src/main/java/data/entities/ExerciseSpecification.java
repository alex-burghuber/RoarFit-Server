package data.entities;

import org.json.JSONObject;

import javax.persistence.*;

@Entity
public class ExerciseSpecification {

    @Id
    @GeneratedValue
    private long id;

    @OneToOne(cascade = CascadeType.PERSIST)
    private Exercise exercise;

    private int sets;
    private int reps;
    private float weight;

    private String info;

    public ExerciseSpecification() {
    }

    public ExerciseSpecification(Exercise exercise, int sets, int reps) {
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
    }

    public ExerciseSpecification(Exercise exercise, int sets, int reps, float weight) {
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
    }

    public ExerciseSpecification(int sets, int reps, float weight, String info) {
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.info = info;
    }

    public JSONObject toJson() {
        return new JSONObject()
                .put("id", id)
                .put("exercise", exercise.toJson())
                .put("sets", sets)
                .put("reps", reps)
                .put("weight", weight)
                .put("info", info);
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

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
