package fr.multimc.api.spigot.worlds.locations.zones.effects.effects;

import fr.multimc.api.spigot.entities.player.MmcPlayer;
import fr.multimc.api.spigot.worlds.locations.zones.effects.interfaces.IZoneEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * This class represents a zone effect that applies a potion effect to players or entities.
 * @author Xen0Xys
 */
public class PotionZoneEffect implements IZoneEffect {

    private final PotionEffectType effectType;
    private final PotionEffect effect;

    /**
     * Constructs a new potion zone effect.
     *
     * @param effectType {@link PotionEffectType} of potion effect to apply
     * @param level the level of the potion effect
     * @param duration the duration of the potion effect, in ticks
     * @param particules whether to display particles for the potion effect
     */
    public PotionZoneEffect(PotionEffectType effectType, int level, int duration, boolean particules) {
        this.effectType = effectType;
        this.effect = new PotionEffect(effectType, duration, level, particules);
    }

    /**
     * Applies the potion effect to the given player.
     *
     * @param player {@link MmcPlayer} to apply the effect to
     */
    @Override
    public void applyEffect(MmcPlayer player) {
        this.applyEffect(player.getPlayer());
    }

    /**
     * Removes the potion effect from the given player.
     *
     * @param player {@link MmcPlayer} to remove the effect from
     */
    @Override
    public void removeEffect(MmcPlayer player) {
        this.removeEffect(player.getPlayer());
    }

    /**
     * Applies the potion effect to the given entity.
     *
     * @param entity {@link Entity} to apply the effect to
     */
    @Override
    public void applyEffect(Entity entity) {
        if(entity instanceof LivingEntity lEntity)
            lEntity.addPotionEffect(this.effect);
    }

    /**
     * Removes the potion effect from the given entity.
     *
     * @param entity {@link Entity} to remove the effect from
     */
    @Override
    public void removeEffect(Entity entity) {
        if(entity instanceof LivingEntity lEntity)
            lEntity.removePotionEffect(this.effectType);
    }
}
