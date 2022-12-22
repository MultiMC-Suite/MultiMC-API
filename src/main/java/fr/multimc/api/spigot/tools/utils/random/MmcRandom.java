package fr.multimc.api.spigot.tools.utils.random;

import java.util.Random;

public class MmcRandom {

    public int nextInt(int max){
        return this.nextInt(0, max);
    }

    public int nextInt(int min, int max){
        return new Random().nextInt(max - min) + min;
    }
}
