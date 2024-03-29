package fr.multimc.api.spigot.common.worlds.locations.zones.effects;

import fr.multimc.api.spigot.common.entities.player.MmcPlayer;
import fr.multimc.api.spigot.common.worlds.locations.RelativeLocation;
import fr.multimc.api.spigot.common.worlds.locations.zones.Zone;
import fr.multimc.api.spigot.common.worlds.locations.zones.effects.interfaces.IZoneEffect;
import fr.multimc.api.spigot.common.worlds.locations.zones.enums.ZoneListener;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents a zone effect that applies a set of effects to players or entities when they enter or exit a specified zone.
 */
@SuppressWarnings("unused")
public class ZoneEffect implements ZoneListener {

    private final Zone zone;
    private final IZoneEffect[] zoneEffects;

    /**
     * Constructs a new zone effect with a rectangular zone.
     *
     * @param plugin {@link Plugin} creating the zone effect
     * @param location1 {@link Location} that represents the first corner of the rectangle
     * @param location2 {@link Location} that represents the second corner of the rectangle
     * @param zoneEffects {@link IZoneEffect[]} to apply when players or entities enter or exit the zone
     */
    public ZoneEffect(@NotNull Plugin plugin, @NotNull Location location1, @NotNull Location location2, IZoneEffect... zoneEffects) {
        this.zone = new Zone(location1, location2, plugin, this);
        this.zoneEffects = zoneEffects;
    }

    /**
     * Constructs a new zone effect with a circular zone.
     *
     * @param plugin {@link Plugin} creating the zone effect
     * @param center {@link Location} that represents the center of the circle
     * @param location1 {@link RelativeLocation} location of the first radius point
     * @param location2 {@link RelativeLocation} the relative location of the second radius point
     * @param zoneEffects {@link IZoneEffect[]} to apply when players or entities enter or exit the zone
     */
    public ZoneEffect(@NotNull Plugin plugin, @NotNull Location center, @NotNull RelativeLocation location1, @NotNull RelativeLocation location2, IZoneEffect... zoneEffects) {
        this.zone = new Zone(center, location1, location2, plugin, this);
        this.zoneEffects = zoneEffects;
    }

    /**
     * Create new ZoneEffect with a given zone, and {@link IZoneEffect} array
     * @param zone: given {@link Zone} to be used as the zone
     * @param zoneEffects {@link IZoneEffect[]} to apply when players or entities enter or exit the zone
     */
    public ZoneEffect(@NotNull Zone zone, IZoneEffect... zoneEffects){
        this.zone = zone;
        this.zoneEffects = zoneEffects;
    }

    /**
     * Re-apply the zone effects to all entities (player includes) in the zone. Must be run synchronously.
     */
    public void update(){
        for(Entity entity : this.zone.getWorld().getEntities())
            if(this.zone.isIn(entity))
                for(IZoneEffect zoneEffect: this.zoneEffects)
                    zoneEffect.applyEffect(entity);
    }

    /**
     * Synchronously re-apply the zone effects to all entities (player includes) in the zone.
     */
    public void updateSync(Plugin plugin){
        Bukkit.getScheduler().runTask(plugin, this::update);
    }

    /**
     * Applies the effects to the player when they enter the zone.
     *
     * @param e {@link PlayerMoveEvent} that triggered the callback
     */
    @Override
    public void onPlayerEnter(PlayerMoveEvent e) {
        for(IZoneEffect zoneEffect: this.zoneEffects)
            zoneEffect.applyEffect(new MmcPlayer(e.getPlayer()));
    }

    /**
     * Applies the effects to the entity when they enter the zone.
     *
     * @param e {@link EntityMoveEvent} that triggered the callback
     */
    @Override
    public void onEntityEnter(EntityMoveEvent e) {
        for(IZoneEffect zoneEffect: this.zoneEffects)
            zoneEffect.applyEffect(e.getEntity());
    }

    /**
     * Removes the effects from the player when they exit the zone.
     *
     * @param e {@link PlayerMoveEvent} that triggered the callback
     */
    @Override
    public void onPlayerExit(PlayerMoveEvent e) {
        for(IZoneEffect zoneEffect: this.zoneEffects)
            zoneEffect.removeEffect(new MmcPlayer(e.getPlayer()));
    }

    /**
     * Removes the effects from the entity when they exit the zone.
     *
     * @param e {@link EntityMoveEvent} that triggered the callback
     */
    @Override
    public void onEntityExit(EntityMoveEvent e) {
        for(IZoneEffect zoneEffect: this.zoneEffects)
            zoneEffect.removeEffect(e.getEntity());
    }
}
