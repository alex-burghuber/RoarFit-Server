package data.entities;

import org.json.JSONObject;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
public class ExerciseTemplate {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String equipment;

    @ElementCollection
    private List<String> bodyParts;

    private String description;

    private String videoUrl;

    public ExerciseTemplate() {
        bodyParts = new ArrayList<>();
    }

    public ExerciseTemplate(String name, String... bodyParts) {
        this();
        this.name = name;
        this.bodyParts.addAll(Arrays.asList(bodyParts));
    }

    public ExerciseTemplate(String name, String equipment, String description, String videoUrl, List<String> bodyParts) {
        this();
        this.name = name;
        this.equipment = equipment;
        this.description = description;
        this.videoUrl = videoUrl;
        this.bodyParts = bodyParts;
    }

    public JSONObject toJson() {
        return new JSONObject()
                .put("id", id)
                .put("name", name)
                .put("equipment", equipment)
                .put("description", description)
                .put("bodyParts", bodyParts)
                .put("videoUrl", videoUrl);
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

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
