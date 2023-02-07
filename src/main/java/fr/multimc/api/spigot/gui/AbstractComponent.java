package fr.multimc.api.spigot.gui;

import fr.multimc.api.spigot.entities.player.MmcPlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract class for representing a GUI component.
 *
 * @author MultiMC
 */
public abstract class AbstractComponent {

    private final ItemStack itemStack;

    /**
     * Creates a new {@link AbstractComponent} instance.
     *
     * @param itemStack The {@link ItemStack} representing this component.
     */
    public AbstractComponent(@NotNull final ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /**
     * Gets the {@link ItemStack} representing this component.
     *
     * @return The {@link ItemStack} representing this component.
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Called when the {@link AbstractComponent} is clicked in the GUI.
     *
     * @param gui The GUI that this component is part of.
     * @param mmcPlayer The {@link MmcPlayer} that clicked this component.
     */
    public abstract void onClicked(@NotNull final AbstractGui gui, @NotNull final MmcPlayer mmcPlayer);

}
