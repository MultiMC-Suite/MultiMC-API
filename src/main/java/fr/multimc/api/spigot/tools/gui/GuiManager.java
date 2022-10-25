package fr.multimc.api.spigot.tools.gui;

import fr.multimc.api.spigot.tools.chat.TextBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Manage the GUIs:
 * - Save it in list;
 * - Open it to a player;
 * - Open multiple instances of one;
 * - Manage interactions.
 *
 * @author Loïc MAES
 * @version 1.0
 * @since 03/10/2022
 */
@SuppressWarnings("unused")
public class GuiManager implements Listener {
    private final Plugin plugin; // Main plugin instance.
    private final Map<Class<? extends GuiBuilder>, GuiBuilder> guis; // GUIs list.

    /**
     * Create the instance of the GUI manager.
     *
     * @param plugin Main plugin instance.
     */
    public GuiManager(@Nonnull Plugin plugin) {
        this.plugin = plugin;
        this.guis = new HashMap<>();
    }

    /**
     * Register a GUI to the list.
     *
     * @param gui GuiBuilder instance.
     */
    public void registerGui(@Nonnull GuiBuilder gui) {
        this.guis.put(gui.getClass(), gui);
    }

    /**
     * Register a list of GUIs to the main list.
     *
     * @param guis Guis list.
     */
    public void registerGui(@Nonnull GuiBuilder... guis) {
        Arrays.asList(guis).forEach(this::registerGui);
    }

    /**
     * Open the target GUI instance to the player to let him interact with.
     *
     * @param player Inventory owner.
     * @param guiClass GUI class reference.
     */
    public void openGui(@Nonnull Player player, @Nonnull Class<? extends GuiBuilder> guiClass) {
        if (player.getOpenInventory() instanceof PlayerInventory) player.closeInventory();

        final GuiBuilder gui = this.getGui(guiClass);
        final Inventory inventory = this.plugin.getServer().createInventory(null, 0, new TextBuilder(Objects.isNull(gui.title()) ? "&8Inventory" : gui.title()).build());
        gui.fill(player, inventory);
        player.openInventory(inventory);
    }

    /**
     * Get the GUI instance in the local list (if present).
     *
     * @param guiClass GUI class reference.
     * @return The GUI instance.
     */
    private GuiBuilder getGui(@Nonnull Class<? extends GuiBuilder> guiClass) {
        return this.guis.get(guiClass);
    }

    /**
     * Manage the whole interactions in GUIs.
     *
     * @param event Event fired.
     */
    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory inventory = event.getClickedInventory();
        final InventoryView view = event.getView();
        final ItemStack item = event.getCurrentItem();
        final int slot = event.getSlot();
        final ClickType click = event.getClick();

        if (Objects.isNull(inventory) || Objects.isNull(item)) return;

        this.guis.values().stream()
                .filter(gui -> view.title().equals(ChatColor.translateAlternateColorCodes('&', ChatColor.translateAlternateColorCodes('&', Objects.isNull(gui.title()) ? "&8Inventory" : gui.title()))))
                .forEach(gui -> event.setCancelled(gui.interact(player, inventory, item, slot, click)));
    }
}
