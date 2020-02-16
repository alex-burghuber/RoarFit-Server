package data.entities;

import data.enums.BodyPart;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ExerciseTemplate {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String equipment;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<BodyPart> bodyParts;

    private String description;

    public ExerciseTemplate() {
        bodyParts = new HashSet<>();
    }

    public ExerciseTemplate(String name) {
        this();
        this.name = name;
    }

    public ExerciseTemplate(String name, BodyPart... bodyParts) {
        this();
        this.name = name;
        this.bodyParts.addAll(Arrays.asList(bodyParts));
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject()
                .put("id", id)
                .put("name", name)
                .put("equipment", equipment)
                .put("description", description);
        JSONArray bodyPartsJA = new JSONArray();
        for (BodyPart bodyPart : bodyParts) {
            bodyPartsJA.put(bodyPart.getGerman());
        }
        json.put("bodyParts", bodyPartsJA);
        return json;
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

    public Set<BodyPart> getBodyParts() {
        return bodyParts;
    }

    public void setBodyParts(Set<BodyPart> bodyParts) {
        this.bodyParts = bodyParts;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }
}
