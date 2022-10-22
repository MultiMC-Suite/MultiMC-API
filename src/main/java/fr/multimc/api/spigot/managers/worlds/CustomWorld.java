package fr.multimc.api.spigot.managers.worlds;

import com.sk89q.worldedit.WorldEditException;
import fr.multimc.api.spigot.managers.schematics.SchematicOptions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomWorld implements Listener {

    private final WorldSettings worldSettings;
    private final JavaPlugin plugin;
    private World world;

    public CustomWorld(JavaPlugin plugin, WorldSettings worldSettings) {
        this.plugin = plugin;
        this.worldSettings = worldSettings;
        this.world = this.getWorld();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private World generateWorld(){
        VoidWorldCreator worldCreator = new VoidWorldCreator();
        this.world = worldCreator.generate(this.worldSettings.WORLD_NAME);
        if(this.worldSettings.SCHEMATIC != null){
            try {
                this.worldSettings.SCHEMATIC.paste(new SchematicOptions(this.getSpawnPoint()));
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            }
        }
        return this.world;
    }

    public World getWorld(){
        if(this.world == null){
            this.plugin.getLogger().info(String.format("Loading world %s...", this.worldSettings.WORLD_NAME));
            this.world = Bukkit.getWorld(this.worldSettings.WORLD_NAME);
            if(this.world == null){
                this.plugin.getLogger().info(String.format("Generating world %s...", this.worldSettings.WORLD_NAME));
                this.world = this.generateWorld();
                this.plugin.getLogger().info(String.format("World %s generated!", this.worldSettings.WORLD_NAME));
            }else{
                this.plugin.getLogger().info(String.format("World %s loaded!", this.worldSettings.WORLD_NAME));

            }
        }
        return this.world;
    }

    public Location getSpawnPoint(){
        return this.worldSettings.SPAWN.toAbsolute(new Location(this.world, 0.5, 100, 0.5));
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageEvent e){
        if(e.getEntity().getWorld().equals(this.world)){
            if(this.worldSettings.PREVENT_DAMAGES){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent e){
        if(e.getEntity().getWorld().equals(this.world)){
            if(this.worldSettings.PREVENT_PVP){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent e){
        if(e.getPlayer().getWorld().equals(this.world)){
            if(this.worldSettings.PREVENT_BUILD){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if(e.getPlayer().getWorld().equals(this.world)){
            if(this.worldSettings.PREVENT_BUILD){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e){
        if(e.getEntity() instanceof Player player){
            if(player.getWorld().equals(this.world)){
                if(this.worldSettings.PREVENT_BUILD){
                    e.setCancelled(true);
                    player.setFoodLevel(20);
                }
            }
        }
    }
}
