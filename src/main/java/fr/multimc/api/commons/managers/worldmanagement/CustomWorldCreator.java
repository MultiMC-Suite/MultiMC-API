package fr.multimc.api.commons.managers.worldmanagement;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Random;

@SuppressWarnings("unused")
public class CustomWorldCreator extends ChunkGenerator {

    public World generate(String name){
        WorldCreator wc = new WorldCreator(name);
        wc.generator(new CustomWorldCreator());
        wc.createWorld();
        return Bukkit.getWorld(name);
    }

    @Override
    public @NotNull ChunkData generateChunkData(@Nonnull World world, @Nonnull Random random, int x, int z, @Nonnull BiomeGrid biome) {
        return createChunkData(world);
    }
}
