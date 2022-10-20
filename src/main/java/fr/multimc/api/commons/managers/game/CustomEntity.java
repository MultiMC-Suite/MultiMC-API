package fr.multimc.api.commons.managers.game;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public record CustomEntity(EntityType entityType, CustomLocation location) {

    public Entity spawn(Location instanceLocation, int instanceId) {
        Location spawnLocation = new Location(instanceLocation.getWorld(),
                instanceLocation.getX() + location.getX(),
                instanceLocation.getY() + location.getY(),
                instanceLocation.getZ() + location.getZ());
        Entity entity = spawnLocation.getWorld().spawnEntity(spawnLocation, entityType);
        this.setInstanceId(entity, instanceId);
        return entity;
    }

    private void setInstanceId(Entity entity, int instanceId){
        NBTEditor.set(entity, instanceId, "instance_id");
    }
}
