package fr.multimc.api.spigot.gui.slots;

import fr.multimc.api.spigot.gui.enums.GuiSize;
import fr.multimc.api.spigot.gui.enums.Side;

import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class SlotsManager {

    private final GuiSize size;

    public SlotsManager(GuiSize size){
        this.size = size;
    }

    public int getFirstSlot(){
        return 0;
    }

    public int getLastSlot(){
        return size.getSize() - 1;
    }

    public List<Integer> getRow(int row){
        return IntStream.range(0, size.getSize()).filter(i -> i / 9 == row).boxed().toList();
    }

    public List<Integer> getColumn(int column){
        return IntStream.range(0, size.getSize()).filter(i -> i % 9 == column).boxed().toList();
    }

    public List<Integer> getBorder(Side side){
        return switch (side) {
            case TOP -> this.getRow(0);
            case BOTTOM -> this.getRow(this.size.getSize() / 9 - 1);
            case LEFT -> this.getColumn(0);
            case RIGHT -> this.getColumn(8);
        };
    }

}
