package fr.multimc.api.spigot.pre_made.samplecode.database;

import fr.multimc.api.commons.database.Database;
import fr.multimc.api.spigot.pre_made.samplecode.SampleCode;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@SuppressWarnings("unused")
public class DatabaseSampleCode implements SampleCode {
    @Override
    public void run(JavaPlugin plugin) {
        Database database = new Database(new File(plugin.getDataFolder().getPath() + "/database.db"), plugin.getLogger());
        CustomTableSample customTableSample = new CustomTableSample(database,
                "api_sample",
                "id INT PRIMARY KEY AUTO_INCREMENT",
                "content1 VARCHAR(255)",
                "content2 VARCHAR(255)");

        for(int i = 0; i < 10; i++){
            customTableSample.addContent(getRandomString(5), getRandomString(10));
        }
    }

    // A method to get a random string
    private String getRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            result.append(characters.charAt(index));
        }
        return result.toString();
    }
}
