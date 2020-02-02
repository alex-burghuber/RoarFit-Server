package data.entities;

import data.enums.BodyPart;

import javax.persistence.*;

@Entity
public class ExerciseTemplate {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String equipment;

    @Enumerated(value = EnumType.STRING)
    private BodyPart bodyPart;

    public ExerciseTemplate() {
    }

    public ExerciseTemplate(String name, String equipment, BodyPart bodyPart) {
        this.name = name;
        this.equipment = equipment;
        this.bodyPart = bodyPart;
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

    public BodyPart getBodyPart() {
        return bodyPart;
    }

    public void setBodyPart(BodyPart bodyPart) {
        this.bodyPart = bodyPart;
    }
}
