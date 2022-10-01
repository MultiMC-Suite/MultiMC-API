package fr.multimc.api.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class ProxyApi extends Plugin {

    private static Plugin instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public static Plugin getInstance() {
        return instance;
    }
}
