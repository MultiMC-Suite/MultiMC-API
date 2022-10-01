package fr.multimc.api.spigot.tools.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class PotionBuilder extends ItemBuilder {
    /**
     *
     */
    public PotionBuilder() {
        super(Material.POTION);
    }

    /**
     *
     * @param item
     */
    public PotionBuilder(@Nonnull ItemStack item) {
        super(item);
    }

    /**
     *
     * @param amount
     */
    public PotionBuilder(int amount) {
        super(Material.POTION, amount);
    }

    // TODO
}
