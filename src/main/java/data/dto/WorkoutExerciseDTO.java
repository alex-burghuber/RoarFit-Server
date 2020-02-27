package data.dto;

import java.util.Date;

public class WorkoutExerciseDTO {

    private long exerciseId;
    private String time;
    private int sets;
    private int reps;
    private int weight;
    private Date completedDate;

    public WorkoutExerciseDTO() {
    }

    public WorkoutExerciseDTO(long exerciseId, String time, int sets, int reps, int weight, Date completedDate) {
        this.exerciseId = exerciseId;
        this.time = time;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.completedDate = completedDate;
    }

    public long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }
}
