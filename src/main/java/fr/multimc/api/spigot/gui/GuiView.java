package fr.multimc.api.spigot.gui;

import fr.multimc.api.spigot.gui.components.AbstractComponent;
import fr.multimc.api.spigot.gui.enums.GuiSize;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class GuiView {

    private final GuiSize size;
    private final Map<Integer, ItemStack> items;

    public GuiView(@NotNull final GuiSize size){
        this.size = size;
        this.items = new HashMap<>();
    }

    public void setComponent(final int slot, @NotNull final AbstractComponent component){
        items.put(slot, component);
    }

    public void setItem(final int slot, @NotNull final ItemStack item){
        items.put(slot, item);
    }

    public void fill(@NotNull final Material background){
        this.fill(new ItemStack(background));
    }

    public void fill(@NotNull final Material material, @NotNull final List<Integer> slots){
        this.fill(new ItemStack(material), slots);
    }

    public void fill(@NotNull final ItemStack item, @NotNull final List<Integer> slots){
        slots.forEach(slot -> this.items.put(slot, item));
    }

    public void fill(@NotNull final ItemStack item){
        for(int i = 0; i < this.size.getSize(); i++)
            this.items.put(i, item);
    }

    public void apply(@NotNull final Inventory inventory){
        if(inventory.getSize() != this.size.getSize())
            throw new IllegalArgumentException("The inventory size must be equal to the GUI size.");
        inventory.clear();
        this.items.forEach(inventory::setItem);
    }

    public Map<Integer, ItemStack> getItems() {
        return items;
    }
}
