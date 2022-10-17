package fr.multimc.api.spigot.tools.items;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Create easily a skull.
 *
 * @author Loïc MAES
 * @version 1.0
 * @since 04/10/2022
 */
public class SkullBuilder extends ItemBuilder {
    /**
     * Create an instance of SkullBuilder based on the ItemBuilder.
     */
    public SkullBuilder() {
        super(Material.PLAYER_HEAD);
    }

    /**
     * Create an instance of SkullBuilder based on the ItemBuilder with an existing item.
     *
     * @param item Item.
     */
    public SkullBuilder(@Nonnull ItemStack item) {
        super(item);
    }

    /**
     * Create an instance of SkullBuilder bas on the ItemBuilder with a custom amount.
     *
     * @param amount Amount.
     */
    public SkullBuilder(int amount) {
        super(Material.PLAYER_HEAD, amount);
    }

    /**
     * Set the skull owner by name.
     *
     * @param ownerName Owner name.
     * @return Current instance of the builder.
     */
    @Deprecated
    public SkullBuilder setOwner(@Nullable String ownerName) {
        SkullMeta meta = (SkullMeta) this.getMeta();
        meta.setOwner(ownerName);
        return (SkullBuilder) this.applyMeta(meta);
    }

    /**
     * Set the skull owner by an offline player (could be connected too).
     *
     * @param owner Owning player.
     * @return Current instance of the builder.
     */
    public SkullBuilder setOwningPlayer(@Nullable OfflinePlayer owner) {
        SkullMeta meta = (SkullMeta) this.getMeta();
        meta.setOwningPlayer(owner);
        return (SkullBuilder) this.applyMeta(meta);
    }
}
