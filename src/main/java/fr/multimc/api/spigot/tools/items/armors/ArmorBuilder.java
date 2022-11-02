package fr.multimc.api.spigot.tools.items.armors;

import fr.multimc.api.spigot.tools.items.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class ArmorBuilder extends ItemBuilder {
    public ArmorBuilder(@NotNull ItemStack item) {
        super(item);
    }

    public ArmorBuilder(@Nonnull ArmorPart part) {
        super(part.getMaterial());
    }

    public ArmorBuilder(@Nonnull ArmorPart part, int amount) {
        super(part.getMaterial(), amount);
    }

    public ArmorBuilder setColor(@Nonnull Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) this.getMeta();
        meta.setColor(color);
        return (ArmorBuilder) this.applyMeta(meta);
    }
}
