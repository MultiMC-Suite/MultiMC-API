package fr.multimc.api.spigot.advancements.utils;

import org.bukkit.Material;

public class BackgroundLoader {

    public static String getBackground(Material material) {
        if(material.isBlock())
            return "textures/block/%s.png".formatted(material.getKey().getKey());
        else if(material.isItem())
            return "textures/item/%s.png".formatted(material.getKey().getKey());
        else
            throw new IllegalArgumentException("Material %s is not a block or an item".formatted(material));
    }

}
