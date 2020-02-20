package data.dto;

public class WorkoutExerciseDTO {

    private long exerciseId;
    private String time;
    private int sets;
    private int reps;
    private String weight;

    public WorkoutExerciseDTO() {
    }

    public WorkoutExerciseDTO(long exerciseId, String time, int sets, int reps, String weight) {
        this.exerciseId = exerciseId;
        this.time = time;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
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

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
