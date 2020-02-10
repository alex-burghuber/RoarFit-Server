package data.entities;

import helper.TimeFormatHelper;
import org.json.JSONObject;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class WorkoutPlan {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String info;

    @Temporal(value = TemporalType.TIME)
    private Date warmup;

    @Temporal(value = TemporalType.TIME)
    private Date cooldown;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Workout> workouts;

    private boolean archived = false;

    public WorkoutPlan() {
        workouts = new ArrayList<>();
    }

    public WorkoutPlan(String name, Date warmup, Date cooldown) {
        this();
        this.name = name;
        this.warmup = warmup;
        this.cooldown = cooldown;
    }

    public JSONObject toJson() {
        SimpleDateFormat formatter = TimeFormatHelper.getInstance().formatter;
        return new JSONObject()
                .put("id", id)
                .put("name", name)
                .put("info", id)
                .put("warmup", formatter.format(warmup))
                .put("cooldown", formatter.format(cooldown))
                .put("archived", archived);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Date getWarmup() {
        return warmup;
    }

    public void setWarmup(Date warmup) {
        this.warmup = warmup;
    }

    public Date getCooldown() {
        return cooldown;
    }

    public void setCooldown(Date cooldown) {
        this.cooldown = cooldown;
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
