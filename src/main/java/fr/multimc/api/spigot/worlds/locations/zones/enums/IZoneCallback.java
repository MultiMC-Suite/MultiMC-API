package fr.multimc.api.spigot.worlds.locations.zones.enums;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public interface IZoneCallback {
    void onEnter(PlayerMoveEvent e);
    void onEnter(EntityMoveEvent e);
    void onExit(PlayerMoveEvent e);
    void onExit(EntityMoveEvent e);
}
