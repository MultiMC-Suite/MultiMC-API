package fr.multimc.api.spigot.managers.games;

import com.sk89q.worldedit.WorldEditException;
import fr.multimc.api.spigot.tools.locations.RelativeLocation;
import fr.multimc.api.spigot.managers.schematics.Schematic;
import fr.multimc.api.spigot.managers.schematics.SchematicOptions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class Lobby implements Listener {
    private final JavaPlugin plugin;
    private final LobbyOptions options;

    public Lobby(@Nonnull JavaPlugin plugin, @Nonnull LobbyOptions options) {
        this.plugin = plugin;
        this.options = options;
    }

    public void init(){
        Schematic schematic = new Schematic(this.plugin, "lobby");
        Location schematicLocation = this.getPasteLocation();
        try {
            SchematicOptions options = new SchematicOptions(schematicLocation);
            schematic.paste(options);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    public Location getPasteLocation(){
        return new Location(Bukkit.getWorld(this.options.WORLD_NAME), 0, 100, 0);
    }

    public Location getSpawnPoint() {
        return this.options.SPAWN.toAbsolute(this.getPasteLocation());
    }
}
