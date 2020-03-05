package data.entities;

import org.json.JSONObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Workout {

    @Id
    @GeneratedValue
    private long id;

    private int day;
    private int week = 0;

    private String info;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<ExerciseSpecification> specifications;

    public Workout() {
        specifications = new ArrayList<>();
    }

    public Workout(int day) {
        this();
        this.day = day;
    }

    public Workout(int day, int week, String info) {
        this();
        this.day = day;
        this.week = week;
        this.info = info;
    }

    public JSONObject toJson() {
        return new JSONObject()
                .put("id", id)
                .put("day", day)
                .put("week", week)
                .put("info", info);
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

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<ExerciseSpecification> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<ExerciseSpecification> exercises) {
        this.specifications = exercises;
    }
}
