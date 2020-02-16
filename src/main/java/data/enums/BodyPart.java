package data.enums;

public enum BodyPart {
    LEGS("Beine"),
    BACK("Rücken"),
    CHEST("Brust"),
    UPPER_BACK("Oberer Rücken"),
    BELLY("Bauch"),
    SHOULDER("Schulter");

    private String german;

    BodyPart(String german) {
        this.german = german;
    }

    public String getGerman() {
        return german;
    }

    public void setGerman(String german) {
        this.german = german;
    }
}
