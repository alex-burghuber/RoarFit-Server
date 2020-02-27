package data.entities;

import helper.TimeFormatHelper;
import org.json.JSONObject;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Exercise {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private ExerciseTemplate template;

    @Temporal(TemporalType.TIME)
    private Date time;

    private int sets;
    private int reps;
    private float weight;

    @Temporal(TemporalType.TIMESTAMP)
    private Date completedDate;

    public Exercise() {
    }

    public Exercise(ExerciseTemplate template) {
        this.template = template;
    }

    public Exercise(ExerciseTemplate template, Date time, int sets, int reps, float weight, Date completedDate) {
        this.template = template;
        this.time = time;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.completedDate = completedDate;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject()
                .put("id", id)
                .put("template", template.toJson())
                .put("sets", sets)
                .put("reps", reps)
                .put("weight", weight);
        if (completedDate != null) {
            json.put("completedDate", completedDate.getTime());
        }
        if (time != null) {
            SimpleDateFormat formatter = TimeFormatHelper.getInstance().formatter;
            json.put("time", formatter.format(time));
        }
        return json;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
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

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }
}
