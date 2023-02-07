package fr.multimc.api.spigot.gui;

import fr.multimc.api.spigot.entities.player.MmcPlayer;
import fr.multimc.api.spigot.gui.enums.GuiSize;
import fr.multimc.api.spigot.gui.slots.SlotsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *  An abstract class representing a GUI.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class AbstractGui extends SlotsManager implements Listener {

    private final GuiSize size;
    private final Inventory inventory;
    private final Map<Integer, AbstractComponent> components;

    /**
     * Creates a new GUI instance with a given title, size and plugin.
     *
     * @param plugin the {@link Plugin} instance
     * @param title the GUI title as a {@link Component}
     * @param size the size of the GUI, represented by a {@link GuiSize}
     */
    public AbstractGui(@NotNull final Plugin plugin, @NotNull final Component title, @NotNull final GuiSize size) {
        super(size);
        this.size = size;
        this.inventory = Bukkit.createInventory(null, size.getSize(), title);
        this.components = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Adds a {@link AbstractComponent} to the GUI.
     *
     * @param slot the slot number where the component will be placed
     * @param component the component to add
     * @return the instance of the GUI
     */
    public AbstractGui addComponent(final int slot, @NotNull final AbstractComponent component){
        components.put(slot, component);
        inventory.setItem(slot, component.getItemStack());
        return this;
    }

    /**
     * Sets an item in the GUI at a specific slot.
     *
     * @param slot the slot number where the item will be placed
     * @param item the item to set
     * @return the instance of the GUI
     */
    public AbstractGui setItem(final int slot, @NotNull final ItemStack item){
        inventory.setItem(slot, item);
        return this;
    }

    /**
     * Opens the GUI for a player.
     *
     * @param mmcPlayer the {@link MmcPlayer} for whom to open the GUI
     */
    public void openInventory(@NotNull final MmcPlayer mmcPlayer){
        mmcPlayer.openInventory(this.inventory);
    }

    /**
     * Closes the GUI for a player.
     *
     * @param mmcPlayer the {@link MmcPlayer} for whom to close the GUI
     */
    public void closeInventory(@NotNull final MmcPlayer mmcPlayer){
        mmcPlayer.closeInventory(this.inventory);
    }

    /**
     * Fills the GUI with a specific material.
     *
     * @param background the {@link Material} to fill the GUI with
     */
    public void fill(@NotNull final Material background){
        this.fill(new ItemStack(background));
    }

    public void fill(@NotNull final ItemStack item){
        for(int i = 0; i < size.getSize(); i++)
            inventory.setItem(i, item);
    }

    public void fill(@NotNull final Material material, @NotNull final List<Integer> slots){
        this.fill(new ItemStack(material), slots);
    }

    public void fill(@NotNull final ItemStack item, @NotNull final List<Integer> slots){
        slots.forEach(slot -> inventory.setItem(slot, item));
    }

    /**
     * Handles clicks on items in the GUI.
     *
     * @param e the {@link InventoryClickEvent} to handle
     */
    @EventHandler
    public void onInventoryClick(@NotNull final InventoryClickEvent e) {
        if (e.getInventory().equals(inventory)){
            e.setCancelled(true);
            final int slot = e.getSlot();
            final AbstractComponent component = components.get(slot);
            if(Objects.nonNull(component))
                component.onClicked(this, new MmcPlayer(e.getWhoClicked()));
        }
    }
}
