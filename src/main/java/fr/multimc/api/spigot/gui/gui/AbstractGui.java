package fr.multimc.api.spigot.gui.gui;

import fr.multimc.api.spigot.entities.player.MmcPlayer;
import fr.multimc.api.spigot.gui.GuiView;
import fr.multimc.api.spigot.gui.components.AbstractComponent;
import fr.multimc.api.spigot.gui.enums.GuiSize;
import fr.multimc.api.spigot.gui.slots.SlotsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 *  An abstract class representing a GUI.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class AbstractGui extends SlotsManager implements Listener {

    private final GuiSize size;
    private final Inventory inventory;
    private GuiView view;

    /**
     * Creates a new GUI instance with a given title, size and plugin.
     *
     * @param plugin the {@link Plugin} instance
     * @param title the GUI title as a {@link Component}
     * @param size the size of the GUI, represented by a {@link GuiSize}
     */
    public AbstractGui(@NotNull final Plugin plugin, @NotNull final Component title, @NotNull final GuiSize size) {
        this(plugin, title, size, new GuiView(size));
    }

    public AbstractGui(@NotNull final Plugin plugin, @NotNull final Component title, @NotNull final GuiSize size, @NotNull final GuiView view) {
        super(size);
        this.size = size;
        this.inventory = Bukkit.createInventory(null, size.getSize(), title);
        this.view = view;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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

    public GuiSize getSize() {
        return size;
    }

    public GuiView getView() {
        return view;
    }

    public void setView(@NotNull final GuiView view) {
        this.view = view;
    }

    public void render(){
        this.view.apply(this.inventory);
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
            this.onInventoryClick(slot, new MmcPlayer(e.getWhoClicked()));
            if(view.getItems().get(slot) instanceof AbstractComponent component){
                component.onClicked(this, new MmcPlayer(e.getWhoClicked()));
            }
        }
    }

    public abstract void onInventoryClick(final int slot, @NotNull final MmcPlayer mmcPlayer);
}
