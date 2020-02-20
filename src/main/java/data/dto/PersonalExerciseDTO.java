package data.dto;

public class PersonalExerciseDTO {

    private long templateId;
    private String time;
    private int sets;
    private int reps;
    private String weight;

    public PersonalExerciseDTO() {
    }

    public PersonalExerciseDTO(long templateId, String time, int sets, int reps, String weight) {
        this.templateId = templateId;
        this.time = time;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
    }

    public long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
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
