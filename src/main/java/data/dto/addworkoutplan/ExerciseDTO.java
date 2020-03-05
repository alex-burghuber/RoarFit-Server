package data.dto.addworkoutplan;

public class ExerciseDTO {

    private ExerciseTemplateDTO template;

    public ExerciseDTO() {
    }

    public ExerciseDTO(ExerciseTemplateDTO template) {
        this.template = template;
    }

    public ExerciseTemplateDTO getTemplate() {
        return template;
    }

    public void setTemplate(ExerciseTemplateDTO template) {
        this.template = template;
    }
}
