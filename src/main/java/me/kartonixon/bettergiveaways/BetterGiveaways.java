package me.kartonixon.bettergiveaways;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class BetterGiveaways extends JavaPlugin {

    private File customConfigFile;
    private FileConfiguration customConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic
        createCustomConfig();
        this.getCommand("giveaway").setExecutor(new Giveaway(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public FileConfiguration getCustomConfig() {
        return this.customConfig;
    }

    private void createCustomConfig() {

        customConfigFile = new File(getDataFolder(), "custom.yml");

        if (!customConfigFile.exists()) {

            customConfigFile.getParentFile().mkdirs();
            saveResource("custom.yml", false);

        }

        customConfig= new YamlConfiguration();

        try {

            customConfig.load(customConfigFile);

        } catch (IOException | InvalidConfigurationException e) {

            e.printStackTrace();

        }

    }

}