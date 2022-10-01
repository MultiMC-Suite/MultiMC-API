package fr.multimc.api.spigot.tools.items;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SkullBuilder extends ItemBuilder {
    /**
     *
     */
    public SkullBuilder() {
        super(Material.PLAYER_HEAD);
    }

    /**
     *
     * @param item
     */
    public SkullBuilder(@Nonnull ItemStack item) {
        super(item);
    }

    /**
     *
     * @param amount
     */
    public SkullBuilder(int amount) {
        super(Material.PLAYER_HEAD, amount);
    }

    /**
     *
     * @param ownerName
     * @return
     */
    @Deprecated
    public SkullBuilder setOwner(@Nullable String ownerName) {
        SkullMeta meta = (SkullMeta) this.getMeta();
        meta.setOwner(ownerName);
        return (SkullBuilder) this.applyMeta(meta);
    }

    /**
     *
     * @param owner
     * @return
     */
    public SkullBuilder setOwningPlayer(@Nullable OfflinePlayer owner) {
        SkullMeta meta = (SkullMeta) this.getMeta();
        meta.setOwningPlayer(owner);
        return (SkullBuilder) this.applyMeta(meta);
    }
}
