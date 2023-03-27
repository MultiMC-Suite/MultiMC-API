package fr.multimc.api.spigot.gui.components;

import fr.multimc.api.spigot.entities.player.MmcPlayer;
import fr.multimc.api.spigot.gui.gui.AbstractGui;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract class for representing a GUI component.
 *
 * @author MultiMC
 */
public abstract class AbstractComponent extends ItemStack{

    /**
     * Creates a new {@link AbstractComponent} instance.
     *
     * @param itemStack The {@link ItemStack} representing this component.
     */
    public AbstractComponent(@NotNull final ItemStack itemStack) {
        super(itemStack);
    }

    /**
     * Called when the {@link AbstractComponent} is clicked in the GUI.
     *
     * @param gui The GUI that this component is part of.
     * @param mmcPlayer The {@link MmcPlayer} that clicked this component.
     */
    public abstract void onClicked(@NotNull final AbstractGui gui, @NotNull final MmcPlayer mmcPlayer);

}
