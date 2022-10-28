package fr.multimc.api.spigot.tools.worlds;

import com.sk89q.worldedit.WorldEditException;
import fr.multimc.api.spigot.tools.schematics.SchematicOptions;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
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
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class MmcWorld implements Listener {

    private final WorldSettings worldSettings;
    private final JavaPlugin plugin;
    private World world;

    public MmcWorld(JavaPlugin plugin, WorldSettings worldSettings) {
        this.plugin = plugin;
        this.worldSettings = worldSettings;
        this.world = this.getWorld();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        // Game rules
        if(this.worldSettings.isPreventTimeFlow()){
            this.world.setTime(6000);
            this.world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        }
        if(this.worldSettings.isPreventWeather()){
            this.world.setStorm(false);
            this.world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        }
    }

    private World generateWorld(){
        VoidWorld worldCreator = new VoidWorld();
        this.world = worldCreator.generate(this.worldSettings.getWorldName());
        if(this.worldSettings.getSchematic() != null){
            try {
                this.worldSettings.getSchematic().paste(new SchematicOptions(this.getSpawnPoint()));
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            }
        }
        return this.world;
    }

    public World getWorld(){
        if(this.world == null){
            this.plugin.getLogger().info(String.format("Loading world %s...", this.worldSettings.getWorldName()));
            this.world = Bukkit.getWorld(this.worldSettings.getWorldName());
            if(this.world == null){
                this.plugin.getLogger().info(String.format("Generating world %s...", this.worldSettings.getWorldName()));
                this.world = this.generateWorld();
                this.plugin.getLogger().info(String.format("World %s generated!", this.worldSettings.getWorldName()));
            }else{
                this.plugin.getLogger().info(String.format("World %s loaded!", this.worldSettings.getWorldName()));

            }
        }
        return this.world;
    }

    public Location getSpawnPoint(){
        return this.worldSettings.getSpawn().toAbsolute(new Location(this.world, 0, 0, 0));
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageEvent e){
        if(e.getEntity().getWorld().equals(this.world)){
            if(this.worldSettings.isPreventDamages()){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent e){
        if(e.getEntity().getWorld().equals(this.world)){
            if(this.worldSettings.isPreventPvp()){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent e){
        if(e.getPlayer().getWorld().equals(this.world)){
            if(this.worldSettings.isPreventBuild()){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if(e.getPlayer().getWorld().equals(this.world)){
            if(this.worldSettings.isPreventBuild()){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e){
        if(e.getEntity() instanceof Player player){
            if(player.getWorld().equals(this.world)){
                if(this.worldSettings.isPreventBuild()){
                    e.setCancelled(true);
                    player.setFoodLevel(20);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent e){
        Player player = e.getPlayer();
        if (player.getWorld().equals(this.world)) {
            if(this.worldSettings.isPreventPortalUse()){
                e.setCancelled(true);
            }
        }
    }
}
