package data.dto.addworkoutplan;

public class ExerciseSpecificationDTO {

    private ExerciseDTO exercise;
    private int sets;
    private int reps;
    private float weight = 0f;
    private String info;

    public ExerciseSpecificationDTO() {
    }

    public ExerciseSpecificationDTO(ExerciseDTO exercise, int sets, int reps, float weight, String info) {
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.info = info;
    }

    public ExerciseDTO getExercise() {
        return exercise;
    }

    public void setExercise(ExerciseDTO exercise) {
        this.exercise = exercise;
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
