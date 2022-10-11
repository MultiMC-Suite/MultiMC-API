package fr.multimc.api.commons.managers.game;

import org.bukkit.entity.EntityType;

public record CustomEntity(EntityType entityType, CustomLocation location) {

}
