package fr.multimc.api.spigot.tools.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface GuiBuilder {
    /**
     *
     * @return
     */
    @Nonnull String title();

    /**
     *
     * @return
     */
    int size();

    /**
     *
     * @param player
     * @param inventory
     */
    void fill(@Nonnull Player player, @Nonnull Inventory inventory);

    /**
     *
     * @param player
     * @param inventory
     * @param item
     * @param slot
     * @param click
     * @return
     */
    boolean interact(@Nonnull Player player, @Nonnull Inventory inventory, @Nonnull ItemStack item, int slot, @Nonnull ClickType click);
}
