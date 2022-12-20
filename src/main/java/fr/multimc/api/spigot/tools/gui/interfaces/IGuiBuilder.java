package fr.multimc.api.spigot.tools.gui.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * Custom GuiBuilder.
 * Make the GUI management easier for you.
 *
 * @author Loïc MAES
 * @version 1.0
 * @since 03/10/2022
 */
public interface IGuiBuilder {
    /**
     * This title will be colored dynamically before the opening of the GUI.
     *
     * @return Inventory's title
     */
    @Nonnull String title();

    /**
     * The size is represented by the number of slots (multiples of 9).
     *
     * @return Inventory's size.
     */
    int size();

    /**
     * Populate the inventory:
     * - Create animations;
     * - Repeat some stuff;
     * - Decorate the GUI.
     *
     * @param player Inventory owner.
     * @param inventory Inventory instance.
     */
    void fill(@Nonnull Player player, @Nonnull Inventory inventory);

    /**
     * Manage the GUI interaction:
     * - Filter actions;
     * - Close inventory if needed;
     * - Cancel the event if needed;
     * - Open sub GUIs.
     *
     * @param player Inventory owner.
     * @param inventory Inventory instance.
     * @param item Clicked item.
     * @param slot Clicked slot.
     * @param click Click type.
     * @return Should the event be cancelled.
     */
    boolean interact(@Nonnull Player player, @Nonnull Inventory inventory, @Nonnull ItemStack item, int slot, @Nonnull ClickType click);
}
