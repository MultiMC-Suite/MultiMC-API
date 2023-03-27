package fr.multimc.api.spigot.gui.enums;

public enum GuiSize {
    ONE_ROW(9),
    TWO_ROWS(18),
    CHEST(27),
    FOUR_ROWS(36),
    FIVE_ROWS(45),
    DOUBLE_CHEST(54);

    private final int size;

    GuiSize(final int size){
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
