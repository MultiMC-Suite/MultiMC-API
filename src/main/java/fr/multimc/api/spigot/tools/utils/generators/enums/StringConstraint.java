package fr.multimc.api.spigot.tools.utils.generators.enums;

public enum StringConstraint {
    LOWER("abcdefghijklmnopqrstuvwxyz"),
    UPPER("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    NUMBERS("0123456789"),
    SPECIALS("#*$€£%+=/:.;?,!§(){}[]&-_");

    private final String value;

    StringConstraint(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
