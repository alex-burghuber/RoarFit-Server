package data.entities;

import data.enums.BodyPart;
import org.json.JSONObject;

import javax.persistence.*;

@Entity
public class Exercise {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String equipment;

    @Enumerated(EnumType.STRING)
    private BodyPart bodyPart;

    private int sets;
    private String reps;
    private String weight;

    private boolean completed = false;
    private int groupId = 0;

    public Exercise() {
    }

    public JSONObject toJson() {
        return new JSONObject()
                .put("id", id)
                .put("name", name)
                .put("equipment", equipment)
                .put("bodyPart", bodyPart)
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

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
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
