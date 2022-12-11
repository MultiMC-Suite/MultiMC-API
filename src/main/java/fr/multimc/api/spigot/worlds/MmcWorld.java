package fr.multimc.api.spigot.worlds;

import com.sk89q.worldedit.WorldEditException;
import fr.multimc.api.spigot.worlds.settings.WorldSettings;
import fr.multimc.api.spigot.worlds.settings.enums.WorldPrevention;
import fr.multimc.api.spigot.worlds.schematics.SchematicOptions;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

@SuppressWarnings({"unused", "rawtypes", "unchecked"})
public class MmcWorld implements Listener {

    private final WorldSettings worldSettings;
    private final JavaPlugin plugin;
    private World world;

    public MmcWorld(JavaPlugin plugin, WorldSettings worldSettings) {
        this.plugin = plugin;
        this.worldSettings = worldSettings;
        this.world = this.getWorld();
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.applyGameRules();
        this.world.setDifficulty(this.worldSettings.getDifficulty());
    }

    private void applyGameRules(){
        for(Map.Entry<GameRule, Object> gameRule : this.worldSettings.getGameRules().entrySet()){
            this.world.setGameRule(gameRule.getKey(), gameRule.getValue());
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

    private boolean checkPrevention(WorldPrevention prevention){
        return this.worldSettings.getPreventions().contains(prevention) ^ this.worldSettings.getPreventions().contains(WorldPrevention.ALL);
    }

    public Location getSpawnPoint(){
        return this.worldSettings.getSpawn().toAbsolute(new Location(this.world, 0, 0, 0));
    }
    public WorldSettings getWorldSettings() {
        return worldSettings;
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageEvent e){
        if(e.getEntity().getWorld().equals(this.world)){
            if(this.checkPrevention(WorldPrevention.PREVENT_DAMAGES)){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent e){
        if(e.getEntity().getWorld().equals(this.world)){
            if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
                if(this.checkPrevention(WorldPrevention.PREVENT_PVP)){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent e){
        if(e.getPlayer().getWorld().equals(this.world)){
            if(this.checkPrevention(WorldPrevention.PREVENT_BUILD)){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if(e.getPlayer().getWorld().equals(this.world)){
            if(this.checkPrevention(WorldPrevention.PREVENT_BUILD)){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e){
        if(e.getEntity() instanceof Player player){
            if(player.getWorld().equals(this.world)){
                if(this.checkPrevention(WorldPrevention.PREVENT_FOOD_LOSS)){
                    e.setCancelled(true);
                    player.setFoodLevel(20);
                    player.setSaturation(20);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent e){
        Player player = e.getPlayer();
        if (player.getWorld().equals(this.world)) {
            if(this.checkPrevention(WorldPrevention.PREVENT_PORTAL_USE)){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        if (player.getWorld().equals(this.world) && this.worldSettings.getGameMode() != null) {
            player.setGameMode(this.worldSettings.getGameMode());
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e){
        Player player = e.getPlayer();
        if(e.getCause() != PlayerTeleportEvent.TeleportCause.SPECTATE){
            if(!e.getFrom().getWorld().equals(this.world)){
                if(e.getTo().getWorld().equals(this.world) && this.worldSettings.getGameMode() != null){
                    player.setGameMode(this.worldSettings.getGameMode());
                }
            }
        }
    }
}
