package fr.multimc.api.spigot.tools.gui;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GuiManager implements Listener {
    private final Plugin plugin;
    private final Map<Class<? extends GuiBuilder>, GuiBuilder> guis;

    /**
     *
     * @param plugin
     */
    public GuiManager(@Nonnull Plugin plugin) {
        this.plugin = plugin;
        this.guis = new HashMap<>();
    }

    /**
     *
     * @param gui
     */
    public void registerGui(@Nonnull GuiBuilder gui) {
        this.guis.put(gui.getClass(), gui);
    }

    /**
     *
     * @param player
     * @param guiClass
     */
    public void openGui(@Nonnull Player player, @Nonnull Class<? extends GuiBuilder> guiClass) {
        if (player.getOpenInventory() instanceof PlayerInventory) player.closeInventory();

        final GuiBuilder gui = this.getGui(guiClass);
        final Inventory inventory = this.plugin.getServer().createInventory(null, 0, ChatColor.translateAlternateColorCodes('&', Objects.isNull(gui.title()) ? "&8Inventory" : gui.title()));
        gui.fill(player, inventory);
        player.openInventory(inventory);
    }

    /**
     *
     * @param guiClass
     * @return
     */
    private GuiBuilder getGui(@Nonnull Class<? extends GuiBuilder> guiClass) {
        return this.guis.get(guiClass);
    }

    /**
     *
     * @param event
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
