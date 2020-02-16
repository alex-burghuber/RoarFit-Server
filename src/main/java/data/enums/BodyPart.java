package data.enums;

import org.apache.commons.text.WordUtils;

public enum BodyPart {
    LEGS,
    BACK,
    CHEST,
    UPPER_BACK,
    BELLY,
    SHOULDER;

    @Override
    public String toString() {
        String str = super.toString().replace('_', ' ');
        return WordUtils.capitalizeFully(str, ' ');
    }
}
