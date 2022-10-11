package fr.multimc.api.spigot.samplecode.teams;

import fr.multimc.api.commons.database.Database;
import fr.multimc.api.commons.managers.teammanager.TeamManager;
import fr.multimc.api.spigot.samplecode.SampleCode;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class TeamSampleCode implements SampleCode, Listener {

    private TeamManager teamManager;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void run(JavaPlugin plugin) {
        new File(plugin.getDataFolder().getPath() + "/database.db").delete();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Database database = new Database(new File(plugin.getDataFolder().getPath() + "/database.db"), plugin.getLogger());
        teamManager = new TeamManager(database);
        teamManager.addTeam("T1", "Xen0Xys");
        teamManager.addTeam("T2", "XenAdmin");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if(Bukkit.getServer().getOnlinePlayers().size() >= 2){
            System.out.println(teamManager.loadTeams().get(0).getPlayers().get(0).getName());
            System.out.println(teamManager.loadTeams().get(1).getPlayers().get(0).getName());
        }
    }
}
