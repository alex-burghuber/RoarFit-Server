package data.dto.addworkoutplan;

import java.util.List;

public class ExerciseTemplateDTO {

    private String name;
    private String equipment;
    private List<String> bodyParts;
    private String description;
    private String videoUrl;

    public ExerciseTemplateDTO() {
    }

    public ExerciseTemplateDTO(String name, String equipment, List<String> bodyParts, String description, String videoUrl) {
        this.name = name;
        this.equipment = equipment;
        this.bodyParts = bodyParts;
        this.description = description;
        this.videoUrl = videoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public List<String> getBodyParts() {
        return bodyParts;
    }

    public void setBodyParts(List<String> bodyParts) {
        this.bodyParts = bodyParts;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
