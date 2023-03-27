package fr.multimc.api.spigot.common.gui.slots;

import fr.multimc.api.spigot.common.gui.enums.GuiSize;
import fr.multimc.api.spigot.common.gui.enums.Side;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class SlotsManager {

    private final GuiSize size;

    public SlotsManager(@NotNull final GuiSize size){
        this.size = size;
    }

    public int getFirstSlot(){
        return 0;
    }

    public int getLastSlot(){
        return size.getSize() - 1;
    }

    public List<Integer> getRow(final int row){
        return IntStream.range(0, size.getSize()).filter(i -> i / 9 == row).boxed().toList();
    }

    public List<Integer> getColumn(final int column){
        return IntStream.range(0, size.getSize()).filter(i -> i % 9 == column).boxed().toList();
    }

    public List<Integer> getBorder(@NotNull final Side side){
        return switch (side) {
            case TOP -> this.getRow(0);
            case BOTTOM -> this.getRow(this.size.getSize() / 9 - 1);
            case LEFT -> this.getColumn(0);
            case RIGHT -> this.getColumn(8);
        };
    }

}
