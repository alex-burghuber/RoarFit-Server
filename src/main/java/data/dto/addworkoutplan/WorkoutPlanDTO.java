package data.dto.addworkoutplan;

import java.util.List;

public class WorkoutPlanDTO {

    private String name;
    private String warmup;
    private String cooldown;
    private String info;
    private List<WorkoutDTO> workouts;

    public WorkoutPlanDTO() {
    }

    public WorkoutPlanDTO(String name, String warmup, String cooldown, String info, List<WorkoutDTO> workouts) {
        this.name = name;
        this.warmup = warmup;
        this.cooldown = cooldown;
        this.info = info;
        this.workouts = workouts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWarmup() {
        return warmup;
    }

    public void setWarmup(String warmup) {
        this.warmup = warmup;
    }

    public String getCooldown() {
        return cooldown;
    }

    public void setCooldown(String cooldown) {
        this.cooldown = cooldown;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<WorkoutDTO> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<WorkoutDTO> workouts) {
        this.workouts = workouts;
    }
}

