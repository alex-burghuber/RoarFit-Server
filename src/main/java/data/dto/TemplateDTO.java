package data.dto;

public class TemplateDTO {

    private String equipment;

    public TemplateDTO() {
    }

    public TemplateDTO(String equipment) {
        this.equipment = equipment;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }
}
