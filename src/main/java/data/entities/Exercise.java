package data.entities;

import javax.persistence.*;
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
    private String weight;

    public Exercise() {
    }

    public Exercise(ExerciseTemplate template) {
        this.template = template;
    }

    public Exercise(ExerciseTemplate template, Date time, int sets, int reps, String weight) {
        this.template = template;
        this.time = time;
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

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
