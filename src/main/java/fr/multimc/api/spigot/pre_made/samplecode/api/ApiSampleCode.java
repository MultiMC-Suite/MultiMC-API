package fr.multimc.api.spigot.pre_made.samplecode.api;

import fr.multimc.api.commons.data.handlers.RestHandler;
import fr.multimc.api.commons.data.sources.rest.RestAPI;
import fr.multimc.api.spigot.pre_made.samplecode.SampleCode;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ApiSampleCode implements SampleCode {
    @Override
    public void run(JavaPlugin plugin) {
        RestAPI api = new RestAPI(plugin.getLogger(), "http://url:3000", "root", "root");
        if(!api.login()) Bukkit.getServer().shutdown();

        RestHandler handler = new RestHandler(api);

        // Setting score for default team (demo)
        Map<String, Integer> scores = new HashMap<>();
        scores.put("TEAM1", 20);
        handler.setScores(scores);

        System.out.println(handler.getScores());
        System.out.println(handler.getTeamNamesByTeam());
        System.out.println(handler.getPlayersByTeam());
    }
}
