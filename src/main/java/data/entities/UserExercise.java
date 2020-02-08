package data.entities;

import org.json.JSONObject;

import javax.persistence.*;

@Entity
public class UserExercise {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private ExerciseTemplate template;

    private int sets;
    private int reps;

    private int weight = 0;
    private boolean completed = false;
    private int groupId = 0;

    public UserExercise() {
    }

    public UserExercise(ExerciseTemplate template, int sets, int reps) {
        this.template = template;
        this.sets = sets;
        this.reps = reps;
    }

    public JSONObject toJson() {
        return new JSONObject()
                .put("id", id)
                .put("template", template.toJson())
                .put("sets", sets)
                .put("reps", reps)
                .put("weight", weight)
                .put("completed", completed)
                .put("groupId", groupId);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ExerciseTemplate getTemplate() {
        return template;
    }

    public void setTemplate(ExerciseTemplate template) {
        this.template = template;
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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
