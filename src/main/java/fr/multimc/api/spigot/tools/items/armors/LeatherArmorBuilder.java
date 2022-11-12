package fr.multimc.api.spigot.tools.items.armors;

import fr.multimc.api.spigot.tools.items.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class LeatherArmorBuilder extends ItemBuilder {
    public LeatherArmorBuilder(@NotNull ItemStack item) {
        super(item);
    }

    public LeatherArmorBuilder(@NotNull LeatherArmorPart part) {
        super(part.getMaterial());
    }

    public LeatherArmorBuilder(@NotNull LeatherArmorPart part, int amount) {
        super(part.getMaterial(), amount);
    }

    public LeatherArmorBuilder setColor(@NotNull Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) this.getMeta();
        meta.setColor(color);
        return (LeatherArmorBuilder) this.applyMeta(meta);
    }
}
