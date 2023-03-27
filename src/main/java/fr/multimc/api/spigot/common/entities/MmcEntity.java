package fr.multimc.api.spigot.common.entities;

import fr.multimc.api.spigot.common.worlds.locations.RelativeLocation;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public record MmcEntity(@NotNull EntityType entityType, @NotNull RelativeLocation location) {

    public Entity spawn(@NotNull Location instanceLocation, int instanceId) {
        Location spawnLocation = new Location(instanceLocation.getWorld(),
                instanceLocation.getX() + location.getX(),
                instanceLocation.getY() + location.getY(),
                instanceLocation.getZ() + location.getZ());
        Entity entity = spawnLocation.getWorld().spawnEntity(spawnLocation, entityType);
        this.setInstanceId(entity, instanceId);
        return entity;
    }

    private void setInstanceId(@NotNull Entity entity, int instanceId){
        NBTEditor.set(entity, instanceId, "instance_id");
    }

}
